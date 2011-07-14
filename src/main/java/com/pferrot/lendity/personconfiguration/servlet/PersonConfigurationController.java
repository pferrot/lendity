package com.pferrot.lendity.personconfiguration.servlet;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.personconfiguration.PersonConfigurationService;
import com.pferrot.lendity.personconfiguration.exception.PersonConfigurationException;


/**
 * Servlet to update person configurations.
 *
 * @author pferrot
 *
 */
public class PersonConfigurationController extends AbstractController {
	
	private final static Log log = LogFactory.getLog(PersonConfigurationController.class);
	
	public final static String ACTION_PARAMETER_NAME = "action";
//	public final static String ACTION_CREATE = "create";
//	public final static String ACTION_READ = "read";
	public final static String ACTION_UPDATE = "update";
	public final static String ACTION_DELETE = "delete";
	
	public final static String KEY_PARAMETER_NAME = "theKey";
	public final static String VALUE_PARAMETER_NAME = "theValue";
	
	public final static String CONTAINER_ITEM_ID_PARAMETER_NAME = "itemID";
	
	public final static String CONTAINER_NEED_ID_PARAMETER_NAME = "needID";
	
	public final static String CONTAINER_LEND_TRANSACTION_ID_PARAMETER_NAME = "lendTransactionID";
	
	public final static String CONTAINER_GROUP_ID_PARAMETER_NAME = "groupID";
	
	private PersonConfigurationService personConfigurationService;

	public PersonConfigurationController() {
		super();
	}
	
	public PersonConfigurationService getPersonConfigurationService() {
		return personConfigurationService;
	}
	
	public void setPersonConfigurationService(
			PersonConfigurationService personConfigurationService) {
		this.personConfigurationService = personConfigurationService;
	}

	
	/**
	 * Dispatches to the correct CRUD method depending on the "action" (request parameter name).
	 * Possible values is only "update" for now.
	 *
	 * @param pRequest
	 * @param pResponse
	 * @return
	 */
	protected ModelAndView handleRequestInternal(final HttpServletRequest pRequest, final HttpServletResponse pResponse) throws Exception {
		final String action = pRequest.getParameter(ACTION_PARAMETER_NAME);
		if (log.isDebugEnabled()) {
			log.debug("Action: " + action);
		}
		
		Map map = null;
		
//		if (ACTION_CREATE.equals(action)) {
//			map = create(pRequest, pResponse);	
//		}
//		else if (ACTION_READ.equals(action)) {
//			map = read(pRequest, pResponse);
//		}
		if (ACTION_UPDATE.equals(action)) {
			map = update(pRequest, pResponse);
		}
		else if (ACTION_DELETE.equals(action)) {
			map = delete(pRequest, pResponse);
		}
		else {
			throw new PersonConfigurationException("Unknonw action: " + action);
		}
		
		return new ModelAndView("personConfigurationJsonView", map);
	}
		
	private Map<String, Object> update(final HttpServletRequest pRequest, final HttpServletResponse pResponse) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			final String theKey = pRequest.getParameter(KEY_PARAMETER_NAME);
			final String theValue = pRequest.getParameter(VALUE_PARAMETER_NAME);
			final Long currentPersonId = PersonUtils.getCurrentPersonId(pRequest.getSession());
			getPersonConfigurationService().updateValue(currentPersonId, theKey, theValue);
			
			map.put("result", "success");
		}
		catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error(e);
			}
			map.put("result", "error");
		}
		
		return map;		
	}
	
	private Map<String, Object> delete(final HttpServletRequest pRequest, final HttpServletResponse pResponse) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			final String theKey = pRequest.getParameter(KEY_PARAMETER_NAME);
			final Long currentPersonId = PersonUtils.getCurrentPersonId(pRequest.getSession());
			getPersonConfigurationService().deleteValue(currentPersonId, theKey);
			
			map.put("result", "success");
		}
		catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error(e);
			}
			map.put("result", "error");
		}
		
		return map;		
	}	
}
