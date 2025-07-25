package com.javanauta.cadastro_usuario.infrastructure.entities;

import com.javanauta.cadastro_usuario.enums.Cargo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "usuario")
@Entity
@Schema(description = "Representa um usuário no sistema")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Schema(description = "ID único do usuário", example = "1")
    private Integer id;

    @Column(name = "email", unique = true)
    @NotBlank(message = "O e-mail não pode estar em branco.")
    @Email(message = "Formato de e-mail inválido.")
    @Schema(description = "E-mail único do usuário", example = "jeanvitorv0@gmail.com")
    private String email;

    @Column(name = "nome")
    @NotBlank(message = "O nome não pode estar em branco.")
    @Schema(description = "Nome completo do usuário", example = "Jean Vitor Vieira")
    private String nome;

    @Column(name = "senha")
    @NotBlank(message = "A senha não pode estar em branco.")
    @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres.")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
            message = "A senha deve conter pelo menos uma letra maiúscula, uma minúscula, um número, um caractere especial e não ter espaços em branco.")
    @Schema(description = "Senha do usuário (minímo 8 caracteres, com maiúscula, minúscula, número e especial)", example = "MinhaSenha123!")
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(name = "cargo")
    @Schema(description = "Papel de acesso do usuário (USUARIO ou ADMINISTRADOR)", example = "USUARIO")
    private Cargo cargo;

}
