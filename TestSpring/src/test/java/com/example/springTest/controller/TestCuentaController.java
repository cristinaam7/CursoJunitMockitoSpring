package com.example.springTest.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.util.Arrays;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.springTest.Datos;
import com.example.springTest.domain.Cuenta;
import com.example.springTest.domain.TransactionDto;
import com.example.springTest.service.CuentaService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(cuentaController.class)
class TestCuentaController {
	
	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private CuentaService cuentaService;
	
	private ObjectMapper objectMapper;
	
	@BeforeEach
	void setup() {
		objectMapper = new ObjectMapper();
	}

	@Test
	void testDetalle() throws Exception {
		//Given
		when(cuentaService.findById(1L)).thenReturn(Datos.crearCuenta01().orElse(null));
		
		//When
		mvc.perform(MockMvcRequestBuilders.get("/api/cuentas/1").contentType(MediaType.APPLICATION_JSON))
		//Then		
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.jsonPath("$.persona").value("Cris"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.saldo").value("1000"));
		
		verify(cuentaService).findById(1L);
	}
	
	@Test
	void testTransferir () throws JsonProcessingException, Exception {
		//Given
		TransactionDto dto = new TransactionDto();
		dto.setBancoId(1L);
		dto.setCuentaDestinoId(1L);
		dto.setCuentaOrigenId(2L);
		dto.setMonto(new BigDecimal(200));
		
		System.out.println(objectMapper.writeValueAsString(dto));
		
		Map<String, Object> response = new HashMap<>();
		response.put("date", LocalDate.now().toString());
		response.put("status", "ok");
		response.put("mensaje", "Transferencia realizada con éxito");
		response.put("transaction", dto);
		
		System.out.println(objectMapper.writeValueAsString(response));
		
		//When
		mvc.perform(MockMvcRequestBuilders.post("/api/cuentas/transferir")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto)))
		//Then
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.jsonPath("$.mensaje").value("Transferencia realizada con éxito"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.date").value("2021-12-23"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.transaction.cuentaOrigenId").value(2L))
				.andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(response)));
	}
	
	@Test
	void testListarCuentas() throws Exception {
		//Given
		List<Cuenta> cuentas = new ArrayList<Cuenta>();
		cuentas.add(Datos.crearCuenta01().orElse(null));
		cuentas.add(Datos.crearCuenta02().orElse(null));
		
		when(cuentaService.findAll()).thenReturn(cuentas);
		
		//When
		mvc.perform(MockMvcRequestBuilders.get("/api/cuentas/")
				.contentType(MediaType.APPLICATION_JSON))
		//Then
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].persona").value("Cris"))
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].saldo").value("1000"))
				.andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(cuentas)));			
	
		verify(cuentaService).findAll();
	}
	
	@Test
	void testGuardarCuenta() throws Exception {
		//Given
		Cuenta cuenta = new Cuenta(10L, "Manuel", new BigDecimal(6000));
		when(cuentaService.save(Mockito.any())).thenReturn(cuenta);
		
		//When
		mvc.perform(MockMvcRequestBuilders.post("/api/cuentas/guardar")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(cuenta)))
		//Then
				.andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.jsonPath("$.persona", Matchers.is("Manuel")))
				.andExpect(MockMvcResultMatchers.jsonPath("$.saldo").value("6000"));
		
		verify(cuentaService).save(cuenta);
	}

}
