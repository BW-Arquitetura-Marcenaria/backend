package br.com.bw.backend.service;

import br.com.bw.backend.repository.CalendarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CalendarioService {
    private final CalendarioRepository calendarioRepository;
}
