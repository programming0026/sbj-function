package com.juniverse.function.repository;

import com.juniverse.function.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("UserRepository Tests")
class UserRepositoryTest {

    /**
     * Administrador de entidades.
     */
    @Autowired
    private TestEntityManager entityManager;

    /**
     * Repositorio de usuario.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Usuario activo.
     */
    private User activeUser;

    /**
     * Usuario inactivo.
     */
    private User inactiveUser;

    /**
     * Prepara los datos de la prueba.
     */
    @BeforeEach
    void setUp() {
        activeUser = new User();
        activeUser.setName("John");
        activeUser.setLastName("Doe");
        activeUser.setEmail("john.doe@email.com");
        activeUser.setPhone("+1234567890");
        activeUser.setActive(true);
        activeUser.setCreatedAt(LocalDateTime.now());
        activeUser.setUpdatedAt(LocalDateTime.now());

        inactiveUser = new User();
        inactiveUser.setName("Jane");
        inactiveUser.setLastName("Smith");
        inactiveUser.setEmail("jane.smith@email.com");
        inactiveUser.setPhone("+9876543210");
        inactiveUser.setActive(false);
        inactiveUser.setCreatedAt(LocalDateTime.now());
        inactiveUser.setUpdatedAt(LocalDateTime.now());

        entityManager.persistAndFlush(activeUser);
        entityManager.persistAndFlush(inactiveUser);
    }

    /**
     * Buscar activos devuelve solo usuarios activos.
     */
    @Test
    @DisplayName("Find by active true - Should return only active users")
    void findByActiveTrue_ShouldReturnOnlyActiveUsers() {
        // Cuando
        List<User> activeUsers = userRepository.findByActiveTrue();

        // Entonces
        assertNotNull(activeUsers);
        assertEquals(1, activeUsers.size());
        assertTrue(activeUsers.get(0).getActive());
        assertEquals("john.doe@email.com", activeUsers.get(0).getEmail());
    }

    /**
     * Buscar activos con paginación devuelve los usuarios activos de la página.
     * paginación.
     */
    @Test
    @DisplayName("Find by active true with pagination - Should return active users with pagination")
    void findByActiveTrue_WithPagination_ShouldReturnActiveUsersWithPagination() {
        // Dado
        Pageable pageable = PageRequest.of(0, 10);

        // Cuando
        Page<User> activeUsersPage = userRepository.findByActiveTrue(pageable);

        // Entonces
        assertNotNull(activeUsersPage);
        assertEquals(1, activeUsersPage.getTotalElements());
        assertEquals(1, activeUsersPage.getContent().size());
        assertTrue(activeUsersPage.getContent().get(0).getActive());
    }

    /**
     * Devuelve verdadero cuando el correo existe.
     */
    @Test
    @DisplayName("Exists by email - Should return true when email exists")
    void existsByEmail_ShouldReturnTrueWhenEmailExists() {
        // Cuando y entonces
        assertTrue(userRepository.existsByEmail("john.doe@email.com"));
        assertTrue(userRepository.existsByEmail("jane.smith@email.com"));
        assertFalse(userRepository.existsByEmail("nonexistent@email.com"));
    }

    /**
     * Verifica si existe el correo en otro usuario distinto.
     */
    @Test
    @DisplayName("Exists by email and id not - Should return correct results")
    void existsByEmailAndIdNot_ShouldReturnCorrectResults() {
        // Cuando y entonces
        assertFalse(userRepository.existsByEmailAndIdNot("john.doe@email.com", activeUser.getId()));
        assertTrue(userRepository.existsByEmailAndIdNot("john.doe@email.com", inactiveUser.getId()));
        assertFalse(userRepository.existsByEmailAndIdNot("nonexistent@email.com", activeUser.getId()));
    }

    /**
     * Filtrar por texto y estado devuelve los usuarios activos que coinciden.
     */
    @Test
    @DisplayName("Find by filter and active - Should return filtered active users")
    void findByFilterAndActive_ShouldReturnFilteredActiveUsers() {
        // Dado
        Pageable pageable = PageRequest.of(0, 10);

        // Cuando
        Page<User> filteredUsers = userRepository.findByFilterAndActive("John", true, pageable);

        // Entonces
        assertNotNull(filteredUsers);
        assertEquals(1, filteredUsers.getTotalElements());
        assertEquals(1, filteredUsers.getContent().size());
        assertEquals("john.doe@email.com", filteredUsers.getContent().get(0).getEmail());
    }

