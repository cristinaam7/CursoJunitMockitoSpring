package com.example.springTest.exception;

public class DineroInsuficienteException extends RuntimeException{

	public DineroInsuficienteException() {
		super("No tiene saldo suficiente para realizar la operación");
	}
}
