package com.mockito.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import com.mockito.dao.ExamenDao;
import com.mockito.dao.ExamenDaoImpl;
import com.mockito.dao.PreguntaDao;
import com.mockito.dao.PreguntaDaoImpl;
import com.mockito.model.Examen;

@ExtendWith(MockitoExtension.class)
class ExamenServiceImplTest {
	
	@Mock
	private ExamenDao examenDao;
	
	@Mock
	private PreguntaDaoImpl preguntaDao;
	
	@InjectMocks
	private ExamenServiceImpl examenService;
	
	@Captor
	private ArgumentCaptor<Long> captor;
	
	@BeforeEach
	void initEach() {
		//Se hace automático con las anotaciones @Mock y @InjectMocks
		/*examenDao = Mockito.mock(ExamenDao.class); 
		preguntaDao = Mockito.mock(PreguntaDao.class); 
		examenService = new ExamenServiceImpl(examenDao, preguntaDao);*/
		//Habilitar anotaciones
		//Se hace automatico con el @ExtendWith
		//MockitoAnnotations.openMocks(this);
	}

	@Test
	void findExamenPorNombre() {
		
		//Simulamos el DAO con Mockito
		Mockito.when(examenDao.findAll()).thenReturn(Datos.EXAMENES);
		
		Optional<Examen> examen = examenService.findExamenPorNombre("Matematicas");
		assertNotNull(examen.isPresent());
		assertEquals(1L, examen.get().getId());
		assertEquals("Matematicas", examen.get().getNombre());
	}
	
	@Test
	void findExamenPorNombreVacio() {
		
		//Simulamos el DAO con Mockito
		List<Examen> datos = Collections.emptyList();
		Mockito.when(examenDao.findAll()).thenReturn(datos);
		
		Optional<Examen> examen = examenService.findExamenPorNombre("Matematicas");
		assertNotNull(examen.isPresent());
	}
	
	@Test
	void testPreguntasExamen() {
		Mockito.when(examenDao.findAll()).thenReturn(Datos.EXAMENES);
		//Mockito.when(preguntaDao.findPreguntarPorExamenId(1L)).thenReturn(Datos.PREGUNTAS_MATEMATICAS);
		Mockito.when(preguntaDao.findPreguntarPorExamenId(Mockito.anyLong())).thenReturn(Datos.PREGUNTAS_MATEMATICAS);
		//anyLong con cualquier entero
		
		Optional<Examen> examen = examenService.findExamenPorNombreConPreguntas("Matematicas");
		assertEquals(3, examen.get().getPreguntas().size());
		assertTrue(examen.get().getPreguntas().contains("Integrales"));
		
	}
	
	@Test
	void testPreguntasExamenVerify() {
		Mockito.when(examenDao.findAll()).thenReturn(Datos.EXAMENES);
		Mockito.when(preguntaDao.findPreguntarPorExamenId(1L)).thenReturn(Datos.PREGUNTAS_MATEMATICAS);
		
		Optional<Examen> examen = examenService.findExamenPorNombreConPreguntas("Matematicas");
		assertEquals(3, examen.get().getPreguntas().size());
		assertTrue(examen.get().getPreguntas().contains("Integrales"));
		Mockito.verify(examenDao).findAll();
		Mockito.verify(preguntaDao).findPreguntarPorExamenId(1L);
		
	}
	
	@Test
	void testExamenVerifyNull() {
		Mockito.when(examenDao.findAll()).thenReturn(Datos.EXAMENES);
		Mockito.when(preguntaDao.findPreguntarPorExamenId(1L)).thenReturn(Datos.PREGUNTAS_MATEMATICAS);
		Optional<Examen> examen = examenService.findExamenPorNombreConPreguntas("Matematicas2");
		
		assertNull(examen.orElse(null));
		Mockito.verify(examenDao).findAll();
		Mockito.verify(preguntaDao).findPreguntarPorExamenId(1L); //Aqui error. Al ser null no se ejecuta
		
	}
	
