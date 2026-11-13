package com.senac.api_epi.service;

import com.senac.api_epi.entities.Funcionario;
import com.senac.api_epi.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class DashboardService {
    private final EpiRepository epiRepository;
    private final EmprestimoRepository emprestimoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final TreinamentoRepository treinamentoRepository;
    private final TreinamentoRealizadoRepository realizadoRepository;

    public DashboardService(EpiRepository epiRepository, EmprestimoRepository emprestimoRepository,
                            FuncionarioRepository funcionarioRepository, TreinamentoRepository treinamentoRepository,
                            TreinamentoRealizadoRepository realizadoRepository) {
        this.epiRepository = epiRepository;
        this.emprestimoRepository = emprestimoRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.treinamentoRepository = treinamentoRepository;
        this.realizadoRepository = realizadoRepository;
    }

    public Map<String, Long> indicadores(Long adminId) {
        verificarAdmin(adminId);
        long funcionarios = funcionarioRepository.count();
        long treinamentos = treinamentoRepository.count();
        long realizados = realizadoRepository.count();
        // Fórmula da Aula 7. O piso zero acomoda históricos de renovação do projeto existente.
        long pendentes = Math.max(0L, funcionarios * treinamentos - realizados);
        Map<String, Long> dados = new LinkedHashMap<>();
        dados.put("episCadastrados", epiRepository.count());
        dados.put("episEmprestados", emprestimoRepository.countByStatus("EMPRESTADO"));
        dados.put("episDevolvidos", emprestimoRepository.countByStatus("DEVOLVIDO"));
        dados.put("caVencidos", epiRepository.countByValidadeCaBefore(LocalDate.now()));
        dados.put("solicitacoesPendentes", emprestimoRepository.countByStatus("SOLICITADO"));
        dados.put("treinamentosPendentes", pendentes);
        // Exercícios 1 e 2 da página 18.
        dados.put("funcionariosCadastrados", funcionarios);
        dados.put("estoqueBaixo", epiRepository.countByQuantidadeDisponivelLessThanEqual(2));
        return dados;
    }

    private void verificarAdmin(Long adminId) {
        Funcionario usuario = funcionarioRepository.findById(adminId)
                .orElseThrow(() -> new SecurityException("Usuário não encontrado"));
        if (!"ADMIN".equalsIgnoreCase(usuario.getPerfil().trim())) {
            throw new SecurityException("Apenas administradores podem ver o painel");
        }
    }
}
