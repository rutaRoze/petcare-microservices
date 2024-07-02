package com.roze.auth_service.mapper;

import com.roze.auth_service.dto.request.UserRequest;
import com.roze.auth_service.dto.response.UserResponse;
import com.roze.auth_service.enums.RoleName;
import com.roze.auth_service.persistance.model.RoleEntity;
import com.roze.auth_service.persistance.model.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "roleNames", source = "roles")
    UserResponse userEntityToUserResponse(UserEntity userEntity);

    default List<RoleName> mapRoles(List<RoleEntity> roles) {
        return roles.stream()
                .map(role -> role.getName())
                .collect(Collectors.toList());
    }

    @Mapping(target = "roles", source = "roleNames")
    UserEntity userRequestToUserEntity(UserRequest userRequest);

    default List<RoleEntity> mapRoleNamesToRoles(List<RoleName> roleNames) {
        return roleNames.stream()
                .map(roleName -> {
                    RoleEntity roleEntity = new RoleEntity();
                    roleEntity.setName(roleName);
                    return roleEntity;
                })
                .collect(Collectors.toList());
    }
}