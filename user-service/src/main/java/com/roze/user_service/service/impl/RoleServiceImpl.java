package com.roze.user_service.service.impl;

import com.roze.user_service.enums.RoleName;
import com.roze.user_service.persistance.RoleRepository;
import com.roze.user_service.persistance.model.RoleEntity;
import com.roze.user_service.service.RoleService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public List<RoleEntity> getRoleEntitiesFormRoleNames(List<RoleName> roleNames) {
        List<RoleEntity> roleEntities = new ArrayList<>();
        for (RoleName roleName : roleNames) {
            RoleEntity roleEntity = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new EntityNotFoundException("Role not found: " + roleName));
            roleEntities.add(roleEntity);
        }

        return roleEntities;
    }
}
