# TechAlert

Aplicação desenvolvida como parte do Checkpoint de REST Client. 
O objetivo é demonstrar a integração entre aplicações através do consumo de uma API REST externa,
utilizando um cliente HTTP em Java (Spring Cloud OpenFeign).

A TechAlert consulta a API pública **The Movie Database (TMDB)** para 
retornar informações sobre filmes, 
simulando um cenário em que uma central de TI 
acompanha dados externos relevantes para suas operações.

## Sumário

- [API Externa Utilizada](#api-externa-utilizada)
- [Arquitetura](#arquitetura)
- [Endpoints da Aplicação](#endpoints-da-aplicação)
- [Como Rodar Localmente](#como-rodar-localmente)
- [Profiles de Ambiente](#profiles-de-ambiente)
- [Testando os Endpoints](#testando-os-endpoints)
- [Tratamento de Erros](#tratamento-de-erros)


## Integrantes

| Nome| RM |
|---|---|
|Arthur dos Santos Cabral| 566515|
|Vitor Fria Dalmagro| 566052 |

## API Externa Utilizada

| | |
|---|---|
| **Nome da API** | The Movie Database (TMDB) |
| **URL** | https://api.themoviedb.org|
| **Autenticação** | Bearer Token (v4 Read Access Token), enviado via header `Authorization` |

**Endpoints consumidos na API externa:**

| Endpoint TMDB | Uso |
|---|---|
| `GET /search/movie` | Busca filmes por título |
| `GET /movie/{id}` | Detalhes de um filme específico |

**Parâmetros utilizados na chamada à API externa:**

- **Query Params:** `query` (título buscado), `language` (idioma da resposta, ex: `pt-BR`)
- **Path Param:** `id` (identificador do filme na TMDB)
- **Header:** `Authorization: Bearer {token}` (autenticação)

**Exemplo de resposta JSON (`GET /movie/603`):**

```json
{
  "id": 603,
  "title": "Matrix",
  "original_title": "The Matrix",
  "overview": "O jovem programador Thomas Anderson é atormentado por estranhos pesadelos...",
  "release_date": "1999-03-31",
  "vote_average": 8.256,
  "poster_path": "/lDqMDI3xpbB9UQRyeXfei0MXhqb.jpg",
  "original_language": "en",
  "popularity": 46.5646
}
```

## Arquitetura

**Fluxo de uma requisição, do recebimento ao retorno:**

1. Cliente faz a requisição para a TechAlert (ex: `GET /api/filmes/603`)
2. `FilmeController` recebe a solicitação
3. `FilmeService` valida os dados e aciona o `TmdbClient`
4. `TmdbClient` (Feign) monta e envia a requisição HTTP para a TMDB, com os parâmetros necessários
5. A resposta JSON da TMDB é convertida automaticamente para `FilmeDTO`
6. O `FilmeService` processa/valida o resultado
7. O `FilmeController` devolve a resposta ao cliente da TechAlert

## Endpoints da Aplicação

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/api/filmes/buscar?titulo={titulo}` | Busca filmes pelo título (query param) |
| `GET` | `/api/filmes/{id}` | Consulta um filme específico pelo ID (path param) |
| `GET` | `/api/filmes/{id}/formatado` | Retorna o filme em formato de texto resumido |
| `GET` | `/api/health` | Health check da aplicação |

**Exemplo de resposta do health check:**

```json
{
  "status": "UP",
  "application": "TechAlert"
}
```

## Como Rodar Localmente

### Pré-requisitos

- Java 17
- Maven (ou use o `./mvnw` incluso no projeto)
- Um token de leitura (v4) da TMDB, obtido em https://www.themoviedb.org/settings/api

### Passos

```bash
# 1. Clone o repositório
git clone https://github.com/VitorDalmagro/JavaCheckpoint4.git
cd JavaCheckpoint4

# 2. Defina o token da TMDB como variável de ambiente
export TMDB_API_TOKEN=seu_token_v4_aqui        # Linux/Mac
# $env:TMDB_API_TOKEN="seu_token_v4_aqui"       # Windows PowerShell

# 3. Rode a aplicação no profile de desenvolvimento
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

A aplicação sobe em `http://localhost:8080`.

## Profiles de Ambiente

O projeto usa profiles do Spring para separar configuração de desenvolvimento e produção:

| Arquivo | Uso |
|---|---|
| `application.properties` | Configuração base, comum a todos os ambientes | 
| `application-dev.properties` | Logs verbosos (`FULL`), devtools ativo | 
| `application-prod.properties` | Logs reduzidos (`BASIC`/`INFO`), devtools desativado |
| `application-example.properties` | Modelo de referência para quem for configurar o projeto | 

O token da TMDB **nunca** é commitado — ele é lido de uma variável de ambiente (`TMDB_API_TOKEN`), tanto local quanto no Render.

## Testando os Endpoints

A aplicação pode ser testada tanto localmente quanto direto na versão publicada no Render. Basta trocar a base da URL:

- **Local:** `http://localhost:8080`
- **Render:** `https://javacheckpoint4.onrender.com`

### Testando localmente

```bash
# Health check
curl http://localhost:8080/api/health

# Busca por título
curl "http://localhost:8080/api/filmes/buscar?titulo=Matrix"

# Consulta por ID
curl http://localhost:8080/api/filmes/603

# Consulta formatada
curl http://localhost:8080/api/filmes/603/formatado

# Casos de erro
curl http://localhost:8080/api/filmes/999999999          # ID inexistente -> 404
curl "http://localhost:8080/api/filmes/buscar?titulo="   # título vazio -> 400
```

### Testando no Render (produção)

```bash
# Health check
curl https://javacheckpoint4.onrender.com/api/health

# Busca por título
curl "https://javacheckpoint4.onrender.com/api/filmes/buscar?titulo=Matrix"

# Consulta por ID
curl https://javacheckpoint4.onrender.com/api/filmes/603

# Consulta formatada
curl https://javacheckpoint4.onrender.com/api/filmes/603/formatado

# Casos de erro
curl https://javacheckpoint4.onrender.com/api/filmes/999999999
curl "https://javacheckpoint4.onrender.com/api/filmes/buscar?titulo="
```

> No navegador, também é possível acessar diretamente, por exemplo: https://javacheckpoint4.onrender.com/api/filmes/603

## Tratamento de Erros

| Cenário | Status HTTP | Origem |
|---|---|---|
| Título de busca vazio | `400 Bad Request` | Validação da própria TechAlert |
| ID inválido (nulo ou ≤ 0) | `400 Bad Request` | Validação da própria TechAlert |
| Filme/busca sem resultado | `404 Not Found` | API externa (TMDB) confirmou que não existe |
| Falha de comunicação com a TMDB (timeout, 401, 500...) | `500 Internal Server Error` | Erro na integração com a API externa |

Exemplo de resposta de erro (filme não encontrado):

```json
{
  "erro": "Filme não encontrado para o ID: 999999999",
  "id": "999999999"
}
```

## Tecnologias Utilizadas

- Java 25
- Spring Boot 4.1
- Spring Cloud OpenFeign (REST Client)
- Lombok
- Maven
- TMDB API