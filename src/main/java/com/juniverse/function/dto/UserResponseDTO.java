package com.juniverse.function.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    /**
     * Identificador.
     */
    private Long id;

    /**
     * Nombre.
     */
    private String name;

    /**
     * Apellido.
     */
    private String lastName;

    /**
     * Correo electrónico.
     */
    private String email;

    /**
     * Teléfono.
     */
    private String phone;

    /**
     * Activo.
     */
    private Boolean active;

    /**
     * Fecha de creación.
     */
    private LocalDateTime createdAt;

    /**
     * Fecha de última actualización.
     */
    private LocalDateTime updatedAt;
}
