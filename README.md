# Sistema de Empréstimo de Equipamentos

API REST para uma empresa que empresta equipamentos (notebooks, monitores, headsets, celulares...) para as pessoas colaboradoras. O sistema guarda **o que está com quem, até quando, e quem está atrasado**.

Projeto 2 do bootcamp **#ElasTech** (PagBank), módulo de Spring Boot.

## 🌐 API no ar

**https://emprestimos-api-magi.onrender.com**

> No plano gratuito do Render, o serviço "dorme" depois de 15 minutos sem uso. O primeiro pedido depois disso pode levar até 1 minuto; os seguintes são rápidos. O banco H2 do Render começa vazio a cada nova publicação.
>
> Abrir só o endereço, sem rota, dá 404: é normal, não existe rota `/`. Use, por exemplo, `/equipamentos`.

## 🎥 Vídeo

>[Link do vídeo de apresentação](COLE_AQUI_O_LINK_DO_VIDEO)

>[Link do site funcional](https://acervo-front-elastech.netlify.app/index.html)

## 🛠️ Tecnologias

- Java 25
- Spring Boot 4 (Spring Web MVC, Spring Data JPA, Validation, DevTools)
- Banco H2 (em arquivo)
- JUnit 5
- Docker + Render (deploy)
- Postman (testes manuais)

## ▶️ Como rodar

**Pré-requisitos:** Java 25 ou mais novo e uma IDE (IntelliJ IDEA).

1. Clone o repositório:
   ```bash
   git clone https://github.com/Maria-Giovanna/emprestimos.git
   ```
2. Abra a pasta do projeto no IntelliJ e espere o Maven baixar as dependências.
3. Rode a classe `EmprestimosApplication` (ou, no terminal, `./mvnw spring-boot:run`).
4. A API sobe em `http://localhost:8080`.

**Console do banco (só no computador):** `http://localhost:8080/h2-console`, com JDBC URL `jdbc:h2:file:./dados/emprestimos`, usuário e senha vazios.

**Testes unitários:** botão direito em `src/test/java` → *Run 'All Tests'* (ou `./mvnw test`).

## 📍 Rotas

### Equipamentos

| Método | Rota | O que faz | Sucesso |
|---|---|---|---|
| POST | `/equipamentos` | Cadastra | 201 |
| GET | `/equipamentos` | Lista todos (filtros opcionais: `?tipo=NOTEBOOK`, `?disponivel=true`) | 200 |
| GET | `/equipamentos/{id}` | Busca um | 200 |
| PUT | `/equipamentos/{id}` | Atualiza nome e tipo | 200 |
| DELETE | `/equipamentos/{id}` | Remove (se nunca foi emprestado) | 204 |

Tipos aceitos: `NOTEBOOK`, `MONITOR`, `HEADSET`, `TECLADO`, `MOUSE`, `CELULAR`, `MESA_DIGITALIZADORA`, `OUTRO`.

```json
{ "patrimonio": "NB-001", "nome": "Notebook Dell", "tipo": "NOTEBOOK" }
```

### Colaboradores

| Método | Rota | O que faz | Sucesso |
|---|---|---|---|
| POST | `/colaboradores` | Cadastra | 201 |
| GET | `/colaboradores` | Lista | 200 |
| GET | `/colaboradores/{id}` | Busca uma pessoa | 200 |
| GET | `/colaboradores/{id}/emprestimos` | Histórico de empréstimos da pessoa | 200 |

```json
{ "nome": "Ana Souza", "email": "ana@empresa.com", "cargo": "Analista" }
```

### Empréstimos

| Método | Rota | O que faz | Sucesso |
|---|---|---|---|
| POST | `/emprestimos` | Empresta | 201 |
| GET | `/emprestimos` | Lista todos (com `?atrasados=true`, só os atrasados) | 200 |
| GET | `/emprestimos/{id}` | Busca um empréstimo | 200 |
| PUT | `/emprestimos/{id}/devolucao` | Registra a devolução | 200 |

```json
{ "equipamentoId": 1, "colaboradorId": 1 }
```

> **Diferença em relação ao enunciado:** no `POST /emprestimos`, os ids vão no **corpo da requisição, em JSON** (um DTO `record EmprestimoRequest`, como na aula de 17/11), e não na URL com `@RequestParam`. A professora deixou essa escolha livre. As datas quem decide é o sistema: quem pede manda só "qual equipamento" e "para quem".

Cada empréstimo volta com o campo calculado `"atrasado"` (e `"diasAtrasados"`), que não é coluna no banco.

## 📏 Regras de negócio

| Regra | O que diz | Onde está |
|---|---|---|
| R1 | Não existem dois equipamentos com o mesmo patrimônio | `EquipamentoService` |
| R2 | Todo equipamento novo nasce disponível, mesmo que o JSON mande `false` | `EquipamentoService` |
| R3 | Não existem duas pessoas com o mesmo e-mail | `ColaboradorService` |
| R4 | Só se empresta equipamento disponível | `Equipamento.emprestar()` |
| R5 | Ao emprestar: equipamento indisponível, empréstimo hoje, devolução prevista em 14 dias | Construtor do `Emprestimo` + `EmprestimoService` |
| R6 | Empréstimo já devolvido não pode ser devolvido de novo | `Emprestimo.devolver()` |
| R7 | Ao devolver: data de hoje e equipamento disponível de novo | `Emprestimo` + `Equipamento` + `EmprestimoService` |
| R8 | Atrasado = não devolvido e prazo vencido (no dia do prazo ainda não está) | `Emprestimo.estaAtrasadoEm()` + repository |
| R9 | Equipamento que já teve empréstimo não pode ser removido | `EquipamentoService` |
| R10 | Quem tem empréstimo atrasado não pode pegar outro | `EmprestimoService` |

As regras que só dependem do próprio objeto ficam na entidade (assim nenhum service esquece delas e dá para testar sem banco). As que precisam consultar o banco ficam no service. `emprestar` e `devolver` usam `@Transactional`.

## ⚠️ Erros

Todos os erros voltam no mesmo formato:

```json
{ "message": "Equipamento indisponível" }
```

| Situação | Status |
|---|---|
| Equipamento, colaborador ou empréstimo que não existe | 404 |
| Patrimônio ou e-mail repetido (R1, R3) | 409 |
| Operação que conflita com o estado atual (R4, R6, R9, R10) | 409 |
| Dados inválidos (campo obrigatório vazio, e-mail inválido) | 400 |

## ✅ Testes

- **Unitários (JUnit 5, sem banco):** `EquipamentoTest` (3 casos) e `EmprestimoTest` (7 casos), cobrindo as regras R4 a R8, todos verdes.
- **Manuais (Postman):** os 17 passos do plano de testes do enunciado, no `localhost` e no Render.

## ⭐ Bônus feitos

**Nível 2**
- **Validação:** `@NotBlank`, `@NotNull` e `@Email` nas entidades e no DTO, com `@Valid` em todo `@RequestBody` → 400 com mensagem clara.
- **Tratamento de erros:** exceções próprias (`NaoEncontradoException`, `ConflitoException`) e um `@RestControllerAdvice` (`TratadorDeErros`) que devolve 404, 409 e 400.
- **Relacionamento com `@ManyToOne`:** o `Emprestimo` guarda o `Equipamento` e o `Colaborador` (chaves estrangeiras de verdade no banco), e o JSON traz os dois inteiros.

**Extras**
- DTO de entrada com `record` (`EmprestimoRequest`).
- Tipos `CELULAR` e `MESA_DIGITALIZADORA` no enum.
- Campo calculado `diasAtrasados` no empréstimo.

## Autora
<p>
  <img src="docs/Union.png" alt="Logo da Maria Giovanna" width="56" align="left" hspace="8">
  <strong>Maria Giovanna</strong><br>
  <a href="https://github.com/Maria-Giovanna">GitHub</a>
</p>
<br clear="left">
