package br.com.marceloneuro.mtgtrade.catalogo.internal.web;

import br.com.marceloneuro.mtgtrade.catalogo.api.dto.CartaCatalogoDTO;
import br.com.marceloneuro.mtgtrade.catalogo.internal.application.service.CatalogoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/carta")
@RequiredArgsConstructor
public class CartaController {

    private final CatalogoService catalogoService;

    @GetMapping
    public ResponseEntity<Page<CartaCatalogoDTO>> buscarPorNome(
            @RequestParam(required = false, defaultValue = "") String nome,
            Pageable pageable) {

        return ResponseEntity.ok(catalogoService.buscarCartasPorNome(nome, pageable));
    }
}
