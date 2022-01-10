package com.example.springTest.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.example.springTest.domain.Cuenta;
import com.example.springTest.domain.TransactionDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
class cuentaControllerRestTemplateTest {
	
	@Autowired
	private TestRestTemplate client;
	
	@LocalServerPort
	private int puerto;
	
	private ObjectMapper mapper;
	
	@BeforeEach
	void setup() {
		mapper = new ObjectMapper();
	}
	
	public String crearUri(String uri) {
		return "http://localhost:"+puerto+uri;
	}

	@Order(1)
	@Test
	void transferirTest() throws JsonProcessingException {
		//Given
		TransactionDto dto = new TransactionDto();
		dto.setBancoId(1L);
		dto.setCuentaDestinoId(1L);
		dto.setCuentaOrigenId(2L);
		dto.setMonto(new BigDecimal(200));
		
		Map<String, Object> responseEsperada = new HashMap<>();
		responseEsperada.put("date", LocalDate.now().toString());
		responseEsperada.put("status", "ok");
		responseEsperada.put("mensaje", "Transferencia realizada con éxito");
		responseEsperada.put("transaction", dto);
		
		//When
		// 1 - Url de conexion: ruta relativa y puerto random
		// ResponseEntity<String> response = client.postForEntity("/api/cuentas/transferir", dto, String.class);
		// 1 - Url absoluta
		// 2 - Request - parametros entrada - no hace falta mapearlos, ya se transforman opor defecto a json
		// 3 - Response - Tipo String - no hace falta mapearlo
		ResponseEntity<String> response = client.postForEntity(crearUri("/api/cuentas/transferir"), dto, String.class);
		
		//Obtener json de respuesta
		String json = response.getBody();
		
		//Then
		
		//Comprobar respuesta
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
		assertNotNull(json);
		assertTrue(json.contains("Transferencia realizada con éxito"));
		
		
		//Comprobar valores respuesta - Mapeamos a json
		JsonNode jsonResponse = mapper.readTree(json);
		assertEquals("Transferencia realizada con éxito", jsonResponse.path("mensaje").asText());
		assertEquals(1L, jsonResponse.path("transaction").path("cuentaDestinoId").asLong());
		assertEquals(LocalDate.now().toString(), jsonResponse.path("date").asText());
		
		//Comprobar valores respuesta de otra manera
		assertEquals(mapper.writeValueAsString(responseEsperada), json);
	}
	
	@Order(2)
	@Test
	void testDetalle() throws JsonMappingException, JsonProcessingException {
		
		ResponseEntity<Cuenta> response = client.getForEntity(crearUri("/api/cuentas/1"), Cuenta.class);
		Cuenta cuenta = response.getBody();
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
		assertNotNull(cuenta);
		
		assertEquals(1L, cuenta.getId());
		assertEquals("Cris", cuenta.getPersona());
		//assertEquals("1000", cuenta.getSaldo());
		
		//assertTrue(cuenta.equals(new Cuenta(1L, "Cris", new BigDecimal(1000))));
	}
	
	@Order(3)
	@Test
	void testListar() throws JsonMappingException, JsonProcessingException {
		
		ResponseEntity<Cuenta[]> response = client.getForEntity(crearUri("/api/cuentas/"), Cuenta[].class);
		List<Cuenta> cuentas =  Arrays.asList(response.getBody());
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
		assertNotNull(cuentas);
		
		assertEquals(1L, cuentas.get(0).getId());
		assertEquals("Cris", cuentas.get(0).getPersona());
		assertEquals(3L, cuentas.get(2).getId());
		assertEquals("Eliz", cuentas.get(2).getPersona());
		assertEquals(3, cuentas.size());
		
		JsonNode json = mapper.readTree(mapper.writeValueAsString(cuentas));
		assertEquals("Cris", json.get(0).path("persona").asText());
		assertEquals(1L, json.get(0).path("id").asLong());
		assertEquals("Eliz", json.get(2).path("persona").asText());
		assertEquals(3L, json.get(2).path("id").asLong());
	}
	
	@Order(4)
	@Test
	void testGuardar() throws JsonMappingException, JsonProcessingException {
		
		Cuenta cuenta = new Cuenta(10L, "Lili", new BigDecimal(3000));
		
		ResponseEntity<Cuenta> response = client.postForEntity(crearUri("/api/cuentas/guardar"), cuenta, Cuenta.class);
		Cuenta cuentaSave =  response.getBody();
		
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
		assertNotNull(cuentaSave);
		
		assertEquals(10L, cuenta.getId());
		assertEquals("Lili", cuenta.getPersona());
		
		JsonNode json = mapper.readTree(mapper.writeValueAsString(cuenta));
		assertEquals("Lili", json.path("persona").asText());
		assertEquals(10L, json.path("id").asLong());
	}
	
	@Order(5)
	@Test
	void testEliminar() throws JsonMappingException, JsonProcessingException {
		
		ResponseEntity<Cuenta[]> response = client.getForEntity(crearUri("/api/cuentas/"), Cuenta[].class);
		List<Cuenta> cuentas =  Arrays.asList(response.getBody());
		assertEquals(4, cuentas.size());
		
		//Todas las opciones son iguales
		//client.delete(crearUri("/api/cuentas/delete/1"));
		//ResponseEntity<Void> exchange = client.exchange(crearUri("/api/cuentas/delete/1"), HttpMethod.DELETE, null, Void.class);
		Map<String, Integer> pathVariable = new HashMap<>();
		pathVariable.put("id", 1);
		ResponseEntity<Void> exchange = client.exchange(crearUri("/api/cuentas/delete/{id}"), HttpMethod.DELETE, null, Void.class,
				pathVariable);
		
		assertEquals(HttpStatus.NO_CONTENT, exchange.getStatusCode());
		assertFalse(exchange.hasBody());
		
		response = client.getForEntity(crearUri("/api/cuentas/"), Cuenta[].class);
		cuentas =  Arrays.asList(response.getBody());
		assertEquals(3, cuentas.size());
		
		ResponseEntity<Cuenta> responseDetail = client.getForEntity(crearUri("/api/cuentas/1"), Cuenta.class);
		Cuenta cuenta = responseDetail.getBody();
		assertEquals(HttpStatus.NOT_FOUND, responseDetail.getStatusCode());
		assertFalse(responseDetail.hasBody());
		
	}
	

}
