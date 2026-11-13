package com.senac.api_epi.service;

import com.senac.api_epi.entities.Funcionario;
import com.senac.api_epi.entities.Treinamento;
import com.senac.api_epi.entities.TreinamentoRealizado;
import com.senac.api_epi.repositories.FuncionarioRepository;
import com.senac.api_epi.repositories.TreinamentoRealizadoRepository;
import com.senac.api_epi.repositories.TreinamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class TreinamentoService {
    private final TreinamentoRepository treinamentoRepository;
    private final TreinamentoRealizadoRepository realizadoRepository;
    private final FuncionarioRepository funcionarioRepository;

    public TreinamentoService(TreinamentoRepository treinamentoRepository,
                             TreinamentoRealizadoRepository realizadoRepository,
                             FuncionarioRepository funcionarioRepository) {
        this.treinamentoRepository = treinamentoRepository;
        this.realizadoRepository = realizadoRepository;
        this.funcionarioRepository = funcionarioRepository;
    }

    public List<Treinamento> listar() {
        return treinamentoRepository.findAllByOrderByTituloAsc();
    }

    public Treinamento cadastrar(Treinamento treinamento, Long adminId) {
        verificarAdmin(adminId);
        if (treinamento.getTitulo() == null || treinamento.getTitulo().isBlank()) {
            throw new IllegalArgumentException("Informe o título do treinamento");
        }
        if (treinamento.getValidadeMeses() != null && treinamento.getValidadeMeses() <= 0) {
            throw new IllegalArgumentException("A validade deve ser maior que zero");
        }
        treinamento.setId(null);
        return treinamentoRepository.save(treinamento);
    }

    @Transactional
    public TreinamentoRealizado concluir(Long treinamentoId, Long funcionarioId) {
        // Serializa conclusões do mesmo funcionário para evitar renovações duplicadas.
        Funcionario funcionario = funcionarioRepository.buscarParaConclusao(funcionarioId)
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado"));
        Treinamento treinamento = treinamentoRepository.findById(treinamentoId)
                .orElseThrow(() -> new IllegalArgumentException("Treinamento não encontrado"));

        realizadoRepository.findFirstByFuncionarioIdAndTreinamentoIdOrderByDataConclusaoDescIdDesc(funcionarioId, treinamentoId)
                .ifPresent(ultimo -> {
                    Integer meses = treinamento.getValidadeMeses();
                    if (meses == null || !ultimo.getDataConclusao().plusMonths(meses).isBefore(LocalDate.now())) {
                        throw new IllegalArgumentException("Este treinamento já possui uma conclusão válida");
                    }
                });

        TreinamentoRealizado registro = new TreinamentoRealizado();
        registro.setFuncionario(funcionario);
        registro.setTreinamento(treinamento);
        registro.setDataConclusao(LocalDate.now());
        // Cada renovação cria um novo registro e preserva o histórico.
        return realizadoRepository.save(registro);
    }

    public List<TreinamentoRealizado> listarRealizados(Long funcionarioId) {
        return realizadoRepository.findByFuncionarioIdOrderByDataConclusaoDesc(funcionarioId);
    }

    private void verificarAdmin(Long adminId) {
        Funcionario usuario = funcionarioRepository.findById(adminId)
                .orElseThrow(() -> new SecurityException("Usuário não encontrado"));
        if (!"ADMIN".equalsIgnoreCase(usuario.getPerfil().trim())) {
            throw new SecurityException("Apenas administradores podem fazer esta operação");
        }
    }
}