	@Test
	void testGuardarExamen() {
		//Given: definir datos y comportamiento para simular la prueba
		Examen examen = Datos.EXAMEN;
		examen.setPreguntas(Datos.PREGUNTAS_INGLES);
		Mockito.when(examenDao.guardar(Mockito.any(Examen.class))).thenReturn(Datos.EXAMEN);
		
		//When: ejecucion del metodo a probar
		Examen examenCreado = examenService.guardarExamen(examen);
		
		//Then: verificacion de los resultados
		assertNotNull(examenCreado);
		assertEquals(10L, examenCreado.getId());
		
		Mockito.verify(examenDao).guardar(Mockito.any(Examen.class));
		Mockito.verify(preguntaDao).guardarPreguntas(Mockito.anyList());
	}
	
	@Test
	void testGuardarExamen2() {
		Examen examen = Datos.EXAMEN;
		examen.setPreguntas(Datos.PREGUNTAS_INGLES);
		//Simular que se autogenere el id
		Mockito.when(examenDao.guardar(Mockito.any(Examen.class))).then(new Answer<Examen>() {
			Long secuencia = 10L;
			
			@Override
			public Examen answer(InvocationOnMock invocation) {
				Examen examen = invocation.getArgument(0);
				examen.setId(secuencia++);
				return examen;
			}
		});
		Examen examenCreado = examenService.guardarExamen(examen);
		
		assertNotNull(examenCreado);
		assertEquals(10L, examenCreado.getId());
		
		Mockito.verify(examenDao).guardar(Mockito.any(Examen.class));
		Mockito.verify(preguntaDao).guardarPreguntas(Mockito.anyList());
	}
	
	@Test
	void testGuardarExamenConDoAnswer() {
		
		//GIVEN
		
		
		Mockito.doAnswer(new Answer<Examen>() {
			Long secuencia = 10L;
			
			@Override
			public Examen answer(InvocationOnMock invocation) {
				Examen examen = invocation.getArgument(0);
				examen.setId(secuencia++);
				return examen;
			}
		}).when(examenDao).guardar(Mockito.any(Examen.class));
		
		//WHEN
		Examen examen = Datos.EXAMEN;
		examen.setPreguntas(Datos.PREGUNTAS_INGLES);
		Examen examenCreado = examenService.guardarExamen(examen);
		
		
		//THEN
		assertNotNull(examenCreado);
		assertEquals(10L, examenCreado.getId());
		
		Mockito.verify(examenDao).guardar(Mockito.any(Examen.class));
		Mockito.verify(preguntaDao).guardarPreguntas(Mockito.anyList());
	}
	
