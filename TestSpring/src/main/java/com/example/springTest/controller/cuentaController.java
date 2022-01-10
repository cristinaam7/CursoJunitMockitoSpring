package com.example.springTest.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.springTest.domain.Cuenta;
import com.example.springTest.domain.TransactionDto;
import com.example.springTest.service.CuentaService;

@RestController
@RequestMapping("/api/cuentas")
public class cuentaController {
	
	@Autowired
	CuentaService cuentaService;
	
	@GetMapping("/{id}")
	public ResponseEntity<?> detalle(@PathVariable(name="id") Long id) {
		Cuenta cuenta = cuentaService.findById(id);
		if(cuenta != null){
			return (ResponseEntity<?>) ResponseEntity.ok(cuenta);
		}
		else{
			return (ResponseEntity<?>) ResponseEntity.notFound().build();
		}
	}
	
	@GetMapping("/")
	@ResponseStatus(HttpStatus.OK)
	public List<Cuenta> listarCuentas() {
		return cuentaService.findAll();
	}
	
	@PostMapping("/guardar")
	@ResponseStatus(HttpStatus.CREATED)
	public Cuenta guardarCuenta(@RequestBody Cuenta cuenta){
		return cuentaService.save(cuenta);
	}

	@PostMapping("/transferir")
	public ResponseEntity<?> transferir(@RequestBody TransactionDto transaction){
		cuentaService.transferir(transaction.getBancoId(), 
								 transaction.getCuentaOrigenId(), 
								 transaction.getCuentaDestinoId(), 
								 transaction.getMonto());
		
		Map<String, Object> response = new HashMap<>();
		response.put("date", LocalDate.now().toString());
		response.put("status", "ok");
		response.put("mensaje", "Transferencia realizada con éxito");
		response.put("transaction", transaction);
		
		return ResponseEntity.ok(response);
	}
	
	@DeleteMapping("/delete/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void eliminar(@PathVariable(name="id") Long id) {
		cuentaService.deleteById(id);
	}
}
