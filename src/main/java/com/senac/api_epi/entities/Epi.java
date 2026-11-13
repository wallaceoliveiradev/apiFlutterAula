package com.senac.api_epi.entities;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Epi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 120)
    private String nome;
    @Column(nullable = false, length = 80)
    private String categoria;
    @Column(nullable = false, length = 20)
    private String ca;
    @Column(nullable = false)
    private LocalDate validadeCa;
    @Column(nullable = false)
    private String imagemUrl;
    @Column(nullable = false)
    private Integer quantidadeDisponivel;
    public boolean isCaVencido() {
        return validadeCa.isBefore(LocalDate.now());
    }
}
