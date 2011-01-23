package com.pferrot.lendity.geolocation;

public class GeoLocationUtils {

	/**
	 * Returns the distance in kilometers between 2 points, using the "Spherical Law of Cosines".
	 * See the http://www.movable-type.co.uk/scripts/latlong.html
	 * 
	 * @param pLatitude1
	 * @param pLongitude1
	 * @param pLatitude2
	 * @param pLongitude2
	 * @return
	 */
	public static double getDistanceKm(final double pLatitude1, final double pLongitude1, final double pLatitude2, final double pLongitude2) {
		
		return Math.acos(
			     Math.sin(pLatitude1 * Math.PI / 180.0) * Math.sin(pLatitude2 * Math.PI / 180) + 
			     Math.cos(pLatitude1 * Math.PI / 180) * Math.cos(pLatitude2 * Math.PI / 180) * Math.cos((pLongitude2-pLongitude1) * Math.PI / 180)
			   ) * GeoLocationConsts.EARTH_RADIUS_KM;
	}
}
