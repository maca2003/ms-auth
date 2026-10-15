package cl.duoc.msauth.service;

import cl.duoc.msauth.dto.LoginRequest;
import cl.duoc.msauth.dto.LoginResponse;
import cl.duoc.msauth.exception.CredencialesInvalidasException;
import cl.duoc.msauth.model.Rol;
import cl.duoc.msauth.model.Usuario;
import cl.duoc.msauth.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final String SECRET = "618ZmZ1dys0nh1R0edHwI8xqGnVLpFZups8KoHrMWe6SnBqU9qnMdLIr6nmJt2Jn";

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioService usuarioService;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtService jwtService = new JwtService(SECRET, 3600000);
    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(usuarioRepository, usuarioService, passwordEncoder, jwtService);
    }

    @Test
    void loginCorrectoGeneraTokenConElRol() {
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario("admin123")));

        LoginResponse response = authService.login(new LoginRequest("admin", "admin123"));

        assertEquals("ADMIN", response.rol());
        assertEquals("admin", jwtService.leerToken(response.token()).getSubject());
        assertEquals("ADMIN", jwtService.leerToken(response.token()).get("rol", String.class));
    }

    @Test
    void loginConContrasenaIncorrectaLanzaExcepcion() {
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario("admin123")));

        assertThrows(CredencialesInvalidasException.class,
                () -> authService.login(new LoginRequest("admin", "otra")));
    }

    private Usuario usuario(String password) {
        Usuario usuario = new Usuario();
        usuario.setUsername("admin");
        usuario.setEmail("admin@duoc.cl");
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setRol(new Rol("ADMIN"));
        return usuario;
    }
}
