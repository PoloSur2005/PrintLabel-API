package com.printlabel.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "tallas")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Talla {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_talla")
    private Integer idTalla;

    @Column(name = "numero_talla", nullable = false, unique = true, precision = 4, scale = 1)
    private BigDecimal numeroTalla;

    @Column(name = "centimetros", nullable = false, precision = 4, scale = 1)
    private BigDecimal centimetros;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @OneToMany(mappedBy = "talla", fetch = FetchType.LAZY)
    private List<EstiloTalla> estiloTallas;
}
