package com.printlabel.controller;

import com.printlabel.dto.CatalogoDto;
import com.printlabel.service.TallaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CRUD 4 — Tallas
 */
@RestController
@RequestMapping("/api/v1/tallas")
public class TallaController {

    private final TallaService tallaService;

    public TallaController(TallaService tallaService) {
        this.tallaService = tallaService;
    }

    /**
     * GET /api/v1/tallas
     * Lista todas las tallas activas con su centimetraje, ordenadas ascendentemente.
     */
    @GetMapping
    public ResponseEntity<List<CatalogoDto.TallaResponse>> findAll() {
        return ResponseEntity.ok(tallaService.findAll());
    }

    /**
     * POST /api/v1/tallas
     * Agrega una nueva talla al catálogo.
     */
    @PostMapping
    public ResponseEntity<CatalogoDto.TallaResponse> create(
            @Valid @RequestBody CatalogoDto.TallaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tallaService.create(request));
    }

    /**
     * GET /api/v1/tallas/{id}
     * Consulta una talla por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CatalogoDto.TallaResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(tallaService.findById(id));
    }

    /**
     * PUT /api/v1/tallas/{id}
     * Edita número de talla o centimetraje.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CatalogoDto.TallaResponse> update(
            @PathVariable Integer id,
            @Valid @RequestBody CatalogoDto.TallaUpdateRequest request) {
        return ResponseEntity.ok(tallaService.update(id, request));
    }

    /**
     * DELETE /api/v1/tallas/{id}
     * Desactiva una talla (soft delete).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        tallaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
