package com.pferrot.sharedcalendar.registration.jsf;

import org.apache.commons.logging.Log;

import org.apache.commons.logging.LogFactory;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

import com.icesoft.faces.async.render.IntervalRenderer;
import com.icesoft.faces.async.render.RenderManager;
import com.icesoft.faces.async.render.Renderable;
import com.icesoft.faces.context.DisposableBean;
import com.icesoft.faces.webapp.xmlhttp.FatalRenderingException;
import com.icesoft.faces.webapp.xmlhttp.PersistentFacesState;
import com.icesoft.faces.webapp.xmlhttp.RenderingException;
import com.pferrot.sharedcalendar.registration.RegistrationService;

public class RegistrationStep1
// Renderable is NOT necessary in sync mode.
//implements Renderable, DisposableBean 
{
	
	private final static Log log = LogFactory.getLog(RegistrationStep1.class);
	
//	private final static int renderInterval = 1000;
	
	private RegistrationViewController registrationViewController;
	private RegistrationService registrationService;
//	private PersistentFacesState state;	 
//	private IntervalRenderer clock;

	
	public RegistrationStep1() {
		super();
//		state = PersistentFacesState.getInstance();
	}
	
	public RegistrationViewController getRegistrationViewController() {
		return registrationViewController;
	}

	public void setRegistrationViewController(
			RegistrationViewController registrationViewController) {
		this.registrationViewController = registrationViewController;
	}

	public RegistrationService getRegistrationService() {
		return registrationService;
	}

	public void setRegistrationService(RegistrationService registrationService) {
		this.registrationService = registrationService;
	}

	public String submit() {
		if (log.isDebugEnabled()) {
			log.debug("Clicked submit");
		}
		// Validation is done by methods below.
		return "success";
	}
	
	public void validateEmail(FacesContext context, UIComponent toValidate, Object value) {
		String message = "";
		String email = (String) value;
		if (!email.contains("@")) {
			((UIInput)toValidate).setValid(false);
			// TODO
			message = "Email not valid";
			//message = CoffeeBreakBean.loadErrorMessage(context, CoffeeBreakBean.CB_RESOURCE_BUNDLE_NAME, "EMailError");
			context.addMessage(toValidate.getClientId(context), new FacesMessage(message));
		}
	}
	
	public void validateUsername(FacesContext context, UIComponent toValidate, Object value) {
		String message = "";
		String username = (String) value;
		if (!registrationService.isUsernameAvailable(username)) {
			((UIInput)toValidate).setValid(false);
			// TODO
			message = "Username already used";
			//message = CoffeeBreakBean.loadErrorMessage(context, CoffeeBreakBean.CB_RESOURCE_BUNDLE_NAME, "EMailError");
			context.addMessage(toValidate.getClientId(context), new FacesMessage(message));
		}
	}
	
	// TODO: Does not work: getPassword() always returns null.
//	public void validatePasswordRepeat(FacesContext context, UIComponent toValidate, Object value) {
//		String message = "";
//		String passwordRepeat = (String) value;
//		if (!passwordRepeat.equals(getPassword())) {
//			((UIInput)toValidate).setValid(false);
//			// TODO
//			message = "Confirmation password does not match password";
//			//message = CoffeeBreakBean.loadErrorMessage(context, CoffeeBreakBean.CB_RESOURCE_BUNDLE_NAME, "EMailError");
//			context.addMessage(toValidate.getClientId(context), new FacesMessage(message));
//		}
//	}	

//	public PersistentFacesState getState() {
//		return state;
//	}

//	public void renderingException(RenderingException renderingException) {
//		if (log. isDebugEnabled()) {			 
//			log.debug("Rendering exception:  " + renderingException);
//		}	 
//		if (renderingException instanceof FatalRenderingException) { 
//			performCleanup();	 
//		}		
//	}

//	protected boolean performCleanup() {
//		try {
//			if (clock != null) {	
//				clock.remove(this);
//				if (clock.isEmpty() ) { 
//					clock.dispose();
//				}
//				clock = null;
//			}
//			return true;
//		} 
//		catch (Exception failedCleanup) {
//			if (log.isErrorEnabled()) {
//				log.error("Failed to cleanup a clock bean", failedCleanup);
//			}
//		}
//		return false;
//	}
	
//	public void setRenderManager(RenderManager renderManager) {
//		clock = renderManager.getIntervalRenderer("clock"); 
//		clock.setInterval(renderInterval); 
//		clock.add(this);  
//		clock.requestRender();
//	}

//	public void dispose() throws Exception {
//        if (log.isInfoEnabled()) {        	 
//            log.info("Dispose RegistrationBean for a user - cleaning up"); 
//        } 
//        performCleanup();		
//	}

}
