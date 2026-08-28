package br.com.marceloneuro.mtgtrade.iam.internal.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nome_usuario", unique = true, nullable = false)
    private String nomeUsuario;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    // A senha será criptografada com o algoritmo PBKDF2,
    // podendo ter um tamanho de até 128 caractéres.
    @Column(name = "senha", nullable = false, length = 128)
    private String senha;

    @CreatedDate
    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

}
