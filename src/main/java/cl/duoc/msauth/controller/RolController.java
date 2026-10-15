package cl.duoc.msauth.controller;

import cl.duoc.msauth.dto.RolResponse;
import cl.duoc.msauth.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RolController {

    private final UsuarioService usuarioService;

    public RolController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<RolResponse>> listar() {
        return ResponseEntity.ok(usuarioService.listarRoles());
    }
}
