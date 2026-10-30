package com.juniverse.function.lambda;

import com.juniverse.function.dto.LambdaRequestDTO;
import com.juniverse.function.dto.LambdaResponseDTO;
import com.juniverse.function.dto.UserRequestDTO;
import com.juniverse.function.dto.UserResponseDTO;
import com.juniverse.function.exception.NotFoundException;
import com.juniverse.function.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * The Class UserLambdaHandlerTest.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserLambdaHandler Tests")
class UserLambdaHandlerTest {

    /**
     * The user service.
     */
    @Mock
    private UserService userService;

    /**
     * The user lambda handler.
     */
    @InjectMocks
    private UserLambdaHandler userLambdaHandler;

    /**
     * The user request dto.
     */
    private UserRequestDTO userRequestDto;

    /**
     * The user response dto.
     */
    private UserResponseDTO userResponseDto;

    /**
     * Sets the up.
     */
    @BeforeEach
    void setUp() {
        userRequestDto = new UserRequestDTO();
        userRequestDto.setName("John");
        userRequestDto.setLastName("Doe");
        userRequestDto.setEmail("john.doe@email.com");
        userRequestDto.setPhone("+1234567890");
        userRequestDto.setActive(true);

        userResponseDto = new UserResponseDTO();
        userResponseDto.setId(1L);
        userResponseDto.setName("John");
        userResponseDto.setLastName("Doe");
        userResponseDto.setEmail("john.doe@email.com");
        userResponseDto.setPhone("+1234567890");
        userResponseDto.setActive(true);
        userResponseDto.setCreatedAt(LocalDateTime.now());
        userResponseDto.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * Handle create success.
     */
    @Test
    @DisplayName("Lambda CREATE - create user successfully")
    void handleCreate_Success() {
        // Given
        LambdaRequestDTO request = LambdaRequestDTO.create(userRequestDto);
        when(userService.createUser(any(UserRequestDTO.class))).thenReturn(userResponseDto);

        // When
        LambdaResponseDTO response = userLambdaHandler.apply(request);

        // Then
        assertTrue(response.isSuccess());
        assertEquals("User created successfully", response.getMessage());
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getData());
        assertNotNull(response.getTimestamp());
        verify(userService, times(1)).createUser(any(UserRequestDTO.class));
    }

    /**
     * Handle create no data.
     */
    @Test
    @DisplayName("Lambda CREATE - without user data")
    void handleCreate_NoData() {
        // Given
        LambdaRequestDTO request = new LambdaRequestDTO("CREATE", null, null);

        // When
        LambdaResponseDTO response = userLambdaHandler.apply(request);

        // Then
        assertFalse(response.isSuccess());
        assertEquals("User data required", response.getMessage());
        assertEquals(400, response.getStatusCode());
        assertNull(response.getData());
        verify(userService, never()).createUser(any(UserRequestDTO.class));
    }

