package cl.bci.evaluacion;

import cl.bci.evaluacion.model.dto.PhoneDTO;
import cl.bci.evaluacion.model.dto.UserRequestDTO;
import cl.bci.evaluacion.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

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
                .andExpect(jsonPath("$.phones[0].countrycode").value("57"));
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
}