    /**
     * Filtrar por texto y estado devuelve los usuarios inactivos que coinciden.
     */
    @Test
    @DisplayName("Find by filter and active - Should return filtered inactive users")
    void findByFilterAndActive_ShouldReturnFilteredInactiveUsers() {
        // Dado
        Pageable pageable = PageRequest.of(0, 10);

        // Cuando
        Page<User> filteredUsers = userRepository.findByFilterAndActive("Jane", false, pageable);

        // Entonces
        assertNotNull(filteredUsers);
        assertEquals(1, filteredUsers.getTotalElements());
        assertEquals(1, filteredUsers.getContent().size());
        assertEquals("jane.smith@email.com", filteredUsers.getContent().get(0).getEmail());
    }

    /**
     * Filtrar por texto y estado busca por correo.
     */
    @Test
    @DisplayName("Find by filter and active - Should search by email")
    void findByFilterAndActive_ShouldSearchByEmail() {
        // Dado
        Pageable pageable = PageRequest.of(0, 10);

        // Cuando
        Page<User> filteredUsers = userRepository.findByFilterAndActive("john.doe@email.com", true, pageable);

        // Entonces
        assertNotNull(filteredUsers);
        assertEquals(1, filteredUsers.getTotalElements());
        assertEquals("john.doe@email.com", filteredUsers.getContent().get(0).getEmail());
    }

    /**
     * Filtrar por texto y estado busca por apellido.
     */
    @Test
    @DisplayName("Find by filter and active - Should search by last name")
    void findByFilterAndActive_ShouldSearchByLastName() {
        // Dado
        Pageable pageable = PageRequest.of(0, 10);

        // Cuando
        Page<User> filteredUsers = userRepository.findByFilterAndActive("Doe", true, pageable);

        // Entonces
        assertNotNull(filteredUsers);
        assertEquals(1, filteredUsers.getTotalElements());
        assertEquals("john.doe@email.com", filteredUsers.getContent().get(0).getEmail());
    }

    /**
     * Filtrar por texto y estado devuelve vacío si no hay coincidencias.
     */
    @Test
    @DisplayName("Find by filter and active - Should return empty for no matches")
    void findByFilterAndActive_ShouldReturnEmptyForNoMatches() {
        // Dado
        Pageable pageable = PageRequest.of(0, 10);

        // Cuando
        Page<User> filteredUsers = userRepository.findByFilterAndActive("NonExistent", true, pageable);

        // Entonces
        assertNotNull(filteredUsers);
        assertEquals(0, filteredUsers.getTotalElements());
        assertTrue(filteredUsers.getContent().isEmpty());
    }

    /**
     * Count active users should return correct count.
     */
    @Test
    @DisplayName("Count active users - Should return correct count")
    void countActiveUsers_ShouldReturnCorrectCount() {
        // Cuando
        Long count = userRepository.countByActiveTrue();

        // Entonces
        assertEquals(1L, count);
    }

    /**
     * Guardar y buscar funciona correctamente.
     */
    @Test
    @DisplayName("Save and find - Should work correctly")
    void saveAndFind_ShouldWorkCorrectly() {
        // Dado
        User newUser = new User();
        newUser.setName("Bob");
        newUser.setLastName("Johnson");
        newUser.setEmail("bob.johnson@email.com");
        newUser.setPhone("+1111111111");
        newUser.setActive(true);
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());

        // Cuando
        User savedUser = userRepository.save(newUser);
        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        // Entonces
        assertNotNull(savedUser.getId());
        assertTrue(foundUser.isPresent());
        assertEquals("bob.johnson@email.com", foundUser.get().getEmail());
        assertTrue(foundUser.get().getActive());
    }

    /**
     * Eliminar por identificador funciona correctamente.
     */
    @Test
    @DisplayName("Delete by ID - Should work correctly")
    void deleteById_ShouldWorkCorrectly() {
        // Dado
        Long userId = activeUser.getId();

        // Cuando
        userRepository.deleteById(userId);
        Optional<User> deletedUser = userRepository.findById(userId);

        // Entonces
        assertFalse(deletedUser.isPresent());
    }

    /**
     * Actualizar un usuario funciona correctamente.
     */
    @Test
    @DisplayName("Update user - Should work correctly")
    void updateUser_ShouldWorkCorrectly() {
        // Dado
        activeUser.setName("Updated John");
        activeUser.setEmail("updated.john@email.com");

        // Cuando
        User updatedUser = userRepository.save(activeUser);

        // Entonces
        assertEquals("Updated John", updatedUser.getName());
        assertEquals("updated.john@email.com", updatedUser.getEmail());
    }
}
