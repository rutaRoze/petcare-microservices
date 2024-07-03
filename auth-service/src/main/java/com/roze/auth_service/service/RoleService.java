package com.roze.auth_service.service;

import com.roze.auth_service.enums.RoleName;
import com.roze.auth_service.persistance.RoleRepository;
import com.roze.auth_service.persistance.model.RoleEntity;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoleService {

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
