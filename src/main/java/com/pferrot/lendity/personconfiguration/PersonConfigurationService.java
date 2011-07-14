package com.pferrot.lendity.personconfiguration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.CoreUtils;
import com.pferrot.lendity.dao.PersonConfigurationDao;
import com.pferrot.lendity.model.PersonConfiguration;
import com.pferrot.lendity.person.PersonService;
import com.pferrot.lendity.personconfiguration.exception.PersonConfigurationException;

public class PersonConfigurationService {
	
	private final static Log log = LogFactory.getLog(PersonConfigurationService.class);
	
	private PersonService personService;
	private PersonConfigurationDao personConfigurationDao;

	// This will prevent anyone from inserting crap in the DB through the json controller.
//	private static Map<String, Collection<String>> allowedConfigurations = new HashMap<String, Collection<String>>();

	
	public PersonService getPersonService() {
		return personService;
	}

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	public PersonConfigurationDao getPersonConfigurationDao() {
		return personConfigurationDao;
	}

	public void setPersonConfigurationDao(
			PersonConfigurationDao personConfigurationDao) {
		this.personConfigurationDao = personConfigurationDao;
	}
	
//	public static void addAllowedConfiguration(final String pKey, final String pValue) {
//		CoreUtils.assertNotNullOrEmptyString(pKey);
//		CoreUtils.assertNotNullOrEmptyString(pValue);
//		
//		if (!allowedConfigurations.containsKey(pKey)) {
//			final Collection<String> values = new ArrayList<String>();
//			values.add(pValue);
//			allowedConfigurations.put(pKey, values);
//		}
//		else {
//			final Collection<String> values = allowedConfigurations.get(pKey);
//			if (!values.contains(pValue)) {
//				values.add(pValue);
//			}
//		}		
//	}
//	
//	public static boolean isAllowedConfiguration(final String pKey, final String pValue) {
//		if (StringUtils.isNullOrEmpty(pKey) ||
//			StringUtils.isNullOrEmpty(pValue)) {
//			return false;
//		}
//		
//		if (!allowedConfigurations.containsKey(pKey)) {
//			return false;
//		}
//		else {
//			final Collection<String> values = allowedConfigurations.get(pKey);
//			return values.contains(pValue);
//		}
//	}
//	
//	public void assertAllowedConfiguration(final String pKey, final String pValue) throws PersonConfigurationException {
//		if (!isAllowedConfiguration(pKey, pValue)) {
//			throw new PersonConfigurationException("Configuration is not allowed. Key: '" + pKey + "', value: '" + pValue + "'");
//		}
//	}

	// The identifier is the entity class itself, see http://docs.jboss.org/hibernate/annotations/3.5/reference/en/html_single/ § 2.2.3.2
	public PersonConfiguration createPersonConfiguration(final PersonConfiguration pPersonConfiguration) throws PersonConfigurationException {
		// TODO: we should validate that not any thing is inserted in the DB...
//		assertAllowedConfiguration(pPersonConfiguration.getKey(), pPersonConfiguration.getValue());
		return getPersonConfigurationDao().createPersonConfiguration(pPersonConfiguration);
	}
	
	public PersonConfiguration findPersonConfiguration(final Long pPersonId, final String pKey) {
		return getPersonConfigurationDao().findPersonConfiguration(pPersonId, pKey);
	}
	
	public String findPersonConfigurationValue(final Long pPersonId, final String pKey) {
		return getPersonConfigurationDao().findPersonConfigurationValue(pPersonId, pKey);
	}
	
	public void updatePersonConfiguration(final PersonConfiguration pPersonConfiguration) throws PersonConfigurationException {
		// TODO: we should validate that not any thing is inserted in the DB...
//		assertAllowedConfiguration(pPersonConfiguration.getKey(), pPersonConfiguration.getValue());
		getPersonConfigurationDao().createPersonConfiguration(pPersonConfiguration);
	}
	
	public void deletePersonConfiguration(final PersonConfiguration pPersonConfiguration) {
		getPersonConfigurationDao().deletePersonConfiguration(pPersonConfiguration);
	}
	
	/**
	 * The parameter must be passed WITHOUT the "show help key prefix".
	 * 
	 * @param pPersonId
	 * @param pHelpKey
	 * @return
	 */
//	public boolean isShowHelpAutomatically(final Long pPersonId, final String pHelpKey) {
//		CoreUtils.assertNotNull(pPersonId);
//		CoreUtils.assertNotNullOrEmptyString(pHelpKey);
//		
//		final String configValue = findPersonConfigurationValue(PersonUtils.getCurrentPersonId(), PersonConfigurationConsts.SHOW_HELP_KEY_PREFIX + pHelpKey);
//		if (PersonConfigurationConsts.HIDE_HELP_VALUE.equals(configValue)) {
//			return false;
//		}
//		else {
//			return true;
//		}		
//	}
	
	/**
	 * This will handle both cases whether there is already a value or not.
	 * The parameter must be passed WITHOUT the "show help key prefix".
	 * 
	 * @param pPersonId
	 * @param pHelpKey
	 * @throws PersonConfigurationException 
	 */
//	public void updateDoNotShowHelpAutomatically(final Long pPersonId, final String pHelpKey) throws PersonConfigurationException {
//		updateValue(pPersonId, PersonConfigurationConsts.SHOW_HELP_KEY_PREFIX + pHelpKey, PersonConfigurationConsts.HIDE_HELP_VALUE);
//		
//	}
	
	/**
	 * This will handle both cases whether there is already a value or not.
	 * 
	 * @param pPersonId
	 * @param pHelpKey
	 * @throws PersonConfigurationException 
	 */
	public void updateValue(final Long pPersonId, final String pKey, final String pValue) throws PersonConfigurationException {
		CoreUtils.assertNotNull(pPersonId);
		CoreUtils.assertNotNullOrEmptyString(pKey);
		CoreUtils.assertNotNullOrEmptyString(pValue);
		
		PersonConfiguration pc = findPersonConfiguration(pPersonId, pKey);
		
		if (pc != null) {
			if (!pValue.equals(pc.getValue())) {
				pc.setValue(pValue);
				updatePersonConfiguration(pc);
			}
		}
		else {
			pc = new PersonConfiguration();
			pc.setPerson(getPersonService().findPerson(pPersonId));
			pc.setKey(pKey);
			pc.setValue(pValue);
			createPersonConfiguration(pc);
		}
		
	}

	public void deleteValue(final Long pPersonId, final String pTheKey) {
		CoreUtils.assertNotNull(pPersonId);
		CoreUtils.assertNotNullOrEmptyString(pTheKey);
		final PersonConfiguration pc = new PersonConfiguration();
		pc.setPerson(getPersonService().findPerson(pPersonId));
		pc.setKey(pTheKey);
		deletePersonConfiguration(pc);		
	}

}
