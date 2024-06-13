package com.roze.appointment_service.dto.response;

import com.roze.appointment_service.enums.RoleName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String phoneNumber;
    private List<RoleName> roleNames;
}
