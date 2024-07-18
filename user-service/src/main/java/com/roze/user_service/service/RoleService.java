package com.roze.user_service.service;

import com.roze.user_service.enums.RoleName;
import com.roze.user_service.persistance.model.RoleEntity;

import java.util.List;

public interface RoleService {

    List<RoleEntity> getRoleEntitiesFormRoleNames(List<RoleName> roleNames);
}
