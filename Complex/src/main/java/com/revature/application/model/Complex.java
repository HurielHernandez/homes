package com.revature.application.model;

import java.util.List;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
//import com.revature.apartment.model.Unit;

@Entity
@Table(name = "COMPLEX")
public class Complex {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int complexId;
	private String website;
	private String email;
	private String phone;
	private String name;
	private String address;
	private String parking;
	private String photoUrl;

	@Column(unique = true)
	@Size(max = 12)
	private String abbreviation; // abbreviation of the name unique and under 12 char

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OFFICE_ID")
	private Office office;

	@OneToMany(mappedBy = "complex", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	@JsonIgnore
	List<Unit> units;

	public Complex(String website, String email, String phone, String name, String abbreviation, String address,
			String parking, String photoUrl, Office office) {
		super();
		this.website = website;
		this.email = email;
		this.phone = phone;
		this.name = name;
		this.abbreviation = abbreviation;
		this.address = address;
		this.parking = parking;
		this.photoUrl = photoUrl;
		this.office = office;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Complex() {
	}

	public int getComplexId() {
		return complexId;
	}

	public void setComplexId(int complexId) {
		this.complexId = complexId;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParking() {
		return parking;
	}

	public void setParking(String parking) {
		this.parking = parking;
	}

	public String getAbbreviation() {
		return abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}

	public List<Unit> getUnits() {
		return units;
	}

	public void setUnits(List<Unit> units) {
		this.units = units;
	}

}
