package com.juniverse.function.repository;

import com.juniverse.function.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The Class UserRepositoryTest.
 */
@DataJpaTest
@DisplayName("UserRepository Tests")
class UserRepositoryTest {

    /**
     * The entity manager.
     */
    @Autowired
    private TestEntityManager entityManager;

    /**
     * The user repository.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * The active user.
     */
    private User activeUser;

    /**
     * The inactive user.
     */
    private User inactiveUser;

    /**
     * Sets the up.
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
     * Find by active true should return only active users.
     */
    @Test
    @DisplayName("Find by active true - Should return only active users")
    void findByActiveTrue_ShouldReturnOnlyActiveUsers() {
        // When
        List<User> activeUsers = userRepository.findByActiveTrue();

        // Then
        assertNotNull(activeUsers);
        assertEquals(1, activeUsers.size());
        assertTrue(activeUsers.get(0).getActive());
        assertEquals("john.doe@email.com", activeUsers.get(0).getEmail());
    }

    /**
     * Find by active true with pagination should return active users with
     * pagination.
     */
    @Test
    @DisplayName("Find by active true with pagination - Should return active users with pagination")
    void findByActiveTrue_WithPagination_ShouldReturnActiveUsersWithPagination() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<User> activeUsersPage = userRepository.findByActiveTrue(pageable);

        // Then
        assertNotNull(activeUsersPage);
        assertEquals(1, activeUsersPage.getTotalElements());
        assertEquals(1, activeUsersPage.getContent().size());
        assertTrue(activeUsersPage.getContent().get(0).getActive());
    }

    /**
     * Exists by email should return true when email exists.
     */
    @Test
    @DisplayName("Exists by email - Should return true when email exists")
    void existsByEmail_ShouldReturnTrueWhenEmailExists() {
        // When & Then
        assertTrue(userRepository.existsByEmail("john.doe@email.com"));
        assertTrue(userRepository.existsByEmail("jane.smith@email.com"));
        assertFalse(userRepository.existsByEmail("nonexistent@email.com"));
    }

    /**
     * Exists by email and id not should return correct results.
     */
    @Test
    @DisplayName("Exists by email and id not - Should return correct results")
    void existsByEmailAndIdNot_ShouldReturnCorrectResults() {
        // When & Then
        assertFalse(userRepository.existsByEmailAndIdNot("john.doe@email.com", activeUser.getId()));
        assertTrue(userRepository.existsByEmailAndIdNot("john.doe@email.com", inactiveUser.getId()));
        assertFalse(userRepository.existsByEmailAndIdNot("nonexistent@email.com", activeUser.getId()));
    }

    /**
     * Find by filter and active should return filtered active users.
     */
    @Test
    @DisplayName("Find by filter and active - Should return filtered active users")
    void findByFilterAndActive_ShouldReturnFilteredActiveUsers() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<User> filteredUsers = userRepository.findByFilterAndActive("John", true, pageable);

        // Then
        assertNotNull(filteredUsers);
        assertEquals(1, filteredUsers.getTotalElements());
        assertEquals(1, filteredUsers.getContent().size());
        assertEquals("john.doe@email.com", filteredUsers.getContent().get(0).getEmail());
    }

    /**
     * Find by filter and active should return filtered inactive users.
     */
    @Test
    @DisplayName("Find by filter and active - Should return filtered inactive users")
    void findByFilterAndActive_ShouldReturnFilteredInactiveUsers() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<User> filteredUsers = userRepository.findByFilterAndActive("Jane", false, pageable);

        // Then
        assertNotNull(filteredUsers);
        assertEquals(1, filteredUsers.getTotalElements());
        assertEquals(1, filteredUsers.getContent().size());
        assertEquals("jane.smith@email.com", filteredUsers.getContent().get(0).getEmail());
    }

    /**
     * Find by filter and active should search by email.
     */
    @Test
    @DisplayName("Find by filter and active - Should search by email")
    void findByFilterAndActive_ShouldSearchByEmail() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<User> filteredUsers = userRepository.findByFilterAndActive("john.doe@email.com", true, pageable);

        // Then
        assertNotNull(filteredUsers);
        assertEquals(1, filteredUsers.getTotalElements());
        assertEquals("john.doe@email.com", filteredUsers.getContent().get(0).getEmail());
    }

    /**
     * Find by filter and active should search by last name.
     */
    @Test
    @DisplayName("Find by filter and active - Should search by last name")
    void findByFilterAndActive_ShouldSearchByLastName() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<User> filteredUsers = userRepository.findByFilterAndActive("Doe", true, pageable);

        // Then
        assertNotNull(filteredUsers);
        assertEquals(1, filteredUsers.getTotalElements());
        assertEquals("john.doe@email.com", filteredUsers.getContent().get(0).getEmail());
    }

    /**
     * Find by filter and active should return empty for no matches.
     */
    @Test
    @DisplayName("Find by filter and active - Should return empty for no matches")
    void findByFilterAndActive_ShouldReturnEmptyForNoMatches() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<User> filteredUsers = userRepository.findByFilterAndActive("NonExistent", true, pageable);

        // Then
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
        // When
        Long count = userRepository.countByActiveTrue();

        // Then
        assertEquals(1L, count);
    }

    /**
     * Save and find should work correctly.
     */
    @Test
    @DisplayName("Save and find - Should work correctly")
    void saveAndFind_ShouldWorkCorrectly() {
        // Given
        User newUser = new User();
        newUser.setName("Bob");
        newUser.setLastName("Johnson");
        newUser.setEmail("bob.johnson@email.com");
        newUser.setPhone("+1111111111");
        newUser.setActive(true);
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());

        // When
        User savedUser = userRepository.save(newUser);
        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        // Then
        assertNotNull(savedUser.getId());
        assertTrue(foundUser.isPresent());
        assertEquals("bob.johnson@email.com", foundUser.get().getEmail());
        assertTrue(foundUser.get().getActive());
    }

    /**
     * Delete by id should work correctly.
     */
    @Test
    @DisplayName("Delete by ID - Should work correctly")
    void deleteById_ShouldWorkCorrectly() {
        // Given
        Long userId = activeUser.getId();

        // When
        userRepository.deleteById(userId);
        Optional<User> deletedUser = userRepository.findById(userId);

        // Then
        assertFalse(deletedUser.isPresent());
    }

    /**
     * Update user should work correctly.
     */
    @Test
    @DisplayName("Update user - Should work correctly")
    void updateUser_ShouldWorkCorrectly() {
        // Given
        activeUser.setName("Updated John");
        activeUser.setEmail("updated.john@email.com");

        // When
        User updatedUser = userRepository.save(activeUser);

        // Then
        assertEquals("Updated John", updatedUser.getName());
        assertEquals("updated.john@email.com", updatedUser.getEmail());
    }
}
