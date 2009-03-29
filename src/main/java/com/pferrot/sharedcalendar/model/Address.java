package com.pferrot.sharedcalendar.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class Address implements Serializable {
	
	@Column(name = "ADDRESS_1", length = 255)
    private String address1;
	
	@Column(name = "ADDRESS_2", length = 255)
    private String address2;
	
	@Column(name = "ADDRESS_3", length = 255)
    private String address3;
	
	@Column(name = "ADDRESS_4", length = 255)
    private String address4;	
	
	@Column(name = "ZIP")
    private Integer zip;
	
	@Column(name = "CITY", length = 255)
    private String city;
	
	@ManyToOne(targetEntity = com.pferrot.sharedcalendar.model.Country.class)
	@JoinColumn(name = "COUNTRY_ID")	
	private Country country;

	
	public Address() {
		super();
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getAddress3() {
		return address3;
	}

	public void setAddress3(String address3) {
		this.address3 = address3;
	}

	public String getAddress4() {
		return address4;
	}

	public void setAddress4(String address4) {
		this.address4 = address4;
	}

	public Integer getZip() {
		return zip;
	}

	public void setZip(Integer zip) {
		this.zip = zip;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}
}
