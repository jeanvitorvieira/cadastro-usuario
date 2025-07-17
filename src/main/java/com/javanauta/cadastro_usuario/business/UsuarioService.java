package com.javanauta.cadastro_usuario.business;

import com.javanauta.cadastro_usuario.exceptions.EmailAlreadyRegisteredException;
import com.javanauta.cadastro_usuario.exceptions.ResourceNotFoundException;
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
        if (repository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new EmailAlreadyRegisteredException("O e-mail '" + usuario.getEmail() + "' já está cadastrado.");
        }

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        repository.saveAndFlush(usuario);
    }

    public Usuario buscarUsuarioPorEmail(String email) {
        return repository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("Usuário com e-mail '" + email + "' não encontrado.")
        );
    }

    public void deletarUsuarioPorEmail(String email) {
        Usuario usuario = repository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("Usuário com e-mail '" + email + "' não encontrado para deleção.")
        );

        repository.delete(usuario);
    }

    public void atualizarUsuarioPorId(Integer id, Usuario usuario) {
        Usuario usuarioEntity = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Usuário com ID " + id + " não encontrado para edição."));

        if (usuario.getEmail() != null && !usuario.getEmail().equals(usuarioEntity.getEmail())) {
            if (repository.findByEmail(usuario.getEmail()).isPresent()) {
                throw new EmailAlreadyRegisteredException("O novo e-mail '" + usuario.getEmail() + "' já está cadastrado para outro usuário.");
            }
        }

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
