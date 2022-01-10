package com.example.springTest;

import java.math.BigDecimal;
import java.util.Optional;

import com.example.springTest.domain.Banco;
import com.example.springTest.domain.Cuenta;

public class Datos {
	
	//public static final Cuenta CUENTA_01 = new Cuenta(1L, "Cris", new BigDecimal(1000));
	//public static final Cuenta CUENTA_02 = new Cuenta(2L, "Piti", new BigDecimal(3000));
	
	//public static final Banco BANCO = new Banco(1L, "Triodos", 0);
	
	//Mejor usar métodos porque asi no se pisan los datos entre metodos
	
	public static Optional<Cuenta> crearCuenta01() {
		return Optional.of(new Cuenta(1L, "Cris", new BigDecimal(1000)));
	}
	
	public static Optional<Cuenta> crearCuenta02() {
		return Optional.of(new Cuenta(2L, "Piti", new BigDecimal(3000)));
	}
	
	public static Optional<Banco> crearBanco() {
		return Optional.of(new Banco(1L, "Triodos", 0));
	}

}
