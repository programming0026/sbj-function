package com.juniverse.function.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * The Class LambdaResponseDTO.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LambdaResponseDTO {

    /**
     * The success.
     */
    private boolean success;

    /**
     * The message.
     */
    private String message;

    /**
     * The data.
     */
    private Object data;

    /**
     * The status code.
     */
    private int statusCode;

    /**
     * The timestamp.
     */
    private LocalDateTime timestamp;

    /**
     * Instantiates a new lambda response DTO.
     *
     * @param success the success
     * @param message the message
     * @param data the data
     * @param statusCode the status code
     */
    private LambdaResponseDTO(boolean success, String message, Object data, int statusCode) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.statusCode = statusCode;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Success.
     *
     * @param message the message
     * @param data the data
     * @return the lambda response DTO
     */
    public static LambdaResponseDTO success(String message, Object data) {
        return new LambdaResponseDTO(true, message, data, 200);
    }

    /**
     * Error.
     *
     * @param message the message
     * @param statusCode the status code
     * @return the lambda response DTO
     */
    public static LambdaResponseDTO error(String message, int statusCode) {
        return new LambdaResponseDTO(false, message, null, statusCode);
    }
}
