package br.com.marceloneuro.mtgtrade.catalogo.internal.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "catalogo_cartas")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
public class CartaCatalogo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "oracle_id", nullable = false)
    private String oracleId;

    @Column(name = "print_id", nullable = false, unique = true)
    private String printId;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String edicao;

    @Column(nullable = false)
    private String imagemUrl;
}
