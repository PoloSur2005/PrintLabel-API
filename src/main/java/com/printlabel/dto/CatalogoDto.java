package com.printlabel.dto;

import com.printlabel.model.Fabrica;
import com.printlabel.model.Programa;
import com.printlabel.model.Talla;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

public class CatalogoDto {

    // ---- FÁBRICA ----

    @Data
    public static class FabricaRequest {
        @NotBlank(message = "El nombre es requerido")
        @Size(max = 150)
        private String nombre;

        @Size(max = 100)
        private String ciudad;

        @Size(max = 100)
        private String contacto;
    }

    @Data
    public static class FabricaUpdateRequest {
        @NotBlank(message = "El nombre es requerido")
        @Size(max = 150)
        private String nombre;

        @Size(max = 100)
        private String ciudad;

        @Size(max = 100)
        private String contacto;

        @NotNull
        private Boolean activo;
    }

    @Data
    public static class FabricaResponse {
        private Integer idFabrica;
        private String nombre;
        private String ciudad;
        private String contacto;
        private Boolean activo;

        public FabricaResponse(Fabrica f) {
            this.idFabrica = f.getIdFabrica();
            this.nombre = f.getNombre();
            this.ciudad = f.getCiudad();
            this.contacto = f.getContacto();
            this.activo = f.getActivo();
        }
    }

    // ---- PROGRAMA ----

    @Data
    public static class ProgramaRequest {
        @NotBlank(message = "La clave es requerida")
        @Size(max = 30)
        private String clave;

        @NotBlank(message = "El nombre es requerido")
        @Size(max = 150)
        private String nombre;

        private String descripcion;
    }

    @Data
    public static class ProgramaUpdateRequest {
        @NotBlank
        @Size(max = 30)
        private String clave;

        @NotBlank
        @Size(max = 150)
        private String nombre;

        private String descripcion;

        @NotNull
        private Boolean activo;
    }

    @Data
    public static class ProgramaResponse {
        private Integer idPrograma;
        private String clave;
        private String nombre;
        private String descripcion;
        private Boolean activo;

        public ProgramaResponse(Programa p) {
            this.idPrograma = p.getIdPrograma();
            this.clave = p.getClave();
            this.nombre = p.getNombre();
            this.descripcion = p.getDescripcion();
            this.activo = p.getActivo();
        }
    }

    // ---- TALLA ----

    @Data
    public static class TallaRequest {
        @NotNull(message = "El número de talla es requerido")
        @DecimalMin(value = "1.0")
        private BigDecimal numeroTalla;

        @NotNull(message = "Los centímetros son requeridos")
        @DecimalMin(value = "1.0")
        private BigDecimal centimetros;
    }

    @Data
    public static class TallaUpdateRequest {
        @NotNull
        private BigDecimal numeroTalla;

        @NotNull
        private BigDecimal centimetros;

        @NotNull
        private Boolean activo;
    }

    @Data
    public static class TallaResponse {
        private Integer idTalla;
        private BigDecimal numeroTalla;
        private BigDecimal centimetros;
        private Boolean activo;

        public TallaResponse(Talla t) {
            this.idTalla = t.getIdTalla();
            this.numeroTalla = t.getNumeroTalla();
            this.centimetros = t.getCentimetros();
            this.activo = t.getActivo();
        }
    }
}
