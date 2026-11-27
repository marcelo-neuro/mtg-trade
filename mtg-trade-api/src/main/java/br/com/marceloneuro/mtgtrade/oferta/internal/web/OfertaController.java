package br.com.marceloneuro.mtgtrade.oferta.internal.web;

import br.com.marceloneuro.mtgtrade.oferta.api.dto.CriaOfertaRequestDTO;
import br.com.marceloneuro.mtgtrade.oferta.api.dto.OfertaResponseDTO;
import br.com.marceloneuro.mtgtrade.oferta.internal.application.service.OfertaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/ofertas")
@RequiredArgsConstructor
public class OfertaController {

    private final OfertaService ofertaService;

    @PostMapping
    public ResponseEntity<OfertaResponseDTO> criarOferta(
            @RequestHeader("X-User-Id") String usuarioId,
            @Valid @RequestBody CriaOfertaRequestDTO request
    ) {

        OfertaResponseDTO response = ofertaService.criaOferta(request, usuarioId);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    // Será público no gateway, portanto não precisará do usuário
    @GetMapping
    public ResponseEntity<Page<OfertaResponseDTO>> buscarOfertasAtivas(
            @PageableDefault(
                    page = 0, size = 20, sort = "ultimaModificacao", direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        Page<OfertaResponseDTO> response = ofertaService.buscarOfertasAtivas(pageable);

        return ResponseEntity.ok(response);
    }

    // Será público no gateway, portanto não precisará do usuário
    @GetMapping("/{id: 0-9a-fA-F-]{36}}")
    public ResponseEntity<OfertaResponseDTO> buscarOfertaPorId(@PathVariable("id") String ofertaId) {
        OfertaResponseDTO response = ofertaService.buscarPorId(ofertaId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/minhas")
    public ResponseEntity<Page<OfertaResponseDTO>> buscarOfertasPorUsuario(
            @RequestHeader("X-User-Id") String usuarioId,
            @PageableDefault(
                    page = 0, size = 20, sort = "ultimaModificacao", direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        Page<OfertaResponseDTO> response = ofertaService.buscarOfertasAtivasPorUsuario(usuarioId, pageable);

        return ResponseEntity.ok(response);
    }

    // Utiliza PATCH, pois apenas modificamos a flag de status de ATIVA para CANCELADA
    @PatchMapping("/{id: 0-9a-fA-F-]{36}}/cancelar")
    public ResponseEntity<OfertaResponseDTO> cancelarOferta(
            @RequestHeader("X-User-Id") String usuarioId,
            @PathVariable("id") String ofertaId
    ) {
        OfertaResponseDTO response = ofertaService.cancelarOferta(ofertaId, usuarioId);

        return ResponseEntity.ok(response);
    }
}
