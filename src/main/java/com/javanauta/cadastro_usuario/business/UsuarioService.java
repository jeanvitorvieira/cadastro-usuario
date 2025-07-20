package com.javanauta.cadastro_usuario.business;

import com.javanauta.cadastro_usuario.enums.Cargo;
import com.javanauta.cadastro_usuario.exceptions.EmailAlreadyRegisteredException;
import com.javanauta.cadastro_usuario.exceptions.ResourceNotFoundException;
import com.javanauta.cadastro_usuario.infrastructure.entities.Usuario;
import com.javanauta.cadastro_usuario.infrastructure.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario salvarUsuario(Usuario usuario) {
        if (repository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new EmailAlreadyRegisteredException("O e-mail '" + usuario.getEmail() + "' já está cadastrado.");
        }

        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

        if (usuario.getCargo() == null) {
            usuario.setCargo(Cargo.USUARIO);
        }

        repository.saveAndFlush(usuario);
        return usuario;
    }

    public List<Usuario> buscarTodosUsuarios() {
        return repository.findAll();
    }

    public Usuario buscarUsuarioPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário com ID " + id + " não encontrado."));
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

    public Usuario atualizarUsuarioPorId(Integer id, Usuario usuario) {
        Usuario usuarioEntity = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Usuário com ID " + id + " não encontrado para edição."));

        if (usuario.getEmail() != null && !usuario.getEmail().equals(usuarioEntity.getEmail())) {
            if (repository.findByEmail(usuario.getEmail()).isPresent()) {
                throw new EmailAlreadyRegisteredException("O novo e-mail '" + usuario.getEmail() + "' já está cadastrado para outro usuário.");
            }
        }

        Usuario usuarioAtualizado = Usuario
                .builder()
                .id(usuarioEntity.getId())
                .nome(usuario.getNome() != null ?
                        usuario.getNome() :
                        usuarioEntity.getNome())
                .email(usuario.getEmail() != null ?
                        usuario.getEmail() :
                        usuarioEntity.getEmail())
                .senha(usuarioEntity.getSenha())
                .cargo(usuarioEntity.getCargo())
                .build();

        repository.saveAndFlush(usuarioAtualizado);
        return usuarioEntity;
    }
}
