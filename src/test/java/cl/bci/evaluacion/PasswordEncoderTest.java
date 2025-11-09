package cl.bci.evaluacion;

import cl.bci.evaluacion.util.PasswordEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitarios para el cifrado de contraseñas.
 * Verifica que las contraseñas se encripten correctamente y se validen apropiadamente.
 */
class PasswordEncoderTest {

    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new PasswordEncoder();
    }

    /**
     * Test que verifica que la contraseña se encripta correctamente.
     * Una contraseña encriptada debe ser diferente a la original.
     */
    @Test
    void testPasswordEncodingProducesEncodedPassword() {
        String rawPassword = "SecurePass123";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        assertThat(encodedPassword)
            .isNotBlank()
            .isNotEqualTo(rawPassword);
        assertThat(encodedPassword.length()).isGreaterThan(rawPassword.length());
    }

    /**
     * Test que verifica que dos contraseñas iguales generan diferentes hashs.
     * Esto es una característica esperada de BCrypt (usa salt aleatorio).
     */
    @Test
    void testDifferentEncodingsForSamePassword() {
        String rawPassword = "SecurePass123";
        String encodedPassword1 = passwordEncoder.encode(rawPassword);
        String encodedPassword2 = passwordEncoder.encode(rawPassword);

        assertThat(encodedPassword1).isNotEqualTo(encodedPassword2);
    }

    /**
     * Test que verifica que una contraseña se valida correctamente contra su hash.
     * El método matches() debe retornar true cuando la contraseña coincide.
     */
    @Test
    void testPasswordMatchesWithCorrectRawPassword() {
        String rawPassword = "SecurePass123";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);

        assertThat(matches).isTrue();
    }

    /**
     * Test que verifica que una contraseña incorrecta no coincide con su hash.
     * El método matches() debe retornar false cuando la contraseña es incorrecta.
     */
    @Test
    void testPasswordDoesNotMatchWithWrongPassword() {
        String rawPassword = "SecurePass123";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        String wrongPassword = "SecurePass456";

        boolean matches = passwordEncoder.matches(wrongPassword, encodedPassword);

        assertThat(matches).isFalse();
    }

    /**
     * Test que verifica que el método encode produce un hash compatible con BCrypt.
     * Un hash BCrypt siempre comienza con "$2a$", "$2b$" o "$2y$".
     */
    @Test
    void testEncodedPasswordFollowsBCryptFormat() {
        String rawPassword = "SecurePass123";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        assertThat(encodedPassword).matches("^\\$2[aby]\\$.{56}$");
    }

    /**
     * Test que verifica que contraseñas vacías se encriptan correctamente.
     */
    @Test
    void testEmptyPasswordCanBeEncoded() {
        String emptyPassword = "";
        String encodedPassword = passwordEncoder.encode(emptyPassword);

        assertThat(encodedPassword).isNotBlank();
        assertThat(passwordEncoder.matches(emptyPassword, encodedPassword)).isTrue();
    }

    /**
     * Test que verifica que contraseñas con caracteres especiales se encriptan correctamente.
     */
    @Test
    void testPasswordWithSpecialCharactersIsEncoded() {
        String specialPassword = "P@ssw0rd!#$%&*()_+-=[]{}|;:',.<>?/~`";
        String encodedPassword = passwordEncoder.encode(specialPassword);

        assertThat(encodedPassword).isNotBlank();
        assertThat(passwordEncoder.matches(specialPassword, encodedPassword)).isTrue();
    }

    /**
     * Test que verifica que contraseñas muy largas se encriptan correctamente.
     */
    @Test
    void testLongPasswordIsEncoded() {
        String longPassword = "a".repeat(70);
        String encodedPassword = passwordEncoder.encode(longPassword);

        assertThat(encodedPassword).isNotBlank();
        assertThat(passwordEncoder.matches(longPassword, encodedPassword)).isTrue();
    }

    /**
     * Test que verifica que case sensitivity se preserva.
     * "password" y "Password" deben producir resultados diferentes.
     */
    @Test
    void testPasswordEncodingIsCaseSensitive() {
        String password1 = "SecurePass123";
        String password2 = "securepass123";
        String encodedPassword = passwordEncoder.encode(password1);

        assertThat(passwordEncoder.matches(password1, encodedPassword)).isTrue();
        assertThat(passwordEncoder.matches(password2, encodedPassword)).isFalse();
    }
}
