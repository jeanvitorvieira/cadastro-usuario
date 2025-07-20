package com.javanauta.cadastro_usuario.security.details;

import com.javanauta.cadastro_usuario.infrastructure.entities.Usuario;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    @Getter
    private final Integer id;
    private final String email;
    private final String senha;
    private final Collection<? extends GrantedAuthority> cargos;

    public CustomUserDetails(Usuario usuario) {
        this.id = usuario.getId();
        this.email = usuario.getEmail();
        this.senha = usuario.getSenha();
        // Converte o papel do seu Usuario para uma lista de GrantedAuthority
        this.cargos = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + usuario.getCargo().name()));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return cargos;
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
