package br.com.marceloneuro.mtgtrade.catalogo.internal.infrastructure.repository;

import br.com.marceloneuro.mtgtrade.catalogo.internal.domain.CartaCatalogo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface CatalogoRepository extends JpaRepository<CartaCatalogo, UUID> {

    Page<CartaCatalogo> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
    Optional<CartaCatalogo> findByPrintId(String printId);

    @Query("SELECT c.printId from CartaCatalogo c")
    Set<String> findAllPrintsIds();

    @Query("SELECT c.printId as printId, c.dataHash as dataHash, c.id as id FROM CartaCatalogo c")
    List<CartaCacheProjection> buscarCacheETL();

    // Query do broad fetch, aqui buscamos o "grosso" das cartas vindas da lista.
    // Criamos um plano cartesiano de edicao x numeroColecionador e deixamos que a service faça a filtragem mais detalhada
    @Query("""
    SELECT c FROM CartaCatalogo c
    WHERE UPPER(c.siglaEdicao) IN :edicoes
    AND UPPER(c.numeroColecionador) IN :numeros
    """)
    List<CartaCatalogo> buscarPorLoteEdicaoENumero(
            @Param("edicoes") Set<String> edicoes,
            @Param("numeros") Set<String> numerosColecionador
    );
}
