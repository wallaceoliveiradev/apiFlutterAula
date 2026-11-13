package com.senac.api_epi;

import com.senac.api_epi.entities.*;
import com.senac.api_epi.repositories.*;
import com.senac.api_epi.service.EmprestimoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmprestimoServiceTest {
    private final EpiRepository epis = mock(EpiRepository.class);
    private final EmprestimoRepository emprestimos = mock(EmprestimoRepository.class);
    private final FuncionarioRepository funcionarios = mock(FuncionarioRepository.class);
    private final EmprestimoService service = new EmprestimoService(emprestimos, funcionarios, epis);
    private Epi epi;
    private Emprestimo pedido;

    @BeforeEach
    void preparar() {
        Funcionario admin = new Funcionario();
        admin.setPerfil("ADMIN");
        when(funcionarios.findById(1L)).thenReturn(Optional.of(admin));
        epi = new Epi();
        epi.setId(1L);
        epi.setValidadeCa(LocalDate.now().plusYears(1));
        epi.setQuantidadeDisponivel(2);
        pedido = new Emprestimo();
        pedido.setId(9L);
        pedido.setEpi(epi);
        pedido.setStatus("SOLICITADO");
        when(emprestimos.buscarParaMovimentacao(9L)).thenReturn(Optional.of(pedido));
        when(epis.buscarParaMovimentacao(1L)).thenReturn(Optional.of(epi));
        when(emprestimos.save(any())).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    void aprovarEDepoisDevolverAtualizaStatusEEstoque() {
        service.aprovar(9L, 1L);
        assertEquals("EMPRESTADO", pedido.getStatus());
        assertEquals(1, epi.getQuantidadeDisponivel());
        service.devolver(9L, 1L);
        assertEquals("DEVOLVIDO", pedido.getStatus());
        assertEquals(2, epi.getQuantidadeDisponivel());
        assertEquals(LocalDate.now(), pedido.getDataDevolucao());
    }

    @Test
    void naoAprovaDuasVezes() {
        service.aprovar(9L, 1L);
        assertThrows(IllegalArgumentException.class, () -> service.aprovar(9L, 1L));
        assertEquals(1, epi.getQuantidadeDisponivel());
    }

    @Test
    void naoDevolveDuasVezes() {
        pedido.setStatus("EMPRESTADO");
        service.devolver(9L, 1L);
        assertThrows(IllegalArgumentException.class, () -> service.devolver(9L, 1L));
        assertEquals(3, epi.getQuantidadeDisponivel());
    }

    @Test
    void naoAprovaCaVencido() {
        epi.setValidadeCa(LocalDate.now().minusDays(1));
        assertThrows(IllegalArgumentException.class, () -> service.aprovar(9L, 1L));
        verify(emprestimos, never()).save(any());
        assertEquals(2, epi.getQuantidadeDisponivel());
    }

    @Test
    void naoAprovaSemEstoque() {
        epi.setQuantidadeDisponivel(0);
        assertThrows(IllegalArgumentException.class, () -> service.aprovar(9L, 1L));
        verify(emprestimos, never()).save(any());
    }

    @Test
    void funcionarioNaoPodeAprovarNemDevolver() {
        Funcionario usuario = new Funcionario();
        usuario.setPerfil("FUNCIONARIO");
        when(funcionarios.findById(2L)).thenReturn(Optional.of(usuario));
        assertThrows(SecurityException.class, () -> service.aprovar(9L, 2L));
        assertThrows(SecurityException.class, () -> service.devolver(9L, 2L));
        verifyNoInteractions(epis, emprestimos);
    }
}
