package com.printlabel.repository;

import com.printlabel.model.Programa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgramaRepository extends JpaRepository<Programa, Integer> {
    List<Programa> findAllByActivoTrue();
    Optional<Programa> findByIdProgramaAndActivoTrue(Integer id);
    boolean existsByClave(String clave);
    List<Programa> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);
}
