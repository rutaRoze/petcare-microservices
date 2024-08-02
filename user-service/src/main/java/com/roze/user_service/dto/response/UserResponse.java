package com.roze.user_service.dto.response;

import com.roze.user_service.enums.RoleName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse implements Serializable {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String phoneNumber;
    private List<RoleName> roleNames;
}
