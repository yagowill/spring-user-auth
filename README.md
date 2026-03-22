# 🔐 Spring User Auth API

<div align="center">

![Java](https://img.shields.io/badge/Java-25-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.4-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-JWT-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI_3-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)
![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)

**API REST completa para gerenciamento de usuários com autenticação JWT**

[Tecnologias](#-tecnologias) •
[Começando](#-começando) •
[Endpoints](#-endpoints-da-api) •
[Testes](#-testes) •
[Estrutura](#-estrutura-do-projeto)

</div>

---

## 📋 Sobre o Projeto

Uma API RESTful robusta para gerenciamento de usuários, construída com **Spring Boot 3.4** e **Java 25**. O projeto implementa autenticação stateless via **JWT (JSON Web Tokens)** com **Spring Security**, documentação interativa com **Swagger/OpenAPI**, persistência com **PostgreSQL** e infraestrutura containerizada com **Docker Compose**.

### ✨ Funcionalidades

- 🔑 **Autenticação JWT** — Registro e login com geração de tokens
- 👥 **CRUD de Usuários** — Criar, listar, atualizar e deletar
- 🛡️ **Controle de Acesso** — Roles `ADMIN` e `USER` com permissões granulares
- 📖 **Documentação Swagger** — Interface interativa para testar a API
- 🐘 **PostgreSQL** — Banco de dados relacional robusto
- 🐋 **Docker Compose** — Ambiente completo em um comando
- ✅ **Testes** — Cobertura com JUnit 5 + Mockito
- 🔒 **Senhas Criptografadas** — BCrypt para hashing seguro

---

## 🛠️ Tecnologias

| Tecnologia | Versão | Descrição |
|---|---|---|
| Java | 25 LTS | Linguagem principal |
| Spring Boot | 3.4.4 | Framework backend |
| Spring Security | 6.4 | Autenticação e autorização |
| Spring Data JPA | 3.4 | Persistência de dados |
| PostgreSQL | 16 | Banco de dados |
| jjwt | 0.12.6 | Biblioteca JWT |
| Springdoc OpenAPI | 2.8.6 | Documentação Swagger |
| Lombok | 1.18.44 | Redução de boilerplate |
| Docker Compose | 2.x | Orquestração de containers |
| JUnit 5 | 5.11 | Framework de testes |
| Mockito | 5.x | Mocking para testes |

---

## 🚀 Começando

### Pré-requisitos

- **Java 25+** (ou 21+)
- **Maven 3.9+**
- **Docker** e **Docker Compose**

### 🐋 Rodando com Docker Compose (recomendado)

```bash
# Clone o repositório
git clone https://github.com/yagowill/spring-user-auth.git
cd spring-user-auth

# Crie o arquivo .env baseado no exemplo
cp .env.example .env

# Suba os containers (PostgreSQL + App)
docker-compose up --build
```

A API estará disponível em **http://localhost:8080**

Swagger UI em **http://localhost:8080/swagger-ui.html** 📖

### 💻 Rodando Localmente

```bash
# Clone o repositório
git clone https://github.com/yagowill/spring-user-auth.git
cd spring-user-auth

# Suba apenas o PostgreSQL via Docker
docker-compose up postgres -d

# Rode a aplicação
mvn spring-boot:run
```

### ⚙️ Variáveis de Ambiente

| Variável | Padrão | Descrição |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/userauth_db` | URL do banco |
| `DB_USERNAME` | `postgres` | Usuário do banco |
| `DB_PASSWORD` | `postgres` | Senha do banco |
| `JWT_SECRET` | *(definido no .env)* | Chave secreta JWT (Base64) |
| `JWT_EXPIRATION` | `86400000` | Expiração do token (ms) |

---

## 📡 Endpoints da API

### 🔓 Autenticação (público)

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/auth/register` | Registrar novo usuário |
| `POST` | `/api/auth/login` | Fazer login |

### 👤 Usuários (autenticado)

| Método | Endpoint | Descrição | Role |
|---|---|---|---|
| `GET` | `/api/users` | Listar todos | `ADMIN` |
| `GET` | `/api/users/{id}` | Buscar por ID | `USER` |
| `PUT` | `/api/users/{id}` | Atualizar | `USER` |
| `DELETE` | `/api/users/{id}` | Deletar | `ADMIN` |

### 📝 Exemplos de Uso

**Registrar:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Yago Will",
    "email": "yago@email.com",
    "password": "123456"
  }'
```

**Login:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "yago@email.com",
    "password": "123456"
  }'
```

**Usar o Token:**
```bash
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer SEU_TOKEN_JWT_AQUI"
```

---

## 🧪 Testes

O projeto inclui testes unitários e de integração com **JUnit 5** e **Mockito**.

```bash
# Rodar todos os testes
mvn test

# Rodar com relatório detalhado
mvn test -Dsurefire.useFile=false
```

### Cobertura de Testes

| Classe | Tipo | Testes |
|---|---|---|
| `JwtServiceTest` | Unitário | 6 testes |
| `AuthServiceTest` | Unitário | 3 testes |
| `UserServiceTest` | Unitário | 7 testes |
| `AuthControllerTest` | Integração | 3 testes |
| `UserControllerTest` | Integração | 5 testes |
| `ApplicationTests` | Contexto | 1 teste |

---

## 📁 Estrutura do Projeto

```
spring-user-auth/
├── src/
│   ├── main/
│   │   ├── java/com/yagowill/springuserauth/
│   │   │   ├── config/          # Configurações (Security, OpenAPI)
│   │   │   ├── controller/      # Controllers REST
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── exception/       # Exceções e handlers
│   │   │   ├── model/           # Entidades JPA
│   │   │   ├── repository/      # Repositórios JPA
│   │   │   ├── security/        # JWT e filtros
│   │   │   └── service/         # Lógica de negócio
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       ├── java/com/yagowill/springuserauth/
│       │   ├── controller/      # Testes de controller
│       │   ├── security/        # Testes de JWT
│       │   └── service/         # Testes de service
│       └── resources/
│           └── application-test.properties
├── docker-compose.yml
├── Dockerfile
├── pom.xml
└── README.md
```

---

## 🏗️ Arquitetura

```
┌─────────────┐     ┌──────────────┐     ┌──────────────┐     ┌────────────┐
│   Client     │────▶│  Controller  │────▶│   Service    │────▶│ Repository │
│  (Swagger)   │◀────│  (REST API)  │◀────│  (Business)  │◀────│   (JPA)    │
└─────────────┘     └──────────────┘     └──────────────┘     └────────────┘
                           │                                        │
                    ┌──────┴──────┐                          ┌──────┴──────┐
                    │ JWT Filter  │                          │ PostgreSQL  │
                    │ (Security)  │                          │  Database   │
                    └─────────────┘                          └─────────────┘
```

---

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

<div align="center">

Feito com ❤️ por [Yago Will](https://github.com/yagowill)

</div>
