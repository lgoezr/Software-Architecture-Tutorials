package com.eafit.nutrition.repository;

import com.eafit.nutrition.model.Medicion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicionRepository extends JpaRepository<Medicion, Long> {

    // Todas las mediciones de un paciente ordenadas por fecha (más recientes primero)
    List<Medicion> findByPacienteIdOrderByFechaDesc(Long pacienteId);

    // Última medición registrada de un paciente
    Optional<Medicion> findFirstByPacienteIdOrderByFechaDesc(Long pacienteId);
}
