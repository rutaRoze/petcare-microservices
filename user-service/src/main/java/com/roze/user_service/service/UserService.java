package com.roze.user_service.service;

import com.roze.user_service.dto.request.UserRequest;
import com.roze.user_service.dto.response.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse findUserById(Long id);

    UserResponse saveUser(UserRequest userRequest);

    UserResponse updateUserById(Long id, UserRequest userRequest);

    void deleteUserById(Long id);

    List<UserResponse> findAllUsers();
}

