package com.printlabel.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity
@Table(name = "orden_estilos")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrdenEstilo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estilo")
    private Integer idEstilo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_orden", nullable = false)
    private Orden orden;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_programa", nullable = false)
    private Programa programa;

    @Column(name = "orden_fila", nullable = false)
    private Integer ordenFila = 0;

    @OneToMany(mappedBy = "ordenEstilo", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EstiloTalla> tallas;
}
