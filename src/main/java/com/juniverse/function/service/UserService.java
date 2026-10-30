package com.juniverse.function.service;

import com.juniverse.function.dto.UserRequestDTO;
import com.juniverse.function.dto.UserResponseDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * The Interface UserService.
 */
public interface UserService {

    /**
     * Creates the user.
     *
     * @param userDto the user dto
     * @return the user response DTO
     */
    UserResponseDTO createUser(@Valid UserRequestDTO userDto);

    /**
     * Gets the user by id.
     *
     * @param id the id
     * @return the user by id
     */
    UserResponseDTO getUserById(Long id);

    /**
     * Gets the all users.
     *
     * @param pageable the pageable
     * @return the all users
     */
    Page<UserResponseDTO> getAllUsers(Pageable pageable);

    /**
     * Gets the active users.
     *
     * @param pageable the pageable
     * @return the active users
     */
    Page<UserResponseDTO> getActiveUsers(Pageable pageable);

    /**
     * Search users.
     *
     * @param filter the filter
     * @param active the active
     * @param pageable the pageable
     * @return the page
     */
    Page<UserResponseDTO> searchUsers(String filter, Boolean active, Pageable pageable);

    /**
     * Update user.
     *
     * @param id the id
     * @param userDto the user dto
     * @return the user response DTO
     */
    UserResponseDTO updateUser(Long id, @Valid UserRequestDTO userDto);

    /**
     * Delete user.
     *
     * @param id the id
     */
    void deleteUser(Long id);

    /**
     * Deactivate user.
     *
     * @param id the id
     */
    void deactivateUser(Long id);

    /**
     * Activate user.
     *
     * @param id the id
     */
    void activateUser(Long id);

    /**
     * Gets the all active users.
     *
     * @return the all active users
     */
    List<UserResponseDTO> getAllActiveUsers();

    /**
     * Count active users.
     *
     * @return the long
     */
    Long countActiveUsers();
}
