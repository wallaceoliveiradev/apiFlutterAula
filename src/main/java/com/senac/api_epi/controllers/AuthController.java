package com.senac.api_epi.controllers;

import com.senac.api_epi.entities.Funcionario;
import com.senac.api_epi.repositories.FuncionarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation .*;
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final FuncionarioRepository repository;

    public AuthController(
            FuncionarioRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Funcionario funcionarioLogin) {
        Funcionario funcionario =
                repository.findByMatriculaAndSenha(
                                funcionarioLogin.getMatricula(),
                                funcionarioLogin.getSenha())
                        .orElse(null);
        if (funcionario == null) {
            return ResponseEntity
                    .badRequest()
                    .body("Matricula ou senha invalidos");
        }
        return ResponseEntity.ok(funcionario);
    }
}