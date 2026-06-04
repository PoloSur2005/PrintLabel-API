package com.printlabel.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "estilo_tallas",
       uniqueConstraints = @UniqueConstraint(columnNames = {"id_estilo", "id_talla"}))
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class EstiloTalla {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Integer idDetalle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estilo", nullable = false)
    private OrdenEstilo ordenEstilo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_talla", nullable = false)
    private Talla talla;

    @Column(name = "cantidad_pares", nullable = false)
    private Integer cantidadPares = 0;
}
