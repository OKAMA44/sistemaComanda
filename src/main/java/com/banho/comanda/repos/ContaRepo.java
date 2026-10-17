package com.banho.comanda.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banho.comanda.conta.Conta;
import com.banho.comanda.conta.ContaId;

import java.util.List;

public interface ContaRepo extends JpaRepository<Conta, ContaId> {
    List<Conta> findByPedidoId(String pedidoId);
}
