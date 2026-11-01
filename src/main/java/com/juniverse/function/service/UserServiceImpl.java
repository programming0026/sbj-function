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

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
@Validated
public class UserServiceImpl implements UserService {

    /**
     * Repositorio de usuario.
     */
    private final UserRepository userRepository;

    /**
     * Crea usuario.
     *
     * @param userDto DTO de usuario
     * @return DTO de respuesta de usuario
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
     * Obtiene usuario por identificador.
     *
     * @param id identificador
     * @return usuario por identificador
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
     * Obtiene usuarios todos.
     *
     * @param pageable paginación
     * @return usuarios todos
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
     * Obtiene usuarios activo.
     *
     * @param pageable paginación
     * @return usuarios activo
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
     * Busca usuarios.
     *
     * @param filter filtro
     * @param active activo
     * @param pageable paginación
     * @return página
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
     * Actualiza usuario.
     *
     * @param id identificador
     * @param userDto DTO de usuario
     * @return DTO de respuesta de usuario
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
     * Elimina usuario.
     *
     * @param id identificador
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
     * Desactiva usuario.
     *
     * @param id identificador
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
     * Activa usuario.
     *
     * @param id identificador
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
     * Obtiene usuarios todos activo.
     *
     * @return usuarios todos activo
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
     * Cuenta usuarios activo.
     *
     * @return valor entero largo
     */
    @Override
    @Transactional(readOnly = true)
    public Long countActiveUsers() {
        log.info("Counting active users");
        return userRepository.countByActiveTrue();
    }

    /**
     * Convierte el DTO en entidad.
     *
     * @param dto DTO
     * @return entidad de usuario
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
     * Convierte la entidad en DTO.
     *
     * @param user usuario
     * @return DTO de respuesta de usuario
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
     * Actualiza datos de usuario.
     *
     * @param user usuario
     * @param dto DTO
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
