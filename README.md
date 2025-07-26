# CRUD Java com Spring Boot

Este projeto é uma aplicação RESTful completa para cadastro e gerenciamento de usuários, desenvolvida em Java com o framework Spring Boot. Ele demonstra a implementação de um CRUD robusto, incluindo segurança, validação de dados, tratamento de erros e documentação de API, seguindo as melhores práticas de desenvolvimento back-end.
## 🔧 Tecnologias Utilizadas:

* **Java 24 (JDK 24):** Plataforma Java.
* **Spring Boot:** Framework para desenvolvimento de aplicações Java autônomas.
* **H2 Database:** Banco de dados em memória para desenvolvimento e testes.
* **Spring Data JPA:** Facilita a implementação de repositórios para acesso a dados.
* **Lombok:** Biblioteca para redução de código boilerplate em classes Java.
* **Spring Security:** Framework de segurança robusto para autenticação (login) e autorização (controle de acesso por papéis).
* **Spring Boot Starter Validation:** Para validação de dados de entrada usando anotações.
* **Springdoc OpenAPI UI:** Para geração automática de documentação interativa da API (Swagger UI).
* **JUnit 5 & Mockito:** Frameworks para escrita de testes unitários e de integração.
* **Spring Security Test:** Para facilitar os testes de integração com segurança.

## 📚 Funcionalidades e Conceitos Implementados:

Ao explorar este projeto, você encontrará e aprenderá sobre:

* **CRUD Completo:** Implementação das operações fundamentais (Create, Read, Update, Delete) para gerenciar dados de usuários.

* **Persistência de Dados:** Configuração e uso do H2 Database e Spring Data JPA.

* **Segurança (Spring Security):**
  - **Hash de Senhas (BCrypt):** Armazenamento seguro de senhas no banco de dados.

  - **Autenticação via Banco de Dados:** Uso de `UserDetailsService` (`UserDetailsServiceImpl`) e `CustomUserDetails` para autenticar usuários com base em dados persistidos.

  - **Autorização por Papéis (`Enum Cargo` e `@PreAuthorize`):** Controle de acesso granular a endpoints e métodos específicos com base em papéis (`USUARIO`, `ADMINISTRADOR`).

    - `GET`s (listar e buscar) são públicos.

    - `POST` (cadastro) é público.

    - `PUT` (atualização) é permitido a `ADMINISTRADOR` ou ao próprio usuário.

    - `DELETE` é restrito a `ADMINISTRADOR` ou ao próprio usuário.

  - **Liberação de Endpoints:** Configuração de acesso público para o H2 Console e para a interface do Swagger UI.

* **Validação de Entrada de Dados (Bean Validation):**

  - Uso de anotações como `@NotBlank`, `@Email`, `@Size` e `@Pattern` diretamente na entidade `Usuario`.

  - Criação de um **DTO** (`UsuarioUpdateDTO`) específico para atualizações parciais, evitando validações desnecessárias.

* **Tratamento de Erros Personalizado:**

  - **Exceções Customizadas:** `ResourceNotFoundException` e `EmailAlreadyRegisteredException` para erros de negócio específicos.

  - **Global Exception Handler (`@ControllerAdvice`):** Classe `RestExceptionHandler` para centralizar e padronizar as respostas de erro da API.

  - **Tratamento de Erros de Validação e Tipo:** Lida com `MethodArgumentNotValidException`, `HttpMessageNotReadableException` e `MethodArgumentTypeMismatchException` para retornar 400 `Bad Request` com mensagens claras.

* **Logging:**

  - Configuração básica de logs informativos, de debug e de erro em pontos estratégicos da aplicação para monitoramento e depuração.

* **Testes Automatizados:**

  - **Testes Unitários:** Para a lógica de negócio do `UsuarioService` (com JUnit 5 e Mockito).

  - **Testes de Integração:** Para a camada do `UsuarioController` (com MockMvc e `@WithUserDetails`), cobrindo cenários de CRUD, validação e autorização.

* **Documentação Interativa da API (Swagger UI):**

  - Geração automática de uma interface web interativa com todos os endpoints, seus detalhes, e a possibilidade de testá-los diretamente no navegador.

## 🚀 Começando

Para executar localmente:

1.  **Clone o repositório:**

    ```bash
    git clone https://github.com/jeanvitorvieira/cadastro-usuario.git
    ```
2.  **Navegue até o diretório do projeto:**
   
    ```bash
    cd cadastro-usuario
    ```
3.  **Compile e execute a aplicação com Maven:**
   
    ```bash
    mvn spring-boot:run
    ```

A aplicação estará disponível em `http://localhost:8081`.

 * **Console H2 Database:** Acessível em `http://localhost:8081/h2-console` (sem autenticação em ambiente de desenvolvimento).

 * **Documentação da API (Swagger UI):** Acessível em `http://localhost:8081/swagger-ui.html`.

## ✨ Exemplos de Uso da API
Utilize ferramentas como o Postman ou Insomnia para interagir com os endpoints da API, ou explore diretamente pelo Swagger UI.

**Exemplo de POST para criar um usuário (acesso público):**

`POST /usuario`

Corpo:

```
{
    "nome": "Novo Usuario",
    "email": "novo.usuario@example.com",
    "senha": "SenhaSegura123!",
    "cargo": "USUARIO"
}
```

**Exemplo de GET para listar todos os usuários (acesso público):**

`GET /usuario`

**Exemplo de PUT para atualizar o próprio usuário (requer autenticação Basic Auth):**

`PUT /usuario/{id_do_seu_usuario}`

Autenticação: Basic Auth (com e-mail e senha do **próprio** usuário a ser atualizado)

Corpo:

```
{
    "nome": "Meu Nome Atualizado"
}
```

**Exemplo de DELETE para deletar o próprio usuário (requer autenticação Basic Auth):**

`DELETE /usuario/email?email={seu_email}`

Autenticação: Basic Auth (com e-mail e senha do **próprio** usuário a ser deletado)

---

## 👨‍💻 Autor
### Jean Vitor Vieira
* Linkedin:
https://www.linkedin.com/in/jean-vitor-vieira-505203197/

* E-mail:
jeanvitorv0@gmail.com
