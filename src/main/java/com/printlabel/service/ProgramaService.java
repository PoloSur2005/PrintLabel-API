package com.printlabel.service;

import com.printlabel.dto.CatalogoDto;
import com.printlabel.exception.GlobalExceptionHandler.*;
import com.printlabel.model.Programa;
import com.printlabel.repository.ProgramaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProgramaService {

    private final ProgramaRepository programaRepository;

    public ProgramaService(ProgramaRepository programaRepository) {
        this.programaRepository = programaRepository;
    }

    public List<CatalogoDto.ProgramaResponse> findAll() {
        return programaRepository.findAllByActivoTrue()
                .stream().map(CatalogoDto.ProgramaResponse::new).collect(Collectors.toList());
    }

    public CatalogoDto.ProgramaResponse findById(Integer id) {
        Programa p = programaRepository.findByIdProgramaAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Programa no encontrado con ID: " + id));
        return new CatalogoDto.ProgramaResponse(p);
    }

    public List<CatalogoDto.ProgramaResponse> search(String nombre) {
        return programaRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre)
                .stream().map(CatalogoDto.ProgramaResponse::new).collect(Collectors.toList());
    }

    public CatalogoDto.ProgramaResponse create(CatalogoDto.ProgramaRequest request) {
        if (programaRepository.existsByClave(request.getClave())) {
            throw new ConflictException("Ya existe un programa con la clave: " + request.getClave());
        }
        Programa p = new Programa();
        p.setClave(request.getClave());
        p.setNombre(request.getNombre());
        p.setDescripcion(request.getDescripcion());
        p.setActivo(true);
        return new CatalogoDto.ProgramaResponse(programaRepository.save(p));
    }

    public CatalogoDto.ProgramaResponse update(Integer id, CatalogoDto.ProgramaUpdateRequest request) {
        Programa p = programaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Programa no encontrado con ID: " + id));
        p.setClave(request.getClave());
        p.setNombre(request.getNombre());
        p.setDescripcion(request.getDescripcion());
        p.setActivo(request.getActivo());
        return new CatalogoDto.ProgramaResponse(programaRepository.save(p));
    }

    public void delete(Integer id) {
        Programa p = programaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Programa no encontrado con ID: " + id));
        p.setActivo(false);
        programaRepository.save(p);
    }
}
