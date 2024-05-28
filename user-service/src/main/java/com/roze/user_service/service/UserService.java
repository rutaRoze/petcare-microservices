package com.roze.user_service.service;

import com.roze.user_service.dto.response.UserResponse;

public interface UserService {

    UserResponse findUserById(Long id);
}

