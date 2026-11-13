package com.senac.api_epi;

import com.senac.api_epi.entities.Funcionario;
import com.senac.api_epi.repositories.*;
import com.senac.api_epi.service.DashboardService;
import com.senac.api_epi.controllers.DashboardController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DashboardServiceTest {
    private final EpiRepository epis = mock(EpiRepository.class);
    private final EmprestimoRepository emprestimos = mock(EmprestimoRepository.class);
    private final FuncionarioRepository funcionarios = mock(FuncionarioRepository.class);
    private final TreinamentoRepository treinamentos = mock(TreinamentoRepository.class);
    private final TreinamentoRealizadoRepository realizados = mock(TreinamentoRealizadoRepository.class);
    private final DashboardService service = new DashboardService(epis, emprestimos, funcionarios, treinamentos, realizados);

    @BeforeEach
    void preparar() {
        Funcionario admin = new Funcionario();
        admin.setPerfil("ADMIN");
        when(funcionarios.findById(1L)).thenReturn(Optional.of(admin));
    }

    @Test
    void contratoEFormulaDaAulaSeteComExercicios() {
        when(funcionarios.count()).thenReturn(3L);
        when(treinamentos.count()).thenReturn(4L);
        when(realizados.count()).thenReturn(2L);
        when(epis.count()).thenReturn(8L);
        when(emprestimos.countByStatus("EMPRESTADO")).thenReturn(2L);
        when(emprestimos.countByStatus("DEVOLVIDO")).thenReturn(1L);
        when(emprestimos.countByStatus("SOLICITADO")).thenReturn(1L);
        when(epis.countByValidadeCaBefore(LocalDate.now())).thenReturn(1L);
        when(epis.countByQuantidadeDisponivelLessThanEqual(2)).thenReturn(2L);
        Map<String, Long> dados = service.indicadores(1L);
        assertInstanceOf(LinkedHashMap.class, dados);
        assertEquals(List.of("episCadastrados", "episEmprestados", "episDevolvidos", "caVencidos", "solicitacoesPendentes", "treinamentosPendentes", "funcionariosCadastrados", "estoqueBaixo"), new ArrayList<>(dados.keySet()));
        assertEquals(10L, dados.get("treinamentosPendentes"));
        assertEquals(8L, dados.get("episCadastrados"));
        assertEquals(2L, dados.get("estoqueBaixo"));
        verify(epis, never()).findAll();
        verify(emprestimos, never()).findAll();
    }

    @Test
    void funcionarioRecebe403SemExecutarContagens() {
        Funcionario funcionario = new Funcionario();
        funcionario.setPerfil("FUNCIONARIO");
        when(funcionarios.findById(2L)).thenReturn(Optional.of(funcionario));
        assertEquals(403, new DashboardController(service).indicadores(2L).getStatusCode().value());
        verifyNoInteractions(epis, emprestimos, treinamentos, realizados);
    }

    @Test
    void historicoDeRenovacaoNaoProduzIndicadorNegativo() {
        when(funcionarios.count()).thenReturn(1L);
        when(treinamentos.count()).thenReturn(1L);
        when(realizados.count()).thenReturn(3L);
        assertEquals(0L, service.indicadores(1L).get("treinamentosPendentes"));
    }
}
