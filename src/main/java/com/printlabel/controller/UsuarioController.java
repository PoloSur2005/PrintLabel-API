package com.printlabel.controller;

import com.printlabel.dto.UsuarioDto;
import com.printlabel.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CRUD 1 — Usuarios
 * Solo accesible por administradores (ROLE_ADMIN).
 */
@RestController
@RequestMapping("/api/v1/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * GET /api/v1/usuarios
     * Lista todos los usuarios activos.
     */
    @GetMapping
    public ResponseEntity<List<UsuarioDto.Response>> findAll() {
        return ResponseEntity.ok(usuarioService.findAll());
    }

    /**
     * POST /api/v1/usuarios
     * Crea un nuevo usuario.
     */
    @PostMapping
    public ResponseEntity<UsuarioDto.Response> create(@Valid @RequestBody UsuarioDto.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.create(request));
    }

    /**
     * GET /api/v1/usuarios/{id}
     * Consulta un usuario por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDto.Response> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(usuarioService.findById(id));
    }

    /**
     * PUT /api/v1/usuarios/{id}
     * Edita datos de un usuario.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDto.Response> update(@PathVariable Integer id,
                                                       @Valid @RequestBody UsuarioDto.UpdateRequest request) {
        return ResponseEntity.ok(usuarioService.update(id, request));
    }

    /**
     * DELETE /api/v1/usuarios/{id}
     * Desactiva un usuario (soft delete).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
