package com.example.springTest.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.example.springTest.domain.Cuenta;
import com.example.springTest.domain.TransactionDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
class cuentaControllerWebClientTest {
	
	@Autowired
	private WebTestClient client;
	
	private ObjectMapper objectMapper;
	
	@BeforeEach
	void setup() {
		objectMapper = new ObjectMapper();
	}
	
	@Test
	@Order(1)
	void testDetalle() throws Exception {
		//Given
		Cuenta cuenta = new Cuenta(1L, "Cris", new BigDecimal(1000));
		
		client.get().uri("/api/cuentas/1")
		.exchange()
		//Then
		.expectStatus().isOk()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.expectBody()
		.jsonPath("$.persona").isEqualTo("Cris")
		.jsonPath("$.saldo").isEqualTo("1000.0")
		.json(objectMapper.writeValueAsString(cuenta));
	}
	
	@Test
	@Order(2)
	void testDetalle2() throws Exception {
		
		client.get().uri("/api/cuentas/2")
		.exchange()
		//Then
		.expectStatus().isOk()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.expectBody(Cuenta.class)
		.consumeWith( response -> {
			Cuenta cuenta = response.getResponseBody();
			assertEquals("Piti", cuenta.getPersona());
			assertEquals("3000.00", cuenta.getSaldo().toString());
		});
	}

	@Test
	@Order(3)
	void testTransferir() throws JsonProcessingException {
		//Given
		TransactionDto dto = new TransactionDto();
		dto.setBancoId(1L);
		dto.setCuentaDestinoId(1L);
		dto.setCuentaOrigenId(2L);
		dto.setMonto(new BigDecimal(200));
		
		Map<String, Object> response = new HashMap<>();
		response.put("date", LocalDate.now().toString());
		response.put("status", "ok");
		response.put("mensaje", "Transferencia realizada con éxito");
		response.put("transaction", dto);
		
		//When
		//client.post().uri("http://localhost:8080/api/cuentas/transferir")
		//Si esta en el mismo server no hace dalta poner el localhost
		client.post().uri("/api/cuentas/transferir")
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(dto)
			.exchange()
		//Then
			.expectStatus().isOk()
			.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectBody()
			//.expectBody(String.class)
			.consumeWith(respuesta -> {
				
				try {
					//Guardamos la respuesta en un json para poder validar
					JsonNode json = objectMapper.readTree(respuesta.getResponseBody());
					assertEquals("Transferencia realizada con éxito", json.path("mensaje").asText());
					assertEquals(1L, json.path("transaction").path("cuentaDestinoId").asLong());
					assertEquals(LocalDate.now().toString(), json.path("date").asText());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			})
			//Esto es lo mismo que lo de consumeWith
			//Dos formas diferentes para hacer las mismas comprobaciones
			.jsonPath("$.mensaje").isNotEmpty()
			.jsonPath("$.mensaje").value(is("Transferencia realizada con éxito"))
			.jsonPath("$.mensaje").value( valor -> assertEquals(valor, "Transferencia realizada con éxito"))
			.jsonPath("$.mensaje").isEqualTo("Transferencia realizada con éxito")
			.jsonPath("$.transaction.cuentaOrigenId").isEqualTo(2L)
			.json(objectMapper.writeValueAsString(response));
	}
	
	@Test
	@Order(4)
	void testListarCuentas() {
		
		client.get().uri("/api/cuentas/")
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectBody()
			.jsonPath("$").isArray()
			.jsonPath("$").value(Matchers.hasSize(3))
			.jsonPath("$[0].persona").isEqualTo("Cris")
			.jsonPath("$[1].persona").isEqualTo("Piti");
		
	}

	@Test
	@Order(5)
	void testListarCuentas2() {
		
		client.get().uri("/api/cuentas/")
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectBodyList(Cuenta.class)
			.consumeWith( response -> {
				List<Cuenta> cuentas = response.getResponseBody();
				
				assertNotNull(cuentas);
				assertEquals(3, cuentas.size());
				assertEquals("Cris", cuentas.get(0).getPersona());
				assertEquals("Piti", cuentas.get(1).getPersona());
			})
			.hasSize(3)
			.value(Matchers.hasSize(3));
	}
	
	@Test
	@Order(6)
	void testGuardasCuentas() {
		
		//Given
		Cuenta cuenta = new Cuenta(10L, "Lili", new BigDecimal(3000));
		
		//When
		client.post().uri("/api/cuentas/guardar")
		.contentType(MediaType.APPLICATION_JSON)
		.bodyValue(cuenta)
		.exchange()
		//Then
		.expectStatus().isCreated()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.expectBody()
		.jsonPath("$.persona").isEqualTo("Lili")
		.jsonPath("$.saldo").isEqualTo("3000");
	}
	
	@Test
	@Order(7)
	void testGuardasCuentas2() {
		
		//Given
		Cuenta cuenta = new Cuenta(11L, "Lili", new BigDecimal(3000));
		
		//When
		client.post().uri("/api/cuentas/guardar")
		.contentType(MediaType.APPLICATION_JSON)
		.bodyValue(cuenta)
		.exchange()
		//Then
		.expectStatus().isCreated()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.expectBody(Cuenta.class)
		.consumeWith( response -> {
			Cuenta cuentaResponse = response.getResponseBody();
			
			assertNotNull(cuentaResponse);
			assertEquals("Lili", cuentaResponse.getPersona());
			assertEquals("3000", cuentaResponse.getSaldo().toString());
		});
	}
	
	@Test
	@Order(8)
	void testEliminarCuenta() {
		
		client.get().uri("/api/cuentas/")
		.exchange()
		.expectStatus().isOk()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.expectBodyList(Cuenta.class)
		.hasSize(5);
		
		client.delete().uri("/api/cuentas/delete/1")
			.exchange()
			.expectStatus().isNoContent()
			.expectBody().isEmpty();
		
		client.get().uri("/api/cuentas/")
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectBodyList(Cuenta.class)
			.consumeWith( response -> {
				List<Cuenta> cuentas = response.getResponseBody();
				
				System.out.println(cuentas.get(0).getPersona());
				System.out.println(cuentas.get(0).getId());
				System.out.println(cuentas.get(1).getPersona());
				System.out.println(cuentas.get(1).getId());
				System.out.println(cuentas.get(2).getPersona());
				System.out.println(cuentas.get(2).getId());
				System.out.println(cuentas.get(3).getPersona());
				System.out.println(cuentas.get(3).getId());
			})
			.hasSize(4);
		
		client.get().uri("/api/cuentas/1")
			.exchange()
			.expectStatus().isNotFound()
			.expectBody().isEmpty();
	}
}
