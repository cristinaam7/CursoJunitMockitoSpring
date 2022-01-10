package com.mockito.service;

import java.util.Optional;

import com.mockito.model.Examen;

public interface ExamenService {

	Optional<Examen> findExamenPorNombre(String nombre);
	
	Optional<Examen> findExamenPorNombreConPreguntas(String nombre);
	
	Examen guardarExamen(Examen examen);
}
