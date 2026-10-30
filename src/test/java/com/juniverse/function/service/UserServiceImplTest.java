package com.juniverse.function.service;

import com.juniverse.function.dto.UserRequestDTO;
import com.juniverse.function.dto.UserResponseDTO;
import com.juniverse.function.entity.User;
import com.juniverse.function.exception.ServiceException;
import com.juniverse.function.exception.NotFoundException;
import com.juniverse.function.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * The Class UserServiceImplTest.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl Tests")
class UserServiceImplTest {

    /**
     * The user repository.
     */
    @Mock
    private UserRepository userRepository;

    /**
     * The user service.
     */
    @InjectMocks
    private UserServiceImpl userService;

    /**
     * The user request dto.
     */
    private UserRequestDTO userRequestDto;

    /**
     * The user.
     */
    private User user;

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

        user = new User();
        user.setId(1L);
        user.setName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@email.com");
        user.setPhone("+1234567890");
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * Creates the user success.
     */
    @Test
    @DisplayName("Create user - Success")
    void createUser_Success() {
        // Given
        when(userRepository.existsByEmail(userRequestDto.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        UserResponseDTO result = userService.createUser(userRequestDto);

        // Then
        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
        verify(userRepository, times(1)).existsByEmail(userRequestDto.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    /**
     * Creates the user email exists.
     */
    @Test
    @DisplayName("Create user - Email already exists")
    void createUser_EmailExists() {
        // Given
        when(userRepository.existsByEmail(userRequestDto.getEmail())).thenReturn(true);

        // When & Then
        ServiceException exception = assertThrows(ServiceException.class,
                () -> userService.createUser(userRequestDto));
        assertEquals("User already exists with email: john.doe@email.com", exception.getMessage());
        verify(userRepository, times(1)).existsByEmail(userRequestDto.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Gets the user by id success.
     *
     * @return the user by id success
     */
    @Test
    @DisplayName("Get user by ID - Success")
    void getUserById_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        UserResponseDTO result = userService.getUserById(1L);

        // Then
        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
        verify(userRepository, times(1)).findById(1L);
    }

    /**
     * Gets the user by id not found.
     *
     * @return the user by id not found
     */
    @Test
    @DisplayName("Get user by ID - Not found")
    void getUserById_NotFound() {
        // Given
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.getUserById(99L));
        assertEquals("User not found with ID: 99", exception.getMessage());
        verify(userRepository, times(1)).findById(99L);
    }

    /**
     * Gets the all users success.
     *
     * @return the all users success
     */
    @Test
    @DisplayName("Get all users with pagination - Success")
    void getAllUsers_Success() {
        // Given
        List<User> users = Arrays.asList(user);
        Page<User> page = new PageImpl<>(users, PageRequest.of(0, 10), 1);
        when(userRepository.findAll(any(Pageable.class))).thenReturn(page);

        // When
        Page<UserResponseDTO> result = userService.getAllUsers(PageRequest.of(0, 10));

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(user.getId(), result.getContent().get(0).getId());
        verify(userRepository, times(1)).findAll(any(Pageable.class));
    }

    /**
     * Gets the active users success.
     *
     * @return the active users success
     */
    @Test
    @DisplayName("Get active users with pagination - Success")
    void getActiveUsers_Success() {
        // Given
        List<User> users = Arrays.asList(user);
        Page<User> page = new PageImpl<>(users, PageRequest.of(0, 10), 1);
        when(userRepository.findByActiveTrue(any(Pageable.class))).thenReturn(page);

        // When
        Page<UserResponseDTO> result = userService.getActiveUsers(PageRequest.of(0, 10));

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertTrue(result.getContent().get(0).getActive());
        verify(userRepository, times(1)).findByActiveTrue(any(Pageable.class));
    }

    /**
     * Search users success.
     */
    @Test
    @DisplayName("Search users - Success")
    void searchUsers_Success() {
        // Given
        List<User> users = Arrays.asList(user);
        Page<User> page = new PageImpl<>(users, PageRequest.of(0, 10), 1);
        when(userRepository.findByFilterAndActive("John", true, PageRequest.of(0, 10)))
                .thenReturn(page);

        // When
        Page<UserResponseDTO> result = userService.searchUsers("John", true, PageRequest.of(0, 10));

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(userRepository, times(1)).findByFilterAndActive("John", true, PageRequest.of(0, 10));
    }

    /**
     * Update user success.
     */
    @Test
    @DisplayName("Update user - Success")
    void updateUser_Success() {
        // Given
        UserRequestDTO updateDto = new UserRequestDTO();
        updateDto.setName("Jane");
        updateDto.setLastName("Smith");
        updateDto.setEmail("jane.smith@email.com");
        updateDto.setPhone("+9876543210");
        updateDto.setActive(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndIdNot("jane.smith@email.com", 1L)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        UserResponseDTO result = userService.updateUser(1L, updateDto);

        // Then
        assertNotNull(result);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).existsByEmailAndIdNot("jane.smith@email.com", 1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    /**
     * Update user not found.
     */
    @Test
    @DisplayName("Update user - Not found")
    void updateUser_NotFound() {
        // Given
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.updateUser(99L, userRequestDto));
        assertEquals("User not found with ID: 99", exception.getMessage());
        verify(userRepository, times(1)).findById(99L);
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Update user email exists.
     */
    @Test
    @DisplayName("Update user - Email already exists")
    void updateUser_EmailExists() {
        // Given
        UserRequestDTO updateDto = new UserRequestDTO();
        updateDto.setName("Jane");
        updateDto.setLastName("Smith");
        updateDto.setEmail("existing@email.com");
        updateDto.setPhone("+9876543210");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndIdNot("existing@email.com", 1L)).thenReturn(true);

        // When & Then
        ServiceException exception = assertThrows(ServiceException.class,
                () -> userService.updateUser(1L, updateDto));
        assertEquals("Another user already exists with email: existing@email.com", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).existsByEmailAndIdNot("existing@email.com", 1L);
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Delete user success.
     */
    @Test
    @DisplayName("Delete user - Success")
    void deleteUser_Success() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        // When
        userService.deleteUser(1L);

        // Then
        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    /**
     * Delete user not found.
     */
    @Test
    @DisplayName("Delete user - Not found")
    void deleteUser_NotFound() {
        // Given
        when(userRepository.existsById(99L)).thenReturn(false);

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.deleteUser(99L));
        assertEquals("User not found with ID: 99", exception.getMessage());
        verify(userRepository, times(1)).existsById(99L);
        verify(userRepository, never()).deleteById(any());
    }

    /**
     * Deactivate user success.
     */
    @Test
    @DisplayName("Deactivate user - Success")
    void deactivateUser_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        userService.deactivateUser(1L);

        // Then
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(user);
        assertFalse(user.getActive());
    }

    /**
     * Deactivate user not found.
     */
    @Test
    @DisplayName("Deactivate user - Not found")
    void deactivateUser_NotFound() {
        // Given
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.deactivateUser(99L));
        assertEquals("User not found with ID: 99", exception.getMessage());
        verify(userRepository, times(1)).findById(99L);
        verify(userRepository, never()).save(any());
    }

    /**
     * Activate user success.
     */
    @Test
    @DisplayName("Activate user - Success")
    void activateUser_Success() {
        // Given
        user.setActive(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        userService.activateUser(1L);

        // Then
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(user);
        assertTrue(user.getActive());
    }

    /**
     * Activate user not found.
     */
    @Test
    @DisplayName("Activate user - Not found")
    void activateUser_NotFound() {
        // Given
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.activateUser(99L));
        assertEquals("User not found with ID: 99", exception.getMessage());
        verify(userRepository, times(1)).findById(99L);
        verify(userRepository, never()).save(any());
    }

    /**
     * Gets the all active users success.
     *
     * @return the all active users success
     */
    @Test
    @DisplayName("Get all active users - Success")
    void getAllActiveUsers_Success() {
        // Given
        List<User> users = Arrays.asList(user);
        when(userRepository.findByActiveTrue()).thenReturn(users);

        // When
        List<UserResponseDTO> result = userService.getAllActiveUsers();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getActive());
        assertEquals(user.getId(), result.get(0).getId());
        verify(userRepository, times(1)).findByActiveTrue();
    }

    /**
     * Count active users success.
     */
    @Test
    @DisplayName("Count active users - Success")
    void countActiveUsers_Success() {
        // Given
        when(userRepository.countByActiveTrue()).thenReturn(5L);

        // When
        Long result = userService.countActiveUsers();

        // Then
        assertEquals(5L, result);
        verify(userRepository, times(1)).countByActiveTrue();
    }
}
