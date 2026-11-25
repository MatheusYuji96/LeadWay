package com.leadway.controller;

import com.leadway.dto.response.CargoResponseDTO;
import com.leadway.mapper.CargoMapper;
import com.leadway.service.CargoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cargos")
public class CargoController {

    private final CargoService cargoService;
    private final CargoMapper cargoMapper;

    public CargoController(CargoService cargoService, CargoMapper cargoMapper) {
        this.cargoService = cargoService;
        this.cargoMapper = cargoMapper;
    }

    @GetMapping
    public ResponseEntity<List<CargoResponseDTO>> buscar(){
        return ResponseEntity.status(200).body(cargoService.buscar().stream().map(cargoMapper::toResponse).toList());
    }
}
