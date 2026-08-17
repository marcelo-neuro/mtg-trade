package br.com.marceloneuro.mtgtrade.catalogo.internal.web;

import br.com.marceloneuro.mtgtrade.catalogo.api.dto.CartaCatalogoDTO;
import br.com.marceloneuro.mtgtrade.catalogo.internal.application.service.CatalogoService;
import br.com.marceloneuro.mtgtrade.catalogo.internal.infrastructure.scryfall.SincronizacaoCatalogoJob;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cartas")
@RequiredArgsConstructor
public class CartaController {

    private final CatalogoService catalogoService;
    private final SincronizacaoCatalogoJob sincronizacaoCatalogoJob;

    @GetMapping
    public ResponseEntity<Page<CartaCatalogoDTO>> buscarPorNome(
            @RequestParam(required = false, defaultValue = "") String nome,
            Pageable pageable) {

        return ResponseEntity.ok(catalogoService.buscarCartasPorNome(nome, pageable));
    }

    @PostMapping("/sincronizar")
    public ResponseEntity<String> sincronizarScryfall() {
        sincronizacaoCatalogoJob.sincronizaCatalogo();

        return ResponseEntity.ok("Sincronização completa.");
    }
}
