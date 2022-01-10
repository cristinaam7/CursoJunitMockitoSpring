package com.mockito.service;

import java.util.Arrays;
import java.util.List;

import com.mockito.model.Examen;

public class Datos {

	public final static List<Examen> EXAMENES = Arrays.asList(
			new Examen(1L, "Matematicas"),
			new Examen(2L, "Lengua"),
			new Examen(3L, "Ingles"));
	
	public final static List<Examen> EXAMENES_ID_NULL = Arrays.asList(
			new Examen(null, "Matematicas"),
			new Examen(null, "Lengua"),
			new Examen(null, "Ingles"));
	
	public final static List<Examen> EXAMENES_ID_NEGATIVO = Arrays.asList(
			new Examen(-1L, "Matematicas"),
			new Examen(-2L, "Lengua"),
			new Examen(null, "Ingles"));
	
	public final static List<String> PREGUNTAS_MATEMATICAS = Arrays.asList(
			"Aritmetica",
			"Numeros primos",
			"Integrales");
	
	public final static List<String> PREGUNTAS_LENGUA = Arrays.asList(
			"Gramatica",
			"Semantica",
			"Verbos");
	
	public final static List<String> PREGUNTAS_INGLES = Arrays.asList(
			"Gramatica",
			"Listening",
			"Writing",
			"Speaking");
	
	public final static Examen EXAMEN = new Examen(null, "Fisica");
}
