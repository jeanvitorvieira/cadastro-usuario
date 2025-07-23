package com.javanauta.cadastro_usuario.business;

import com.javanauta.cadastro_usuario.enums.Cargo;
import com.javanauta.cadastro_usuario.exceptions.EmailAlreadyRegisteredException;
import com.javanauta.cadastro_usuario.exceptions.ResourceNotFoundException;
import com.javanauta.cadastro_usuario.infrastructure.entities.Usuario;
import com.javanauta.cadastro_usuario.infrastructure.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test; 
import org.junit.jupiter.api.extension.ExtendWith; 
import org.mockito.InjectMocks; 
import org.mockito.Mock; 
import org.mockito.junit.jupiter.MockitoExtension; 
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional; 

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;
    private Usuario adminUsuario;

    @BeforeEach
    void setup() {
        usuario = Usuario.builder()
                .id(1)
                .nome("Usuario Comum")
                .email("comum@test.com")
                .senha("senhaComum123!")
                .cargo(Cargo.USUARIO)
                .build();

        adminUsuario = Usuario.builder()
                .id(2)
                .nome("Usuario Admin")
                .email("admin@test.com")
                .senha("senhaAdmin123!")
                .cargo(Cargo.ADMINISTRADOR)
                .build();
    }

    @Test
    @DisplayName("Deve salvar um usuário com sucesso quando o e-mail não existe")
    void salvarUsuario_Success() {
        when(repository.findByEmail(usuario.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(usuario.getSenha())).thenReturn("senhaComum123!");
        when(repository.saveAndFlush(any(Usuario.class))).thenReturn(usuario);

        Usuario savedUser = usuarioService.salvarUsuario(usuario);

        assertNotNull(savedUser);
        assertEquals(usuario.getEmail(), savedUser.getEmail());
        assertEquals("senhaComum123!", savedUser.getSenha());
        assertEquals(Cargo.USUARIO, savedUser.getCargo());

        verify(repository, times(1)).findByEmail(usuario.getEmail());
        verify(passwordEncoder, times(1)).encode(usuario.getSenha());
        verify(repository, times(1)).saveAndFlush(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar EmailAlreadyRegisteredException quando o e-mail ja existe")
    void salvarUsuario_EmailAlreadyExists_ThrowsException() {
        when(repository.findByEmail(usuario.getEmail())).thenReturn(Optional.of(usuario));

        assertThrows(EmailAlreadyRegisteredException.class, () -> usuarioService.salvarUsuario(usuario));

        verify(repository, never()).saveAndFlush(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve buscar um usuário por e-mail com sucesso")
    void buscarUsuarioPorEmail_Success() {
        when(repository.findByEmail(usuario.getEmail())).thenReturn(Optional.of(usuario));

        Usuario foundUser = usuarioService.buscarUsuarioPorEmail(usuario.getEmail());

        assertNotNull(foundUser);
        assertEquals(usuario.getEmail(), foundUser.getEmail());

        verify(repository, times(1)).findByEmail(usuario.getEmail());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando o e-mail não for encontrado")
    void buscarUsuarioPorEmail_NotFound_ThrowsException() {
        when(repository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> usuarioService.buscarUsuarioPorEmail("naoexiste@exemplo.com"));

        verify(repository, times(1)).findByEmail(anyString());
    }

    @Test
    @DisplayName("Deve deletar um usuário por e-mail com sucesso")
    void deletarUsuarioPorEmail_Success() {
        when(repository.findByEmail(usuario.getEmail())).thenReturn(Optional.of(usuario));
        doNothing().when(repository).delete(any(Usuario.class));

        usuarioService.deletarUsuarioPorEmail(usuario.getEmail());

        verify(repository, times(1)).findByEmail(usuario.getEmail());
        verify(repository, times(1)).delete(usuario);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao tentar deletar usuário por e-mail não encontrado")
    void deletarUsuarioPorEmail_NotFound_ThrowsException() {
        when(repository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> usuarioService.deletarUsuarioPorEmail("naoexiste@exemplo.com"));

        verify(repository, never()).delete(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve atualizar o nome e e-mail de um usuário com sucesso")
    void atualizarUsuarioPorId_Success() {
        com.javanauta.cadastro_usuario.dto.UsuarioUpdateDTO updateDTO = com.javanauta.cadastro_usuario.dto.UsuarioUpdateDTO.builder()
                .nome("Nome Atualizado")
                .email("atualizado@exemplo.com")
                .build();

        when(repository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
        when(repository.findByEmail(updateDTO.getEmail())).thenReturn(Optional.empty());
        when(repository.saveAndFlush(any(Usuario.class))).thenReturn(usuario);

        Usuario updatedUser = usuarioService.atualizarUsuarioPorId(usuario.getId(), updateDTO);

        assertNotNull(updatedUser);
        assertEquals(updateDTO.getNome(), updatedUser.getNome());
        assertEquals(updateDTO.getEmail(), updatedUser.getEmail());
        assertEquals(usuario.getSenha(), updatedUser.getSenha());
        assertEquals(usuario.getCargo(), updatedUser.getCargo());

        verify(repository, times(1)).findById(usuario.getId());
        verify(repository, times(1)).findByEmail(updateDTO.getEmail());
        verify(repository, times(1)).saveAndFlush(usuario);
    }

    @Test
    @DisplayName("Deve lançar EmailAlreadyRegisteredException ao tentar atualizar com e-mail ja existente")
    void atualizarUsuarioPorId_EmailAlreadyExists_ThrowsException() {
        com.javanauta.cadastro_usuario.dto.UsuarioUpdateDTO updateDTO = com.javanauta.cadastro_usuario.dto.UsuarioUpdateDTO.builder()
                .email("existente@exemplo.com")
                .build();

        Usuario anotherUser = Usuario.builder().id(2).email("existente@exemplo.com").build();

        when(repository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
        when(repository.findByEmail(updateDTO.getEmail())).thenReturn(Optional.of(anotherUser));

        assertThrows(EmailAlreadyRegisteredException.class, () -> usuarioService.atualizarUsuarioPorId(usuario.getId(), updateDTO));

        verify(repository, never()).saveAndFlush(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao tentar atualizar usuário não encontrado")
    void atualizarUsuarioPorId_NotFound_ThrowsException() {
        com.javanauta.cadastro_usuario.dto.UsuarioUpdateDTO updateDTO = com.javanauta.cadastro_usuario.dto.UsuarioUpdateDTO.builder()
                .nome("Outro Nome")
                .build();

        when(repository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> usuarioService.atualizarUsuarioPorId(999, updateDTO));

        verify(repository, never()).saveAndFlush(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve buscar todos os usuários com sucesso")
    void buscarTodosUsuarios_Success() {
        List<Usuario> usuarios = List.of(usuario, adminUsuario);

        when(repository.findAll()).thenReturn(usuarios);

        List<Usuario> usuariosEncontrados = usuarioService.buscarTodosUsuarios();

        assertNotNull(usuariosEncontrados);
        assertEquals(2, usuariosEncontrados.size());
        assertEquals(usuarios, usuariosEncontrados);

        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar um usuário por ID com sucesso")
    void buscarUsuarioPorId_Success() {
        when(repository.findById(usuario.getId())).thenReturn(Optional.of(usuario));

        Usuario foundUser = usuarioService.buscarUsuarioPorId(usuario.getId());

        assertNotNull(foundUser);
        assertEquals(usuario.getId(), foundUser.getId());

        verify(repository, times(1)).findById(usuario.getId());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando o ID não for encontrado")
    void buscarUsuarioPorId_NotFound_ThrowsException() {
        when(repository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> usuarioService.buscarUsuarioPorId(999));

        verify(repository, times(1)).findById(anyInt());
    }
}
