package com.printlabel.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ordenes")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Orden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_orden")
    private Integer idOrden;

    @Column(name = "folio", nullable = false, unique = true, length = 30)
    private String folio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_fabrica", nullable = false)
    private Fabrica fabrica;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "fecha_programacion", nullable = false)
    private LocalDate fechaProgramacion;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Enumerated(EnumType.STRING)
    @Column(name = "estatus", nullable = false)
    private Estatus estatus = Estatus.borrador;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "orden", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<OrdenEstilo> estilos;

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum Estatus {
        borrador, cerrada, exportada
    }
}
