package com.senac.api_epi.repositories;

import com.senac.api_epi.entities.Treinamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TreinamentoRepository extends JpaRepository<Treinamento, Long> {

    // Todos os treinamentos em ordem alfabetica
    List<Treinamento> findAllByOrderByTituloAsc();
}