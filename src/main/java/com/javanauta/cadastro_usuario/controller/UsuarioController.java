package com.javanauta.cadastro_usuario.controller;

import com.javanauta.cadastro_usuario.business.UsuarioService;
import com.javanauta.cadastro_usuario.dto.UsuarioUpdateDTO;
import com.javanauta.cadastro_usuario.infrastructure.entities.Usuario;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
@Slf4j
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<Usuario> salvarUsuario(@Valid @RequestBody Usuario usuario) {
        log.info("Recebida requisição POST para salvar usuário: {}", usuario.getEmail());
        Usuario novoUsuario = usuarioService.salvarUsuario(usuario);
        log.info("Usuário salvo com sucesso: ID {}", novoUsuario.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> buscarTodosUsuarios() {
        log.info("Recebida requisição GET para buscar todos os usuários.");
        List<Usuario> usuarios = usuarioService.buscarTodosUsuarios();
        log.info("Retornando {} usuários.", usuarios.size());
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarUsuarioPorId(@PathVariable Integer id) {
        log.info("Recebida requisição GET para buscar usuário por ID: {}", id);
        Usuario usuario = usuarioService.buscarUsuarioPorId(id);
        log.info("Usuário encontrado por ID {}: {}", id, usuario.getEmail());
        return ResponseEntity.ok(usuario);
    }

    @GetMapping("/email")
    public ResponseEntity<Usuario> buscarUsuarioPorEmail(@RequestParam String email) {
        log.info("Recebida requisição GET para buscar usuário por e-mail: {}", email);
        Usuario usuario = usuarioService.buscarUsuarioPorEmail(email);
        log.info("Usuário encontrado por e-mail {}: {}", email, usuario.getId());
        return ResponseEntity.ok(usuario);
    }

    @DeleteMapping("/email")
    @PreAuthorize("hasRole('ADMINISTRADOR') or #id == authentication.principal.id")
    public ResponseEntity<Void> deletarUsuarioPorEmail(@RequestParam String email) {
        log.warn("Recebida requisição DELETE para deletar usuário por e-mail (requer ADMIN): {}", email);
        usuarioService.deletarUsuarioPorEmail(email);
        log.info("Usuário com e-mail {} deletado com sucesso.", email);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or #id == authentication.principal.id")
    public ResponseEntity<Usuario> atualizarUsuarioPorId(@PathVariable Integer id, @Valid @RequestBody UsuarioUpdateDTO usuarioUpdateDTO) {
        log.info("Recebida requisição PUT para atualizar usuário ID {}.", id);
        Usuario usuarioAtualizado = usuarioService.atualizarUsuarioPorId(id, usuarioUpdateDTO);
        log.info("Usuário ID {} atualizado com sucesso.", id);
        return ResponseEntity.ok(usuarioAtualizado);
    }
}
