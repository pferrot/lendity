package com.pferrot.lendity.geolocation;

import com.pferrot.lendity.i18n.I18nUtils;

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
	
	/**
	 * Returns a text indicating the approximative between 2 points.
	 *
	 * @param pLatitude1
	 * @param pLongitude1
	 * @param pLatitude2
	 * @param pLongitude2
	 * @return
	 */
	public static String getApproxDistanceKm(final double pLatitude1, final double pLongitude1, final double pLatitude2, final double pLongitude2) {
		final double distance = getDistanceKm(pLatitude1, pLongitude1, pLatitude2, pLongitude2);
		
		if (distance < 1.0) {
			return I18nUtils.getMessageResourceString("geolocation_distanceLessThanKm", new Object[]{"1"}, I18nUtils.getDefaultLocale());
		}
		else {
			final Integer ceilInt = new Integer(Double.valueOf(Math.ceil(distance)).intValue());
			return I18nUtils.getMessageResourceString("geolocation_distanceAroundKm", new Object[]{ceilInt}, I18nUtils.getDefaultLocale());
		}		
	}

	public static String getApproxDistanceKm(final Double pLatitude1, final Double pLongitude1, final Double pLatitude2, final Double pLongitude2) {
		if (pLatitude1 == null || pLongitude1 == null || pLatitude2 == null || pLongitude2 == null) {
			return I18nUtils.getMessageResourceString("geolocation_distanceNotAvailable", I18nUtils.getDefaultLocale());
		}
		else {
			return getApproxDistanceKm(pLatitude1.doubleValue(), pLongitude1.doubleValue(), pLatitude2.doubleValue(), pLongitude2.doubleValue());
		}	
	}

	/**
	 * Return the formula to be used when querying the DB by distance.
	 *
	 * @param pOriginLongitude
	 * @param pOriginLatitude
	 * @return
	 */
	public static String getDistanceFormula(final Double pOriginLongitude, final Double pOriginLatitude) {
		return getDistanceFormula(pOriginLatitude.toString(), pOriginLatitude.toString(), pOriginLongitude.toString());
	}
	
	public static String getDistanceFormula(final String pString1, final String pString2, final String pString3) {
		return "acos(sin(" + pString1 + " * pi()/180) * sin({0} * pi()/180) + " +
				"cos(" + pString2 + " * pi()/180) * cos({1} * pi()/180) * cos(({2} - " + pString3 + ") * pi()/180)) * 6371";
	}
}
