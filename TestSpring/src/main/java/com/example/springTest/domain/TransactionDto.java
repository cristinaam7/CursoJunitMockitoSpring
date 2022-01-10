package com.example.springTest.domain;

import java.math.BigDecimal;

public class TransactionDto {

	private Long BancoId;
	private Long cuentaOrigenId;
	private Long cuentaDestinoId;
	private BigDecimal monto;
	
	public Long getBancoId() {
		return BancoId;
	}
	public void setBancoId(Long bancoId) {
		BancoId = bancoId;
	}
	
	public Long getCuentaOrigenId() {
		return cuentaOrigenId;
	}
	public void setCuentaOrigenId(Long cuentaOrigenId) {
		this.cuentaOrigenId = cuentaOrigenId;
	}
	
	public Long getCuentaDestinoId() {
		return cuentaDestinoId;
	}
	public void setCuentaDestinoId(Long cuentaDestinoId) {
		this.cuentaDestinoId = cuentaDestinoId;
	}
	
	public BigDecimal getMonto() {
		return monto;
	}
	public void setMonto(BigDecimal monto) {
		this.monto = monto;
	}	
}
