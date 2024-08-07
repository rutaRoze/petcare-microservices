package com.roze.user_service.service.impl;

import com.roze.user_service.dto.request.UserRequest;
import com.roze.user_service.dto.response.UserResponse;
import com.roze.user_service.enums.RoleName;
import com.roze.user_service.exception.NoChangesMadeException;
import com.roze.user_service.exception.UserAlreadyExist;
import com.roze.user_service.kafka.producer.KafkaEventProducer;
import com.roze.user_service.mapper.UserMapper;
import com.roze.user_service.persistance.UserRepository;
import com.roze.user_service.persistance.model.RoleEntity;
import com.roze.user_service.persistance.model.UserEntity;
import com.roze.user_service.service.RoleService;
import com.roze.user_service.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleService roleService;

    @Autowired
    private KafkaEventProducer kafkaEventProducer;

    @Override
    @CacheEvict(value = "usersCache", allEntries = true)
    public UserResponse saveUser(UserRequest userRequest) {

        if (checkIfUserExistsByEmail(userRequest.getEmail())) {
            throw new UserAlreadyExist(String.format("User with email %s already exists", userRequest.getEmail()));
        }

        UserEntity userToSave = userMapper.userRequestToUserEntity(userRequest);

        userToSave.setName(sanitizeData(userRequest.getName()));
        userToSave.setSurname(sanitizeData(userRequest.getSurname()));
        userToSave.setRoles(roleService.getRoleEntitiesFormRoleNames(userRequest.getRoleNames()));

        UserEntity savedUser = userRepository.save(userToSave);

        return userMapper.userEntityToUserResponse(savedUser);
    }

    @Override
    @Cacheable(value = "usersCache", key = "'allUsers'")
    public List<UserResponse> findAllUsers() {

        return userRepository.findAll().stream()
                .map(userEntity -> userMapper.userEntityToUserResponse(userEntity))
                .toList();
    }

    @Override
    public UserResponse findUserById(Long id) {

        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found by id: " + id));

        return userMapper.userEntityToUserResponse(userEntity);
    }

    @Override
    @CacheEvict(value = "usersCache", allEntries = true)
    public UserResponse updateUserById(Long id, UserRequest userRequest) {
        UserEntity existingUser = getUserByIdOrThrow(id);

        if (isUserDataEqual(existingUser, userRequest)) {
            throw new NoChangesMadeException("User entry was not updated as no changes of entry were made.");
        }

        if (!existingUser.getEmail().equals(userRequest.getEmail()) && checkIfUserExistsByEmail(userRequest.getEmail())) {
            throw new UserAlreadyExist(String.format("User with email %s already exists", userRequest.getEmail()));
        }

        updateUserData(existingUser, userRequest);
        UserEntity savedUser = userRepository.save(existingUser);

        return userMapper.userEntityToUserResponse(savedUser);
    }

    @Override
    @CacheEvict(value = "usersCache", allEntries = true)
    public void deleteUserById(Long id) {
        getUserByIdOrThrow(id);
        userRepository.deleteById(id);

        kafkaEventProducer.sendUserDeletionEvent(id);
        kafkaEventProducer.sendCacheEvictionEvent(id);
    }

    private UserEntity getUserByIdOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found by id: " + id));
    }

    private boolean checkIfUserExistsByEmail(String email) {
        return userRepository.existsByEmailIgnoreCase(email);
    }

    private boolean isUserDataEqual(UserEntity existingUser, UserRequest userRequest) {
        return existingUser.getName().equals(sanitizeData(userRequest.getName())) &&
                existingUser.getSurname().equals(sanitizeData(userRequest.getSurname())) &&
                existingUser.getEmail().equals(userRequest.getEmail()) &&
                existingUser.getPhoneNumber().equals(userRequest.getPhoneNumber()) &&
                isRolesEqual(existingUser.getRoles(), userRequest.getRoleNames());
    }

    private boolean isRolesEqual(List<RoleEntity> existingRoles, List<RoleName> requestedRoleNames) {
        List<RoleName> existingRoleNames = existingRoles.stream()
                .map(RoleEntity::getName)
                .toList();

        return existingRoleNames.equals(requestedRoleNames);
    }

    private void updateUserData(UserEntity existingUser, UserRequest userRequest) {
        existingUser.setName(sanitizeData(userRequest.getName()));
        existingUser.setSurname(sanitizeData(userRequest.getSurname()));
        existingUser.setEmail(userRequest.getEmail());
        existingUser.setPhoneNumber(existingUser.getPhoneNumber());
        existingUser.setRoles(roleService.getRoleEntitiesFormRoleNames(userRequest.getRoleNames()));
    }

    private String sanitizeData(String data) {
        return data.trim();
    }
}
