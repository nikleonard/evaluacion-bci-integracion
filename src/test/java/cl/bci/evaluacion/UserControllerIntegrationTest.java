package cl.bci.evaluacion;

import cl.bci.evaluacion.model.dto.PhoneDTO;
import cl.bci.evaluacion.model.dto.UserRequestDTO;
import cl.bci.evaluacion.model.entity.User;
import cl.bci.evaluacion.repository.UserRepository;
import cl.bci.evaluacion.util.PasswordEncoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Nota: Test generado parcialmente con IA.
 */
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testRegisterUserSuccess() throws Exception {
        UserRequestDTO request = UserRequestDTO.builder()
                .name("Juan Rodriguez")
                .email("juan@rodriguez.org")
                .password("SecurePass123")
                .phones(List.of(
                        PhoneDTO.builder()
                                .number("1234567")
                                .citycode("1")
                                .countrycode("57")
                                .build()
                ))
                .build();

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Juan Rodriguez"))
                .andExpect(jsonPath("$.email").value("juan@rodriguez.org"))
                .andExpect(jsonPath("$.created").exists())
                .andExpect(jsonPath("$.modified").exists())
                .andExpect(jsonPath("$.last_login").exists())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.isactive").value(true))
                .andExpect(jsonPath("$.phones[0].number").value("1234567"))
                .andExpect(jsonPath("$.phones[0].citycode").value("1"))
                .andExpect(jsonPath("$.phones[0].contrycode").value("57"));
    }

    /**
     * Test para formato de correo electrónico.
     * Nota: Si se cambia el regex de email, este test debe ser actualizado.
     */
    @Test
    void testRegisterUserWithInvalidEmailFormat() throws Exception {
        UserRequestDTO request = UserRequestDTO.builder()
                .name("Juan Rodriguez")
                .email("juan@rodriguezorg") // Falta TLD
                .password("SecurePass123")
                .phones(List.of(
                        PhoneDTO.builder()
                                .number("1234567")
                                .citycode("1")
                                .countrycode("57")
                                .build()
                ))
                .build();

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").exists());
    }

    /**
     * Test para formato de contraseña.
     * Nota: Si se cambia el regex de contraseña, este test debe ser actualizado.
     */
    @Test
    void testRegisterUserWithInvalidPasswordFormat() throws Exception {
        UserRequestDTO request = UserRequestDTO.builder()
                .name("Juan Rodriguez")
                .email("juan@rodriguez.org")
                .password("weakpass")
                .phones(List.of(
                        PhoneDTO.builder()
                                .number("1234567")
                                .citycode("1")
                                .countrycode("57")
                                .build()
                ))
                .build();

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").exists());
    }

    @Test
    void testRegisterUserWithDuplicateEmail() throws Exception {
        // Usuario 1
        UserRequestDTO firstRequest = UserRequestDTO.builder()
                .name("Juan Rodriguez")
                .email("juan@rodriguez.org")
                .password("SecurePass123")
                .phones(List.of(
                        PhoneDTO.builder()
                                .number("1234567")
                                .citycode("1")
                                .countrycode("57")
                                .build()
                ))
                .build();

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(firstRequest)))
                .andExpect(status().isCreated());

        // Usuario 2
        UserRequestDTO secondRequest = UserRequestDTO.builder()
                .name("Otro Juan Rodriguez")
                .email("juan@rodriguez.org")
                .password("SecurePass456")
                .phones(List.of(
                        PhoneDTO.builder()
                                .number("9876543")
                                .citycode("2")
                                .countrycode("57")
                                .build()
                ))
                .build();

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(secondRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("El correo ya registrado"));
    }

    @Test
    void testRegisterUserMissingName() throws Exception {
        UserRequestDTO request = UserRequestDTO.builder()
                .name(null)
                .email("juan@rodriguez.org")
                .password("SecurePass123")
                .phones(List.of(
                        PhoneDTO.builder()
                                .number("1234567")
                                .citycode("1")
                                .countrycode("57")
                                .build()
                ))
                .build();

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").exists());
    }

    @Test
    void testRegisterUserEmptyName() throws Exception {
        UserRequestDTO request = UserRequestDTO.builder()
                .name("")
                .email("juan@rodriguez.org")
                .password("SecurePass123")
                .phones(List.of(
                        PhoneDTO.builder()
                                .number("1234567")
                                .citycode("1")
                                .countrycode("57")
                                .build()
                ))
                .build();

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").exists());
    }

    @Test
    void testRegisterUserMissingPhone() throws Exception {
        UserRequestDTO request = UserRequestDTO.builder()
                .name("Juan Rodriguez")
                .email("juan@rodriguez.org")
                .password("SecurePass123")
                .phones(List.of())
                .build();

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").exists());
    }

    @Test
    void testRegisterUserWithMultiplePhones() throws Exception {
        UserRequestDTO request = UserRequestDTO.builder()
                .name("Juan Rodriguez")
                .email("juan@rodriguez.org")
                .password("SecurePass123")
                .phones(List.of(
                        PhoneDTO.builder()
                                .number("1234567")
                                .citycode("1")
                                .countrycode("57")
                                .build(),
                        PhoneDTO.builder()
                                .number("9876543")
                                .citycode("2")
                                .countrycode("57")
                                .build()
                ))
                .build();

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.phones.length()").value(2))
                .andExpect(jsonPath("$.phones[0].number").value("1234567"))
                .andExpect(jsonPath("$.phones[1].number").value("9876543"));
    }

    /**
     * Test para validar que el token JWT se genera correctamente.
     * Verifica que:
     * - El token no es nulo ni vacío
     * - El token tiene el formato JWT (tres partes separadas por puntos)
     */
    @Test
    void testRegisterUserGeneratesValidJWT() throws Exception {
        UserRequestDTO request = UserRequestDTO.builder()
                .name("Juan Rodriguez")
                .email("juan@rodriguez.org")
                .password("SecurePass123")
                .phones(List.of(
                        PhoneDTO.builder()
                                .number("1234567")
                                .citycode("1")
                                .countrycode("57")
                                .build()
                ))
                .build();

        MvcResult result = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();

        // Extraer token de la respuesta
        String response = result.getResponse().getContentAsString();
        String token = objectMapper.readTree(response).get("token").asText();

        // Validar formato JWT (tres partes separadas por puntos)
        String[] parts = token.split("\\.");
        assertThat(parts).hasSize(3);
        assertThat(token).isNotBlank();
    }

    /**
     * Test para validar que la contraseña se encripta correctamente en el registro.
     * Verifica que:
     * - La contraseña en la BD no es igual a la contraseña enviada
     * - La contraseña encriptada tiene el formato BCrypt válido
     * - La contraseña se puede validar correctamente contra el hash
     */
    @Test
    void testPasswordIsEncryptedCorrectly() throws Exception {
        String rawPassword = "SecurePass123";
        UserRequestDTO request = UserRequestDTO.builder()
                .name("Juan Rodriguez")
                .email("juan@rodriguez.org")
                .password(rawPassword)
                .phones(List.of(
                        PhoneDTO.builder()
                                .number("1234567")
                                .citycode("1")
                                .countrycode("57")
                                .build()
                ))
                .build();

        MvcResult result = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        // Extraer ID de la respuesta
        String response = result.getResponse().getContentAsString();
        UUID userId = UUID.fromString(objectMapper.readTree(response).get("id").asText());

        // Obtener el usuario de la BD
        Optional<User> savedUser = userRepository.findById(userId);
        assertThat(savedUser).isPresent();

        User user = savedUser.get();

        // Verificar que la contraseña no es igual a la original (está encriptada)
        assertThat(user.getPassword()).isNotEqualTo(rawPassword);

        // Verificar que la contraseña encriptada tiene el formato BCrypt
        assertThat(user.getPassword()).matches("^\\$2[aby]\\$.{56}$");

        // Verificar que la contraseña se puede validar correctamente
        assertThat(passwordEncoder.matches(rawPassword, user.getPassword())).isTrue();

        // Verificar que una contraseña incorrecta no coincide
        assertThat(passwordEncoder.matches("WrongPassword", user.getPassword())).isFalse();
    }

    /**
     * Test para validar que contraseñas diferentes producen hashs diferentes.
     * Incluso si se registran dos usuarios con contraseñas diferentes,
     * sus hashs deben ser completamente diferentes.
     */
    @Test
    void testDifferentPasswordsProduceDifferentHashes() throws Exception {
        String password1 = "SecurePass123";
        String password2 = "SecurePass456";

        // Registrar primer usuario
        UserRequestDTO request1 = UserRequestDTO.builder()
                .name("Juan Rodriguez")
                .email("juan@rodriguez.org")
                .password(password1)
                .phones(List.of(
                        PhoneDTO.builder()
                                .number("1234567")
                                .citycode("1")
                                .countrycode("57")
                                .build()
                ))
                .build();

        MvcResult result1 = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated())
                .andReturn();

        UUID userId1 = UUID.fromString(objectMapper.readTree(result1.getResponse().getContentAsString()).get("id").asText());
        User user1 = userRepository.findById(userId1).get();

        // Registrar segundo usuario
        UserRequestDTO request2 = UserRequestDTO.builder()
                .name("Carlos Martinez")
                .email("carlos@martinez.org")
                .password(password2)
                .phones(List.of(
                        PhoneDTO.builder()
                                .number("9876543")
                                .citycode("2")
                                .countrycode("57")
                                .build()
                ))
                .build();

        MvcResult result2 = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated())
                .andReturn();

        UUID userId2 = UUID.fromString(objectMapper.readTree(result2.getResponse().getContentAsString()).get("id").asText());
        User user2 = userRepository.findById(userId2).get();

        // Verificar que los hashs son diferentes
        assertThat(user1.getPassword()).isNotEqualTo(user2.getPassword());

        // Verificar que cada contraseña sólo coincide con su propio hash
        assertThat(passwordEncoder.matches(password1, user1.getPassword())).isTrue();
        assertThat(passwordEncoder.matches(password2, user1.getPassword())).isFalse();
        assertThat(passwordEncoder.matches(password2, user2.getPassword())).isTrue();
        assertThat(passwordEncoder.matches(password1, user2.getPassword())).isFalse();
    }

    /**
     * Test para validar que la contraseña encriptada tiene la longitud esperada.
     * BCrypt generalmente produce hashs de 60 caracteres.
     */
    @Test
    void testEncryptedPasswordHasExpectedLength() throws Exception {
        UserRequestDTO request = UserRequestDTO.builder()
                .name("Juan Rodriguez")
                .email("juan@rodriguez.org")
                .password("SecurePass123")
                .phones(List.of(
                        PhoneDTO.builder()
                                .number("1234567")
                                .citycode("1")
                                .countrycode("57")
                                .build()
                ))
                .build();

        MvcResult result = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        UUID userId = UUID.fromString(objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText());
        User user = userRepository.findById(userId).get();

        // BCrypt normalmente produce un hash de 60 caracteres
        assertThat(user.getPassword()).hasSizeBetween(55, 65);
    }

    /**
     * Test para validar que contraseñas más largas que 70 caracteres son rechazadas.
     * Verifica que el validador @Length(max = 70) funciona correctamente.
     */
    @Test
    void testPasswordLongerThan70CharactersIsRejected() throws Exception {
        // Generar una contraseña de 71 caracteres que cumple con el patrón de validación
        String longPassword = "P" + "a".repeat(8) + "1".repeat(62); // P + 8 'a's + 62 '1's = 71 caracteres
        // Cumple con: al menos una mayúscula (P), al menos un dígito (1), mínimo 8 caracteres
        
        UserRequestDTO request = UserRequestDTO.builder()
                .name("Juan Rodriguez")
                .email("juan@rodriguez.org")
                .password(longPassword)
                .phones(List.of(
                        PhoneDTO.builder()
                                .number("1234567")
                                .citycode("1")
                                .countrycode("57")
                                .build()
                ))
                .build();

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").exists());
    }

    /**
     * Test para validar que una contraseña exactamente de 70 caracteres es aceptada.
     * Verifica que el límite máximo es inclusivo (70 caracteres es válido).
     */
    @Test
    void testPasswordExactly70CharactersIsAccepted() throws Exception {
        // Generar una contraseña de exactamente 70 caracteres que cumple con el patrón
        String passwordOf70Chars = "P" + "a".repeat(8) + "1".repeat(61); // P + 8 'a's + 61 '1's = 70 caracteres
        // Cumple con: al menos una mayúscula (P), al menos un dígito (1), mínimo 8 caracteres
        
        UserRequestDTO request = UserRequestDTO.builder()
                .name("Juan Rodriguez")
                .email("juan@rodriguez.org")
                .password(passwordOf70Chars)
                .phones(List.of(
                        PhoneDTO.builder()
                                .number("1234567")
                                .citycode("1")
                                .countrycode("57")
                                .build()
                ))
                .build();

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Juan Rodriguez"));
    }
}
