package com.senac.api_epi.repositories;

import com.senac.api_epi.entities.Epi;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.Optional;

public interface EpiRepository extends JpaRepository<Epi, Long> {
    long countByValidadeCaBefore(LocalDate data);
    long countByQuantidadeDisponivelLessThanEqual(int limite);

    // Use MariaDB's locking syntax directly; Hibernate may append the unsupported
    // "of alias" clause when translating PESSIMISTIC_WRITE.
    @Query(value = "select * from epi where id = :id for update", nativeQuery = true)
    Optional<Epi> buscarParaMovimentacao(@Param("id") Long id);
}
