package com.printlabel.service;

import com.printlabel.dto.CatalogoDto;
import com.printlabel.exception.GlobalExceptionHandler.*;
import com.printlabel.model.Fabrica;
import com.printlabel.repository.FabricaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FabricaService {

    private final FabricaRepository fabricaRepository;

    public FabricaService(FabricaRepository fabricaRepository) {
        this.fabricaRepository = fabricaRepository;
    }

    public List<CatalogoDto.FabricaResponse> findAll() {
        return fabricaRepository.findAllByActivoTrue()
                .stream().map(CatalogoDto.FabricaResponse::new).collect(Collectors.toList());
    }

    public CatalogoDto.FabricaResponse findById(Integer id) {
        Fabrica f = fabricaRepository.findByIdFabricaAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fábrica no encontrada con ID: " + id));
        return new CatalogoDto.FabricaResponse(f);
    }

    public CatalogoDto.FabricaResponse create(CatalogoDto.FabricaRequest request) {
        if (fabricaRepository.existsByNombre(request.getNombre())) {
            throw new ConflictException("Ya existe una fábrica con el nombre: " + request.getNombre());
        }
        Fabrica f = new Fabrica();
        f.setNombre(request.getNombre());
        f.setCiudad(request.getCiudad());
        f.setContacto(request.getContacto());
        f.setActivo(true);
        return new CatalogoDto.FabricaResponse(fabricaRepository.save(f));
    }

    public CatalogoDto.FabricaResponse update(Integer id, CatalogoDto.FabricaUpdateRequest request) {
        Fabrica f = fabricaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fábrica no encontrada con ID: " + id));
        f.setNombre(request.getNombre());
        f.setCiudad(request.getCiudad());
        f.setContacto(request.getContacto());
        f.setActivo(request.getActivo());
        return new CatalogoDto.FabricaResponse(fabricaRepository.save(f));
    }

    public void delete(Integer id) {
        Fabrica f = fabricaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fábrica no encontrada con ID: " + id));
        f.setActivo(false);
        fabricaRepository.save(f);
    }
}
