package com.projeto.emprestimos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Colaborador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome é obrigatório!")
    @Column(nullable = false)
    private String nome;

    @NotBlank(message = "O e-mail é obrigatório!")
    @Email(message = "E-mail inválido.")
    @Column(nullable = false, unique = true)
    private String email;

    private String cargo;

    @JsonIgnore
    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Perfil perfil;

    //CONSTRUTOR
    public Colaborador() {
    }

    //GETTER E SETTER
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getCargo() {
        return cargo;
    }
    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getSenha(){return senha;}
    public void setSenha(String senha){this.senha = senha;}

    public Perfil getPerfil(){return perfil;}
    public void setPerfil(Perfil perfil){this.perfil = perfil;}
}
