package com.pferrot.lendity.geolocation.googlemaps;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.pferrot.core.CoreUtils;
import com.pferrot.lendity.geolocation.bean.Coordinate;
import com.pferrot.lendity.geolocation.exception.GeolocalisationException;
import com.pferrot.lendity.utils.JsfUtils;

public class GoogleMapsUtils {
	
	private final static Log log = LogFactory.getLog(GoogleMapsUtils.class);
	
	private static String GOOGLE_MAPS_URL = "http://maps.google.com";
	private static String QUERY_PARAMETER_NAME = "q";
	
	private static String GEOCODING_XML_URL = "http://maps.googleapis.com/maps/api/geocode/xml";
	private static String GEOCODING_ADDRESS_PARAMETER_NAME = "address";
	private static String GEOCODING_SENSOR_PARAMETER_NAME = "sensor";
	private static String GEOCODING_SENSOR_PARAMETER_VALUE = "false";

	/**
	 * Given an address, returns the Google Maps URL for that address.
	 * 
	 * @param pAddress
	 * @return
	 * @throws GeolocalisationException
	 */
	public static String getLocationUrl(final String pAddress) throws GeolocalisationException {
		try {
			CoreUtils.assertNotNullOrEmptyString(pAddress);
			
			return GOOGLE_MAPS_URL + "?" + QUERY_PARAMETER_NAME + "=" + getParameterValue(pAddress);
		}
		catch (Exception e) {
			throw new GeolocalisationException(e);
		}
	}
	
	/**
	 * Given an address, returns its coordinate if possible. GeolocalisationException is thrown
	 * if the address cannot be found.
	 * 
	 * @param pAddress
	 * @return
	 * @throws GeolocalisationException
	 */
	public static Coordinate getCoordinate(final String pAddress) throws GeolocalisationException {
		if (log.isDebugEnabled()) {
			log.debug("Searching coordinate for address: ");
			log.debug(pAddress);
		}
		
		InputStream is = null;
		try {
			CoreUtils.assertNotNullOrEmptyString(pAddress);
			final StringBuffer url = new StringBuffer(GEOCODING_XML_URL);
			url.append("?");
			url.append(GEOCODING_ADDRESS_PARAMETER_NAME);
			url.append("=");
			url.append(getParameterValue(pAddress));
			url.append("&");
			url.append(GEOCODING_SENSOR_PARAMETER_NAME);
			url.append("=");
			url.append(GEOCODING_SENSOR_PARAMETER_VALUE);
			
			final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			final DocumentBuilder db = dbf.newDocumentBuilder();
			is = new URL(url.toString()).openStream();
			final Document doc = db.parse(is);
			
			final Element status = (Element)doc.getElementsByTagName("status").item(0);
			final String statusValue = ((Node)status.getChildNodes().item(0)).getNodeValue().trim();
			
			if (!"OK".equals(statusValue)) {
				throw new GeolocalisationException("Error with request. Response status: " + statusValue);
			}
			
			final Element result = (Element)doc.getElementsByTagName("result").item(0);
			final Element geometry = (Element)result.getElementsByTagName("geometry").item(0);
			final Element location = (Element)geometry.getElementsByTagName("location").item(0);
			
			final Element longitude = (Element)location.getElementsByTagName("lng").item(0);
			final Double longitudeDouble = Double.valueOf(((Node)longitude.getChildNodes().item(0)).getNodeValue().trim());
			
			final Element latitude = (Element)location.getElementsByTagName("lat").item(0);
			final Double latitudeDouble = Double.valueOf(((Node)latitude.getChildNodes().item(0)).getNodeValue().trim());
			
			if (log.isDebugEnabled()) {
				log.debug("Coordinate found: ");
				log.debug("  Latitude: " + latitudeDouble.toString());
				log.debug("  Longitude: " + longitudeDouble.toString());				
			}
			
			return new Coordinate(latitudeDouble, longitudeDouble);		
		}
		catch (Exception e) {
			throw new GeolocalisationException(e);
		}
		finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					throw new GeolocalisationException(e);
				}
			}
		}
	}
	
	/**
	 * Given a "raw" value, new line characters are replaced by a comma and the
	 * text is UTF-8 escaped.
	 *  
	 * @param pRawValue
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private static String getParameterValue(final String pRawValue) throws UnsupportedEncodingException {
		return URLEncoder.encode(pRawValue.replaceAll("\n", ", ").replaceAll("\r", ""), JsfUtils.URL_ENCODING);
	}	
}
