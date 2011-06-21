package com.pferrot.lendity.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Index;

@Entity
@Table(name = "IP_TO_COUNTRY")
public class IpToCountry implements Serializable {	
	
	public final static String SWITZERLAND_CTRY = "CH";
	
	@Id
	@Column(name = "IP_FROM", nullable = false)
	private Long ipFrom;
	
	@Column(name = "IP_TO", nullable = false)
	@Index(name = "I_IP_TO")
	private Long ipTo;
	
	@Column(name = "REGISTRY", nullable = false, length = 20)
	private String registry;
	
	@Column(name = "ASSIGNED", nullable = false)
	private Long assigned;
	
	@Column(name = "CTRY", nullable = false, length = 2)
	private String ctry;
	
	@Column(name = "CNTRY", nullable = false, length = 3)
	private String cntry;
	
	@Column(name = "COUNTRY", nullable = false, length = 40)
	private String country;
	
    public IpToCountry() {
    	super();
    }

	public Long getIpFrom() {
		return ipFrom;
	}

	public void setIpFrom(Long ipFrom) {
		this.ipFrom = ipFrom;
	}

	public Long getIpTo() {
		return ipTo;
	}

	public void setIpTo(Long ipTo) {
		this.ipTo = ipTo;
	}

	public String getRegistry() {
		return registry;
	}

	public void setRegistry(String registry) {
		this.registry = registry;
	}

	public Long getAssigned() {
		return assigned;
	}

	public void setAssigned(Long assigned) {
		this.assigned = assigned;
	}

	public String getCtry() {
		return ctry;
	}

	public void setCtry(String ctry) {
		this.ctry = ctry;
	}

	public String getCntry() {
		return cntry;
	}

	public void setCntry(String cntry) {
		this.cntry = cntry;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ipFrom == null) ? 0 : ipFrom.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IpToCountry other = (IpToCountry) obj;
		if (ipFrom == null) {
			if (other.ipFrom != null)
				return false;
		} else if (!ipFrom.equals(other.ipFrom))
			return false;
		return true;
	}
}
