package com.printlabel.controller;

import com.printlabel.dto.CatalogoDto;
import com.printlabel.service.FabricaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CRUD 2 — Fábricas
 */
@RestController
@RequestMapping("/api/v1/fabricas")
public class FabricaController {

    private final FabricaService fabricaService;

    public FabricaController(FabricaService fabricaService) {
        this.fabricaService = fabricaService;
    }

    /**
     * GET /api/v1/fabricas
     * Lista todas las fábricas activas.
     */
    @GetMapping
    public ResponseEntity<List<CatalogoDto.FabricaResponse>> findAll() {
        return ResponseEntity.ok(fabricaService.findAll());
    }

    /**
     * POST /api/v1/fabricas
     * Registra una nueva fábrica.
     */
    @PostMapping
    public ResponseEntity<CatalogoDto.FabricaResponse> create(
            @Valid @RequestBody CatalogoDto.FabricaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fabricaService.create(request));
    }

    /**
     * GET /api/v1/fabricas/{id}
     * Consulta una fábrica por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CatalogoDto.FabricaResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(fabricaService.findById(id));
    }

    /**
     * PUT /api/v1/fabricas/{id}
     * Edita datos de una fábrica.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CatalogoDto.FabricaResponse> update(
            @PathVariable Integer id,
            @Valid @RequestBody CatalogoDto.FabricaUpdateRequest request) {
        return ResponseEntity.ok(fabricaService.update(id, request));
    }

    /**
     * DELETE /api/v1/fabricas/{id}
     * Desactiva una fábrica (soft delete).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        fabricaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
