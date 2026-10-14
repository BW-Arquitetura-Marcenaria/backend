package br.com.bw.backend.service;

import br.com.bw.backend.repository.CalendarioRepository;
import org.springframework.stereotype.Service;

@Service
public class CalendarioService {
    private final CalendarioRepository calendarioRepository;

    public CalendarioService(CalendarioRepository calendarioRepository) {
        this.calendarioRepository = calendarioRepository;
    }
}
