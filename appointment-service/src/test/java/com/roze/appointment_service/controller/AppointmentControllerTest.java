package com.roze.appointment_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roze.appointment_service.dto.request.AppointmentRequest;
import com.roze.appointment_service.dto.response.AppointmentResponse;
import com.roze.appointment_service.service.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AppointmentController.class)
class AppointmentControllerTest {

    public static String URL = "/api/v1/appointments";
    public static String URLWithId = URL + "/{id}";
    public static Long validId = 1L;
    private static Long invalidId = 0L;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private AppointmentService appointmentServiceMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private AppointmentRequest setUpAppointmentRequest() {
        return AppointmentRequest.builder()
                .ownerId(validId)
                .vetId(2L)
                .appointmentDateTime(LocalDateTime
                        .of(2024, 8, 8, 10, 30, 0))
                .reason("Regular checkup")
                .build();
    }

    private AppointmentResponse setUpAppointmentResponse() {
        return AppointmentResponse.builder()
                .appointmentId(validId)
                .ownerName("John")
                .ownerSurname("Doe")
                .vetName("Jin")
                .vetSurname("Din")
                .appointmentDateTime(LocalDateTime
                        .of(2024, 8, 8, 10, 30, 0))
                .reason("Regular checkup")
                .build();
    }

    @Test
    void createNewAppointment_WhenValidRequest_ReturnsUserResponse() throws Exception {
        AppointmentRequest appointmentRequest = setUpAppointmentRequest();
        AppointmentResponse appointmentResponse = setUpAppointmentResponse();

        when(appointmentServiceMock.saveAppointment(appointmentRequest)).thenReturn(appointmentResponse);

        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appointmentRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.appointmentId").value(validId))
                .andExpect(jsonPath("$.ownerName").value("John"))
                .andExpect(jsonPath("$.ownerSurname").value("Doe"))
                .andExpect(jsonPath("$.vetName").value("Jin"))
                .andExpect(jsonPath("$.vetSurname").value("Din"))
                .andExpect(jsonPath("$.appointmentDateTime").value("2024-08-08 10:30:00"))
                .andExpect(jsonPath("$.reason").value("Regular checkup"));

        verify(appointmentServiceMock, times(1)).saveAppointment(appointmentRequest);
    }

