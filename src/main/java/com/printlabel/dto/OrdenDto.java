package com.printlabel.dto;

import com.printlabel.model.Orden;
import com.printlabel.model.OrdenEstilo;
import com.printlabel.model.EstiloTalla;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class OrdenDto {

    // ---- ORDEN ----

    @Data
    public static class CreateRequest {
        @NotNull(message = "La fábrica es requerida")
        private Integer idFabrica;

        @NotNull(message = "La fecha de programación es requerida")
        private LocalDate fechaProgramacion;

        private String observaciones;
    }

    @Data
    public static class UpdateRequest {
        @NotNull
        private Integer idFabrica;

        @NotNull
        private LocalDate fechaProgramacion;

        private String observaciones;
    }

    @Data
    public static class Response {
        private Integer idOrden;
        private String folio;
        private Integer idFabrica;
        private String nombreFabrica;
        private Integer idUsuario;
        private String nombreUsuario;
        private LocalDate fechaProgramacion;
        private String observaciones;
        private Orden.Estatus estatus;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<EstiloResponse> estilos;

        public Response(Orden o) {
            this.idOrden = o.getIdOrden();
            this.folio = o.getFolio();
            this.idFabrica = o.getFabrica().getIdFabrica();
            this.nombreFabrica = o.getFabrica().getNombre();
            this.idUsuario = o.getUsuario().getIdUsuario();
            this.nombreUsuario = o.getUsuario().getNombre();
            this.fechaProgramacion = o.getFechaProgramacion();
            this.observaciones = o.getObservaciones();
            this.estatus = o.getEstatus();
            this.createdAt = o.getCreatedAt();
            this.updatedAt = o.getUpdatedAt();
            if (o.getEstilos() != null) {
                this.estilos = o.getEstilos().stream()
                        .map(EstiloResponse::new)
                        .collect(Collectors.toList());
            }
        }
    }

    // ---- ESTILO ----

    @Data
    public static class AgregarEstiloRequest {
        @NotNull(message = "El programa es requerido")
        private Integer idPrograma;

        private Integer ordenFila;
    }

    @Data
    public static class UpdateEstiloRequest {
        @NotNull(message = "La posición es requerida")
        private Integer ordenFila;
    }

    @Data
    public static class EstiloResponse {
        private Integer idEstilo;
        private Integer idPrograma;
        private String clavePrograma;
        private String nombrePrograma;
        private Integer ordenFila;
        private List<EstiloTallaResponse> tallas;

        public EstiloResponse(OrdenEstilo oe) {
            this.idEstilo = oe.getIdEstilo();
            this.idPrograma = oe.getPrograma().getIdPrograma();
            this.clavePrograma = oe.getPrograma().getClave();
            this.nombrePrograma = oe.getPrograma().getNombre();
            this.ordenFila = oe.getOrdenFila();
            if (oe.getTallas() != null) {
                this.tallas = oe.getTallas().stream()
                        .map(EstiloTallaResponse::new)
                        .collect(Collectors.toList());
            }
        }
    }

    // ---- TALLAS DE ESTILO ----

    @Data
    public static class TallaItem {
        @NotNull
        private Integer idTalla;

        @NotNull
        @Min(value = 0)
        private Integer cantidadPares;
    }

    @Data
    public static class UpsertTallasRequest {
        @NotNull(message = "La lista de tallas es requerida")
        private List<TallaItem> tallas;
    }

    @Data
    public static class EstiloTallaResponse {
        private Integer idDetalle;
        private Integer idTalla;
        private BigDecimal numeroTalla;
        private BigDecimal centimetros;
        private Integer cantidadPares;

        public EstiloTallaResponse(EstiloTalla et) {
            this.idDetalle = et.getIdDetalle();
            this.idTalla = et.getTalla().getIdTalla();
            this.numeroTalla = et.getTalla().getNumeroTalla();
            this.centimetros = et.getTalla().getCentimetros();
            this.cantidadPares = et.getCantidadPares();
        }
    }

    // ---- RESPONSE LISTA (sin estilos completos) ----

    @Data
    public static class ListResponse {
        private Integer idOrden;
        private String folio;
        private String nombreFabrica;
        private String nombreUsuario;
        private LocalDate fechaProgramacion;
        private Orden.Estatus estatus;
        private LocalDateTime createdAt;

        public ListResponse(Orden o) {
            this.idOrden = o.getIdOrden();
            this.folio = o.getFolio();
            this.nombreFabrica = o.getFabrica().getNombre();
            this.nombreUsuario = o.getUsuario().getNombre();
            this.fechaProgramacion = o.getFechaProgramacion();
            this.estatus = o.getEstatus();
            this.createdAt = o.getCreatedAt();
        }
    }
}
