package com.leadway.service;

import com.leadway.entity.Log;
import com.leadway.repository.LogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogService {

    private final LogRepository logRepository;

    public LogService(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public List<Log> buscar(){
        List<Log> lista = logRepository.findAll();
        return lista;
    }
}
