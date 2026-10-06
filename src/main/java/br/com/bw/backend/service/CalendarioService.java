package br.com.bw.backend.service;

import org.springframework.stereotype.Service;

@Service
public class CalendarioService {
    private final CalendarioService calendarioService;

    public CalendarioService(CalendarioService calendarioService) {
        this.calendarioService = calendarioService;
    }
}
