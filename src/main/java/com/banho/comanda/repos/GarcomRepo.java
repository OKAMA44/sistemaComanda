package com.banho.comanda.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banho.comanda.garcom.Garcom;

public interface GarcomRepo extends JpaRepository<Garcom, String> {
    
}
