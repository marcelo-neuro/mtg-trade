package br.com.marceloneuro.mtgtrade.oferta.internal.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
// Entidade embutida na Oferta, será utilizada para armazenar os dados do ItemInventario para prevenir perda de referência
// Quando entidade reservada for apagada
public class ItemOferecido {

    @Column(name = "item_oferecido_id", nullable = false)
    private UUID itemOferecidoId;

    @Column(name = "carta_catalogo_id", nullable = false)
    private UUID cartaCatalogoId;

    @Column(name = "acabamento", nullable = false)
    private String acabamento;

    @Column(name = "promo", nullable = false)
    private String promo;

    @Column(name = "estado", nullable = false)
    private String estado;

    @Column(name = "idioma", nullable = false)
    private String idioma;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;
}
