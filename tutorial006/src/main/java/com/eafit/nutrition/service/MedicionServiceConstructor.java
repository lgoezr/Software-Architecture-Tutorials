package com.eafit.nutrition.service;

import com.eafit.nutrition.model.Medicion;
import com.eafit.nutrition.model.Paciente;
import com.eafit.nutrition.model.Nutricionista;
import com.eafit.nutrition.repository.MedicionRepository;
import com.eafit.nutrition.repository.NutricionistaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MedicionServiceConstructor {

    private final MedicionRepository medicionRepository;
    private final NutricionistaRepository nutricionistaRepository;

    // Constructor con inyección de dependencias
    public MedicionServiceConstructor(
            MedicionRepository medicionRepository,
            NutricionistaRepository nutricionistaRepository) {
        this.medicionRepository = medicionRepository;
        this.nutricionistaRepository = nutricionistaRepository;
    }

    @Transactional(readOnly = true)
    public List<Medicion> findAll() {
        return medicionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Medicion> findById(Long id) {
        return medicionRepository.findById(id);
    }

    // ✅ Nuevo método createMedicion
    @Transactional
    public Medicion createMedicion(Long pacienteId, Long nutricionistaId, Medicion medicion) {
        // Buscar nutricionista
        Nutricionista nutricionista = nutricionistaRepository.findById(nutricionistaId)
                .orElseThrow(() -> new IllegalArgumentException("Nutricionista no encontrado con id: " + nutricionistaId));

        medicion.setNutricionista(nutricionista);

        // Guardar en BD
        return medicionRepository.save(medicion);
    }
}
