package com.roze.user_service.service.impl;

import com.roze.user_service.dto.response.UserResponse;
import com.roze.user_service.mapper.UserMapper;
import com.roze.user_service.persistance.UserRepository;
import com.roze.user_service.persistance.model.UserEntity;
import com.roze.user_service.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserMapper userMapper;

    @Override
    public UserResponse findUserById(Long id) {

        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found by id: " + id));

        return userMapper.userEntityToUserResponse(userEntity);
    }
}
