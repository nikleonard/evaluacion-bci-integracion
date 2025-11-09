package cl.bci.evaluacion.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import org.hibernate.validator.constraints.Length;

import cl.bci.evaluacion.validation.ValidEmail;
import cl.bci.evaluacion.validation.ValidPassword;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDTO {
    @NotBlank(message = "El nombre es requerido")
    private String name;

    @NotBlank(message = "El correo es requerido")
    @ValidEmail(message = "Formato de correo inválido")
    private String email;

    @NotBlank(message = "La contraseña es requerida")
    @ValidPassword(message = "Formato de contraseña inválido")
    @Length(max = 70, message = "La contraseña no debe exceder los 70 caracteres")
    private String password;

    @NotEmpty(message = "Al menos un teléfono es requerido")
    @Valid
    private List<PhoneDTO> phones;
}
