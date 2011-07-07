package com.pferrot.lendity.login.jsf;

import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.geolocation.bean.Coordinate;
import com.pferrot.lendity.geolocation.exception.GeolocalisationException;
import com.pferrot.lendity.geolocation.googlemaps.GoogleMapsUtils;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.utils.CookieUtils;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.security.SecurityUtils;

@ViewController(viewIds={"/login.jspx"})
public class HomePublicController extends AbstractHomeController {
	
	private final static Log log = LogFactory.getLog(HomePublicController.class);
	
	public final static String COOKIE_LOCATION_LABEL = "locationLabel";
	public final static String COOKIE_LOCATION_LATITUDE = "locationLatitude";
	public final static String COOKIE_LOCATION_LONGITUDE  = "locationLongitude";
	
	// City, NPA,...
	private String location;
	
	@InitView
	public void initView() {
		if (SecurityUtils.isLoggedIn()) {
			JsfUtils.redirect(PagesURL.HOME);
		}
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
		// Done in validator.
//		if (!StringUtils.isNullOrEmpty(location)) {
//			try {
//				final Coordinate coordinate = GoogleMapsUtils.getCoordinate(location);
//				createLocationLabelCookie(location);
//				createLocationLatitudeCookie(coordinate.getLatitude().toString());
//				createLocationLongitudeCookie(coordinate.getLongitude().toString());
//			}
//			catch (GeolocalisationException e) {
//				deleteLocationLabelCookie();
//				deleteLocationLatitudeCookie();
//				deleteLocationLongitudeCookie();
//			}
//		}
		JsfUtils.redirect(PagesURL.LOGIN);
	}
	
	private boolean isExampleAddressText(final String pText) {
		final Locale locale = I18nUtils.getDefaultLocale();
		final String exampleText = I18nUtils.getMessageResourceString("geolocation_inputLocationExample", locale);
		return exampleText != null && exampleText.equals(pText);
	}

	public void validateLocation(FacesContext context, UIComponent toValidate, Object value) {
		
		String message = "";
		String location = (String) value;
		if (isExampleAddressText(location)) {
			deleteLocationLabelCookie();
			deleteLocationLatitudeCookie();
			deleteLocationLongitudeCookie();
			
			((UIInput)toValidate).setValid(false);
			final Locale locale = I18nUtils.getDefaultLocale();
			message = I18nUtils.getMessageResourceString("validation_geolocationNotFound", locale);
			context.addMessage(toValidate.getClientId(context), new FacesMessage(message));		
		}
		else if (!StringUtils.isNullOrEmpty(location)) {	
			try {
				final Coordinate c = GoogleMapsUtils.getCoordinate(location);
				createLocationLabelCookie(location);
				createLocationLatitudeCookie(c.getLatitude().toString());
				createLocationLongitudeCookie(c.getLongitude().toString());
			}
			catch (GeolocalisationException e) {
				deleteLocationLabelCookie();
				deleteLocationLatitudeCookie();
				deleteLocationLongitudeCookie();
				
				((UIInput)toValidate).setValid(false);
				final Locale locale = I18nUtils.getDefaultLocale();
				message = I18nUtils.getMessageResourceString("validation_geolocationNotFound", locale);
				context.addMessage(toValidate.getClientId(context), new FacesMessage(message));					
			}			
		}
		else {
			deleteLocationLabelCookie();
			deleteLocationLatitudeCookie();
			deleteLocationLongitudeCookie();
		}
	}

	public String resetLocation() {
		deleteLocationLabelCookie();
		deleteLocationLatitudeCookie();
		deleteLocationLongitudeCookie();
		
		JsfUtils.redirect(PagesURL.LOGIN);
		return null;
	}

	public boolean isLocationLabelCookieAvailable() {
		return getLocationLabelCookieValue() != null;
	}

	public String getLocationLabelCookieValue() {
		return CookieUtils.getCookieValue(COOKIE_LOCATION_LABEL);		
	}
	
	public String getLocationLatitudeCookieValue() {
		return CookieUtils.getCookieValue(COOKIE_LOCATION_LATITUDE);		
	}
	
	public String getLocationLongitudeCookieValue() {
		return CookieUtils.getCookieValue(COOKIE_LOCATION_LONGITUDE);		
	}
	
	public void createLocationLabelCookie(final String pValue) {
		CookieUtils.createCookie(COOKIE_LOCATION_LABEL, pValue);		
	}
	
	public void createLocationLongitudeCookie(final String pValue) {
		CookieUtils.createCookie(COOKIE_LOCATION_LONGITUDE, pValue);		
	}
	
	public void createLocationLatitudeCookie(final String pValue) {
		CookieUtils.createCookie(COOKIE_LOCATION_LATITUDE, pValue);		
	}

	public void deleteLocationLabelCookie() {
		CookieUtils.deleteCookie(COOKIE_LOCATION_LABEL);		
	}
	
	public void deleteLocationLongitudeCookie() {
		CookieUtils.deleteCookie(COOKIE_LOCATION_LONGITUDE);		
	}
	
	public void deleteLocationLatitudeCookie() {
		CookieUtils.deleteCookie(COOKIE_LOCATION_LATITUDE);		
	}
	
	@Override
	protected boolean isLocationAvailable() {
		return isLocationLabelCookieAvailable();
	}
	
	@Override
	protected Double getLocationLatitude() {
		return Double.valueOf(getLocationLatitudeCookieValue());
	}
	
	@Override
	protected Double getLocationLongitude() {
		return Double.valueOf(getLocationLongitudeCookieValue());
	}
}
