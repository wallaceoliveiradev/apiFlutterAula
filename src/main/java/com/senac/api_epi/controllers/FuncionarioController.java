package com.senac.api_epi.controllers;

import com.senac.api_epi.entities.Funcionario;
import com.senac.api_epi.repositories.FuncionarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
public class FuncionarioController {
    private final FuncionarioRepository repository;

    public FuncionarioController(FuncionarioRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/funcionarios")
    public List<Funcionario> listar(@RequestParam Long adminId) {
        Funcionario usuario = repository.findById(adminId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso restrito"));
        if (!"ADMIN".equalsIgnoreCase(usuario.getPerfil().trim())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas administradores podem listar funcionários");
        }
        return repository.findAll();
    }
}
