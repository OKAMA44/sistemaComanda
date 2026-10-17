package com.banho.comanda.conta;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ContaId implements Serializable {
    @Column(name = "id_produto")
    private String idProduto;

    @Column(name = "id_pedido")
    private String idPedido;
}
