package br.com.marceloneuro.mtgtrade.inventario.internal.application.service;

import br.com.marceloneuro.mtgtrade.catalogo.api.CatalogoFacade;
import br.com.marceloneuro.mtgtrade.catalogo.api.dto.CartaCatalogoDTO;
import br.com.marceloneuro.mtgtrade.catalogo.internal.domain.exception.CartaCatalogoNaoEncontradaException;
import br.com.marceloneuro.mtgtrade.inventario.internal.application.service.dto.AdicionarItemRequestDTO;
import br.com.marceloneuro.mtgtrade.inventario.internal.application.service.dto.LoteItemInventarioRequestDTO;
import br.com.marceloneuro.mtgtrade.inventario.internal.application.service.dto.LoteItemInventarioResponseDTO;
import br.com.marceloneuro.mtgtrade.inventario.internal.application.service.validation.ValidadorItemInventario;
import br.com.marceloneuro.mtgtrade.inventario.internal.domain.Estado;
import br.com.marceloneuro.mtgtrade.inventario.internal.domain.Idioma;
import br.com.marceloneuro.mtgtrade.inventario.internal.infrastructure.ItemInventarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoteItemInventarioService {

    private final ItemInventarioRepository itemInventarioRepository;
    private final CatalogoFacade catalogoFacade;

    private final List<ValidadorItemInventario> validadores;


    // Esse bulk upsert sofre do mesmo problema do upsert, porém temos uma nova ocorrência de N+1
    // Aqui precisamos evitar N+1 na busca das cartas do catálogo e na validação de duplicatas.
    // Além disso, caso o usuário envie a lista com 2 itens identicos precisamos validar para que as quantidades sejam somadas.
    @Transactional
    public LoteItemInventarioResponseDTO adicionarPorLote(String usuarioId, LoteItemInventarioRequestDTO request) {
        List<AdicionarItemRequestDTO> lista = request.lista();

        // Tratamos o primerio caso de N+1
        Set<UUID> uuidsDetalheCartas = request.lista()
                        .stream().map(item-> UUID.fromString(item.cartaCatalogoId()))
                        .collect(Collectors.toSet());

        Map<UUID, CartaCatalogoDTO> detalhesCartas = catalogoFacade.buscaPorConjuntoIdsMap(uuidsDetalheCartas);

        // Validações
        lista.forEach(item -> {
            CartaCatalogoDTO detalhesCarta = detalhesCartas.get(UUID.fromString(item.cartaCatalogoId()));
            if (detalhesCarta == null) {
                throw new CartaCatalogoNaoEncontradaException("Detalhes da carta não encontrados para o ID: " + item.cartaCatalogoId());
            }
            validadores.forEach(validador -> validador.validar(item, detalhesCarta));
        });

        // Prevenindo que linhas duplicadas não sejam somadas
        // Mapeamos a quantidade adicionada a cada ChaveFisica.
        Map<ChaveFisica, Integer> quantidadesMap = request.lista().stream()
                .collect(Collectors.toMap(
                        item -> new ChaveFisica(
                                UUID.fromString(item.cartaCatalogoId()),
                                item.acabamento(),
                                item.promo(),
                                Estado.valueOf(item.estado()),
                                Idioma.valueOf(item.idioma())
                        ),
                        AdicionarItemRequestDTO::quantidade,
                        // Condição de duplicata: caso duas linhas tenham a mesma ChaveFisica a função será chamada para atribuir um novo valor
                        Integer::sum
                ));
    }

    // Criado para servir como chave de mapeamento dos itens físicos buscados no inventário para evitar N+1.
    private record ChaveFisica(
        UUID uuidCartaCatalogo,
        String acabamento,
        String tipoPromo,
        Estado estado,
        Idioma idioma
    ) {
    }
}
