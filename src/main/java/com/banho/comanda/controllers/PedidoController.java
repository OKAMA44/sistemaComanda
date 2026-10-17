
package com.banho.comanda.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.banho.comanda.services.PedidoDTO.PedidoRequest;
import com.banho.comanda.services.PedidoDTO.PedidoResponse;
import com.banho.comanda.services.PedidoService;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    @GetMapping
    public List<PedidoResponse> listar() {
        return service.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponse criar(@RequestBody PedidoRequest req) {
        return service.criar(req);
    }

    @PatchMapping("/{id}/fechar")
    public PedidoResponse fechar(@PathVariable String id) {
        return service.fechar(id);
    }
}
