package com.pferrot.lendity.dao;

import com.pferrot.lendity.model.PersonConfiguration;


public interface PersonConfigurationDao {
	
	// The identifier is the entity class itself, see http://docs.jboss.org/hibernate/annotations/3.5/reference/en/html_single/ § 2.2.3.2
	PersonConfiguration createPersonConfiguration(PersonConfiguration personConfiguration);
	
	PersonConfiguration findPersonConfiguration(Long personId, String key);
	
	String findPersonConfigurationValue(Long personId, String key);
	
	void updatePersonConfiguration(PersonConfiguration personConfiguration);
	
	void deletePersonConfiguration(PersonConfiguration personConfiguration);
	
}
