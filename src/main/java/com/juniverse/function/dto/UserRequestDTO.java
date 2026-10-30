package com.juniverse.function.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Class UserRequestDTO.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {

    /**
     * The name.
     */
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    /**
     * The last name.
     */
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
    private String lastName;

    /**
     * The email.
     */
    @Email(message = "Email must have a valid format")
    @NotBlank(message = "Email is required")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    private String email;

    /**
     * The phone.
     */
    @Size(max = 20, message = "Phone cannot exceed 20 characters")
    private String phone;

    /**
     * The active.
     */
    private Boolean active = true;
}
