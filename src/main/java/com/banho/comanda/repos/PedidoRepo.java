package com.banho.comanda.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banho.comanda.pedido.Pedido;

public interface PedidoRepo extends JpaRepository<Pedido, String> {
    
}
