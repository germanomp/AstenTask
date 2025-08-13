
# 📌 AstenTask — API REST de Projetos, Tarefas e Colaboração

O **AstenTask** é uma API REST (Java 17 + Spring Boot 3) para gerenciar **usuários, projetos, tarefas, comentários, registros de tempo (time logs)** e **anexos**, com **autenticação JWT**, **controle de papéis (roles)**, **filtros dinâmicos com Specifications**, **cache interno (Spring Cache)** e **documentação via Swagger**.  
O projeto está pronto para rodar via **Docker Compose** com **PostgreSQL**.

----------

## 🧰 Tecnologias

-   **Java 17**, **Spring Boot 3**
    
-   **Spring Security + JWT**
    
-   **Spring Data JPA (Hibernate) + PostgreSQL**
    
-   **Spring Cache** (ConcurrentMapCacheManager – cache em memória)
    
-   **Lombok**
    
-   **Springdoc OpenAPI** (Swagger UI)
    
-   **Docker & Docker Compose**
    

----------

## 🚀 Rodando com Docker

### 1) Clonar o repositório

`git clone https://github.com/germanomp/AstenTask.git ` 

` cd AstenTask` 

### 2) Subir os containers

`docker-compose up` 

-   **API**: [http://localhost:8080](http://localhost:8080)
    
-   **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
    

### 🎯 Banco de dados

-   Se **via Docker**: host `localhost`, **porta 5433** (mapeada), usuário `postgres`, senha `123456`
    
-   Se **local** (sem Docker): host `localhost`, **porta 5432**, usuário `postgres`, senha `123456`

-   É criado um usuário **Admin**  automaticamente para gerenciamento, email: `admin@astentask`, senha: `admin123`

----------

## 🔐 Autenticação & Autorização

-   **JWT Bearer** em todas as rotas protegidas.
    
-   Após **registrar** e **logar**, copie o token retornado e envie no header: `Authorization: Bearer SEU_TOKEN_JWT` (no postman - a coleção está na pasta raiz) .
    
-   No **Swagger UI** clique em **Authorize** e cole `SEU_TOKEN_JWT` (apenas token).
    
----------

## 🧭 Convenções de API

-   **Formato**: JSON por padrão (salvo download de arquivos/PNG).
    
-   **Paginação**: `page`, `size`
    
-   **Ordenação**: `sortBy`, `direction` (`asc`|`desc`)
    
-   **Datas**: ISO-8601 (`yyyy-MM-dd'T'HH:mm:ss`) — ex.: `2025-08-01T10:00:00`
    

----------

## 📘 Documentação via Swagger

-   Acesse: **[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**
    
-   Autentique-se (Authorize) com **Token** para testar rotas protegidas.
    

----------

## ⚙️ Cache Interno (Spring Cache)

-   Consultas mais pesadas contam com **@Cacheable**.
    
-   Alterações (create/update/delete) disparam **@CacheEvict**.
    
-   Exemplos:
    
    -   `UserService#getUserById` → cacheia por `id`.
        
    -   `ProjectService#listProjectByUser` → cacheia listas por filtros/paginação.
        
    -   `TaskService#listTasks`/`getTaskById` → cacheia por filtros e por id.
        

> Para testar: chame a mesma rota 2x com os mesmos filtros. No **primeiro** request você verá logs/SQL; no **segundo**, **não** (hit no cache).

----------

# 📚 Endpoints

Abaixo, a documentação por módulo. 

----------

## 1) 🔐 Autenticação — `/api/auth`

### POST `/api/auth/register`

**Descrição:** cria um novo usuário.  
**Entradas:** `{  "name":  "John Doe",  "email":  "john@company.com",  "password":  "123456"}` 

### POST `/api/auth/login`

**Descrição:** autentica e retorna tokens.  
**Entradas:** `{  "email":  "john@company.com",  "password":  "123456"  }` 

### POST `/api/auth/refresh`

**Descrição:** renova o access token a partir do refresh token.  
**Entradas:** `{  "refreshToken":  "jwt_refresh_token"  }` 

### POST `/api/auth/logout`

**Descrição:** invalida o refresh token do usuário.  

----------

## 2) 👤 Usuários — `/api/users`

> **Protegido** (JWT). Algumas rotas podem exigir `ADMIN`.

### GET `/api/users`

**Descrição:** lista usuários com filtros e paginação.  
**Query params (opcionais):**  
`name`, `email`, `role`, `startDate`, `endDate`, `page`, `size`, `sortBy`, `direction`  

### GET `/api/users/{id}`

**Descrição:** retorna detalhes de um usuário.  

### PUT `/api/users/{id}`

**Descrição:** atualiza nome/role.  
**Entradas:**`{  "name":  "John Updated",  "role":  "MANAGER"  }` 

### DELETE `/api/users/{id}`

**Descrição:** remove usuário.  
**Saídas:** 204  

----------

## 3) 📁 Projetos — `/api/projects`

> **Protegido**. Normalmente listado por **usuário autenticado** (owner).

### GET `/api/projects`

**Descrição:** lista projetos do usuário logado (filtros/paginação).  
**Query params (opcionais):** `name`, `startDate`, `endDate`, `page`, `size`, `sortBy`, `direction`  

### GET `/api/projects/{id}`

**Descrição:** detalhes de um projeto pertencente ao usuário.  

### POST `/api/projects`

**Descrição:** cria projeto.  
**Entradas:** `{  "name":  "Novo Projeto",  "description":  "Descrição opcional"  }` 

### PUT `/api/projects/{id}`

**Descrição:** atualiza nome/descrição.  
**Entradas:** `{  "name":  "Projeto Atualizado",  "description":  "Nova descrição"  }` 

### DELETE `/api/projects/{id}`

**Descrição:** exclui projeto.  

### GET `/api/projects/{id}/stats`

**Descrição:** estatísticas do projeto (tarefas por status/prioridade, % de conclusão) com **filtros**.  
**Query params (opcionais):** `status`, `priority`, `assigneeId`, `startCreated`, `endCreated`, `page`, `size`, `sortBy`, `direction`  

----------

## 4) ✅ Tarefas

> **Protegido**

### GET `/api/projects/{projectId}/tasks`

**Descrição:** lista tarefas de um projeto (filtros/paginação).  
**Query params (opcionais):** `title`, `status`, `priority`, `assigneeId`, `startCreated`, `endCreated`, `page`, `size`, `sortBy`, `direction`  

### GET `/api/tasks/{id}`

**Descrição:** detalhes de uma tarefa.  

### POST `/api/projects/{projectId}/tasks`

**Descrição:** cria tarefa dentro do projeto.  
**Entradas:** `{  "title":  "Implementar login",  "description":  "JWT",  "priority":  "HIGH",  "assigneeId":  3  }` 

### PUT `/api/tasks/{id}`

**Descrição:** atualiza dados da tarefa.  
**Entradas:** `{  "title":  "Login + Refresh",  "description":  "JWT + refresh",  "priority":  "MEDIUM",  "assigneeId":  5  }` 

### DELETE `/api/tasks/{id}`

**Descrição:** exclui tarefa.  

### PUT `/api/tasks/{id}/status`

**Descrição:** altera status da tarefa.  
**Query param obrigatório:** `status` (ex.: `OPEN`, `IN_PROGRESS`, `DONE`)  

### PUT `/api/tasks/{id}/assign`

**Descrição:** atribui a tarefa a um usuário.  
**Query param obrigatório:** `userId`  

----------

## 5) 💬 Comentários

> **Protegido**

### GET `/api/tasks/{taskId}/comments`

**Descrição:** lista comentários da tarefa (paginado).  
**Query params (opcionais):** `page`, `size`, `sortBy`, `direction`  

### POST `/api/tasks/{taskId}/comments`

**Descrição:** cria comentário na tarefa.  
**Entradas:** `{  "content":  "Por favor, adicionar testes."  }` 

### PUT `/api/comments/{id}`

**Descrição:** atualiza comentário.  
**Entradas:** `{  "content":  "Editar: adicionar testes unitários e integração."  }` 


### DELETE `/api/comments/{id}`

**Descrição:** exclui comentário.  

----------

## 6) ⏱ Time Logs

> **Protegido**

### GET `/api/tasks/{taskId}/timelogs`

**Descrição:** lista registros de tempo da tarefa (filtros/paginação).  
**Query params (opcionais):**  
`page`, `size`, `sortBy`, `direction`, `userId`, `startDate`, `endDate`  

### POST `/api/tasks/{taskId}/timelogs`

**Descrição:** registra tempo em uma tarefa.  
**Entradas:** `{  "startTime":  "2025-08-01T10:00:00",  "endTime":  "2025-08-01T12:00:00",  "durationInMinutes":  120  }` 

### PUT `/api/timelogs/{id}`

**Descrição:** edita um registro de tempo.  
**Entradas:** (mesma estrutura do POST)  

### DELETE `/api/timelogs/{id}`

**Descrição:** remove um registro de tempo.  

----------

## 7) 📎 Anexos de Tarefas (Attachments)

> **Protegido**

### POST `/api/tasks/{taskId}/attachments`

**Descrição:** upload de arquivo para a tarefa.  
**Entrada:** `multipart/form-data`

-   campo **`file`** (obrigatório; arquivo)  

### GET `/api/tasks/{taskId}/attachments`

**Descrição:** lista metadados dos anexos da tarefa.  

### GET `/api/tasks/{taskId}/attachments/{attachmentId}`

**Descrição:** download do anexo.  

### DELETE `/api/tasks/{taskId}/attachments/{attachmentId}`

**Descrição:** remove anexo.  

----------

## 8) 🎛 Dashboard — `/api/dashboard`

> **Protegido**

### GET `/api/dashboard/overview`

**Descrição:** visão geral para o usuário logado.  

### GET `/api/dashboard/my-tasks`

**Descrição:** lista **minhas** tarefas (do usuário autenticado) com filtros/paginação.  
**Query params (opcionais):**  
`page`, `size`, `sortBy` (default: `dueDate`), `direction` (`asc|desc`), `status`, `priority`, `dueDateStart`, `dueDateEnd`  

### GET `/api/dashboard/reports/project/{projectId}`

**Descrição:** relatório consolidado de um projeto.  

----------

## 9) 🌐 Importação Externa

### POST `/external/import-users`

**Descrição:** importa usuários da **JSONPlaceholder** (mock API) para popular ambiente de testes.  


----------

## 🏗️ Estrutura do Projeto

      /config # JWT, Swagger, Cache etc. 
      /controller # REST Controllers 
      /dtos # DTOs de entrada/saída 
      /exception # Exceptions customizadas 
      /mapper # Mapeamento entidade <-> DTO 
      /model # Entidades JPA (User, Project, Task, ...) 
      /repositories # Spring Data JPA repositories 
      /service # Regras de negócio + Cache 
      /specification # Filtros dinâmicos (JPA Specifications) 
      /resources
      application.properties

----------

## 🚦 Códigos de Status Comuns

-   `200 OK` — sucesso
    
-   `201 Created` — recurso criado
    
-   `204 No Content` — sem corpo (ex.: DELETE, logout)
    
-   `400 Bad Request` — payload/parâmetros inválidos
    
-   `403 Forbidden` — sem permissão (role errada)
    
-   `404 Not Found` — recurso não encontrado
    
