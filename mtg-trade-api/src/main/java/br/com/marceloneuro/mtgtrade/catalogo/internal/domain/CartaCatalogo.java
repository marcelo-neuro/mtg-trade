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
// Essa entidade representa uma carta que existe no nosso catálogo de cartas
// disponíveis para os usuários adicionarem ao inventário.
// Essa entidade é desnormalizada, a fim de prover uma busca rápida pelas cartas,
// além de permitir um fetch veloz para atualizar o bulk.
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

    // O acabamento representa um efeito especial que pode contar em algumas cartas, normalmente de colecionador
    // Uma carta pode conter várias opções de acabamentos, esse campo irá armazenar as opções.
    @Column(name = "acabamentos", length = 100)
    private String acabamentos;

    // Segue a mesma lógica do acabamento, armazenas as opções promocionais da carta.
    // Uma carta pode ser promo, mas pode ter as opções de promo nulas, isso acontece,
    // pois existem prints que são exclusivamente promo.
    @Column(name = "tipos_promo", length = 100)
    private String tiposPromo;

    @Column(name = "promo")
    private Boolean isPromo;
}
