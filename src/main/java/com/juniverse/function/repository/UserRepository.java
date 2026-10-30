package com.juniverse.function.repository;

import com.juniverse.function.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * The Interface UserRepository.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find by email.
     *
     * @param email the email
     * @return the optional
     */
    Optional<User> findByEmail(String email);

    /**
     * Find by active true.
     *
     * @return the list
     */
    List<User> findByActiveTrue();

    /**
     * Find by active true.
     *
     * @param pageable the pageable
     * @return the page
     */
    Page<User> findByActiveTrue(Pageable pageable);

    /**
     * Find by filter and active.
     *
     * @param filter the filter
     * @param active the active
     * @param pageable the pageable
     * @return the page
     */
    @Query("SELECT u FROM User u WHERE "
            + "(LOWER(u.name) LIKE LOWER(CONCAT('%', :filter, '%')) OR "
            + "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :filter, '%')) OR "
            + "LOWER(u.email) LIKE LOWER(CONCAT('%', :filter, '%'))) AND "
            + "u.active = :active")
    Page<User> findByFilterAndActive(@Param("filter") String filter,
            @Param("active") Boolean active,
            Pageable pageable);

    /**
     * Exists by email and id not.
     *
     * @param email the email
     * @param id the id
     * @return true, if successful
     */
    boolean existsByEmailAndIdNot(String email, Long id);

    /**
     * Exists by email.
     *
     * @param email the email
     * @return true, if successful
     */
    boolean existsByEmail(String email);

    /**
     * Count by active true.
     *
     * @return the long
     */
    Long countByActiveTrue();
}
