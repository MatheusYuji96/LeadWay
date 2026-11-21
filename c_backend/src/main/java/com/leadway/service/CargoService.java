package com.leadway.service;

import com.leadway.entity.Cargo;
import com.leadway.repository.CargoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CargoService {

    private final CargoRepository cargoRepository;

    public CargoService(CargoRepository cargoRepository) {
        this.cargoRepository = cargoRepository;
    }

    public List<Cargo> buscar(){

        List<Cargo> lista = cargoRepository.findAll();
        return lista;
    }
}
