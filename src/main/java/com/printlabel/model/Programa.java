package com.printlabel.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity
@Table(name = "programas")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Programa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_programa")
    private Integer idPrograma;

    @Column(name = "clave", nullable = false, unique = true, length = 30)
    private String clave;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @OneToMany(mappedBy = "programa", fetch = FetchType.LAZY)
    private List<OrdenEstilo> ordenEstilos;
}
