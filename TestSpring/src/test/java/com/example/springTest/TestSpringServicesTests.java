package com.example.springTest;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.example.springTest.dao.BancoDao;
import com.example.springTest.dao.CuentaDao;
import com.example.springTest.domain.Banco;
import com.example.springTest.domain.Cuenta;
import com.example.springTest.exception.DineroInsuficienteException;
import com.example.springTest.service.CuentaService;

@SpringBootTest
class TestSpringServicesTests {
	
	@MockBean
	//@Mock
	CuentaDao cuentaDao;
	
	@MockBean
	//@Mock
	BancoDao bancoDao;
	
	@Autowired
	//@InjectMocks
	CuentaService cuentaService;
	
	@BeforeEach
	void setup() {
		//Reiniciar datos para que no se cambien entre metodos
		//Datos.CUENTA_01.setSaldo(new BigDecimal(1000));
		//Datos.CUENTA_02.setSaldo(new BigDecimal(3000));
		//Datos.BANCO.setTotalTransferencias(0);
	}

	@Test
	void testTransferir() {
		
		//GIVEN
		when(bancoDao.findById(1L)).thenReturn(Datos.crearBanco());
		when(cuentaDao.findById(1L)).thenReturn(Datos.crearCuenta01());
		when(cuentaDao.findById(2L)).thenReturn(Datos.crearCuenta02());
		
		//WHEN
		BigDecimal saldoOrigen = cuentaService.revisarSaldo(1L);
		BigDecimal saldoDestino = cuentaService.revisarSaldo(2L);
		
		//THEN
		assertEquals("1000", saldoOrigen.toString());
		assertEquals("3000", saldoDestino.toString());
		
		//WHEN
		cuentaService.transferir(1L, 1L, 2L, new BigDecimal(100));
		
		saldoOrigen = cuentaService.revisarSaldo(1L);
		saldoDestino = cuentaService.revisarSaldo(2L);
		
		//THEN
		assertEquals("900", saldoOrigen.toString());
		assertEquals("3100", saldoDestino.toString());
		
		assertEquals(1, cuentaService.revisarTotalTransferencias(1L));
		
		verify(cuentaDao, times(3)).findById(1L);
		verify(cuentaDao, times(3)).findById(2L);
		verify(cuentaDao, times(2)).save(any(Cuenta.class));
		
		verify(bancoDao, times(2)).findById(1L);
		verify(bancoDao).save(any(Banco.class));
		
		verify(cuentaDao, times(6)).findById(anyLong());
		verify(cuentaDao, never()).findAll();
		
	}
	
	@Test
	void testTransferirSinSaldo() {
		
		//GIVEN
		when(bancoDao.findById(1L)).thenReturn(Datos.crearBanco());
		when(cuentaDao.findById(1L)).thenReturn(Datos.crearCuenta01());
		when(cuentaDao.findById(2L)).thenReturn(Datos.crearCuenta02());
		
		//WHEN
		BigDecimal saldoOrigen = cuentaService.revisarSaldo(1L);
		BigDecimal saldoDestino = cuentaService.revisarSaldo(2L);
		
		//THEN
		assertEquals("1000", saldoOrigen.toString());
		assertEquals("3000", saldoDestino.toString());
		
		//WHEN
		assertThrows(DineroInsuficienteException.class, () -> {
			cuentaService.transferir(1L, 1L, 2L, new BigDecimal(2000));
		});
		
		
		saldoOrigen = cuentaService.revisarSaldo(1L);
		saldoDestino = cuentaService.revisarSaldo(2L);
		
		//THEN
		assertEquals("1000", saldoOrigen.toString());
		assertEquals("3000", saldoDestino.toString());
		
		assertEquals(0, cuentaService.revisarTotalTransferencias(1L));
		
		verify(cuentaDao, times(3)).findById(1L);
		verify(cuentaDao, times(2)).findById(2L);
		verify(cuentaDao, never()).save(any(Cuenta.class));
		
		verify(bancoDao, times(1)).findById(1L);
		verify(bancoDao, never()).save(any(Banco.class));
		
		verify(cuentaDao, times(5)).findById(anyLong());
		verify(cuentaDao, never()).findAll();
	}
	
	@Test
	void testCompararCuentas() {
		when(cuentaDao.findById(1L)).thenReturn(Datos.crearCuenta01());
		
		Cuenta cuenta1 = cuentaService.findById(1L);
		Cuenta cuenta2 = cuentaService.findById(1L);
		
		assertSame(cuenta1, cuenta2);
		assertTrue(cuenta1 == cuenta2);
		
		verify(cuentaDao, times(2)).findById(anyLong());
	}
	
	@Test
	void testListarCuentas() {
		//Given
		List<Cuenta> cuentas = new ArrayList<Cuenta>();
		cuentas.add(Datos.crearCuenta01().orElse(null));
		cuentas.add(Datos.crearCuenta02().orElse(null));
		
		when(cuentaDao.findAll()).thenReturn(cuentas);
		
		//When
		List<Cuenta> cuentasResult = cuentaService.findAll();
		
		//Then
		assertFalse(cuentasResult.isEmpty());
		assertEquals(2, cuentasResult.size());
		assertTrue(cuentasResult.contains(Datos.crearCuenta01().orElse(null)));
		
		verify(cuentaDao).findAll();
	}
	
	@Test
	void testCrearCuentas() {
		//Given
		Cuenta cuenta = new Cuenta(10L, "Lulu", new BigDecimal(4000));
		
		when(cuentaDao.save(Mockito.any())).thenReturn(cuenta);
		
		//When
		Cuenta cuentaSave = cuentaService.save(cuenta);
		
		//Then
		assertEquals("Lulu", cuentaSave.getPersona());
		assertEquals("4000", cuentaSave.getSaldo().toString());
		
		verify(cuentaDao).save(Mockito.any());
	}

}
