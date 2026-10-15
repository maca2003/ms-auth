package cl.duoc.msauth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioRequest(
        @NotBlank(message = "El usuario es obligatorio")
        @Size(min = 3, max = 50, message = "El usuario debe tener entre 3 y 50 caracteres")
        String username,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato válido")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, max = 60, message = "La contraseña debe tener entre 6 y 60 caracteres")
        String password,

        @NotBlank(message = "El rol es obligatorio (ADMIN o VENDEDOR)")
        String rol,

        Boolean activo
) {
}
