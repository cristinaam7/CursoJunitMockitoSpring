package com.mockito.dao;

import java.util.Arrays;
import java.util.List;

import com.mockito.model.Examen;

public class ExamenDaoImpl implements ExamenDao{

	@Override
	public List<Examen> findAll() {
		System.out.println("Examen: Metodo findAll real");
		return Arrays.asList(
				new Examen(1L, "Matematicas"),
				new Examen(2L, "Lengua"),
				new Examen(3L, "Ingles"));
	}

	@Override
	public Examen guardar(Examen examen) {
		System.out.println("Examen: Metodo guardar real");
		return new Examen(5L, "Fisica");
	}

}
