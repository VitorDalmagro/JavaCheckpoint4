package com.cp4.TechAlert.service;
/*
Service responsável pela lógica de negócio relacionada a filmes.
Essa camada usa o cliente Feign para buscar os dados no TMDB.
 */

import com.cp4.TechAlert.client.TmdbClient;
import com.cp4.TechAlert.dto.FilmeDTO;
import com.cp4.TechAlert.dto.FilmeResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class FilmeService {

    @Autowired
    private TmdbClient tmdbClient;

    @Value("${tmdb.api.language}")
    private String language;


    //Busca uma lista de filmes por título

    public List<FilmeDTO> buscarFilmesPorTitulo(String titulo){
        if (titulo == null || titulo.isBlank()){
            throw new RuntimeException("O título de busca não pode estar vazio!!!");
        }
        try {
            FilmeResponseDTO filmeResponseDTO = tmdbClient.buscarFilmesPorTitulo(titulo, language);

            if (filmeResponseDTO == null || filmeResponseDTO.getResults() == null || filmeResponseDTO.getResults().isEmpty()){
                throw new RuntimeException("Nenhum filme encontrado para: " + titulo);
            }
            return filmeResponseDTO.getResults();
        } catch (Exception e){
            throw new RuntimeException("Erro ao buscar filmes: " + e.getMessage(), e);
        }
    }

    // Busca um filme específico pelo ID

    public FilmeDTO buscarFilmePorId(Long id){
        if (id == null || id <= 0){
            throw new IllegalArgumentException("ID do filme inválido!!!");
        }
        try {
            FilmeDTO filme = tmdbClient.buscarFilmePorId(id, language);
            if (filme == null || filme.getTitle() == null){
                throw new NoSuchElementException("Filme não encontrado!!!");
            }
            return filme;
        } catch (feign.FeignException.NotFound e){
            // A própria API externa confirmou que o recurso não existe
            throw new NoSuchElementException("Filme não encontrado para o ID: " + id);
        } catch (feign.FeignException e){
            // Qualquer outro erro de comunicação com o TMDB (timeout, 401, 500...)
            throw new RuntimeException("Erro ao comunicar com a API externa: " + e.getMessage(), e);
        }
    }

    public String formatarFilme(FilmeDTO filme){
        return String.format(
                "%s (%s) - Nota: %s - %s",
                filme.getTitle() != null ? filme.getTitle() : "Sem título",
                filme.getReleaseDate() != null ? filme.getReleaseDate() : "Sem data",
                filme.getVoteAverage() != null ? filme.getVoteAverage() : "Sem nota",
                filme.getOverview() != null ? filme.getOverview() : "Sem sinopse"
        );
    }
}