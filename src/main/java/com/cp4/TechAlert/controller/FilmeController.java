package com.cp4.TechAlert.controller;


import com.cp4.TechAlert.dto.FilmeDTO;
import com.cp4.TechAlert.service.FilmeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/filmes")
public class FilmeController {

    @Autowired
    private FilmeService filmeService;

    @GetMapping("/buscar")
    public ResponseEntity<?> buscarFilmes(@RequestParam String titulo){
        try {
            List<FilmeDTO> filmes = filmeService.buscarFilmesPorTitulo(titulo);
            return ResponseEntity.ok(filmes);
        } catch (Exception e){
            Map<String, String> erro = new HashMap<>();
            erro.put("erro", e.getMessage());
            erro.put("titulo", titulo);
            return ResponseEntity.badRequest().body(erro);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarFilmePorId(@PathVariable Long id){
        try {
            FilmeDTO filme = filmeService.buscarFilmePorId(id);
            return ResponseEntity.ok(filme);
        } catch (NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", e.getMessage(), "id", String.valueOf(id)));
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", e.getMessage(), "id", String.valueOf(id)));
        } catch (Exception e){
            return ResponseEntity.internalServerError()
                    .body(Map.of("erro", "Erro inesperado: " + e.getMessage(), "id", String.valueOf(id)));
        }
    }

    @GetMapping("/{id}/formatado")
    public ResponseEntity<?> buscarFilmeFormatado(@PathVariable Long id){
        try {
            FilmeDTO filme = filmeService.buscarFilmePorId(id);
            String filmeFormatado = filmeService.formatarFilme(filme);

            Map<String, String> response = new HashMap<>();
            response.put("filme", filmeFormatado);
            response.put("id", String.valueOf(id));
            return ResponseEntity.ok(response);
        } catch (Exception e){
            Map<String, String> erro = new HashMap<>();
            erro.put("erro", e.getMessage());
            erro.put("id", String.valueOf(id));
            return ResponseEntity.badRequest().body(erro);
        }
    }

}