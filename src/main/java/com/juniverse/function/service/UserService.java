package com.juniverse.function.service;

import com.juniverse.function.dto.UserRequestDTO;
import com.juniverse.function.dto.UserResponseDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    /**
     * Crea usuario.
     *
     * @param userDto DTO de usuario
     * @return DTO de respuesta de usuario
     */
    UserResponseDTO createUser(@Valid UserRequestDTO userDto);

    /**
     * Obtiene usuario por identificador.
     *
     * @param id identificador
     * @return usuario por identificador
     */
    UserResponseDTO getUserById(Long id);

    /**
     * Obtiene usuarios todos.
     *
     * @param pageable paginación
     * @return usuarios todos
     */
    Page<UserResponseDTO> getAllUsers(Pageable pageable);

    /**
     * Obtiene usuarios activo.
     *
     * @param pageable paginación
     * @return usuarios activo
     */
    Page<UserResponseDTO> getActiveUsers(Pageable pageable);

    /**
     * Busca usuarios.
     *
     * @param filter filtro
     * @param active activo
     * @param pageable paginación
     * @return página
     */
    Page<UserResponseDTO> searchUsers(String filter, Boolean active, Pageable pageable);

    /**
     * Actualiza usuario.
     *
     * @param id identificador
     * @param userDto DTO de usuario
     * @return DTO de respuesta de usuario
     */
    UserResponseDTO updateUser(Long id, @Valid UserRequestDTO userDto);

    /**
     * Elimina usuario.
     *
     * @param id identificador
     */
    void deleteUser(Long id);

    /**
     * Desactiva usuario.
     *
     * @param id identificador
     */
    void deactivateUser(Long id);

    /**
     * Activa usuario.
     *
     * @param id identificador
     */
    void activateUser(Long id);

    /**
     * Obtiene usuarios todos activo.
     *
     * @return usuarios todos activo
     */
    List<UserResponseDTO> getAllActiveUsers();

    /**
     * Cuenta usuarios activo.
     *
     * @return valor entero largo
     */
    Long countActiveUsers();
}
