package br.com.marceloneuro.mtgtrade.catalogo.internal.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "catalogo_cartas", indexes = {
        //Essas colunas são as mais utilizadas em queries.
        @Index(name = "idx_carta_nome", columnList = "nome"),
        @Index(name = "idx_oracle_id", columnList = "oracle_id")
})
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

    @Column(name = "imagem_frente_url",nullable = false)
    private String imagemFrenteUrl;

    @Column(name = "imagem_verso_url")
    private String imagemVersoUrl;
}
