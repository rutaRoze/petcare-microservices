package com.roze.auth_service.dto.response;

import com.roze.auth_service.enums.RoleName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String phoneNumber;
    private List<RoleName> roleNames;
}
