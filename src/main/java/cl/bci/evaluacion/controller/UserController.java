package cl.bci.evaluacion.controller;

import cl.bci.evaluacion.model.dto.UserRequestDTO;
import cl.bci.evaluacion.model.dto.UserResponseDTO;
import cl.bci.evaluacion.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Validated
@Tag(name = "Usuarios", description = "Operaciones de registro de usuarios")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    @Operation(summary = "Registrar nuevo usuario", description = "Crea un nuevo usuario con validación de correo y contraseña")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o correo duplicado",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserRequestDTO request) {
        UserResponseDTO response = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
