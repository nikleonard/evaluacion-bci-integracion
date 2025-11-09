package cl.bci.evaluacion.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI evaluacionOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Registro de Usuarios - BCI Evaluación")
                        .description("REST API para el registro de usuarios con validación de correo y contraseña")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("BCI")
                                .url("https://www.bci.cl"))
                        );
    }
}
