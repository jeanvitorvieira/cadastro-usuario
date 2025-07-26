package com.javanauta.cadastro_usuario.controller;

import com.javanauta.cadastro_usuario.business.UsuarioService;
import com.javanauta.cadastro_usuario.dto.UsuarioUpdateDTO;
import com.javanauta.cadastro_usuario.infrastructure.entities.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Usuário", description = "Operações relacionadas a usuários")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    @Operation(summary = "Cria um novo usuário",
            description = "Permite o cadastro de um novo usuário no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso",
                    content = @Content(schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "409", description = "E-mail ja cadastrado",
                    content = @Content(schema = @Schema(implementation = Object.class)))
    })
    public ResponseEntity<Usuario> salvarUsuario(@Valid @RequestBody Usuario usuario) {
        log.info("Recebida requisição POST para salvar usuário: {}", usuario.getEmail());
        Usuario novoUsuario = usuarioService.salvarUsuario(usuario);
        log.info("Usuário salvo com sucesso: ID {}", novoUsuario.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
    }

    @GetMapping
    @Operation(summary = "Lista todos os usuários",
            description = "Retorna uma lista de todos os usuários cadastrados no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = Usuario.class)))
    })
    public ResponseEntity<List<Usuario>> buscarTodosUsuarios() {
        log.info("Recebida requisição GET para buscar todos os usuários.");
        List<Usuario> usuarios = usuarioService.buscarTodosUsuarios();
        log.info("Retornando {} usuários.", usuarios.size());
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um usuário por ID",
            description = "Retorna os detalhes de um usuário específico pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso",
                    content = @Content(schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "400", description = "ID inválido",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                    content = @Content(schema = @Schema(implementation = Object.class)))
    })
    public ResponseEntity<Usuario> buscarUsuarioPorId(@PathVariable Integer id) {
        log.info("Recebida requisição GET para buscar usuário por ID: {}", id);
        Usuario usuario = usuarioService.buscarUsuarioPorId(id);
        log.info("Usuário encontrado por ID {}: {}", id, usuario.getEmail());
        return ResponseEntity.ok(usuario);
    }

    @GetMapping("/email")
    @Operation(summary = "Busca um usuário por e-mail",
            description = "Retorna os detalhes de um usuário específico pelo seu e-mail.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso",
                    content = @Content(schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                    content = @Content(schema = @Schema(implementation = Object.class)))
    })
    public ResponseEntity<Usuario> buscarUsuarioPorEmail(@RequestParam String email) {
        log.info("Recebida requisição GET para buscar usuário por e-mail: {}", email);
        Usuario usuario = usuarioService.buscarUsuarioPorEmail(email);
        log.info("Usuário encontrado por e-mail {}: {}", email, usuario.getId());
        return ResponseEntity.ok(usuario);
    }

    @DeleteMapping("/email")
    @Operation(summary = "Deleta um usuário por e-mail",
            description = "Deleta um usuário do sistema pelo seu e-mail. Requer papel ADMINISTRADOR.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                    content = @Content(schema = @Schema(implementation = Object.class)))
    })
    @PreAuthorize("hasRole('ADMINISTRADOR') or authentication.principal.username == #email")
    public ResponseEntity<Void> deletarUsuarioPorEmail(@RequestParam String email) {
        log.warn("Recebida requisição DELETE para deletar usuário por e-mail (requer ADMIN): {}", email);
        usuarioService.deletarUsuarioPorEmail(email);
        log.info("Usuário com e-mail {} deletado com sucesso.", email);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um usuário por ID",
            description = "Atualiza os dados de um usuário existente. Requer papel ADMINISTRADOR ou ser o proprio usuário.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "409", description = "E-mail já cadastrado",
                    content = @Content(schema = @Schema(implementation = Object.class)))
    })
    @PreAuthorize("hasRole('ADMINISTRADOR') or #id == authentication.principal.id")
    public ResponseEntity<Usuario> atualizarUsuarioPorId(@PathVariable Integer id, @Valid @RequestBody UsuarioUpdateDTO usuarioUpdateDTO) {
        log.info("Recebida requisição PUT para atualizar usuário ID {}.", id);
        Usuario usuarioAtualizado = usuarioService.atualizarUsuarioPorId(id, usuarioUpdateDTO);
        log.info("Usuário ID {} atualizado com sucesso.", id);
        return ResponseEntity.ok(usuarioAtualizado);
    }
}
