package cl.duoc.msauth.config;

import cl.duoc.msauth.model.Rol;
import cl.duoc.msauth.model.Usuario;
import cl.duoc.msauth.repository.RolRepository;
import cl.duoc.msauth.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Crea los roles base y un usuario administrador la primera vez que se inicia el servicio.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RolRepository rolRepository,
                           UsuarioRepository usuarioRepository,
                           PasswordEncoder passwordEncoder) {
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        Rol admin = rolRepository.findByNombre("ADMIN").orElseGet(() -> rolRepository.save(new Rol("ADMIN")));
        rolRepository.findByNombre("VENDEDOR").orElseGet(() -> rolRepository.save(new Rol("VENDEDOR")));

        if (!usuarioRepository.existsByUsername("admin")) {
            Usuario usuario = new Usuario();
            usuario.setUsername("admin");
            usuario.setEmail("admin@duoc.cl");
            usuario.setPassword(passwordEncoder.encode("admin123"));
            usuario.setRol(admin);
            usuarioRepository.save(usuario);
        }
    }
}
