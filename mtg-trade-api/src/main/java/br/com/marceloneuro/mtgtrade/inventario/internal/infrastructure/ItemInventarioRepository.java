package br.com.marceloneuro.mtgtrade.inventario.internal.infrastructure;

import br.com.marceloneuro.mtgtrade.inventario.internal.domain.Estado;
import br.com.marceloneuro.mtgtrade.inventario.internal.domain.Idioma;
import br.com.marceloneuro.mtgtrade.inventario.internal.domain.ItemInventario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ItemInventarioRepository extends JpaRepository<ItemInventario, UUID> {

    Optional<ItemInventario> findByUsuarioIdAndCartaCatalogoIdAndAcabamentoAndPromoAndEstadoAndIdioma(UUID usuarioId, UUID cartaCatalogoId,
                                                                                      String acabamento, String promo,
                                                                                      Estado estado, Idioma idioma);

    Optional<ItemInventario> findByIdAndUsuarioId(UUID id, UUID usuarioId);

    List<ItemInventario> findByUsuarioId(UUID usuarioId);
}