    @ParameterizedTest
    @MethodSource("requestBodiesWithNullValues")
    void createAppointment_RequestBodyWithNullValue_DisplaysErrorMessage(AppointmentRequest appointmentRequest) throws Exception {
        String expectedErrorMessage = "must not be null";

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appointmentRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    String actualErrorMessage = result.getResponse().getContentAsString();
                    assertThat(actualErrorMessage).contains(expectedErrorMessage);
                });

        verify(appointmentServiceMock, never()).saveAppointment(appointmentRequest);
    }

    private static Stream<AppointmentRequest> requestBodiesWithNullValues() {
        return Stream.of(
                AppointmentRequest.builder()
                        .ownerId(null)
                        .vetId(2L)
                        .appointmentDateTime(LocalDateTime
                                .of(2024, 8, 8, 10, 30, 0))
                        .reason("Regular checkup")
                        .build(),
                AppointmentRequest.builder()
                        .ownerId(validId)
                        .vetId(null)
                        .appointmentDateTime(LocalDateTime
                                .of(2024, 8, 8, 10, 30, 0))
                        .reason("Regular checkup")
                        .build(),
                AppointmentRequest.builder()
                        .ownerId(validId)
                        .vetId(2L)
                        .appointmentDateTime(null)
                        .reason("Regular checkup")
                        .build()
        );
    }

    @ParameterizedTest
    @MethodSource("requestBodiesWithInvalidId")
    void createAppointment_RequestBodyWithInvalidId_DisplaysErrorMessage(AppointmentRequest appointmentRequest) throws Exception {
        String expectedErrorMessage = "must be greater than or equal to 1";

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appointmentRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    String actualErrorMessage = result.getResponse().getContentAsString();
                    assertThat(actualErrorMessage).contains(expectedErrorMessage);
                });

        verify(appointmentServiceMock, never()).saveAppointment(appointmentRequest);
    }

    private static Stream<AppointmentRequest> requestBodiesWithInvalidId() {
        return Stream.of(
                AppointmentRequest.builder()
                        .ownerId(invalidId)
                        .vetId(validId)
                        .appointmentDateTime(LocalDateTime
                                .of(2024, 8, 8, 10, 30, 0))
                        .reason("Regular checkup")
                        .build(),
                AppointmentRequest.builder()
                        .ownerId(validId)
                        .vetId(invalidId)
                        .appointmentDateTime(LocalDateTime
                                .of(2024, 8, 8, 10, 30, 0))
                        .reason("Regular checkup")
                        .build()
        );
    }

    @ParameterizedTest
    @CsvSource({
            "2024-08-08 10:30:00, 201",
            "2024-08-08T10:30:00, 400",
            "2024-08-08, 400",
            "10:30:00, 400",
            "2024-08-08 10:30, 400",
            "08-08-2024 10:30:00, 400",
            ", 400"
    })
    void createAppointment_WithVariousDateTimeFormats(String dateTime, int expectedStatus) throws Exception {
        String requestBody = "{\"vetId\": 1, \"ownerId\": 1, \"reason\": \"Regular checkup\", \"appointmentDateTime\": \"" + dateTime + "\"}";

        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is(expectedStatus));
    }

    @Test
    void createAppointment_BlankReasonField_DisplaysErrorMessage() throws Exception {
        AppointmentRequest appointmentRequest = setUpAppointmentRequest();
        appointmentRequest.setReason("  ");
        String expectedErrorMessage = "must not be blank";

        mockMvc.perform(put(URLWithId, validId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appointmentRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    String actualErrorMessage = result.getResponse().getContentAsString();
                    assertThat(actualErrorMessage).contains(expectedErrorMessage);
                });

        verify(appointmentServiceMock, never()).saveAppointment(appointmentRequest);
    }

    @Test
    void createAppointment_TooLongMessageInReasonField_DisplaysErrorMessage() throws Exception {
        AppointmentRequest appointmentRequest = setUpAppointmentRequest();
        appointmentRequest.setReason("ToLongReasonToLongReasonToLongReasonToLongReasonToLongReasonToLongReason" +
                "ToLongReasonToLongReasonToLongReasonToLongReasonToLongReasonToLongReasonToLongReason" +
                "ToLongReasonToLongReasonToLongReasonToLongReasonToLongReasonToLongReasonToLongReason" +
                "ToLongReasonToLongReasonToLongReasonToLongReasonToLongReasonToLongReasonToLongReason");
        int length = appointmentRequest.getReason().length();
        System.out.println(length);
        String expectedErrorMessage = "length must be between 0 and 300";

        mockMvc.perform(put(URLWithId, validId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appointmentRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    String actualErrorMessage = result.getResponse().getContentAsString();
                    assertThat(actualErrorMessage).contains(expectedErrorMessage);
                });

        verify(appointmentServiceMock, never()).saveAppointment(appointmentRequest);
    }

    @Test
    void getAppointmentById_WhenValidId_ReturnsUserResponse() throws Exception {
        AppointmentResponse appointmentResponse = setUpAppointmentResponse();

        when(appointmentServiceMock.findAppointmentById(validId)).thenReturn(appointmentResponse);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(URLWithId, validId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.appointmentId").value(validId))
                .andExpect(jsonPath("$.ownerName").value("John"))
                .andExpect(jsonPath("$.ownerSurname").value("Doe"))
                .andExpect(jsonPath("$.vetName").value("Jin"))
                .andExpect(jsonPath("$.vetSurname").value("Din"))
                .andExpect(jsonPath("$.appointmentDateTime").value("2024-08-08 10:30:00"))
                .andExpect(jsonPath("$.reason").value("Regular checkup"));

        verify(appointmentServiceMock, times(1)).findAppointmentById(validId);
    }

    @Test
    void getAppointmentById_WhenInvalidId_ReturnsNotFound() throws Exception {
        String expectedErrorMessage = "must be greater than or equal to 1";

        mockMvc.perform(MockMvcRequestBuilders
                        .get(URLWithId, invalidId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    String actualErrorMessage = result.getResponse().getContentAsString();
                    assertThat(actualErrorMessage).contains(expectedErrorMessage);
                });

        verify(appointmentServiceMock, never()).findAppointmentById(invalidId);
    }

    @Test
    public void testUpdateAppointmentById_WhenValidRequest_ReturnsUserResponse() throws Exception {
        AppointmentRequest request = setUpAppointmentRequest();
        AppointmentResponse response = setUpAppointmentResponse();

        when(appointmentServiceMock.updateAppointmentById(validId, request)).thenReturn(response);

        mockMvc.perform(put(URLWithId, validId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.appointmentId").value(validId))
                .andExpect(jsonPath("$.ownerName").value("John"))
                .andExpect(jsonPath("$.ownerSurname").value("Doe"))
                .andExpect(jsonPath("$.vetName").value("Jin"))
                .andExpect(jsonPath("$.vetSurname").value("Din"))
                .andExpect(jsonPath("$.appointmentDateTime").value("2024-08-08 10:30:00"))
                .andExpect(jsonPath("$.reason").value("Regular checkup"));
    }

    @Test
    public void testDeleteAppointmentById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete(URLWithId, validId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}