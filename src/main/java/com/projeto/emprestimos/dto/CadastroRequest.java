package com.projeto.emprestimos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

// o que a própria pessoa manda na tela "Criar conta".
// Não tem "perfil" de propósito: quem se cadastra sozinho é sempre COLABORADOR,
// senão qualquer pessoa poderia se cadastrar como ADMIN
public record CadastroRequest(
        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "E-mail inválido")
        String email,

        String cargo,

        @NotBlank(message = "A senha é obrigatória")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
                message = "A senha precisa ter 8 caracteres ou mais, com letra maiúscula, letra minúscula e número"
        )
        String senha
) {}