    /**
     * Handle get by id success.
     */
    @Test
    @DisplayName("Lambda GET_BY_ID - get user successfully")
    void handleGetById_Success() {
        // Given
        LambdaRequestDTO request = LambdaRequestDTO.getById(1L);
        when(userService.getUserById(1L)).thenReturn(userResponseDto);

        // When
        LambdaResponseDTO response = userLambdaHandler.apply(request);

        // Then
        assertTrue(response.isSuccess());
        assertEquals("User retrieved successfully", response.getMessage());
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getData());
        verify(userService, times(1)).getUserById(1L);
    }

    /**
     * Handle get by id no id.
     */
    @Test
    @DisplayName("Lambda GET_BY_ID - without ID")
    void handleGetById_NoId() {
        // Given
        LambdaRequestDTO request = new LambdaRequestDTO("GET_BY_ID", null, null);

        // When
        LambdaResponseDTO response = userLambdaHandler.apply(request);

        // Then
        assertFalse(response.isSuccess());
        assertEquals("User ID required", response.getMessage());
        assertEquals(400, response.getStatusCode());
        assertNull(response.getData());
        verify(userService, never()).getUserById(any());
    }

    /**
     * Handle get all active success.
     */
    @Test
    @DisplayName("Lambda GET_ALL_ACTIVE - get active users")
    void handleGetAllActive_Success() {
        // Given
        LambdaRequestDTO request = LambdaRequestDTO.getAllActive();
        List<UserResponseDTO> users = Arrays.asList(userResponseDto);
        when(userService.getAllActiveUsers()).thenReturn(users);

        // When
        LambdaResponseDTO response = userLambdaHandler.apply(request);

        // Then
        assertTrue(response.isSuccess());
        assertEquals("Active users retrieved successfully", response.getMessage());
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getData());
        verify(userService, times(1)).getAllActiveUsers();
    }

    /**
     * Handle update success.
     */
    @Test
    @DisplayName("Lambda UPDATE - update user successfully")
    void handleUpdate_Success() {
        // Given
        LambdaRequestDTO request = LambdaRequestDTO.update(1L, userRequestDto);
        when(userService.updateUser(eq(1L), any(UserRequestDTO.class))).thenReturn(userResponseDto);

        // When
        LambdaResponseDTO response = userLambdaHandler.apply(request);

        // Then
        assertTrue(response.isSuccess());
        assertEquals("User updated successfully", response.getMessage());
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getData());
        verify(userService, times(1)).updateUser(eq(1L), any(UserRequestDTO.class));
    }

    /**
     * Handle update no id.
     */
    @Test
    @DisplayName("Lambda UPDATE - without ID")
    void handleUpdate_NoId() {
        // Given
        LambdaRequestDTO request = new LambdaRequestDTO("UPDATE", null, userRequestDto);

        // When
        LambdaResponseDTO response = userLambdaHandler.apply(request);

        // Then
        assertFalse(response.isSuccess());
        assertEquals("User ID and data required", response.getMessage());
        assertEquals(400, response.getStatusCode());
        assertNull(response.getData());
        verify(userService, never()).updateUser(any(), any());
    }

    /**
     * Handle update no data.
     */
    @Test
    @DisplayName("Lambda UPDATE - without data")
    void handleUpdate_NoData() {
        // Given
        LambdaRequestDTO request = new LambdaRequestDTO("UPDATE", 1L, null);

        // When
        LambdaResponseDTO response = userLambdaHandler.apply(request);

        // Then
        assertFalse(response.isSuccess());
        assertEquals("User ID and data required", response.getMessage());
        assertEquals(400, response.getStatusCode());
        assertNull(response.getData());
        verify(userService, never()).updateUser(any(), any());
    }

    /**
     * Handle delete success.
     */
    @Test
    @DisplayName("Lambda DELETE - delete user successfully")
    void handleDelete_Success() {
        // Given
        LambdaRequestDTO request = LambdaRequestDTO.delete(1L);
        doNothing().when(userService).deleteUser(1L);

        // When
        LambdaResponseDTO response = userLambdaHandler.apply(request);

        // Then
        assertTrue(response.isSuccess());
        assertEquals("User deleted successfully", response.getMessage());
        assertEquals(200, response.getStatusCode());
        assertNull(response.getData());
        verify(userService, times(1)).deleteUser(1L);
    }

    /**
     * Handle delete no id.
     */
    @Test
    @DisplayName("Lambda DELETE - without ID")
    void handleDelete_NoId() {
        // Given
        LambdaRequestDTO request = new LambdaRequestDTO("DELETE", null, null);

        // When
        LambdaResponseDTO response = userLambdaHandler.apply(request);

        // Then
        assertFalse(response.isSuccess());
        assertEquals("User ID required", response.getMessage());
        assertEquals(400, response.getStatusCode());
        assertNull(response.getData());
        verify(userService, never()).deleteUser(any());
    }

    /**
     * Handle activate success.
     */
    @Test
    @DisplayName("Lambda ACTIVATE - activate user successfully")
    void handleActivate_Success() {
        // Given
        LambdaRequestDTO request = LambdaRequestDTO.activate(1L);
        doNothing().when(userService).activateUser(1L);

        // When
        LambdaResponseDTO response = userLambdaHandler.apply(request);

        // Then
        assertTrue(response.isSuccess());
        assertEquals("User activated successfully", response.getMessage());
        assertEquals(200, response.getStatusCode());
        assertNull(response.getData());
        verify(userService, times(1)).activateUser(1L);
    }

    /**
     * Handle activate no id.
     */
    @Test
    @DisplayName("Lambda ACTIVATE - without ID")
    void handleActivate_NoId() {
        // Given
        LambdaRequestDTO request = new LambdaRequestDTO("ACTIVATE", null, null);

        // When
        LambdaResponseDTO response = userLambdaHandler.apply(request);

        // Then
        assertFalse(response.isSuccess());
        assertEquals("User ID required", response.getMessage());
        assertEquals(400, response.getStatusCode());
        assertNull(response.getData());
        verify(userService, never()).activateUser(any());
    }

    /**
     * Handle deactivate success.
     */
    @Test
    @DisplayName("Lambda DEACTIVATE - deactivate user successfully")
    void handleDeactivate_Success() {
        // Given
        LambdaRequestDTO request = LambdaRequestDTO.deactivate(1L);
        doNothing().when(userService).deactivateUser(1L);

        // When
        LambdaResponseDTO response = userLambdaHandler.apply(request);

        // Then
        assertTrue(response.isSuccess());
        assertEquals("User deactivated successfully", response.getMessage());
        assertEquals(200, response.getStatusCode());
        assertNull(response.getData());
        verify(userService, times(1)).deactivateUser(1L);
    }

    /**
     * Handle deactivate no id.
     */
    @Test
    @DisplayName("Lambda DEACTIVATE - without ID")
    void handleDeactivate_NoId() {
        // Given
        LambdaRequestDTO request = new LambdaRequestDTO("DEACTIVATE", null, null);

        // When
        LambdaResponseDTO response = userLambdaHandler.apply(request);

        // Then
        assertFalse(response.isSuccess());
        assertEquals("User ID required", response.getMessage());
        assertEquals(400, response.getStatusCode());
        assertNull(response.getData());
        verify(userService, never()).deactivateUser(any());
    }

    /**
     * Handle count active success.
     */
    @Test
    @DisplayName("Lambda COUNT_ACTIVE - count active users")
    void handleCountActive_Success() {
        // Given
        LambdaRequestDTO request = LambdaRequestDTO.countActive();
        when(userService.countActiveUsers()).thenReturn(5L);

        // When
        LambdaResponseDTO response = userLambdaHandler.apply(request);

        // Then
        assertTrue(response.isSuccess());
        assertEquals("Count retrieved successfully", response.getMessage());
        assertEquals(200, response.getStatusCode());
        assertEquals(5L, response.getData());
        verify(userService, times(1)).countActiveUsers();
    }

    /**
     * Handle unsupported operation.
     */
    @Test
    @DisplayName("Lambda - unsupported operation")
    void handleUnsupportedOperation() {
        // Given
        LambdaRequestDTO request = new LambdaRequestDTO("INVALID_OPERATION", null, null);

        // When
        LambdaResponseDTO response = userLambdaHandler.apply(request);

        // Then
        assertFalse(response.isSuccess());
        assertEquals("Unsupported operation: INVALID_OPERATION", response.getMessage());
        assertEquals(400, response.getStatusCode());
        assertNull(response.getData());
        verifyNoInteractions(userService);
    }

    /**
     * Handle exception during processing.
     */
    @Test
    @DisplayName("Lambda - exception handling during processing")
    void handleExceptionDuringProcessing() {
        // Given
        LambdaRequestDTO request = LambdaRequestDTO.getById(1L);
        when(userService.getUserById(1L))
                .thenThrow(new NotFoundException("User not found"));

        // When
        LambdaResponseDTO response = userLambdaHandler.apply(request);

        // Then
        assertFalse(response.isSuccess());
        assertEquals("User not found", response.getMessage());
        assertEquals(404, response.getStatusCode());
        assertNull(response.getData());
        verify(userService, times(1)).getUserById(1L);
    }

    /**
     * Una excepción inesperada devuelve 500 sin exponer su mensaje.
     */
    @Test
    @DisplayName("Lambda - unexpected exception does not leak details")
    void unexpectedExceptionReturns500WithoutDetails() {
        // Given
        LambdaRequestDTO request = LambdaRequestDTO.getById(1L);
        when(userService.getUserById(1L)).thenThrow(new IllegalStateException("jdbc:mysql://secret-host"));

        // When
        LambdaResponseDTO response = userLambdaHandler.apply(request);

        // Then
        assertEquals(500, response.getStatusCode());
        assertEquals("Internal error", response.getMessage());
    }

    /**
     * Una operación nula es una petición incorrecta (400).
     */
    @Test
    @DisplayName("Lambda - null operation returns 400")
    void nullOperationReturns400() {
        LambdaResponseDTO response = userLambdaHandler.apply(new LambdaRequestDTO());

        assertEquals(400, response.getStatusCode());
    }

    /**
     * Verify lambda request static methods.
     */
    @Test
    @DisplayName("Verify static methods of LambdaRequest")
    void verifyLambdaRequestStaticMethods() {
        // Test create
        LambdaRequestDTO createRequest = LambdaRequestDTO.create(userRequestDto);
        assertEquals("CREATE", createRequest.getOperation());
        assertNull(createRequest.getUserId());
        assertEquals(userRequestDto, createRequest.getUserData());

        // Test getById
        LambdaRequestDTO getByIdRequest = LambdaRequestDTO.getById(1L);
        assertEquals("GET_BY_ID", getByIdRequest.getOperation());
        assertEquals(1L, getByIdRequest.getUserId());
        assertNull(getByIdRequest.getUserData());

        // Test getAllActive
        LambdaRequestDTO getAllActiveRequest = LambdaRequestDTO.getAllActive();
        assertEquals("GET_ALL_ACTIVE", getAllActiveRequest.getOperation());
        assertNull(getAllActiveRequest.getUserId());
        assertNull(getAllActiveRequest.getUserData());

        // Test update
        LambdaRequestDTO updateRequest = LambdaRequestDTO.update(1L, userRequestDto);
        assertEquals("UPDATE", updateRequest.getOperation());
        assertEquals(1L, updateRequest.getUserId());
        assertEquals(userRequestDto, updateRequest.getUserData());

        // Test delete
        LambdaRequestDTO deleteRequest = LambdaRequestDTO.delete(1L);
        assertEquals("DELETE", deleteRequest.getOperation());
        assertEquals(1L, deleteRequest.getUserId());
        assertNull(deleteRequest.getUserData());

        // Test activate
        LambdaRequestDTO activateRequest = LambdaRequestDTO.activate(1L);
        assertEquals("ACTIVATE", activateRequest.getOperation());
        assertEquals(1L, activateRequest.getUserId());
        assertNull(activateRequest.getUserData());

        // Test deactivate
        LambdaRequestDTO deactivateRequest = LambdaRequestDTO.deactivate(1L);
        assertEquals("DEACTIVATE", deactivateRequest.getOperation());
        assertEquals(1L, deactivateRequest.getUserId());
        assertNull(deactivateRequest.getUserData());

        // Test countActive
        LambdaRequestDTO countActiveRequest = LambdaRequestDTO.countActive();
        assertEquals("COUNT_ACTIVE", countActiveRequest.getOperation());
        assertNull(countActiveRequest.getUserId());
        assertNull(countActiveRequest.getUserData());
    }

    /**
     * Verify lambda response DTO static methods.
     */
    @Test
    @DisplayName("Verify static methods of LambdaResponseDTO")
    void verifyLambdaResponseDTOStaticMethods() {
        // Test success
        LambdaResponseDTO successResponse = LambdaResponseDTO.success("Test success", userResponseDto);
        assertTrue(successResponse.isSuccess());
        assertEquals("Test success", successResponse.getMessage());
        assertEquals(userResponseDto, successResponse.getData());
        assertEquals(200, successResponse.getStatusCode());
        assertNotNull(successResponse.getTimestamp());

        // Test error
        LambdaResponseDTO errorResponse = LambdaResponseDTO.error("Test error", 404);
        assertFalse(errorResponse.isSuccess());
        assertEquals("Test error", errorResponse.getMessage());
        assertNull(errorResponse.getData());
        assertEquals(404, errorResponse.getStatusCode());
        assertNotNull(errorResponse.getTimestamp());
    }
}
