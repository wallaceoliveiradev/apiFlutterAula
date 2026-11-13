# API de Controle de EPIs

Backend do sistema educacional de Controle de EPIs. A API permite que o aplicativo consulte e gerencie informações de funcionários, equipamentos, empréstimos e treinamentos.

## Principais recursos

- Login de usuários por matrícula e senha.
- Consulta dos indicadores do painel administrativo.
- Listagem de EPIs e funcionários.
- Solicitação, aprovação e devolução de empréstimos.
- Consulta, cadastro e conclusão de treinamentos.
- Acesso a imagens de EPIs e materiais de treinamento em PDF.

## Tecnologias

- Java 17
- Spring Boot
- Spring Data JPA
- MySQL
- Maven

## Requisitos

- Java 17 ou superior.
- MySQL em execução.
- Banco de dados chamado `epi_db`.

## Configuração do banco

As configurações estão em `src/main/resources/application.properties`. Confira e ajuste usuário, senha e endereço do MySQL para o seu ambiente. Por padrão, o projeto usa `localhost:3306`, banco `epi_db`, usuário `root` e senha vazia.

## Como executar

Na pasta do projeto, execute:

```bash
./mvnw spring-boot:run
```

No Windows, use:

```bat
mvnw.cmd spring-boot:run
```

A API inicia na porta `8080`.

## Rotas principais

- `POST /auth/login` — autentica um usuário.
- `GET /dashboard?adminId=1` — retorna os indicadores do administrador.
- `GET /epis` — lista os EPIs.
- `GET /funcionarios?adminId=1` — lista os funcionários (acesso administrativo).
- `/emprestimos` — cria e consulta empréstimos; também permite aprovar e registrar devoluções.
- `/treinamentos` — consulta e cadastra treinamentos; há rotas para registrar conclusão e consultar o histórico.
- `/imagens/` e `/arquivos/` — disponibilizam imagens e materiais de treinamento.

## Estrutura

```text
src/main/java/          # API: controllers, entidades, repositórios e serviços
src/main/resources/     # Configuração, imagens e PDFs
src/test/               # Testes automatizados
pom.xml                 # Dependências e configuração do Maven
```
