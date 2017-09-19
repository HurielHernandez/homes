package com.revature.application.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.revature.application.model.Complex;
import com.revature.application.model.Office;
import com.revature.application.model.Unit;
import com.revature.application.service.ComplexService;
import com.revature.application.service.OfficeService;

@RestController
@RequestMapping("/complex")
public class ComplexController {
	@Autowired
	ComplexService cs;
	
	@GetMapping
	public Object findAll() {
		return cs.findAll();
	}
	
	@GetMapping("{id}")
	public Object findOne(@PathVariable("id") int id) {
		return cs.findByComplexId(id);
	}
	
	@GetMapping("{id}/units")
	public Object findUnits(@PathVariable("id") int id) {
		return cs.findByComplexId(id).getUnits();
	}
	
	@PostMapping
	public Object createComplex(@RequestBody Complex complex) {
		return cs.save(complex);
	}
	
	@PutMapping(value = "{id}")
	public Object updateComplex(@PathVariable("id") int id, @RequestBody Complex complex) {
		Complex com = cs.findByComplexId(id);
		if(complex.getCity() != null) com.setCity(complex.getCity());
		if(complex.getStreet() != null) com.setStreet(complex.getStreet());
		if(complex.getState	() != null) com.setState(complex.getState());
		if(complex.getZip() != null) com.setZip(complex.getZip());
		if(complex.getParking() != null) com.setParking(complex.getParking());
		if(complex.getWebsite() != null) com.setWebsite(complex.getWebsite());
		if(complex.getEmail() != null) com.setEmail(complex.getEmail());
		if(complex.getPhone() != null) com.setPhone(complex.getPhone());
		if(complex.getName() != null) com.setName(complex.getName());
		if(complex.getAbbreviation() != null) com.setAbbreviation(complex.getAbbreviation());
		cs.save(com);
		return cs.update(complex);
	}

	@DeleteMapping(value = "{id}")
	public Object deleteComplex(@PathVariable("id") int id){
		 return cs.delete(id);
		 
	}
	
}
