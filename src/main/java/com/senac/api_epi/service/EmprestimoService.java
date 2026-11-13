package com.senac.api_epi.service;

import com.senac.api_epi.entities.*;
import com.senac.api_epi.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class EmprestimoService {
    private final EmprestimoRepository emprestimoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final EpiRepository epiRepository;

    public EmprestimoService(EmprestimoRepository emprestimoRepository,
                             FuncionarioRepository funcionarioRepository, EpiRepository epiRepository) {
        this.emprestimoRepository = emprestimoRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.epiRepository = epiRepository;
    }

    public Emprestimo solicitar(Emprestimo pedido) {
        if (pedido.getFuncionario() == null || pedido.getFuncionario().getId() == null ||
                pedido.getEpi() == null || pedido.getEpi().getId() == null) {
            throw new IllegalArgumentException("Informe o funcionário e o EPI");
        }
        Funcionario funcionario = funcionarioRepository.findById(pedido.getFuncionario().getId())
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado"));
        Epi epi = epiRepository.findById(pedido.getEpi().getId())
                .orElseThrow(() -> new IllegalArgumentException("EPI não encontrado"));
        validarDisponibilidade(epi);
        if (emprestimoRepository.existsByFuncionarioIdAndEpiIdAndStatus(funcionario.getId(), epi.getId(), "SOLICITADO")) {
            throw new IllegalArgumentException("Você já tem uma solicitação aberta para este EPI");
        }
        pedido.setId(null);
        pedido.setFuncionario(funcionario);
        pedido.setEpi(epi);
        pedido.setDataEmprestimo(LocalDate.now());
        pedido.setDataDevolucao(null);
        pedido.setStatus("SOLICITADO");
        return emprestimoRepository.save(pedido);
    }

    public List<Emprestimo> listarPorFuncionario(Long funcionarioId) {
        return emprestimoRepository.findByFuncionarioIdOrderByIdDesc(funcionarioId);
    }

    public List<Emprestimo> listar(Long adminId) {
        verificarAdmin(adminId);
        return emprestimoRepository.findAllByOrderByIdDesc();
    }

    @Transactional
    public Emprestimo aprovar(Long id, Long adminId) {
        verificarAdmin(adminId);
        Emprestimo pedido = buscarBloqueado(id);
        if (!"SOLICITADO".equals(pedido.getStatus())) {
            throw new IllegalArgumentException("Somente solicitações pendentes podem ser aprovadas");
        }
        Epi epi = epiRepository.buscarParaMovimentacao(pedido.getEpi().getId())
                .orElseThrow(() -> new IllegalArgumentException("EPI não encontrado"));
        validarDisponibilidade(epi);
        epi.setQuantidadeDisponivel(epi.getQuantidadeDisponivel() - 1);
        epiRepository.save(epi);
        pedido.setStatus("EMPRESTADO");
        return emprestimoRepository.save(pedido);
    }

    @Transactional
    public Emprestimo devolver(Long id, Long adminId) {
        verificarAdmin(adminId);
        Emprestimo pedido = buscarBloqueado(id);
        if (!"EMPRESTADO".equals(pedido.getStatus())) {
            throw new IllegalArgumentException("Somente EPIs emprestados podem ser devolvidos");
        }
        Epi epi = epiRepository.buscarParaMovimentacao(pedido.getEpi().getId())
                .orElseThrow(() -> new IllegalArgumentException("EPI não encontrado"));
        epi.setQuantidadeDisponivel(epi.getQuantidadeDisponivel() + 1);
        epiRepository.save(epi);
        pedido.setStatus("DEVOLVIDO");
        pedido.setDataDevolucao(LocalDate.now());
        return emprestimoRepository.save(pedido);
    }

    private Emprestimo buscarBloqueado(Long id) {
        return emprestimoRepository.buscarParaMovimentacao(id)
                .orElseThrow(() -> new IllegalArgumentException("Empréstimo não encontrado"));
    }

    private void validarDisponibilidade(Epi epi) {
        if (epi.isCaVencido()) throw new IllegalArgumentException("EPI com CA vencido não pode ser emprestado");
        if (epi.getQuantidadeDisponivel() <= 0) throw new IllegalArgumentException("EPI sem estoque disponível");
    }

    private void verificarAdmin(Long adminId) {
        Funcionario usuario = funcionarioRepository.findById(adminId)
                .orElseThrow(() -> new SecurityException("Usuário não encontrado"));
        if (!"ADMIN".equalsIgnoreCase(usuario.getPerfil().trim())) {
            throw new SecurityException("Apenas administradores podem gerenciar empréstimos");
        }
    }
}
