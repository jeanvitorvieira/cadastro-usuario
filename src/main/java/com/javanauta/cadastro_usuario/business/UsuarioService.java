package com.javanauta.cadastro_usuario.business;

import com.javanauta.cadastro_usuario.dto.UsuarioUpdateDTO;
import com.javanauta.cadastro_usuario.enums.Cargo;
import com.javanauta.cadastro_usuario.exceptions.EmailAlreadyRegisteredException;
import com.javanauta.cadastro_usuario.exceptions.ResourceNotFoundException;
import com.javanauta.cadastro_usuario.infrastructure.entities.Usuario;
import com.javanauta.cadastro_usuario.infrastructure.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario salvarUsuario(Usuario usuario) {
        log.info("Tentando salvar usuário com e-mail: {}", usuario.getEmail());
        if (repository.findByEmail(usuario.getEmail()).isPresent()) {
            log.warn("E-mail já cadastrado: {}", usuario.getEmail());
            throw new EmailAlreadyRegisteredException("O e-mail '" + usuario.getEmail() + "' já está cadastrado.");
        }

        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

        if (usuario.getCargo() == null) {
            usuario.setCargo(Cargo.USUARIO);
        }

        log.info("Usuário salvo com sucesso. ID: {}", usuario.getId());
        return repository.saveAndFlush(usuario);
    }

    public List<Usuario> buscarTodosUsuarios() {
        log.info("Buscando usuários.");
        return repository.findAll();
    }

    public Usuario buscarUsuarioPorId(Integer id) {
        log.info("Buscando usuário. ID: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário com ID " + id + " não encontrado."));
    }

    public Usuario buscarUsuarioPorEmail(String email) {
        log.info("Buscando usuário. E-mail: {}", email);
        return repository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("Usuário com e-mail '" + email + "' não encontrado.")
        );
    }

    public void deletarUsuarioPorEmail(String email) {
        log.info("Buscando usuário. E-mail: {}", email);
        Usuario usuario = repository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("Usuário com e-mail '" + email + "' não encontrado para deleção.")
        );

        log.info("Usuário deletado com sucesso. ID: {}", usuario.getId());
        repository.delete(usuario);
    }

    public Usuario atualizarUsuarioPorId(Integer id, UsuarioUpdateDTO usuarioUpdateDTO) {
        log.info("Buscando usuário. ID: {}", id);
        Usuario usuarioEntity = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Usuário com ID " + id + " não encontrado para edição."));

        if (usuarioUpdateDTO.getNome() != null) {
            log.info("Alterando nome do usuário.");
            usuarioEntity.setNome(usuarioUpdateDTO.getNome());
        }

        if (usuarioUpdateDTO.getEmail() != null && !usuarioUpdateDTO.getEmail().equals(usuarioEntity.getEmail())) {
            log.info("Alterando e-mail do usuário.");
            if (repository.findByEmail(usuarioUpdateDTO.getEmail()).isPresent()) {
                log.warn("E-mail já cadastrado: {}", usuarioUpdateDTO.getEmail());
                throw new EmailAlreadyRegisteredException("O novo e-mail '" + usuarioUpdateDTO.getEmail() + "' já está cadastrado para outro usuário.");
            }
            usuarioEntity.setEmail(usuarioUpdateDTO.getEmail());
        }

        log.info("Usuário alterado com sucesso. ID: {}", usuarioEntity.getId());
        return repository.saveAndFlush(usuarioEntity);
    }
}
