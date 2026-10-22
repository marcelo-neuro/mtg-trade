package br.com.marceloneuro.mtgtrade.catalogo.internal.infrastructure.repository;

import java.util.UUID;

public interface CartaCacheProjection {
    String getPrintId();
    String getDataHash();
    UUID getId();
}
