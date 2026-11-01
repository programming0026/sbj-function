package com.juniverse.function.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LambdaRequestDTO {

    /**
     * Operación.
     */
    private String operation;

    /**
     * Identificador de usuario.
     */
    private Long userId;

    /**
     * Datos de usuario.
     */
    private UserRequestDTO userData;

    /**
     * Crea.
     *
     * @param userData datos de usuario
     * @return DTO de petición de lambda
     */
    public static LambdaRequestDTO create(UserRequestDTO userData) {
        return new LambdaRequestDTO("CREATE", null, userData);
    }

    /**
     * Obtiene por identificador.
     *
     * @param userId identificador de usuario
     * @return por identificador
     */
    public static LambdaRequestDTO getById(Long userId) {
        return new LambdaRequestDTO("GET_BY_ID", userId, null);
    }

    /**
     * Obtiene todos activo.
     *
     * @return todos activo
     */
    public static LambdaRequestDTO getAllActive() {
        return new LambdaRequestDTO("GET_ALL_ACTIVE", null, null);
    }

    /**
     * Actualiza.
     *
     * @param userId identificador de usuario
     * @param userData datos de usuario
     * @return DTO de petición de lambda
     */
    public static LambdaRequestDTO update(Long userId, UserRequestDTO userData) {
        return new LambdaRequestDTO("UPDATE", userId, userData);
    }

    /**
     * Elimina.
     *
     * @param userId identificador de usuario
     * @return DTO de petición de lambda
     */
    public static LambdaRequestDTO delete(Long userId) {
        return new LambdaRequestDTO("DELETE", userId, null);
    }

    /**
     * Activa.
     *
     * @param userId identificador de usuario
     * @return DTO de petición de lambda
     */
    public static LambdaRequestDTO activate(Long userId) {
        return new LambdaRequestDTO("ACTIVATE", userId, null);
    }

    /**
     * Desactiva.
     *
     * @param userId identificador de usuario
     * @return DTO de petición de lambda
     */
    public static LambdaRequestDTO deactivate(Long userId) {
        return new LambdaRequestDTO("DEACTIVATE", userId, null);
    }

    /**
     * Cuenta activo.
     *
     * @return DTO de petición de lambda
     */
    public static LambdaRequestDTO countActive() {
        return new LambdaRequestDTO("COUNT_ACTIVE", null, null);
    }
}
