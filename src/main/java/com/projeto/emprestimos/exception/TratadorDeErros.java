package com.projeto.emprestimos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TratadorDeErros {

    @ExceptionHandler(NaoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErroResposta naoEncontrado(NaoEncontradoException nee) {
        return new ErroResposta(nee.getMessage());
    }

    @ExceptionHandler(ConflitoException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErroResposta conflito(ConflitoException ce) {
        return new ErroResposta(ce.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErroResposta validacao(MethodArgumentNotValidException manve) {
        String mensagem = manve.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        return new ErroResposta(mensagem);
    }

    // login com e-mail ou senha errados (slide 54). A mensagem não diz qual dos dois errou, de propósito
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErroResposta loginInvalido(AuthenticationException ae) {
        return new ErroResposta("E-mail ou senha inválidos");
    }

    // logado, mas tentando mexer no que é de outra pessoa
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErroResposta acessoNegado(AccessDeniedException ade) {
        return new ErroResposta(ade.getMessage());
    }
}