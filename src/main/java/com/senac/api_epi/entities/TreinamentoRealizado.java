package com.senac.api_epi.entities;

import jakarta.persistence .*;
import lombok. AllArgsConstructor;
import lombok. Getter;
import lombok. NoArgsConstructor;
import lombok. Setter;

import java.time.LocalDate;

// Registro de atividade: "o funcionario X concluiu o treinamento Y no dia Z"
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class TreinamentoRealizado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "funcionario_id")
    private Funcionario funcionario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "treinamento_id")
    private Treinamento treinamento;

    @Column(nullable = false)
    private LocalDate dataConclusao;
}