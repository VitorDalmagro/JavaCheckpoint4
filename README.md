## API Externa Utilizada

**Nome da API:** The Movie Database (TMDB)
**URL:** https://api.themoviedb.org/3
**Objetivo:** Consultar informações sobre filmes (título, sinopse, avaliação,
data de lançamento) para simular um cenário de monitoramento de conteúdo
externo relevante para a equipe de TI da TechAlert.

**Endpoints utilizados:**
- `GET /search/movie` — busca filmes por título
- `GET /movie/{id}` — detalhes de um filme específico

**Parâmetros utilizados:**
- Query Param: `query` (título buscado), `language` (idioma da resposta)
- Path Param: `id` (identificador do filme)
- Header: `Authorization: Bearer {token}` (autenticação na TMDB)

**Exemplo de resposta JSON (`/movie/{id}`):**
​
```json
{
  "id": 603,
  "title": "The Matrix",
  "original_title": "The Matrix",
  "overview": "Set in the 22nd century...",
  "release_date": "1999-03-30",
  "vote_average": 8.2,
  "poster_path": "/f89U3ADr1oiB1s9GkdPOEpXUk5H.jpg",
  "original_language": "en",
  "popularity": 83.2
}
​```