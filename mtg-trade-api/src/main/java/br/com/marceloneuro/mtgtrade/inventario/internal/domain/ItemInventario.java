package br.com.marceloneuro.mtgtrade.inventario.internal.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Table(
        name = "item_inventario",
        uniqueConstraints = {
                // Garante que o usuário não possua duas linhas para armazenar carta com mesma características
                @UniqueConstraint(
                        name = "uk_inventario_estado_fisico",
                        columnNames = {"usuario_id", "carta_catalogo_id", "acabamento", "tipo_promo", "estado", "idioma"}
                )
        }
)
@Entity
// Essa entidade representa uma carta física que um usuário possui.
public class ItemInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "usuario_id" , nullable = false)
    private UUID usuarioId;

    @Column(name = "carta_catalogo_id", nullable = false)
    private UUID cartaCatalogoId;

    @Column(name = "acabamento", nullable = false, length = 50)
    private String acabamento;

    @Column(name = "tipo_promo", nullable = false, length = 50)
    private String promo;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @Column(name = "estado", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Estado estado;

    @Column(name = "idioma", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private Idioma idioma;
}
