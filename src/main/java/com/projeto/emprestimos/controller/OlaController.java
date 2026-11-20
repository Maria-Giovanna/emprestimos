package com.projeto.emprestimos.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

//Anotação
@RestController
public class OlaController {

    //ele liga um endereço a um metodo: quer dizer q quando derem get no endereco ola, vai rodar o metodo abaixo
    @GetMapping("/ola")
    public String dizendoOla(){
        return "Oiiee!! Minha primeira API de emprestimos está no ar";
    }
}
