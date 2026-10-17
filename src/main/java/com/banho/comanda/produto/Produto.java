package com.banho.comanda.produto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Table (name = "produto")
@Getter 
@Setter 
@AllArgsConstructor 
@NoArgsConstructor 
public class Produto {
    @Id
    @GeneratedValue (strategy = GenerationType.UUID)
    private String id;

    private String nome;
    private String tipo;
    private Double preco;
}
