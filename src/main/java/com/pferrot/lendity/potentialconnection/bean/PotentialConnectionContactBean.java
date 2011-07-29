package com.pferrot.lendity.potentialconnection.bean;

public class PotentialConnectionContactBean {
	
	private String email;
	private String name;
	
	public PotentialConnectionContactBean(String email, String name) {
		super();
		this.email = email;
		this.name = name;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.toLowerCase().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		PotentialConnectionContactBean other = (PotentialConnectionContactBean) obj;
		if (email == null) {
			if (other.email != null) {
				return false;
			}
		} 
		else {
			if (other.email == null) {
				return false;
			}
			else if (!email.toLowerCase().equals(other.email.toLowerCase())) {
				return false;
			}
		}
		return true;
	}
}
