package com.banho.comanda.garcom;

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
@Table (name = "garcom")
@Getter 
@Setter 
@AllArgsConstructor 
@NoArgsConstructor 
public class Garcom {
    @Id 
    @GeneratedValue (strategy = GenerationType.UUID)
    private String id;
    private String nome;

}
