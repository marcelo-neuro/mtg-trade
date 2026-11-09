package br.com.marceloneuro.mtgtrade.oferta.internal.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Table
@Entity
// Essa entidade representa uma oferta realizada por um usuário
// Ela contém um item oferecido e uma lista de itens desejados
// Diferente do resto do projeto aqui utilizamos um relacionamento, pois a entidade relacionada pertence a esse contexto.
public class Oferta {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Column(name = "item_inventario_id", nullable = false)
    private UUID itemOferecido;

    @OneToMany(mappedBy = "oferta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemDesejado> itensDesejados = new ArrayList<>();

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusOferta status;

    @Column(name = "ultima_modificacao", nullable = false)
    private Instant ultimaModificacao;
}
