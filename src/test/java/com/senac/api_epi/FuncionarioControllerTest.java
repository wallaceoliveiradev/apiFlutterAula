package com.senac.api_epi;

import com.senac.api_epi.controllers.FuncionarioController;
import com.senac.api_epi.entities.Funcionario;
import com.senac.api_epi.repositories.FuncionarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FuncionarioControllerTest {
    @Test
    void funcionarioNaoPodeListarOutrosFuncionarios() {
        FuncionarioRepository repo = mock(FuncionarioRepository.class);
        Funcionario usuario = new Funcionario();
        usuario.setPerfil("FUNCIONARIO");
        when(repo.findById(7L)).thenReturn(Optional.of(usuario));
        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> new FuncionarioController(repo).listar(7L));
        assertEquals(403, erro.getStatusCode().value());
        verify(repo, never()).findAll();
    }

    @Test
    void administradorPodeListar() {
        FuncionarioRepository repo = mock(FuncionarioRepository.class);
        Funcionario usuario = new Funcionario();
        usuario.setPerfil("ADMIN");
        when(repo.findById(1L)).thenReturn(Optional.of(usuario));
        when(repo.findAll()).thenReturn(List.of(usuario));
        assertEquals(1, new FuncionarioController(repo).listar(1L).size());
    }
}
