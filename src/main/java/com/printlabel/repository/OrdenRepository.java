package com.printlabel.repository;

import com.printlabel.model.Orden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdenRepository extends JpaRepository<Orden, Integer> {

    @Query("SELECT o FROM Orden o WHERE " +
           "(:idFabrica IS NULL OR o.fabrica.idFabrica = :idFabrica) AND " +
           "(:estatus IS NULL OR o.estatus = :estatus) " +
           "ORDER BY o.createdAt DESC")
    List<Orden> findWithFilters(@Param("idFabrica") Integer idFabrica,
                                @Param("estatus") Orden.Estatus estatus);

    Optional<Orden> findByFolio(String folio);

    boolean existsByFolio(String folio);

    @Query("SELECT o FROM Orden o LEFT JOIN FETCH o.estilos e LEFT JOIN FETCH e.tallas WHERE o.idOrden = :id")
    Optional<Orden> findByIdWithEstilosAndTallas(@Param("id") Integer id);
}
