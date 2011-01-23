package com.pferrot.lendity.geolocation.bean;

public class Coordinate {
	
	private Double longitude;
	private Double latitude;
	
	public Coordinate(final Double pLatitude, final Double pLongitude) {
		super();
		this.longitude = pLongitude;
		this.latitude = pLatitude;
	}
	
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(final Double pLongitude) {
		this.longitude = pLongitude;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(final Double pLatitude) {
		this.latitude = pLatitude;
	}

	@Override
	public String toString() {
		return "Coordinate [longitude=" + longitude + ", latitude=" + latitude + "]";
	}	
}
