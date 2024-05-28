package com.roze.user_service.mapper;

import com.roze.user_service.dto.response.UserResponse;
import com.roze.user_service.enums.RoleName;
import com.roze.user_service.persistance.model.RoleEntity;
import com.roze.user_service.persistance.model.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "roleNames", source = "roles")
    UserResponse userEntityToUserResponse(UserEntity userEntity);

    default Set<RoleName> mapRoles(Set<RoleEntity> roles) {
        return roles.stream()
                .map(role -> role.getName())
                .collect(Collectors.toSet());
    }
}
