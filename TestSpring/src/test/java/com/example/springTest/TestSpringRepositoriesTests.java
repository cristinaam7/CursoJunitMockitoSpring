package com.example.springTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.springTest.dao.CuentaDao;
import com.example.springTest.domain.Cuenta;

@DataJpaTest
class TestSpringRepositoriesTests {
	
	@Autowired
	CuentaDao cuentaDao;

	@Test
	void testFindById() {
		Optional<Cuenta> cuenta = cuentaDao.findById(1L);
		assertTrue(cuenta.isPresent());
		assertEquals("Cris", cuenta.get().getPersona());
	}
	
	@Test
	void testFindByPersona() {
		Optional<Cuenta> cuenta = cuentaDao.findByPersona("Cris");
		assertTrue(cuenta.isPresent());
		assertEquals("Cris", cuenta.get().getPersona());
		assertEquals("1000.00", cuenta.get().getSaldo().toString());
	}
	
	@Test
	void testFindByPersonaNull() {
		Optional<Cuenta> cuenta = cuentaDao.findByPersona("Lolo");
		assertFalse(cuenta.isPresent());
	}
	
	@Test
	void testFindAll() {
		List<Cuenta> cuentas = cuentaDao.findAll();
		assertFalse(cuentas.isEmpty());
		assertEquals(3, cuentas.size());
	}
	
	@Test
	void testSave(){
		//Given
		Cuenta cuentaLolo = new Cuenta(10L, "Lolo", new BigDecimal(2500));
		
		//When
		Cuenta cuenta = cuentaDao.save(cuentaLolo);
		
		//Then
		assertNotNull(cuenta);
		assertEquals("Lolo", cuenta.getPersona());
		assertEquals("2500", cuenta.getSaldo().toString());
	}
	
	@Test
	void testUpdate(){
		//Given
		Cuenta cuentaLolo = new Cuenta(10L, "Lolo", new BigDecimal(2500));
		Cuenta cuenta = cuentaDao.save(cuentaLolo);
		cuentaLolo.setSaldo(new BigDecimal(3500));
		
		//When
		cuenta = cuentaDao.save(cuentaLolo);
		
		//Then
		assertEquals("Lolo", cuenta.getPersona());
		assertEquals("3500", cuenta.getSaldo().toString());
		
	}
	
	@Test
	void testDelete(){
		//Given
		Cuenta cuentaLolo = new Cuenta(10L, "Lolo", new BigDecimal(2500));
		Cuenta cuenta = cuentaDao.save(cuentaLolo);
		
		//When
		cuentaDao.delete(cuenta);
		Optional<Cuenta> cuentaDel = cuentaDao.findByPersona("Lolo");
		
		//Then
		assertFalse(cuentaDel.isPresent());
	}

}
