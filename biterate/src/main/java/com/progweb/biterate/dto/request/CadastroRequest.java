package com.progweb.biterate.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

// Cadastro de novo usuário
@Data
public class CadastroRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 80)
    private String nome;

    @NotBlank(message = "Sobrenome é obrigatório")
    @Size(min = 2, max = 80)
    private String sobrenome;

    @NotBlank(message = "Username é obrigatório")
    @Size(min = 3, max = 50)
    @Pattern(regexp = "^[a-zA-Z0-9_.]+$", message = "Username aceita apenas letras, números, _ e .")
    private String username;

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
    private String senha;

    @NotBlank(message = "Confirmação de senha é obrigatória")
    private String confirmarSenha;
}
