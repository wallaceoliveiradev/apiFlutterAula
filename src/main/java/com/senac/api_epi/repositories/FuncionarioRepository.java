package com.senac.api_epi.repositories;
import com.senac.api_epi.entities.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {
    Optional<Funcionario> findByMatricula(String matricula);

    Optional<Funcionario> findByMatriculaAndSenha(
            String matricula,
            String senha);

    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select f from Funcionario f where f.id = :id")
    Optional<Funcionario> buscarParaConclusao(@org.springframework.data.repository.query.Param("id") Long id);
}