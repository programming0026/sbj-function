package com.juniverse.function.service;

import com.juniverse.function.dto.UserRequestDTO;
import com.juniverse.function.dto.UserResponseDTO;
import com.juniverse.function.entity.User;
import com.juniverse.function.exception.ServiceException;
import com.juniverse.function.exception.NotFoundException;
import com.juniverse.function.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The Class UserServiceImpl.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
@Validated
public class UserServiceImpl implements UserService {

    /**
     * The user repository.
     */
    private final UserRepository userRepository;

    /**
     * Creates the user.
     *
     * @param userDto the user dto
     * @return the user response DTO
     */
    @Override
    public UserResponseDTO createUser(UserRequestDTO userDto) {
        log.info("Creating user with email: {}", userDto.getEmail());

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new ServiceException("User already exists with email: " + userDto.getEmail());
        }

        User user = mapDtoToEntity(userDto);
        User savedUser = userRepository.save(user);

        log.info("User created successfully with ID: {}", savedUser.getId());
        return mapEntityToDto(savedUser);
    }

    /**
     * Gets the user by id.
     *
     * @param id the id
     * @return the user by id
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {
        log.info("Finding user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + id));

        return mapEntityToDto(user);
    }

    /**
     * Gets the all users.
     *
     * @param pageable the pageable
     * @return the all users
     */
    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        log.info("Getting all users - Page: {}, Size: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<User> users = userRepository.findAll(pageable);
        return users.map(this::mapEntityToDto);
    }

    /**
     * Gets the active users.
     *
     * @param pageable the pageable
     * @return the active users
     */
    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDTO> getActiveUsers(Pageable pageable) {
        log.info("Getting active users - Page: {}, Size: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<User> users = userRepository.findByActiveTrue(pageable);
        return users.map(this::mapEntityToDto);
    }

    /**
     * Search users.
     *
     * @param filter the filter
     * @param active the active
     * @param pageable the pageable
     * @return the page
     */
    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDTO> searchUsers(String filter, Boolean active, Pageable pageable) {
        log.info("Searching users with filter: '{}', active: {}", filter, active);

        // Con un filtro nulo CONCAT('%', null, '%') es nulo y la búsqueda no devolvía nada
        Page<User> users = userRepository.findByFilterAndActive(filter == null ? "" : filter, active, pageable);
        return users.map(this::mapEntityToDto);
    }

    /**
     * Update user.
     *
     * @param id the id
     * @param userDto the user dto
     * @return the user response DTO
     */
    @Override
    public UserResponseDTO updateUser(Long id, UserRequestDTO userDto) {
        log.info("Updating user with ID: {}", id);

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + id));

        if (!existingUser.getEmail().equals(userDto.getEmail())
                && userRepository.existsByEmailAndIdNot(userDto.getEmail(), id)) {
            throw new ServiceException("Another user already exists with email: " + userDto.getEmail());
        }

        updateUserData(existingUser, userDto);
        User updatedUser = userRepository.save(existingUser);

        log.info("User updated successfully with ID: {}", id);
        return mapEntityToDto(updatedUser);
    }

    /**
     * Delete user.
     *
     * @param id the id
     */
    @Override
    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);

        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User not found with ID: " + id);
        }

        userRepository.deleteById(id);
        log.info("User deleted successfully with ID: {}", id);
    }

    /**
     * Deactivate user.
     *
     * @param id the id
     */
    @Override
    public void deactivateUser(Long id) {
        log.info("Deactivating user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + id));

        user.setActive(false);
        userRepository.save(user);

        log.info("User deactivated successfully with ID: {}", id);
    }

    /**
     * Activate user.
     *
     * @param id the id
     */
    @Override
    public void activateUser(Long id) {
        log.info("Activating user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + id));

        user.setActive(true);
        userRepository.save(user);

        log.info("User activated successfully with ID: {}", id);
    }

    /**
     * Gets the all active users.
     *
     * @return the all active users
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllActiveUsers() {
        log.info("Getting all active users");

        List<User> users = userRepository.findByActiveTrue();
        return users.stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    /**
     * Count active users.
     *
     * @return the long
     */
    @Override
    @Transactional(readOnly = true)
    public Long countActiveUsers() {
        log.info("Counting active users");
        return userRepository.countByActiveTrue();
    }

    /**
     * Map dto to entity.
     *
     * @param dto the dto
     * @return the usuario
     */
    private User mapDtoToEntity(UserRequestDTO dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setActive(dto.getActive() != null ? dto.getActive() : true);
        return user;
    }

    /**
     * Map entity to dto.
     *
     * @param user the user
     * @return the user response DTO
     */
    private UserResponseDTO mapEntityToDto(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.getActive(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    /**
     * Update user data.
     *
     * @param user the user
     * @param dto the dto
     */
    private void updateUserData(User user, UserRequestDTO dto) {
        user.setName(dto.getName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        if (dto.getActive() != null) {
            user.setActive(dto.getActive());
        }
    }
}
