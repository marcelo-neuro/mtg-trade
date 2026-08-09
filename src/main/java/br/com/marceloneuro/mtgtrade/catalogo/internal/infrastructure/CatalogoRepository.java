package br.com.marceloneuro.mtgtrade.catalogo.internal.infrastructure;

import br.com.marceloneuro.mtgtrade.catalogo.internal.domain.CartaCatalogo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CatalogoRepository extends JpaRepository<CartaCatalogo, UUID> {

    Page<CartaCatalogo> findByNameContainingIgnoreCase(String nome, Pageable pageable);
    Optional<CartaCatalogo> findByPrintId(String printId);
}
