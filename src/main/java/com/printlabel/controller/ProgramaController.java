package com.printlabel.controller;

import com.printlabel.dto.CatalogoDto;
import com.printlabel.service.ProgramaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CRUD 3 — Programas / Modelos de calzado
 */
@RestController
@RequestMapping("/api/v1/programas")
public class ProgramaController {

    private final ProgramaService programaService;

    public ProgramaController(ProgramaService programaService) {
        this.programaService = programaService;
    }

    /**
     * GET /api/v1/programas
     * Lista todos los modelos activos. Soporta búsqueda por nombre con ?nombre=
     */
    @GetMapping
    public ResponseEntity<List<CatalogoDto.ProgramaResponse>> findAll(
            @RequestParam(required = false) String nombre) {
        if (nombre != null && !nombre.isBlank()) {
            return ResponseEntity.ok(programaService.search(nombre));
        }
        return ResponseEntity.ok(programaService.findAll());
    }

    /**
     * POST /api/v1/programas
     * Crea un nuevo modelo.
     */
    @PostMapping
    public ResponseEntity<CatalogoDto.ProgramaResponse> create(
            @Valid @RequestBody CatalogoDto.ProgramaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(programaService.create(request));
    }

    /**
     * GET /api/v1/programas/{id}
     * Consulta un modelo por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CatalogoDto.ProgramaResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(programaService.findById(id));
    }

    /**
     * PUT /api/v1/programas/{id}
     * Edita datos de un modelo.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CatalogoDto.ProgramaResponse> update(
            @PathVariable Integer id,
            @Valid @RequestBody CatalogoDto.ProgramaUpdateRequest request) {
        return ResponseEntity.ok(programaService.update(id, request));
    }

    /**
     * DELETE /api/v1/programas/{id}
     * Desactiva un modelo (soft delete).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        programaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
