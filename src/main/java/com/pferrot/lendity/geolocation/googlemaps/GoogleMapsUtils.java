package com.pferrot.lendity.geolocation.googlemaps;

import java.net.URLEncoder;

import com.pferrot.core.CoreUtils;
import com.pferrot.lendity.geolocation.exception.GeolocalisationException;
import com.pferrot.lendity.utils.JsfUtils;

public class GoogleMapsUtils {
	
	private static String GOOGLE_MAPS_URL = "http://maps.google.com";
	private static String QUERY_PARAMETER_NAME = "q";
	
	public static String getLocationUrl(final String pAddress) throws GeolocalisationException {
		try {
			CoreUtils.assertNotNull(pAddress);
			
			final String parameterValue = pAddress.replaceAll("\n", ", ").replaceAll("\r", "");
			
			return GOOGLE_MAPS_URL + "?" + QUERY_PARAMETER_NAME + "=" + URLEncoder.encode(parameterValue, JsfUtils.URL_ENCODING);
		}
		catch (Exception e) {
			throw new GeolocalisationException(e);
		}
	}
}
