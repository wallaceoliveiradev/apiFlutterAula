package com.senac.api_epi;

import com.senac.api_epi.entities.*;
import com.senac.api_epi.repositories.*;
import com.senac.api_epi.service.TreinamentoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TreinamentoServiceTest {
    private final TreinamentoRepository treinamentos = mock(TreinamentoRepository.class);
    private final TreinamentoRealizadoRepository realizados = mock(TreinamentoRealizadoRepository.class);
    private final FuncionarioRepository funcionarios = mock(FuncionarioRepository.class);
    private final TreinamentoService service = new TreinamentoService(treinamentos, realizados, funcionarios);
    private Treinamento treinamento;

    @BeforeEach
    void preparar() {
        Funcionario funcionario = new Funcionario();
        funcionario.setId(7L);
        treinamento = new Treinamento();
        treinamento.setId(1L);
        treinamento.setValidadeMeses(12);
        when(funcionarios.buscarParaConclusao(7L)).thenReturn(Optional.of(funcionario));
        when(treinamentos.findById(1L)).thenReturn(Optional.of(treinamento));
        when(realizados.save(any())).thenAnswer(invocacao -> invocacao.getArgument(0));
    }

    private void conclusaoEm(LocalDate data) {
        TreinamentoRealizado anterior = new TreinamentoRealizado();
        anterior.setDataConclusao(data);
        when(realizados.findFirstByFuncionarioIdAndTreinamentoIdOrderByDataConclusaoDescIdDesc(7L, 1L))
                .thenReturn(Optional.of(anterior));
    }

    @Test
    void renovacaoVencidaCriaOutroRegistroSemApagarHistorico() {
        conclusaoEm(LocalDate.now().minusYears(2));
        TreinamentoRealizado novo = service.concluir(1L, 7L);
        assertNull(novo.getId());
        assertEquals(LocalDate.now(), novo.getDataConclusao());
        verify(realizados).save(novo);
        verify(realizados, never()).delete(any());
    }

    @Test
    void conclusaoQueVenceHojeContinuaValida() {
        conclusaoEm(LocalDate.now().minusMonths(12));
        assertThrows(IllegalArgumentException.class, () -> service.concluir(1L, 7L));
        verify(realizados, never()).save(any());
    }

    @Test
    void semValidadeNaoPermiteConclusaoDuplicada() {
        treinamento.setValidadeMeses(null);
        conclusaoEm(LocalDate.now().minusYears(5));
        assertThrows(IllegalArgumentException.class, () -> service.concluir(1L, 7L));
    }
}
