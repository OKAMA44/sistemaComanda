package com.banho.comanda.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banho.comanda.produto.Produto;

public interface ProdutoRepo extends JpaRepository<Produto, String> {
    
}
