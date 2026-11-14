package br.com.marceloneuro.mtgtrade.oferta.internal.infrastructure.repository;

import br.com.marceloneuro.mtgtrade.oferta.internal.domain.Oferta;
import br.com.marceloneuro.mtgtrade.oferta.internal.domain.StatusOferta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OfertaRepository extends JpaRepository<Oferta, UUID> {

    Optional<Oferta> findByIdAndUsuarioIdAndStatus(UUID id, UUID usuarioId, StatusOferta status);
}
