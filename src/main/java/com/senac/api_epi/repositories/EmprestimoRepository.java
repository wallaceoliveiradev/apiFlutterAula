package com.senac.api_epi.repositories;

import com.senac.api_epi.entities.Emprestimo;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {
    List<Emprestimo> findByFuncionarioIdOrderByIdDesc(Long funcionarioId);
    boolean existsByFuncionarioIdAndEpiIdAndStatus(Long funcionarioId, Long epiId, String status);
    long countByStatus(String status);
    List<Emprestimo> findAllByOrderByIdDesc();

    // MariaDB does not accept Hibernate's generated "for update of alias".
    // Keep the row lock with MariaDB-compatible SQL for approval/return flows.
    @Query(value = "select * from emprestimo where id = :id for update", nativeQuery = true)
    Optional<Emprestimo> buscarParaMovimentacao(@Param("id") Long id);
}
