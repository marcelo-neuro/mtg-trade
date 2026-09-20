package br.com.marceloneuro.mtgtrade.inventario.internal.infrastructure;

import br.com.marceloneuro.mtgtrade.inventario.internal.domain.ItemInventario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ItemInventarioRepository extends JpaRepository<ItemInventario, UUID> {
}
