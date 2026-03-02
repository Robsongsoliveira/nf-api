package br.com.nf.nfapi.controller;

import br.com.nf.nfapi.entity.NotaFiscal;
import br.com.nf.nfapi.service.NotaFiscalService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notas-fiscais")
public class NotaFiscalController {

    private final NotaFiscalService notaFiscalService;

    public NotaFiscalController(NotaFiscalService notaFiscalService) {
        this.notaFiscalService = notaFiscalService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NotaFiscal criar(@RequestBody NotaFiscal notaFiscal) {
        return notaFiscalService.criar(notaFiscal);
    }

    @GetMapping
    public List<NotaFiscal> listar() {
        return notaFiscalService.listar();
    }

    @GetMapping("/{id}")
    public NotaFiscal buscarPorId(@PathVariable Long id) {
        return notaFiscalService.buscarPorId(id);
    }
}
