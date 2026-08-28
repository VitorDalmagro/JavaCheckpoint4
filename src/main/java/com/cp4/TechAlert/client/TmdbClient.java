package com.cp4.TechAlert.client;

import com.cp4.TechAlert.config.FeignConfig;
import com.cp4.TechAlert.dto.FilmeDTO;
import com.cp4.TechAlert.dto.FilmeResponseDTO;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "tmdb",
        url = "https://api.themoviedb.org/3",
        configuration = FeignConfig.class
)
public interface TmdbClient {


    //Busca filmes pelo título (ex: /search/movie?query=Matrix&language=pt-BR)

    @GetMapping("/search/movie")
    FilmeResponseDTO buscarFilmesPorTitulo(
            @RequestParam("query") String titulo,
            @RequestParam("language") String language
    );


    //Busca detalhes de um filme pelo ID (ex: /movie/603?language=pt-BR)

    @GetMapping("/movie/{id}")
    FilmeDTO buscarFilmePorId(
            @PathVariable("id") Long id,
            @RequestParam("language") String language
    );
}
