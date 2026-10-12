package br.com.marceloneuro.mtgtrade.inventario.internal.web;

import br.com.marceloneuro.mtgtrade.inventario.internal.application.service.ItemInventarioService;
import br.com.marceloneuro.mtgtrade.inventario.internal.application.service.LoteItemInventarioService;
import br.com.marceloneuro.mtgtrade.inventario.internal.application.service.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventario")
@RequiredArgsConstructor
public class ItemInventarioController {

    private final ItemInventarioService itemInventarioService;
    private final LoteItemInventarioService loteItemInventarioService;

    // Normalmente quando criamos um registro retornamos um 201 (CREATED), com um header de Location,
    // nesse caso não faremos isso, uma vez que estamos realizando um Upsert, não podemos retornar um 201 se nada foi criado,
    // portanto, retornaremos 200 (Ok).
    @PostMapping
    public ResponseEntity<ItemInventarioResponseDTO> adicionarItem(
            @RequestHeader("X-User-Id") String usuarioId,
            @Valid @RequestBody AdicionarItemRequestDTO request) {

        return ResponseEntity.ok(itemInventarioService.adicionarItem(usuarioId, request));
    }

    // O mesmo se aplica para essa requisição, como estamos fazendo um bulk upsert retornamos um 200 (Ok)
    @PostMapping("/lote")
    public ResponseEntity<LoteItemInventarioResponseDTO> adicionarEmLote(
            @RequestHeader("X-User-Id") String usuarioId,
            @Valid @RequestBody LoteItemInventarioRequestDTO request) {

        return ResponseEntity.ok(loteItemInventarioService.adicionarPorLote(usuarioId, request));
    }

    @GetMapping
    public ResponseEntity<Page<ItemInventarioResponseDTO>> listarItensUsuario(
            @RequestHeader("X-User-Id") String usuarioId,
            Pageable pageable) {

        return ResponseEntity.ok(itemInventarioService.listarItensUsuario(usuarioId, pageable));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemInventarioResponseDTO> buscarItemPorId(
            @RequestHeader("X-User-Id") String usuarioId,
            @PathVariable String itemId) {

        return ResponseEntity.ok(itemInventarioService.buscarItemPorId(usuarioId, itemId));
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<ItemInventarioResponseDTO> atualizarItem(
            @RequestHeader("X-User-Id") String usuarioId,
            @PathVariable String itemId,
            @Valid @RequestBody AtualizarItemRequestDTO request) {

        return ResponseEntity.ok(itemInventarioService.atualizarItem(usuarioId, itemId, request));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> removerItem(
            @RequestHeader("X-User-Id") String usuarioId,
            @PathVariable String itemId) {

        itemInventarioService.removerItem(usuarioId, itemId);
        return ResponseEntity.noContent().build();
    }
}
