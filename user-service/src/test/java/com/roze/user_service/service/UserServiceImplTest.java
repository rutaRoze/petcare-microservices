package com.roze.user_service.service;

import com.roze.user_service.dto.response.UserResponse;
import com.roze.user_service.enums.RoleName;
import com.roze.user_service.mapper.UserMapper;
import com.roze.user_service.persistance.UserRepository;
import com.roze.user_service.persistance.model.RoleEntity;
import com.roze.user_service.persistance.model.UserEntity;
import com.roze.user_service.service.impl.UserServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepositoryMock;
    @Mock
    private UserMapper userMapperMock;
    @InjectMocks
    private UserServiceImpl userServiceMock;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private UserResponse setUpUserResponse() {
        return UserResponse.builder()
                .id(1L)
                .name("John")
                .surname("Doe")
                .email("john@doe.com")
                .phoneNumber("1234567890")
                .roleNames(Set.of(RoleName.VET))
                .build();
    }

    private UserEntity setUpUserEntity() {
        RoleEntity role = new RoleEntity();
        role.setName(RoleName.VET);

        return UserEntity.builder()
                .id(1L)
                .name("John")
                .surname("Doe")
                .email("john@doe.com")
                .phoneNumber("1234567890")
                .roles(Set.of(role))
                .build();
    }

    @Test
    void findUserById_WhenSuccessful() {
        Long validId = 1L;
        UserResponse userResponse = setUpUserResponse();
        UserEntity userEntity = setUpUserEntity();

        when(userRepositoryMock.findById(validId)).thenReturn(Optional.of(userEntity));
        when(userMapperMock.userEntityToUserResponse(userEntity)).thenReturn(userResponse);

        UserResponse result = userServiceMock.findUserById(validId);
        assertEquals(userResponse, result);

        verify(userRepositoryMock, times(1)).findById(validId);
    }

    @Test
    void findUserById_WhenNotFound() {
        Long nonExistentId = 999L;

        when(userRepositoryMock.findById(nonExistentId)).thenReturn(Optional.empty());

        EntityNotFoundException thrownException = assertThrows(EntityNotFoundException.class, () ->
                userServiceMock.findUserById(nonExistentId));

        assertEquals("User not found by id: " + nonExistentId, thrownException.getMessage());

        verify(userRepositoryMock, times(1)).findById(nonExistentId);
    }
}
