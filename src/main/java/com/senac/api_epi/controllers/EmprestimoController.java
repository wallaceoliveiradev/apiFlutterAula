package com.senac.api_epi.controllers;

import com.senac.api_epi.entities.Emprestimo;
import com.senac.api_epi.service.EmprestimoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.function.Supplier;

@RestController
@RequestMapping("/emprestimos")
public class EmprestimoController {
    private final EmprestimoService service;
    public EmprestimoController(EmprestimoService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<?> solicitar(@RequestBody Emprestimo pedido) {
        return responder(() -> service.solicitar(pedido), HttpStatus.CREATED);
    }

    @GetMapping("/funcionario/{id}")
    public List<Emprestimo> listarPorFuncionario(@PathVariable Long id) {
        return service.listarPorFuncionario(id);
    }

    @GetMapping
    public ResponseEntity<?> listar(@RequestParam Long adminId) {
        return responder(() -> service.listar(adminId), HttpStatus.OK);
    }

    @PutMapping("/{id}/aprovar")
    public ResponseEntity<?> aprovar(@PathVariable Long id, @RequestParam Long adminId) {
        return responder(() -> service.aprovar(id, adminId), HttpStatus.OK);
    }

    @PutMapping("/{id}/devolver")
    public ResponseEntity<?> devolver(@PathVariable Long id, @RequestParam Long adminId) {
        return responder(() -> service.devolver(id, adminId), HttpStatus.OK);
    }

    private ResponseEntity<?> responder(Supplier<?> operacao, HttpStatus sucesso) {
        try { return ResponseEntity.status(sucesso).body(operacao.get()); }
        catch (SecurityException e) { return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage()); }
        catch (IllegalArgumentException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
    }
}
