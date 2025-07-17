package com.javanauta.cadastro_usuario.business;

import com.javanauta.cadastro_usuario.infrastructure.entities.Usuario;
import com.javanauta.cadastro_usuario.infrastructure.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public void salvarUsuario(Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        repository.saveAndFlush(usuario);
    }

    public Usuario buscarUsuarioPorEmail(String email) {
        return repository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("E-mail não encontrado.")
        );
    }

    public void deletarUsuarioPorEmail(String email) {
        repository.deleteByEmail(email);
    }

    public void atualizarUsuarioPorId(Integer id, Usuario usuario) {
        Usuario usuarioEntity = repository.findById(id).orElseThrow(
                () -> new RuntimeException("Usuário não encontrado."));
        Usuario usuarioAtualizado = Usuario
                .builder()
                .email(usuario.getEmail() != null ?
                        usuario.getEmail() :
                        usuarioEntity.getEmail())
                .nome(usuario.getNome() != null ?
                        usuario.getNome() :
                        usuarioEntity.getNome())
                .id(usuarioEntity.getId())
                .password(usuarioEntity.getPassword())
                .build();

        repository.saveAndFlush(usuarioAtualizado);
    }
}
