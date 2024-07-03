package com.roze.auth_service.mapper;

import com.roze.auth_service.dto.request.UserProfileRequest;
import com.roze.auth_service.dto.request.UserRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    @Mappings({
            @Mapping(target = "name", source = "name"),
            @Mapping(target = "surname", source = "surname"),
            @Mapping(target = "email", source = "email"),
            @Mapping(target = "phoneNumber", source = "phoneNumber"),
            @Mapping(target = "roleNames", source = "roleNames")
    })
    UserProfileRequest userRequestToUserProfileRequest(UserRequest userRequest);
}
