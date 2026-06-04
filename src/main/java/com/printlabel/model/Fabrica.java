package com.printlabel.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity
@Table(name = "fabricas")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Fabrica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_fabrica")
    private Integer idFabrica;

    @Column(name = "nombre", nullable = false, unique = true, length = 150)
    private String nombre;

    @Column(name = "ciudad", length = 100)
    private String ciudad;

    @Column(name = "contacto", length = 100)
    private String contacto;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @OneToMany(mappedBy = "fabrica", fetch = FetchType.LAZY)
    private List<Orden> ordenes;
}
