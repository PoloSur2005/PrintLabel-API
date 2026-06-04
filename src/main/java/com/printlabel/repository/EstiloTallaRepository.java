package com.printlabel.repository;

import com.printlabel.model.EstiloTalla;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstiloTallaRepository extends JpaRepository<EstiloTalla, Integer> {

    List<EstiloTalla> findByOrdenEstilo_IdEstilo(Integer idEstilo);

    Optional<EstiloTalla> findByOrdenEstilo_IdEstiloAndTalla_IdTalla(Integer idEstilo, Integer idTalla);

    @Modifying
    @Query("DELETE FROM EstiloTalla et WHERE et.ordenEstilo.idEstilo = :idEstilo")
    void deleteByOrdenEstilo_IdEstilo(@Param("idEstilo") Integer idEstilo);

    @Query("SELECT et FROM EstiloTalla et " +
           "JOIN FETCH et.talla t " +
           "JOIN FETCH et.ordenEstilo oe " +
           "JOIN FETCH oe.programa p " +
           "WHERE oe.orden.idOrden = :idOrden AND et.cantidadPares > 0 " +
           "ORDER BY oe.ordenFila, t.numeroTalla")
    List<EstiloTalla> findForCsvByOrdenId(@Param("idOrden") Integer idOrden);
}
