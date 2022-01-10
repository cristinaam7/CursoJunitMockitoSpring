package com.example.springTest.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.springTest.domain.Cuenta;

public interface CuentaDao extends JpaRepository<Cuenta, Long> {
	
	//List<Cuenta> findAll();
	
	//Cuenta findById(Long id);
	
	//void update(Cuenta cuenta);
	
	@Query("SELECT c FROM Cuenta c WHERE c.persona=?1")
	Optional<Cuenta> findByPersona(String persona);
}
