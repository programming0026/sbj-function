package com.juniverse.function.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * The Class UserResponseDTO.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    /**
     * The id.
     */
    private Long id;

    /**
     * The name.
     */
    private String name;

    /**
     * The last name.
     */
    private String lastName;

    /**
     * The email.
     */
    private String email;

    /**
     * The phone.
     */
    private String phone;

    /**
     * The active.
     */
    private Boolean active;

    /**
     * The created at.
     */
    private LocalDateTime createdAt;

    /**
     * The updated at.
     */
    private LocalDateTime updatedAt;
}
