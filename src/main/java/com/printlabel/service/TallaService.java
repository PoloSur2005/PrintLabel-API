package com.printlabel.service;

import com.printlabel.dto.CatalogoDto;
import com.printlabel.exception.GlobalExceptionHandler.*;
import com.printlabel.model.Talla;
import com.printlabel.repository.TallaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TallaService {

    private final TallaRepository tallaRepository;

    public TallaService(TallaRepository tallaRepository) {
        this.tallaRepository = tallaRepository;
    }

    public List<CatalogoDto.TallaResponse> findAll() {
        return tallaRepository.findAllByActivoTrueOrderByNumeroTallaAsc()
                .stream().map(CatalogoDto.TallaResponse::new).collect(Collectors.toList());
    }

    public CatalogoDto.TallaResponse findById(Integer id) {
        Talla t = tallaRepository.findByIdTallaAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Talla no encontrada con ID: " + id));
        return new CatalogoDto.TallaResponse(t);
    }

    public CatalogoDto.TallaResponse create(CatalogoDto.TallaRequest request) {
        Talla t = new Talla();
        t.setNumeroTalla(request.getNumeroTalla());
        t.setCentimetros(request.getCentimetros());
        t.setActivo(true);
        return new CatalogoDto.TallaResponse(tallaRepository.save(t));
    }

    public CatalogoDto.TallaResponse update(Integer id, CatalogoDto.TallaUpdateRequest request) {
        Talla t = tallaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Talla no encontrada con ID: " + id));
        t.setNumeroTalla(request.getNumeroTalla());
        t.setCentimetros(request.getCentimetros());
        t.setActivo(request.getActivo());
        return new CatalogoDto.TallaResponse(tallaRepository.save(t));
    }

    public void delete(Integer id) {
        Talla t = tallaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Talla no encontrada con ID: " + id));
        t.setActivo(false);
        tallaRepository.save(t);
    }
}
