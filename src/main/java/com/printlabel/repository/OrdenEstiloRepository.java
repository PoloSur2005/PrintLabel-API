package com.printlabel.repository;

import com.printlabel.model.OrdenEstilo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdenEstiloRepository extends JpaRepository<OrdenEstilo, Integer> {

    List<OrdenEstilo> findByOrden_IdOrdenOrderByOrdenFilaAsc(Integer idOrden);

    Optional<OrdenEstilo> findByIdEstiloAndOrden_IdOrden(Integer idEstilo, Integer idOrden);

    @Query("SELECT oe FROM OrdenEstilo oe " +
           "JOIN FETCH oe.programa p " +
           "LEFT JOIN FETCH oe.tallas et " +
           "LEFT JOIN FETCH et.talla t " +
           "WHERE oe.orden.idOrden = :idOrden " +
           "ORDER BY oe.ordenFila, t.numeroTalla")
    List<OrdenEstilo> findByOrdenIdWithDetails(@Param("idOrden") Integer idOrden);

    @Query("SELECT MAX(oe.ordenFila) FROM OrdenEstilo oe WHERE oe.orden.idOrden = :idOrden")
    Integer findMaxOrdenFila(@Param("idOrden") Integer idOrden);
}
