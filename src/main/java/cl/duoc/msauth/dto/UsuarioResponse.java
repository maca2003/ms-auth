package cl.duoc.msauth.dto;

public record UsuarioResponse(
        Long id,
        String username,
        String email,
        String rol,
        boolean activo
) {
}
