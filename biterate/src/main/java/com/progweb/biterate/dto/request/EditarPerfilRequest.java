package com.progweb.biterate.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
 
@Data
public class EditarPerfilRequest {
 
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
 
    @Size(max = 300)
    private String bio;
 
    @Size(max = 100)
    private String cidade;
}