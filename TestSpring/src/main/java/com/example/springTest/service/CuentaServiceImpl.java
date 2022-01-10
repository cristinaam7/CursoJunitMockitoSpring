package com.example.springTest.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.springTest.dao.BancoDao;
import com.example.springTest.dao.CuentaDao;
import com.example.springTest.domain.Banco;
import com.example.springTest.domain.Cuenta;
import com.example.springTest.exception.DineroInsuficienteException;

@Service
public class CuentaServiceImpl implements CuentaService{
	
	@Autowired
	CuentaDao cuentaDao;
	
	@Autowired
	BancoDao bancoDao;

	@Override
	@Transactional(readOnly=true)
	public Cuenta findById(Long id) {
		return this.cuentaDao.findById(id).orElse(null);
	}

	@Override
	@Transactional(readOnly=true)
	public int revisarTotalTransferencias(Long bancoId) {
		Banco banco = this.bancoDao.findById(bancoId).orElse(null);
		return banco.getTotalTransferencias();
	}

	@Override
	@Transactional(readOnly=true)
	public BigDecimal revisarSaldo(Long cuentaId) {
		Cuenta cuenta = this.cuentaDao.findById(cuentaId).orElse(null);
		return cuenta.getSaldo();
	}

	@Override
	@Transactional
	public void transferir(Long bancoId, Long cuentaOrigenId, Long cuentaDestinoId, BigDecimal cantidad) {
		
		Cuenta origen = this.cuentaDao.findById(cuentaOrigenId).orElse(null);
		origen.debito(cantidad);
		cuentaDao.save(origen);
		
		Cuenta destino = this.cuentaDao.findById(cuentaDestinoId).orElse(null);
		destino.credito(cantidad);
		cuentaDao.save(destino);
		
		Banco banco = this.bancoDao.findById(bancoId).orElse(null);
		int totalTransferencias = banco.getTotalTransferencias();
		banco.setTotalTransferencias(totalTransferencias+1);
		bancoDao.save(banco);
	}

	@Override
	@Transactional(readOnly=true)
	public List<Cuenta> findAll() {
		return cuentaDao.findAll();
	}

	@Override
	@Transactional
	public Cuenta save(Cuenta cuenta) {
		return cuentaDao.save(cuenta);
	}

	@Override
	@Transactional
	public void deleteById(Long id) {
		cuentaDao.deleteById(id);
	}

}
