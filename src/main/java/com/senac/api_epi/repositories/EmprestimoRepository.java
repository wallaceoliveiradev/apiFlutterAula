package com.senac.api_epi.repositories;

import com.senac.api_epi.entities.Emprestimo;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {
    List<Emprestimo> findByFuncionarioIdOrderByIdDesc(Long funcionarioId);
    boolean existsByFuncionarioIdAndEpiIdAndStatus(Long funcionarioId, Long epiId, String status);
    long countByStatus(String status);
    List<Emprestimo> findAllByOrderByIdDesc();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Emprestimo e where e.id = :id")
    Optional<Emprestimo> buscarParaMovimentacao(@Param("id") Long id);
}
