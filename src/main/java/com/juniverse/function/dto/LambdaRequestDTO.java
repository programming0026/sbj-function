package com.juniverse.function.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Class LambdaRequestDTO.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LambdaRequestDTO {

    /**
     * The operation.
     */
    private String operation;

    /**
     * The user id.
     */
    private Long userId;

    /**
     * The user data.
     */
    private UserRequestDTO userData;

    /**
     * Creates the.
     *
     * @param userData the user data
     * @return the lambda request DTO
     */
    public static LambdaRequestDTO create(UserRequestDTO userData) {
        return new LambdaRequestDTO("CREATE", null, userData);
    }

    /**
     * Gets the by id.
     *
     * @param userId the user id
     * @return the by id
     */
    public static LambdaRequestDTO getById(Long userId) {
        return new LambdaRequestDTO("GET_BY_ID", userId, null);
    }

    /**
     * Gets the all active.
     *
     * @return the all active
     */
    public static LambdaRequestDTO getAllActive() {
        return new LambdaRequestDTO("GET_ALL_ACTIVE", null, null);
    }

    /**
     * Update.
     *
     * @param userId the user id
     * @param userData the user data
     * @return the lambda request DTO
     */
    public static LambdaRequestDTO update(Long userId, UserRequestDTO userData) {
        return new LambdaRequestDTO("UPDATE", userId, userData);
    }

    /**
     * Delete.
     *
     * @param userId the user id
     * @return the lambda request DTO
     */
    public static LambdaRequestDTO delete(Long userId) {
        return new LambdaRequestDTO("DELETE", userId, null);
    }

    /**
     * Activate.
     *
     * @param userId the user id
     * @return the lambda request DTO
     */
    public static LambdaRequestDTO activate(Long userId) {
        return new LambdaRequestDTO("ACTIVATE", userId, null);
    }

    /**
     * Deactivate.
     *
     * @param userId the user id
     * @return the lambda request DTO
     */
    public static LambdaRequestDTO deactivate(Long userId) {
        return new LambdaRequestDTO("DEACTIVATE", userId, null);
    }

    /**
     * Count active.
     *
     * @return the lambda request DTO
     */
    public static LambdaRequestDTO countActive() {
        return new LambdaRequestDTO("COUNT_ACTIVE", null, null);
    }
}
