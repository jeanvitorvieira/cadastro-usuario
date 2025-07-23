package com.javanauta.cadastro_usuario.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javanauta.cadastro_usuario.enums.Cargo;
import com.javanauta.cadastro_usuario.infrastructure.entities.Usuario;
import com.javanauta.cadastro_usuario.infrastructure.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@SpringBootTest
@AutoConfigureMockMvc
public class UsuarioControllerIntegrationTest {

    private static Usuario usuarioComum;
    private static Usuario usuarioAdmin;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeAll
    static void setupBeforeAll(@Autowired UsuarioRepository usuarioRepository, @Autowired PasswordEncoder passwordEncoder) {

        usuarioRepository.deleteAll();

        usuarioComum = Usuario.builder()
                .nome("Usuario Comum")
                .email("comum@test.com")
                .senha(passwordEncoder.encode("senhaComum123!"))
                .cargo(Cargo.USUARIO)
                .build();
        usuarioRepository.save(usuarioComum);

        usuarioAdmin = Usuario.builder()
                .nome("Usuario Admin")
                .email("admin@test.com")
                .senha(passwordEncoder.encode("senhaAdmin123!"))
                .cargo(Cargo.ADMINISTRADOR)
                .build();
        usuarioRepository.save(usuarioAdmin);
    }

    @Test
    @DisplayName("Deve cadastrar um novo usuário com sucesso e retornar 201 Created")
    void salvarUsuario_Success() throws Exception {
        Usuario novoUsuario = Usuario.builder()
                .nome("Novo Usuario")
                .email("novo@exemplo.com")
                .senha("Senha123!")
                .cargo(Cargo.USUARIO)
                .build();

        mockMvc.perform(post("/usuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novoUsuario)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", is("novo@exemplo.com")));
    }

    @Test
    @DisplayName("Deve retornar 409 Conflict ao tentar cadastrar usuário com email existente")
    void salvarUsuario_EmailAlreadyExists_ReturnsConflict() throws Exception {
        Usuario usuarioDuplicado = Usuario.builder()
                .nome("Duplicado")
                .email(usuarioComum.getEmail())
                .senha("SenhaDuplicada123!")
                .cargo(Cargo.USUARIO)
                .build();

        mockMvc.perform(post("/usuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioDuplicado)))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", is("O e-mail '" + usuarioComum.getEmail() + "' já está cadastrado.")));
    }

    @Test
    @DisplayName("Deve retornar 200 OK e listar todos os usuários")
    void buscarTodosUsuarios_Success() throws Exception {
        mockMvc.perform(get("/usuario")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("Deve retornar 200 OK e buscar usuário por ID")
    void buscarUsuarioPorId_Success() throws Exception {
        mockMvc.perform(get("/usuario/{id}", usuarioComum.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", is(usuarioComum.getEmail())));
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao buscar usuário por ID inexistente")
    void buscarUsuarioPorId_NotFound() throws Exception {
        mockMvc.perform(get("/usuario/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", is("Usuário com ID 999 não encontrado.")));
    }

    @Test
    @DisplayName("Deve retornar 200 OK e buscar usuário por email")
    void buscarUsuarioPorEmail_Success() throws Exception {
        mockMvc.perform(get("/usuario/email")
                        .param("email", usuarioComum.getEmail())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.nome", is(usuarioComum.getNome())));
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao buscar usuário por email inexistente")
    void buscarUsuarioPorEmail_NotFound() throws Exception {
        mockMvc.perform(get("/usuario/email")
                        .param("email", "naoexiste@exemplo.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", is("Usuário com e-mail 'naoexiste@exemplo.com' não encontrado.")));
    }

    @Test
    @DisplayName("Usuário comum deve atualizar seu próprio usuário com sucesso e retornar 200 OK")
    @WithUserDetails(value = "comum@test.com", userDetailsServiceBeanName = "userDetailsServiceImpl")
    void atualizarUsuario_ComumUser_OwnUser_Success() throws Exception {
        com.javanauta.cadastro_usuario.dto.UsuarioUpdateDTO updateDTO = com.javanauta.cadastro_usuario.dto.UsuarioUpdateDTO.builder()
                .nome("Nome Atualizado Comum")
                .build();

        mockMvc.perform(put("/usuario/{id}", usuarioComum.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.nome", is("Nome Atualizado Comum")));
    }

    @Test
    @DisplayName("Usuário comum não deve atualizar outro usuário e retornar 403 Forbidden")
    @WithUserDetails(value = "comum@test.com", userDetailsServiceBeanName = "userDetailsServiceImpl")
    void atualizarUsuario_ComumUser_OtherUser_Forbidden() throws Exception {
        com.javanauta.cadastro_usuario.dto.UsuarioUpdateDTO updateDTO = com.javanauta.cadastro_usuario.dto.UsuarioUpdateDTO.builder()
                .nome("Nome Atualizado Outro")
                .build();

        mockMvc.perform(put("/usuario/{id}", usuarioAdmin.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @DisplayName("Admin deve atualizar qualquer usuário com sucesso e retornar 200 OK")
    @WithUserDetails(value = "admin@test.com", userDetailsServiceBeanName = "userDetailsServiceImpl")
    void atualizarUsuario_Admin_AnyUser_Success() throws Exception {
        com.javanauta.cadastro_usuario.dto.UsuarioUpdateDTO updateDTO = com.javanauta.cadastro_usuario.dto.UsuarioUpdateDTO.builder()
                .nome("Nome Atualizado Admin")
                .build();

        mockMvc.perform(put("/usuario/{id}", usuarioComum.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.nome", is("Nome Atualizado Admin")));
    }

    @Test
    @DisplayName("Admin deve deletar usuário com sucesso e retornar 204 No Content")
    @WithUserDetails(value = "admin@test.com", userDetailsServiceBeanName = "userDetailsServiceImpl")
    void deletarUsuario_Admin_Success() throws Exception {
        mockMvc.perform(delete("/usuario/email")
                        .param("email", usuarioComum.getEmail()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        mockMvc.perform(get("/usuario/email")
                        .param("email", usuarioComum.getEmail())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("Usuário comum não deve deletar usuário e retornar 403 Forbidden")
    @WithUserDetails(value = "comum@test.com", userDetailsServiceBeanName = "userDetailsServiceImpl")
    void deletarUsuario_ComumUser_Forbidden() throws Exception {
        mockMvc.perform(delete("/usuario/email")
                        .param("email", usuarioAdmin.getEmail()))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @DisplayName("Usuário não autenticado não deve deletar usuário e retornar 401 Unauthorized")
    void deletarUsuario_Unauthorized() throws Exception {
        mockMvc.perform(delete("/usuario/email")
                        .param("email", usuarioComum.getEmail()))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @DisplayName("Usuário não autenticado não deve atualizar usuário e retornar 401 Unauthorized")
    void atualizarUsuario_Unauthorized() throws Exception {
        com.javanauta.cadastro_usuario.dto.UsuarioUpdateDTO updateDTO = com.javanauta.cadastro_usuario.dto.UsuarioUpdateDTO.builder()
                .nome("Nome Nao Autorizado")
                .build();

        mockMvc.perform(put("/usuario/{id}", usuarioComum.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}