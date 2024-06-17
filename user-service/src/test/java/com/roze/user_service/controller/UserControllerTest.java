package com.roze.user_service.controller;

import com.roze.user_service.dto.response.UserResponse;
import com.roze.user_service.enums.RoleName;
import com.roze.user_service.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    public static String URLWithId = "/api/v1/users/{id}";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userServiceMock;

    private UserResponse setUpUserResponse() {
        return UserResponse.builder()
                .id(1L)
                .name("John")
                .surname("Doe")
                .email("john@doe.com")
                .phoneNumber("1234567890")
                .roleNames(List.of(RoleName.VET))
                .build();
    }

    @Test
    void getUserById_WhenValidRequest_ReturnsUserResponse() throws Exception {
        Long validId = 1L;
        UserResponse userResponse = setUpUserResponse();

        when(userServiceMock.findUserById(validId)).thenReturn(userResponse);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(URLWithId, validId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(validId))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.surname").value("Doe"))
                .andExpect(jsonPath("$.email").value("john@doe.com"))
                .andExpect(jsonPath("$.phoneNumber").value("1234567890"))
                .andExpect(jsonPath("$.roleNames[0]").value("VET"));

        verify(userServiceMock, times(1)).findUserById(validId);
    }

    @Test
    void findUserById_WhenNotFound_ReturnsNotFound() throws Exception {
        Long nonExistentId = 999L;
        String exceptionMessage = "User not found by id: ";

        when(userServiceMock.findUserById(nonExistentId)).thenThrow(new EntityNotFoundException(exceptionMessage));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(URLWithId, nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof EntityNotFoundException))
                .andExpect(result -> {
                    String actualErrorMessage = result.getResponse().getContentAsString();
                    assertThat(actualErrorMessage).contains(exceptionMessage);
                });

        verify(userServiceMock, times(1)).findUserById(nonExistentId);
    }
}