package com.printlabel.repository;

import com.printlabel.model.Fabrica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FabricaRepository extends JpaRepository<Fabrica, Integer> {
    List<Fabrica> findAllByActivoTrue();
    Optional<Fabrica> findByIdFabricaAndActivoTrue(Integer id);
    boolean existsByNombre(String nombre);
}
