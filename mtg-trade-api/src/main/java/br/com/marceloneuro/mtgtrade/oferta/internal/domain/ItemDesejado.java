package br.com.marceloneuro.mtgtrade.oferta.internal.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Table(name = "item_desejado")
@Entity
public class ItemDesejado {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "carta_catalogo_id", nullable = false)
    private UUID cartaCatalogoId;

    @ManyToOne
    @JoinColumn(name = "oferta_id")
    private Oferta oferta;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    // Os acabamentos, tiposPromo e idiomasDesejados seguem o esquema do catálogo e inventário ("acabamento1,acabamento2,... ")
    // Caso sejam "" significa que qualquer definição cumpre a oferta
    @Column(name = "acabamentos", nullable = false)
    private String acabamentos;

    @Column(name = "tipos_promo", nullable = false)
    private String tiposPromo;

    @Column(name = "idiomas_desejados", nullable = false)
    private String idiomasDesejados;

    @Column(name = "estado_minimo")
    @Enumerated(EnumType.STRING)
    private EstadoMinimo estadoMinimo;
}
