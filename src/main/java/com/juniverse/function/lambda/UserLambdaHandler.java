package com.juniverse.function.lambda;

import com.juniverse.function.dto.LambdaResponseDTO;
import com.juniverse.function.dto.LambdaRequestDTO;
import com.juniverse.function.dto.UserRequestDTO;
import com.juniverse.function.dto.UserResponseDTO;
import com.juniverse.function.service.UserService;
import com.juniverse.function.exception.NotFoundException;
import com.juniverse.function.exception.ServiceException;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserLambdaHandler implements Function<LambdaRequestDTO, LambdaResponseDTO> {

    /**
     * Servicio de usuario.
     */
    private final UserService userService;

    /**
     * Aplica.
     *
     * @param request petición
     * @return DTO de respuesta de lambda
     */
    @Override
    public LambdaResponseDTO apply(LambdaRequestDTO request) {
        // Solo se registra la operación: el objeto completo incluye correo y teléfono (datos personales)
        log.info("Processing Lambda request: operation={}", request == null ? null : request.getOperation());
        if (request == null || request.getOperation() == null) {
            return LambdaResponseDTO.error("Operation required", 400); // antes: NullPointerException -> 500
        }

        try {
            return switch (request.getOperation()) {
                case "CREATE" ->
                    handleCreate(request);
                case "GET_BY_ID" ->
                    handleGetById(request);
                case "GET_ALL_ACTIVE" ->
                    handleGetAllActive();
                case "UPDATE" ->
                    handleUpdate(request);
                case "DELETE" ->
                    handleDelete(request);
                case "ACTIVATE" ->
                    handleActivate(request);
                case "DEACTIVATE" ->
                    handleDeactivate(request);
                case "COUNT_ACTIVE" ->
                    handleCountActive();
                default ->
                    LambdaResponseDTO.error("Unsupported operation: " + request.getOperation(), 400);
            };
        } catch (NotFoundException e) {
            return LambdaResponseDTO.error(e.getMessage(), 404); // antes se devolvía 500
        } catch (ServiceException e) {
            return LambdaResponseDTO.error(e.getMessage(), 409); // regla de negocio: por ejemplo, correo duplicado
        } catch (ConstraintViolationException e) {
            String details = e.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .sorted()
                    .collect(Collectors.joining("; "));
            return LambdaResponseDTO.error("Validation error: " + details, 400);
        } catch (Exception e) {
            log.error("Error processing Lambda request", e);
            // No se devuelve el mensaje de la excepción: podría contener detalles internos (SQL, rutas...)
            return LambdaResponseDTO.error("Internal error", 500);
        }
    }

    /**
     * Procesa la creación.
     *
     * @param request petición
     * @return DTO de respuesta de lambda
     */
    private LambdaResponseDTO handleCreate(LambdaRequestDTO request) {
        log.info("Creating user via Lambda");
        UserRequestDTO userDto = request.getUserData();
        if (userDto == null) {
            return LambdaResponseDTO.error("User data required", 400);
        }

        UserResponseDTO user = userService.createUser(userDto);
        return LambdaResponseDTO.success("User created successfully", user);
    }

    /**
     * Procesa la consulta por identificador.
     *
     * @param request petición
     * @return DTO de respuesta de lambda
     */
    private LambdaResponseDTO handleGetById(LambdaRequestDTO request) {
        log.info("Getting user by ID via Lambda: {}", request.getUserId());
        if (request.getUserId() == null) {
            return LambdaResponseDTO.error("User ID required", 400);
        }

        UserResponseDTO user = userService.getUserById(request.getUserId());
        return LambdaResponseDTO.success("User retrieved successfully", user);
    }

    /**
     * Procesa la consulta de usuarios activos.
     *
     * @return DTO de respuesta de lambda
     */
    private LambdaResponseDTO handleGetAllActive() {
        log.info("Getting active users via Lambda");
        List<UserResponseDTO> users = userService.getAllActiveUsers();
        return LambdaResponseDTO.success("Active users retrieved successfully", users);
    }

    /**
     * Procesa la actualización.
     *
     * @param request petición
     * @return DTO de respuesta de lambda
     */
    private LambdaResponseDTO handleUpdate(LambdaRequestDTO request) {
        log.info("Updating user via Lambda: {}", request.getUserId());
        if (request.getUserId() == null || request.getUserData() == null) {
            return LambdaResponseDTO.error("User ID and data required", 400);
        }

        UserResponseDTO user = userService.updateUser(request.getUserId(), request.getUserData());
        return LambdaResponseDTO.success("User updated successfully", user);
    }

    /**
     * Procesa la eliminación.
     *
     * @param request petición
     * @return DTO de respuesta de lambda
     */
    private LambdaResponseDTO handleDelete(LambdaRequestDTO request) {
        log.info("Deleting user via Lambda: {}", request.getUserId());
        if (request.getUserId() == null) {
            return LambdaResponseDTO.error("User ID required", 400);
        }

        userService.deleteUser(request.getUserId());
        return LambdaResponseDTO.success("User deleted successfully", null);
    }

    /**
     * Procesa la activación.
     *
     * @param request petición
     * @return DTO de respuesta de lambda
     */
    private LambdaResponseDTO handleActivate(LambdaRequestDTO request) {
        log.info("Activating user via Lambda: {}", request.getUserId());
        if (request.getUserId() == null) {
            return LambdaResponseDTO.error("User ID required", 400);
        }

        userService.activateUser(request.getUserId());
        return LambdaResponseDTO.success("User activated successfully", null);
    }

    /**
     * Procesa la desactivación.
     *
     * @param request petición
     * @return DTO de respuesta de lambda
     */
    private LambdaResponseDTO handleDeactivate(LambdaRequestDTO request) {
        log.info("Deactivating user via Lambda: {}", request.getUserId());
        if (request.getUserId() == null) {
            return LambdaResponseDTO.error("User ID required", 400);
        }

        userService.deactivateUser(request.getUserId());
        return LambdaResponseDTO.success("User deactivated successfully", null);
    }

    /**
     * Procesa cantidad activo.
     *
     * @return DTO de respuesta de lambda
     */
    private LambdaResponseDTO handleCountActive() {
        log.info("Counting active users via Lambda");
        Long count = userService.countActiveUsers();
        return LambdaResponseDTO.success("Count retrieved successfully", count);
    }
}
