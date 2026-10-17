package br.com.marceloneuro.mtgtrade.catalogo.internal.infrastructure.repository;

import br.com.marceloneuro.mtgtrade.catalogo.internal.domain.CartaCatalogo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface CatalogoRepository extends JpaRepository<CartaCatalogo, UUID> {

    Page<CartaCatalogo> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
    Optional<CartaCatalogo> findByPrintId(String printId);

    @Query("SELECT c.printId from CartaCatalogo c")
    Set<String> findAllPrintsIds();

    @Query("SELECT c.printId as printId, c.dataHash as dataHash FROM CartaCatalogo c")
    List<CartaCacheProjection> buscarCacheETL();
}
