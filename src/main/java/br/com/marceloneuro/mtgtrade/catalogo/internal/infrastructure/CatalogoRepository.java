package br.com.marceloneuro.mtgtrade.catalogo.internal.infrastructure;

import br.com.marceloneuro.mtgtrade.catalogo.internal.domain.CartaCatalogo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CatalogoRepository extends JpaRepository<CartaCatalogo, UUID> {
}
