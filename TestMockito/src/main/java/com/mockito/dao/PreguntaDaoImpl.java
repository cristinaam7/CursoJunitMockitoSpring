package com.mockito.dao;

import java.util.Arrays;
import java.util.List;

public class PreguntaDaoImpl implements PreguntaDao{

	@Override
	public List<String> findPreguntarPorExamenId(Long id) {
		System.out.println("Pregunta: Metodo findPreguntarPorExamenId real");
		return Arrays.asList(
				"Aritmetica",
				"Numeros primos",
				"Integrales");
	}

	@Override
	public void guardarPreguntas(List<String> preguntas) {
		System.out.println("Pregunta: Metodo guardarPreguntas real");
		// TODO Auto-generated method stub
		
	}

}
