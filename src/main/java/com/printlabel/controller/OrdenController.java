package com.printlabel.controller;

import com.printlabel.dto.OrdenDto;
import com.printlabel.service.OrdenService;
import jakarta.validation.Valid;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * CRUD 5 — Órdenes
 * CRUD 6 — Estilos y detalles de tallas por orden
 */
@RestController
@RequestMapping("/api/v1/ordenes")
public class OrdenController {

    private final OrdenService ordenService;

    public OrdenController(OrdenService ordenService) {
        this.ordenService = ordenService;
    }

    // =====================================================================
    // CRUD 5 — ÓRDENES
    // =====================================================================

    /**
     * GET /api/v1/ordenes
     * Lista órdenes con filtros opcionales: ?idFabrica=1&estatus=borrador
     */
    @GetMapping
    public ResponseEntity<List<OrdenDto.ListResponse>> findAll(
            @RequestParam(required = false) Integer idFabrica,
            @RequestParam(required = false) String estatus) {
        return ResponseEntity.ok(ordenService.findAll(idFabrica, estatus));
    }

    /**
     * POST /api/v1/ordenes
     * Crea una nueva orden (encabezado). El folio se genera automáticamente.
     */
    @PostMapping
    public ResponseEntity<OrdenDto.Response> create(@Valid @RequestBody OrdenDto.CreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ordenService.create(request));
    }

    /**
     * GET /api/v1/ordenes/{id}
     * Consulta una orden completa con sus estilos y tallas.
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrdenDto.Response> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(ordenService.findById(id));
    }

    /**
     * PUT /api/v1/ordenes/{id}
     * Edita el encabezado de la orden. Solo válido en estado borrador.
     */
    @PutMapping("/{id}")
    public ResponseEntity<OrdenDto.Response> update(@PathVariable Integer id,
                                                      @Valid @RequestBody OrdenDto.UpdateRequest request) {
        return ResponseEntity.ok(ordenService.update(id, request));
    }

    /**
     * DELETE /api/v1/ordenes/{id}
     * Elimina una orden en estado borrador.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        ordenService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * PATCH /api/v1/ordenes/{id}/cerrar
     * Cambia el estatus de la orden a 'cerrada'.
     */
    @PatchMapping("/{id}/cerrar")
    public ResponseEntity<OrdenDto.Response> cerrar(@PathVariable Integer id) {
        return ResponseEntity.ok(ordenService.cerrarOrden(id));
    }

    /**
     * GET /api/v1/ordenes/{id}/csv
     * Genera y descarga el archivo CSV listo para la impresora de etiquetas.
     * Formato: PROGRAMA,TALLA,CM — una fila por etiqueta.
     */
    @GetMapping("/{id}/csv")
    public ResponseEntity<byte[]> descargarCsv(@PathVariable Integer id) {
        String csvContent = ordenService.generarCsv(id);
        byte[] csvBytes = csvContent.getBytes(StandardCharsets.UTF_8);

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = "etiquetas_orden_" + id + "_" + timestamp + ".csv";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv;charset=UTF-8"));
        headers.setContentDisposition(
            ContentDisposition.attachment().filename(filename, StandardCharsets.UTF_8).build()
        );
        headers.setContentLength(csvBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(csvBytes);
    }

    // =====================================================================
    // CRUD 6 — ESTILOS DE ORDEN
    // =====================================================================

    /**
     * GET /api/v1/ordenes/{id}/estilos
     * Lista los estilos de una orden con sus cantidades por talla.
     */
    @GetMapping("/{id}/estilos")
    public ResponseEntity<List<OrdenDto.EstiloResponse>> findEstilos(@PathVariable Integer id) {
        return ResponseEntity.ok(ordenService.findEstilos(id));
    }

    /**
     * POST /api/v1/ordenes/{id}/estilos
     * Agrega un estilo/programa a la orden.
     */
    @PostMapping("/{id}/estilos")
    public ResponseEntity<OrdenDto.EstiloResponse> agregarEstilo(
            @PathVariable Integer id,
            @Valid @RequestBody OrdenDto.AgregarEstiloRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ordenService.agregarEstilo(id, request));
    }

    /**
     * PUT /api/v1/ordenes/{id}/estilos/{eid}
     * Edita la posición (ordenFila) de un estilo en la grilla.
     */
    @PutMapping("/{id}/estilos/{eid}")
    public ResponseEntity<OrdenDto.EstiloResponse> updateEstilo(
            @PathVariable Integer id,
            @PathVariable Integer eid,
            @Valid @RequestBody OrdenDto.UpdateEstiloRequest request) {
        return ResponseEntity.ok(ordenService.updateEstilo(id, eid, request));
    }

    /**
     * DELETE /api/v1/ordenes/{id}/estilos/{eid}
     * Elimina un estilo de la orden (y sus cantidades por talla).
     */
    @DeleteMapping("/{id}/estilos/{eid}")
    public ResponseEntity<Void> deleteEstilo(@PathVariable Integer id, @PathVariable Integer eid) {
        ordenService.deleteEstilo(id, eid);
        return ResponseEntity.noContent().build();
    }

    /**
     * PUT /api/v1/ordenes/{id}/estilos/{eid}/tallas
     * Guarda o actualiza todas las cantidades por talla de un estilo (batch upsert).
     * Celdas con cantidad 0 se eliminan si existen.
     */
    @PutMapping("/{id}/estilos/{eid}/tallas")
    public ResponseEntity<List<OrdenDto.EstiloTallaResponse>> upsertTallas(
            @PathVariable Integer id,
            @PathVariable Integer eid,
            @Valid @RequestBody OrdenDto.UpsertTallasRequest request) {
        return ResponseEntity.ok(ordenService.upsertTallas(id, eid, request));
    }
}
