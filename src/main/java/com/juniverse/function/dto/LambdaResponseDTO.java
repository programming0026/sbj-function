package com.juniverse.function.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LambdaResponseDTO {

    /**
     * Éxito.
     */
    private boolean success;

    /**
     * Mensaje.
     */
    private String message;

    /**
     * Datos.
     */
    private Object data;

    /**
     * Código de estado.
     */
    private int statusCode;

    /**
     * Marca de tiempo.
     */
    private LocalDateTime timestamp;

    private LambdaResponseDTO(boolean success, String message, Object data, int statusCode) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.statusCode = statusCode;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Respuesta exitosa.
     *
     * @param message mensaje
     * @param data datos
     * @return DTO de respuesta de lambda
     */
    public static LambdaResponseDTO success(String message, Object data) {
        return new LambdaResponseDTO(true, message, data, 200);
    }

    /**
     * Respuesta de error.
     *
     * @param message mensaje
     * @param statusCode código de estado
     * @return DTO de respuesta de lambda
     */
    public static LambdaResponseDTO error(String message, int statusCode) {
        return new LambdaResponseDTO(false, message, null, statusCode);
    }
}
