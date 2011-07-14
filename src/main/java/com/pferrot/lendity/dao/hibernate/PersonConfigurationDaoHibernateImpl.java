package com.pferrot.lendity.dao.hibernate;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.pferrot.core.CoreUtils;
import com.pferrot.lendity.dao.PersonConfigurationDao;
import com.pferrot.lendity.model.PersonConfiguration;


public class PersonConfigurationDaoHibernateImpl extends HibernateDaoSupport implements PersonConfigurationDao {

	public String findPersonConfigurationValue(final Long pPersonId, final String pKey) {
		final PersonConfiguration pc = findPersonConfiguration(pPersonId, pKey);
		if (pc != null) {
			return pc.getValue();
		}
		else {
			return null;
		}
	}

	public PersonConfiguration createPersonConfiguration(final PersonConfiguration pPersonConfiguration) {
		return (PersonConfiguration)getHibernateTemplate().save(pPersonConfiguration);
	}

	public void deletePersonConfiguration(final PersonConfiguration pPersonConfiguration) {
		getHibernateTemplate().delete(pPersonConfiguration);		
	}

	public PersonConfiguration findPersonConfiguration(final Long pPersonId, final String pKey) {
		CoreUtils.assertNotNull(pPersonId);
		CoreUtils.assertNotNullOrEmptyString(pKey);
		
		List<PersonConfiguration> list = getHibernateTemplate().find("from PersonConfiguration pc where pc.person.id = ? and pc.key = ?", new Object[]{pPersonId, pKey});		
		
		if (list == null ||
			list.isEmpty()) {
			return null;
		}
		else if (list.size() == 1) {
			final PersonConfiguration pc = list.get(0); 
			return pc;
		}
		else {
			throw new DataIntegrityViolationException("More that one PersonConfiguration for person ID '" + pPersonId + "' and key '" + pKey + "'");
		}
	}

	public void updatePersonConfiguration(final PersonConfiguration pPersonConfiguration) {
		getHibernateTemplate().update(pPersonConfiguration);		
	}

}