package com.senac.api_epi.entities;

import jakarta.persistence .*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Treinamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String titulo;

    @Column(length = 500)
    private String descricao;

    // Caminho do material, igual ao imagemUrl do EPI:
// /arquivos/nr06.pdf
    @Column(length = 200)
    private String arquivoUrl;

    // Null significa treinamento sem prazo de validade.
    private Integer validadeMeses;
}