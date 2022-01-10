package com.mockito.dao;

import java.util.List;

import com.mockito.model.Examen;

public interface ExamenDao {
	
	List<Examen> findAll();
	
	Examen guardar(Examen examen);

}
