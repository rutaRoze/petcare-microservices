package com.roze.auth_service.mapper;

import com.roze.auth_service.dto.request.UserRequest;
import com.roze.auth_service.persistance.model.AuthUserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface AuthUserMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "password", source = "password"),
            @Mapping(target = "email", source = "email")
    })
    AuthUserEntity userRequestToAuthUserEntity(UserRequest userRequest);
}