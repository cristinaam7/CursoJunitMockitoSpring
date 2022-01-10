package com.example.springTest.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.springTest.domain.Banco;

public interface BancoDao extends JpaRepository<Banco, Long> {

	//List<Banco> findAll();
	
	//Banco findById(Long id);
	
	//void update(Banco banco);
}
