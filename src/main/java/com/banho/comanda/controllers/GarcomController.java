package com.banho.comanda.controllers;

import com.banho.comanda.garcom.Garcom;
import com.banho.comanda.repos.GarcomRepo;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/garcons")
public class GarcomController {
    private final GarcomRepo garcomRepo;
    public GarcomController(GarcomRepo garcomRepo) {
        this.garcomRepo = garcomRepo;
    }

    @GetMapping
    public List<Garcom> listarGarcom() {
        return garcomRepo.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Garcom criarGarcom(@RequestBody Garcom garcom) {
        return garcomRepo.save(garcom);
    }

    @DeleteMapping("/{id}")
    public void deletarGarcom(@PathVariable String id) {
        garcomRepo.deleteById(id);
    }
}