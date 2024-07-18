package com.roze.auth_service.dto.request;


import com.roze.auth_service.enums.RoleName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    @NotBlank
    @Size(min = 2, max = 30)
    private String name;

    @NotBlank
    @Size(min = 2, max = 30)
    private String surname;

    @NotBlank
    @Email(message = "Invalid email format. Please provide a valid email address.")
    private String email;

    @NotBlank
    @Size(min = 8, max = 30)
    @Pattern(regexp = "^(?!.*\\s)(?=.*[A-Z])(?=.*\\d)(?=.*[a-z])(?=.*[!@#$%^&*()]).+$",
            message = "Invalid password format. Please provide a valid password.")
    private String password;

    @NotBlank
    @Size(min = 2, max = 20)
    @Pattern(regexp = "^\\+\\d+$", message = "Invalid phone format. Please provide a valid phone number.")
    private String phoneNumber;

    private List<RoleName> roleNames;
}
