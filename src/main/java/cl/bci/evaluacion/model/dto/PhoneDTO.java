package cl.bci.evaluacion.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhoneDTO {
    @NotBlank(message = "El número de teléfono es requerido")
    private String number;

    @NotBlank(message = "El código de ciudad es requerido")
    @JsonProperty("citycode")
    private String citycode;

    @NotBlank(message = "El código de país es requerido")
    @JsonProperty("contrycode")
    private String countrycode;
}
