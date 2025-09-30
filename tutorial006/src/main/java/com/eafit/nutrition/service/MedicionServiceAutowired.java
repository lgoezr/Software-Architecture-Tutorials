package com.eafit.nutrition.service;

import com.eafit.nutrition.model.Medicion;
import com.eafit.nutrition.repository.MedicionRepository;
import com.eafit.nutrition.repository.NutricionistaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MedicionServiceAutowired {

    // ✅ Inyección de dependencias a nivel de campo
    @Autowired
    private MedicionRepository medicionRepository;


    @Autowired
    private NutricionistaRepository nutricionistaRepository;

    @Transactional(readOnly = true)
    public List<Medicion> findAll() {
        return medicionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Medicion> findById(Long id) {
        return medicionRepository.findById(id);
    }

    // Aquí puedes añadir más métodos de negocio
}
