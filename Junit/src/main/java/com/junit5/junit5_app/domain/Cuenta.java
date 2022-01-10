package com.junit5.junit5_app.domain;

import java.math.BigDecimal;
import com.junit5.junit5_app.exceptions.DineroInsuficienteException;

public class Cuenta {

	private String persona;
	private BigDecimal saldo;
	private Banco banco;
	
	public Cuenta(String persona, BigDecimal saldo) {
		this.persona = persona;
		this.saldo = saldo;
	}
	
	public String getPersona() {
		return persona;
	}
	
	public void setPersona(String persona) {
		this.persona = persona;
	}
	
	public BigDecimal getSaldo() {
		return saldo;
	}
	
	public void setSaldo(BigDecimal saldo) {
		this.saldo = saldo;
	}
	
	public Banco getBanco() {
		return banco;
	}

	public void setBanco(Banco banco) {
		this.banco = banco;
	}

	public void credito(BigDecimal monto) {
		this.saldo = this.saldo.add(monto);
	}
	
	public void debito(BigDecimal monto) throws DineroInsuficienteException{
		BigDecimal nuevoSaldo = this.saldo.subtract(monto);
		if(nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
			throw new DineroInsuficienteException("Saldo insuficiente");
		}
		this.saldo = nuevoSaldo;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Cuenta)) {
			return false;
		}
		
		Cuenta c = (Cuenta) obj;
		if(this.persona == null || this.saldo == null) {
			return false;
		}
		
		return this.persona.equals(c.getPersona()) && this.saldo.equals(c.getSaldo());
	}
}
