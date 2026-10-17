package com.banho.comanda.conta;


import com.banho.comanda.produto.Produto;


import com.banho.comanda.pedido.Pedido;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Table (name = "conta")
@Getter 
@Setter
@AllArgsConstructor 
@NoArgsConstructor 
public class Conta {
    @EmbeddedId 
    private ContaId id;
    
    private Integer qtdProduto;
    private Double valorProduto;

    @ManyToOne 
    @JoinColumn (name = "id_produto")
    @MapsId ("idProduto")
    private Produto produto;

    @ManyToOne 
    @JoinColumn (name = "id_pedido")
    @MapsId ("idPedido")
    private Pedido pedido;
}
