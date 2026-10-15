package cl.duoc.msauth.dto;

public record LoginResponse(
        String token,
        String tipo,
        String username,
        String rol
) {
}
