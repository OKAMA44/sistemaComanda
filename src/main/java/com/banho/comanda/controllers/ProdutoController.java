package com.banho.comanda.controllers;

import com.banho.comanda.produto.Produto;
import com.banho.comanda.repos.ProdutoRepo;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {
    private final ProdutoRepo produtoRepo;

    public ProdutoController(ProdutoRepo produtoRepo) {
        this.produtoRepo = produtoRepo;
    }

    @GetMapping
    public List<Produto> listarProdutos() {
        return produtoRepo.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Produto criarProduto(@RequestBody Produto produto) {
        return produtoRepo.save(produto);
    }

    @DeleteMapping("/{id}")
    public void deletarProduto(@PathVariable String id) {
        produtoRepo.deleteById(id);
    }

}
