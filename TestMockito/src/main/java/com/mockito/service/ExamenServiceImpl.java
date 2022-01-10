package com.mockito.service;

import java.util.List;
import java.util.Optional;

import com.mockito.dao.ExamenDao;
import com.mockito.dao.PreguntaDao;
import com.mockito.model.Examen;

public class ExamenServiceImpl implements ExamenService{
	
	private ExamenDao examenDao;
	private PreguntaDao preguntaDao;
	
	public ExamenServiceImpl(ExamenDao examenDao, PreguntaDao preguntaDao) {
		this.examenDao = examenDao;
		this.preguntaDao = preguntaDao;
	}

	@Override
	public Optional<Examen> findExamenPorNombre(String nombre) {
		return examenDao.findAll()
				.stream()
				.filter(e -> e.getNombre().contains(nombre))
				.findFirst();
	}

	@Override
	public Optional<Examen> findExamenPorNombreConPreguntas(String nombre) {
		Optional<Examen> examen = examenDao.findAll()
									.stream()
									.filter(e -> e.getNombre().contains(nombre))
									.findFirst();
		if(examen.isPresent()) {
			List<String> preguntas = preguntaDao.findPreguntarPorExamenId(examen.get().getId());
			examen.get().setPreguntas(preguntas);
		}
		return examen;
	}

	@Override
	public Examen guardarExamen(Examen examen) {
		if(!examen.getPreguntas().isEmpty()) {
			preguntaDao.guardarPreguntas(examen.getPreguntas());
		}
		
		return examenDao.guardar(examen);
	}

}
