package com.example.springTest.service;

import java.math.BigDecimal;
import java.util.List;

import com.example.springTest.domain.Cuenta;
import com.example.springTest.exception.DineroInsuficienteException;

public interface CuentaService {
	
	List<Cuenta> findAll();
	
	Cuenta save(Cuenta cuenta);

	Cuenta findById(Long id);
	
	int revisarTotalTransferencias(Long bancoId);
	
	BigDecimal revisarSaldo(Long cuentaId);
	
	void transferir(Long bancoId, Long cuentaOrigenId, Long cuentaDestinoId, BigDecimal cantidad);
	
	void deleteById(Long id);
}
