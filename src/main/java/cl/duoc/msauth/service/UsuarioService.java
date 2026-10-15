package cl.duoc.msauth.service;

import cl.duoc.msauth.dto.RolResponse;
import cl.duoc.msauth.dto.UsuarioRequest;
import cl.duoc.msauth.dto.UsuarioResponse;
import cl.duoc.msauth.exception.ConflictoException;
import cl.duoc.msauth.exception.RecursoNoEncontradoException;
import cl.duoc.msauth.model.Rol;
import cl.duoc.msauth.model.Usuario;
import cl.duoc.msauth.repository.RolRepository;
import cl.duoc.msauth.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          RolRepository rolRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll().stream().map(this::aResponse).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtener(Long id) {
        return aResponse(buscar(id));
    }

    @Transactional
    public UsuarioResponse crear(UsuarioRequest request) {
        if (usuarioRepository.existsByUsername(request.username())) {
            throw new ConflictoException("El usuario " + request.username() + " ya existe");
        }
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new ConflictoException("El email " + request.email() + " ya está registrado");
        }
        Usuario usuario = new Usuario();
        aplicarDatos(usuario, request);
        return aResponse(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponse actualizar(Long id, UsuarioRequest request) {
        Usuario usuario = buscar(id);
        if (usuarioRepository.existsByUsernameAndIdNot(request.username(), id)) {
            throw new ConflictoException("El usuario " + request.username() + " ya existe");
        }
        if (usuarioRepository.existsByEmailAndIdNot(request.email(), id)) {
            throw new ConflictoException("El email " + request.email() + " ya está registrado");
        }
        aplicarDatos(usuario, request);
        return aResponse(usuarioRepository.save(usuario));
    }

    @Transactional
    public void eliminar(Long id) {
        usuarioRepository.delete(buscar(id));
    }

    @Transactional(readOnly = true)
    public List<RolResponse> listarRoles() {
        return rolRepository.findAll().stream()
                .map(r -> new RolResponse(r.getId(), r.getNombre()))
                .toList();
    }

    @Transactional(readOnly = true)
    public Rol buscarRol(String nombre) {
        return rolRepository.findByNombre(nombre.toUpperCase())
                .orElseThrow(() -> new RecursoNoEncontradoException("El rol " + nombre + " no existe"));
    }

    private void aplicarDatos(Usuario usuario, UsuarioRequest request) {
        usuario.setUsername(request.username());
        usuario.setEmail(request.email());
        usuario.setPassword(passwordEncoder.encode(request.password()));
        usuario.setRol(buscarRol(request.rol()));
        if (request.activo() != null) {
            usuario.setActivo(request.activo());
        }
    }

    private Usuario buscar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario con id " + id + " no encontrado"));
    }

    private UsuarioResponse aResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.getRol().getNombre(),
                usuario.isActivo());
    }
}