	@Test
	void testManejoExcepciones() {
		Mockito.when(examenDao.findAll()).thenReturn(Datos.EXAMENES_ID_NULL);
		Mockito.when(preguntaDao.findPreguntarPorExamenId(Mockito.isNull())).thenThrow(IllegalArgumentException.class);
		
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			examenService.findExamenPorNombreConPreguntas("Matematicas");
		});
		assertEquals(IllegalArgumentException.class, exception.getClass());
		
		Mockito.verify(examenDao).findAll();
		Mockito.verify(preguntaDao).findPreguntarPorExamenId(Mockito.isNull());
	}
	
	@Test
	void testArgumentMatchers() {
		Mockito.when(examenDao.findAll()).thenReturn(Datos.EXAMENES);
		Mockito.when(preguntaDao.findPreguntarPorExamenId(Mockito.anyLong())).thenReturn(Datos.PREGUNTAS_MATEMATICAS);
		
		examenService.findExamenPorNombreConPreguntas("Lengua");
		
		Mockito.verify(examenDao).findAll();
		Mockito.verify(preguntaDao).findPreguntarPorExamenId(Mockito.argThat(arg -> arg.equals(2L)));
		//Mockito.verify(preguntaDao).findPreguntarPorExamenId(2L); //Es lo mismo que arriba
		//Mockito.verify(preguntaDao).findPreguntarPorExamenId(Mockito.eq(2L)); //Es lo mismo que arriba
		
		Mockito.verify(preguntaDao).findPreguntarPorExamenId(Mockito.argThat(arg -> arg > 1L && arg != null));
	}
	
	@Test
	void testArgumentMatchers2() {
		Mockito.when(examenDao.findAll()).thenReturn(Datos.EXAMENES);
		Mockito.when(preguntaDao.findPreguntarPorExamenId(Mockito.anyLong())).thenReturn(Datos.PREGUNTAS_MATEMATICAS);
		
		examenService.findExamenPorNombreConPreguntas("Lengua");
		
		Mockito.verify(examenDao).findAll();
		Mockito.verify(preguntaDao).findPreguntarPorExamenId(Mockito.argThat(new MiArgsMatchers()));
	}
	
	public static class MiArgsMatchers implements ArgumentMatcher<Long>{
		
		private Long argument;

		@Override
		public boolean matches(Long argument) {
			this.argument = argument;
			return argument != null && argument > 0;
		}
		
		@Override
		public String toString() {
			return "Error: el argumento debe ser mayor que cero. Dado: "+argument;
		}
		
	}
	
	@Test
	void testArgumentCaptor() {
		Mockito.when(examenDao.findAll()).thenReturn(Datos.EXAMENES);
		examenService.findExamenPorNombreConPreguntas("Lengua");
		
		//Capturamos el valor del parametro
		//ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
		Mockito.verify(preguntaDao).findPreguntarPorExamenId((Long) this.captor.capture());
		
		//Validamos el valor pasado como argumento
		assertEquals(2L, captor.getValue());
	}
	
	@Test
	void testDoThrow() {
		Examen examen = Datos.EXAMEN;
		examen.setPreguntas(Datos.PREGUNTAS_INGLES);
		Mockito.doThrow(IllegalArgumentException.class).when(preguntaDao).guardarPreguntas(Mockito.anyList());
		
		assertThrows(IllegalArgumentException.class, () -> examenService.guardarExamen(examen));
	}
	
	@Test
	void testDoAnswer() {
		
		//GIVEN
		Mockito.when(examenDao.findAll()).thenReturn(Datos.EXAMENES);
		//Mockito.when(preguntaDao.findPreguntarPorExamenId(Mockito.anyLong())).thenReturn(Datos.PREGUNTAS_MATEMATICAS);
		
		//Obtengo el parametro id y en funcion del valor
		//Devuelvo una lista de preguntas u otra
		Mockito.doAnswer( invocation -> {
			Long id = invocation.getArgument(0);
			if(id.equals(1L)) {
				return Datos.PREGUNTAS_MATEMATICAS;
			}
			else if(id.equals(3L)){
				return Datos.PREGUNTAS_INGLES;
			}
			else {
				return Datos.PREGUNTAS_LENGUA;
			}
		}).when(preguntaDao).findPreguntarPorExamenId(1L);
		
		//WHEN
		Examen examen = examenService.findExamenPorNombreConPreguntas("Matematicas").orElse(null);
		
		//THEN
		//Verificar valores
		assertEquals(1L, examen.getId());
		assertEquals("Matematicas", examen.getNombre());
		assertEquals(3, examen.getPreguntas().size());
		
		//Verificar metodos
		Mockito.verify(preguntaDao).findPreguntarPorExamenId(Mockito.anyLong());
	}
	
	@Test
	void testDoCallRealMethod() {
		//GIVEN
		Mockito.when(examenDao.findAll()).thenReturn(Datos.EXAMENES);
		//Mockito.when(preguntaDao.findPreguntarPorExamenId(Mockito.anyLong())).thenReturn(Datos.PREGUNTAS_MATEMATICAS);
		Mockito.doCallRealMethod().when(preguntaDao).findPreguntarPorExamenId(1L);
		
		//WHEN
		Examen examen = examenService.findExamenPorNombreConPreguntas("Matematicas").orElse(null);
		
		//THEN
		assertEquals(1L, examen.getId());
		assertEquals("Matematicas", examen.getNombre());
		assertEquals(3, examen.getPreguntas().size());
		
	}
	
	@Test
	void testSpy() {
		
		//Necesito que se cree a partir de una clase implementada
		//QUe no sea abstracta ni una interfaz
		//ExamenDao examenDao = Mockito.mock(ExamenDao.class);
		ExamenDao examenDao = Mockito.spy(ExamenDaoImpl.class);
		PreguntaDao preguntaDao = Mockito.spy(PreguntaDaoImpl.class);
		ExamenService examenService = new ExamenServiceImpl(examenDao, preguntaDao);
		
		//Esto se podria hacer tambien con anotaciones
		//@Spy
		//ExamenDaoImpl examenDao;
		//@Spy
		//PreguntaDaoImpl preguntaDao;
		
		//Si lo hago con el when el metodo se llama dos veces
		//Una real y otra simulada
		//Aunque la que realmente ejecuta es la simulada
		//Para evitar eso es mejor usar doReturn en lugar de when
		//Mockito.when(preguntaDao.findPreguntarPorExamenId(Mockito.anyLong())).thenReturn(Datos.PREGUNTAS_MATEMATICAS);
		Mockito.doReturn(Datos.PREGUNTAS_MATEMATICAS).when(preguntaDao).findPreguntarPorExamenId(Mockito.anyLong());
		
		//WHEN
		Examen examen = examenService.findExamenPorNombreConPreguntas("Matematicas").orElse(null);
		
		//THEN
		assertEquals(1L, examen.getId());
		assertEquals("Matematicas", examen.getNombre());
		
		Mockito.verify(examenDao).findAll();
		Mockito.verify(preguntaDao).findPreguntarPorExamenId(Mockito.anyLong());
	}
	
	@Test
	void testOrdenInvocaciones() {
		
		//GIVEN
		Mockito.when(examenDao.findAll()).thenReturn(Datos.EXAMENES);
		
		//WHEN
		examenService.findExamenPorNombreConPreguntas("Matematicas");
		examenService.findExamenPorNombreConPreguntas("Lengua");
		
		//THEN
		InOrder inOrder = Mockito.inOrder(examenDao, preguntaDao);
		inOrder.verify(examenDao).findAll();
		inOrder.verify(preguntaDao).findPreguntarPorExamenId(1L);
		inOrder.verify(examenDao).findAll();
		inOrder.verify(preguntaDao).findPreguntarPorExamenId(2L);
	}
	
	@Test
	void testNumeroInvocaciones() {
		
		//GIVEN
		Mockito.when(examenDao.findAll()).thenReturn(Datos.EXAMENES);
		
		//WHEN
		examenService.findExamenPorNombreConPreguntas("Matematicas");

		//THEN
		//Son todas equivalentes
		Mockito.verify(preguntaDao).findPreguntarPorExamenId(1L); 
		Mockito.verify(preguntaDao, Mockito.times(1)).findPreguntarPorExamenId(1L);
		Mockito.verify(preguntaDao, Mockito.atLeast(1)).findPreguntarPorExamenId(1L);
		Mockito.verify(preguntaDao, Mockito.atLeastOnce()).findPreguntarPorExamenId(1L);
		Mockito.verify(preguntaDao, Mockito.atMost(1)).findPreguntarPorExamenId(1L);
		Mockito.verify(preguntaDao, Mockito.atMostOnce()).findPreguntarPorExamenId(1L);
		
		//Mockito.verify(preguntaDao, Mockito.never()).findPreguntarPorExamenId(1L);
		Mockito.verifyNoMoreInteractions(preguntaDao);
	}

}
