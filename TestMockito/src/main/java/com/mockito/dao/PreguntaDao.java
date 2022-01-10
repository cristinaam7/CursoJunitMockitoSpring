package com.mockito.dao;

import java.util.List;

public interface PreguntaDao {
	
	List<String> findPreguntarPorExamenId(Long id);
	
	void guardarPreguntas(List<String> preguntas);
}
