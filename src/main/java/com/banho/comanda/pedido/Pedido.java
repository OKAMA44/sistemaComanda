package com.banho.comanda.pedido;

import java.sql.Date;

import com.banho.comanda.garcom.Garcom;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Getter 
@Setter 
@AllArgsConstructor 
@NoArgsConstructor 
public class Pedido {
    @Id 
    @GeneratedValue (strategy = GenerationType.UUID)
    private String id;

    private Integer mesa;
    private Boolean status;
    private Double valorTotal;
    private Date data;

    @ManyToOne
    @JoinColumn (name = "idGarcom")
    private Garcom garcom;

}
