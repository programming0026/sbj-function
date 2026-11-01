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

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl Tests")
class UserServiceImplTest {

    /**
     * Repositorio de usuario.
     */
    @Mock
    private UserRepository userRepository;

    /**
     * Servicio de usuario.
     */
    @InjectMocks
    private UserServiceImpl userService;

    /**
     * DTO de petición de usuario.
     */
    private UserRequestDTO userRequestDto;

    /**
     * Usuario.
     */
    private User user;

    /**
     * Prepara los datos de la prueba.
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
     * Crea éxito de usuario.
     */
    @Test
    @DisplayName("Create user - Success")
    void createUser_Success() {
        // Dado
        when(userRepository.existsByEmail(userRequestDto.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Cuando
        UserResponseDTO result = userService.createUser(userRequestDto);

        // Entonces
        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
        verify(userRepository, times(1)).existsByEmail(userRequestDto.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    /**
     * Crea un usuario cuyo correo ya existe.
     */
    @Test
    @DisplayName("Create user - Email already exists")
    void createUser_EmailExists() {
        // Dado
        when(userRepository.existsByEmail(userRequestDto.getEmail())).thenReturn(true);

        // Cuando y entonces
        ServiceException exception = assertThrows(ServiceException.class,
                () -> userService.createUser(userRequestDto));
        assertEquals("User already exists with email: john.doe@email.com", exception.getMessage());
        verify(userRepository, times(1)).existsByEmail(userRequestDto.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Obtiene usuario por éxito de identificador.
     *
     * @return usuario por éxito de identificador
     */
    @Test
    @DisplayName("Get user by ID - Success")
    void getUserById_Success() {
        // Dado
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Cuando
        UserResponseDTO result = userService.getUserById(1L);

        // Entonces
        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
        verify(userRepository, times(1)).findById(1L);
    }

    /**
     * Consulta de un usuario inexistente por identificador.
     *
     * @return usuario no encontrado por identificador
     */
    @Test
    @DisplayName("Get user by ID - Not found")
    void getUserById_NotFound() {
        // Dado
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Cuando y entonces
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.getUserById(99L));
        assertEquals("User not found with ID: 99", exception.getMessage());
        verify(userRepository, times(1)).findById(99L);
    }

    /**
     * Obtiene éxito de usuarios todos.
     *
     * @return éxito de usuarios todos
     */
    @Test
    @DisplayName("Get all users with pagination - Success")
    void getAllUsers_Success() {
        // Dado
        List<User> users = Arrays.asList(user);
        Page<User> page = new PageImpl<>(users, PageRequest.of(0, 10), 1);
        when(userRepository.findAll(any(Pageable.class))).thenReturn(page);

        // Cuando
        Page<UserResponseDTO> result = userService.getAllUsers(PageRequest.of(0, 10));

        // Entonces
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(user.getId(), result.getContent().get(0).getId());
        verify(userRepository, times(1)).findAll(any(Pageable.class));
    }

    /**
     * Obtiene éxito de usuarios activo.
     *
     * @return éxito de usuarios activo
     */
    @Test
    @DisplayName("Get active users with pagination - Success")
    void getActiveUsers_Success() {
        // Dado
        List<User> users = Arrays.asList(user);
        Page<User> page = new PageImpl<>(users, PageRequest.of(0, 10), 1);
        when(userRepository.findByActiveTrue(any(Pageable.class))).thenReturn(page);

        // Cuando
        Page<UserResponseDTO> result = userService.getActiveUsers(PageRequest.of(0, 10));

        // Entonces
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertTrue(result.getContent().get(0).getActive());
        verify(userRepository, times(1)).findByActiveTrue(any(Pageable.class));
    }

    /**
     * Busca éxito de usuarios.
     */
    @Test
    @DisplayName("Search users - Success")
    void searchUsers_Success() {
        // Dado
        List<User> users = Arrays.asList(user);
        Page<User> page = new PageImpl<>(users, PageRequest.of(0, 10), 1);
        when(userRepository.findByFilterAndActive("John", true, PageRequest.of(0, 10)))
                .thenReturn(page);

        // Cuando
        Page<UserResponseDTO> result = userService.searchUsers("John", true, PageRequest.of(0, 10));

        // Entonces
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(userRepository, times(1)).findByFilterAndActive("John", true, PageRequest.of(0, 10));
    }

    /**
     * Actualiza éxito de usuario.
     */
    @Test
    @DisplayName("Update user - Success")
    void updateUser_Success() {
        // Dado
        UserRequestDTO updateDto = new UserRequestDTO();
        updateDto.setName("Jane");
        updateDto.setLastName("Smith");
        updateDto.setEmail("jane.smith@email.com");
        updateDto.setPhone("+9876543210");
        updateDto.setActive(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndIdNot("jane.smith@email.com", 1L)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Cuando
        UserResponseDTO result = userService.updateUser(1L, updateDto);

        // Entonces
        assertNotNull(result);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).existsByEmailAndIdNot("jane.smith@email.com", 1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    /**
     * Actualizar un usuario inexistente.
     */
    @Test
    @DisplayName("Update user - Not found")
    void updateUser_NotFound() {
        // Dado
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Cuando y entonces
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.updateUser(99L, userRequestDto));
        assertEquals("User not found with ID: 99", exception.getMessage());
        verify(userRepository, times(1)).findById(99L);
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Actualizar con un correo que ya existe.
     */
    @Test
    @DisplayName("Update user - Email already exists")
    void updateUser_EmailExists() {
        // Dado
        UserRequestDTO updateDto = new UserRequestDTO();
        updateDto.setName("Jane");
        updateDto.setLastName("Smith");
        updateDto.setEmail("existing@email.com");
        updateDto.setPhone("+9876543210");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndIdNot("existing@email.com", 1L)).thenReturn(true);

        // Cuando y entonces
        ServiceException exception = assertThrows(ServiceException.class,
                () -> userService.updateUser(1L, updateDto));
        assertEquals("Another user already exists with email: existing@email.com", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).existsByEmailAndIdNot("existing@email.com", 1L);
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Elimina éxito de usuario.
     */
    @Test
    @DisplayName("Delete user - Success")
    void deleteUser_Success() {
        // Dado
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        // Cuando
        userService.deleteUser(1L);

        // Entonces
        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    /**
     * Eliminar un usuario inexistente.
     */
    @Test
    @DisplayName("Delete user - Not found")
    void deleteUser_NotFound() {
        // Dado
        when(userRepository.existsById(99L)).thenReturn(false);

        // Cuando y entonces
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.deleteUser(99L));
        assertEquals("User not found with ID: 99", exception.getMessage());
        verify(userRepository, times(1)).existsById(99L);
        verify(userRepository, never()).deleteById(any());
    }

    /**
     * Desactiva éxito de usuario.
     */
    @Test
    @DisplayName("Deactivate user - Success")
    void deactivateUser_Success() {
        // Dado
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Cuando
        userService.deactivateUser(1L);

        // Entonces
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(user);
        assertFalse(user.getActive());
    }

    /**
     * Desactivar un usuario inexistente.
     */
    @Test
    @DisplayName("Deactivate user - Not found")
    void deactivateUser_NotFound() {
        // Dado
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Cuando y entonces
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.deactivateUser(99L));
        assertEquals("User not found with ID: 99", exception.getMessage());
        verify(userRepository, times(1)).findById(99L);
        verify(userRepository, never()).save(any());
    }

    /**
     * Activa éxito de usuario.
     */
    @Test
    @DisplayName("Activate user - Success")
    void activateUser_Success() {
        // Dado
        user.setActive(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Cuando
        userService.activateUser(1L);

        // Entonces
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(user);
        assertTrue(user.getActive());
    }

    /**
     * Activar un usuario inexistente.
     */
    @Test
    @DisplayName("Activate user - Not found")
    void activateUser_NotFound() {
        // Dado
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Cuando y entonces
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.activateUser(99L));
        assertEquals("User not found with ID: 99", exception.getMessage());
        verify(userRepository, times(1)).findById(99L);
        verify(userRepository, never()).save(any());
    }

    /**
     * Obtiene éxito de usuarios todos activo.
     *
     * @return éxito de usuarios todos activo
     */
    @Test
    @DisplayName("Get all active users - Success")
    void getAllActiveUsers_Success() {
        // Dado
        List<User> users = Arrays.asList(user);
        when(userRepository.findByActiveTrue()).thenReturn(users);

        // Cuando
        List<UserResponseDTO> result = userService.getAllActiveUsers();

        // Entonces
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getActive());
        assertEquals(user.getId(), result.get(0).getId());
        verify(userRepository, times(1)).findByActiveTrue();
    }

    /**
     * Cuenta éxito de usuarios activo.
     */
    @Test
    @DisplayName("Count active users - Success")
    void countActiveUsers_Success() {
        // Dado
        when(userRepository.countByActiveTrue()).thenReturn(5L);

        // Cuando
        Long result = userService.countActiveUsers();

        // Entonces
        assertEquals(5L, result);
        verify(userRepository, times(1)).countByActiveTrue();
    }
}
