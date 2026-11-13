package com.senac.api_epi.repositories;

import com.senac.api_epi.entities.Epi;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.Optional;

public interface EpiRepository extends JpaRepository<Epi, Long> {
    long countByValidadeCaBefore(LocalDate data);
    long countByQuantidadeDisponivelLessThanEqual(int limite);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Epi e where e.id = :id")
    Optional<Epi> buscarParaMovimentacao(@Param("id") Long id);
}
