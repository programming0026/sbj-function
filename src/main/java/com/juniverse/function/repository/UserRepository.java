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

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca por correo electrónico.
     *
     * @param email correo electrónico
     * @return opcional
     */
    Optional<User> findByEmail(String email);

    /**
     * Busca los usuarios activos.
     *
     * @return lista
     */
    List<User> findByActiveTrue();

    /**
     * Busca los usuarios activos.
     *
     * @param pageable paginación
     * @return página
     */
    Page<User> findByActiveTrue(Pageable pageable);

    /**
     * Busca por activo de y de filtro.
     *
     * @param filter filtro
     * @param active activo
     * @param pageable paginación
     * @return página
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
     * Indica si el correo pertenece a otro usuario distinto.
     *
     * @param email correo electrónico
     * @param id identificador
     * @return {@code true} si la operación tuvo éxito
     */
    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByEmail(String email);

    /**
     * Cuenta los usuarios activos.
     *
     * @return valor entero largo
     */
    Long countByActiveTrue();
}
