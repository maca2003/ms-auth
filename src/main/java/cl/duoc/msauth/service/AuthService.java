package cl.duoc.msauth.service;

import cl.duoc.msauth.dto.LoginRequest;
import cl.duoc.msauth.dto.LoginResponse;
import cl.duoc.msauth.dto.RegistroRequest;
import cl.duoc.msauth.dto.UsuarioRequest;
import cl.duoc.msauth.dto.UsuarioResponse;
import cl.duoc.msauth.exception.CredencialesInvalidasException;
import cl.duoc.msauth.model.Usuario;
import cl.duoc.msauth.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository,
                       UsuarioService usuarioService,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByUsername(request.username())
                .orElseThrow(() -> new CredencialesInvalidasException("Usuario o contraseña incorrectos"));

        if (!passwordEncoder.matches(request.password(), usuario.getPassword())) {
            throw new CredencialesInvalidasException("Usuario o contraseña incorrectos");
        }
        if (!usuario.isActivo()) {
            throw new CredencialesInvalidasException("El usuario está desactivado");
        }

        String rol = usuario.getRol().getNombre();
        String token = jwtService.generarToken(usuario.getUsername(), rol);
        return new LoginResponse(token, "Bearer", usuario.getUsername(), rol);
    }

    /**
     * Registro público: los nuevos usuarios quedan con rol VENDEDOR.
     */
    public UsuarioResponse registrar(RegistroRequest request) {
        return usuarioService.crear(new UsuarioRequest(
                request.username(), request.email(), request.password(), "VENDEDOR", true));
    }
}
