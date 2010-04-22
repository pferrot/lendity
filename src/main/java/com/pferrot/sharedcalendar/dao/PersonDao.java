package com.pferrot.sharedcalendar.dao;

import com.pferrot.sharedcalendar.dao.bean.ListWithRowCount;
import com.pferrot.sharedcalendar.model.Person;

public interface PersonDao {
	
	Long createPerson(Person person);
	
	Person findPerson(Long personId);
	Person findPersonFromUsername(String username);
	
	/**
	 * pConnectionLink can be null, "connections" or "bannedPersons". If not null, then pPersonId must be set.
	 * It will then return the connections / banned persons for that person.
	 *
	 * @param pPersonId
	 * @param pConnectionLink
	 * @param pSearchString
	 * @param pEnabled
	 * @param pEmailExactMatch
	 * @param pFirstResult
	 * @param pMaxResults
	 * @return
	 */
	ListWithRowCount findPersons(Long pPersonId, String pConnectionLink, String pSearchString, Boolean pEnabled, boolean pEmailExactMatch, int pFirstResult, int pMaxResults);
		
	void updatePerson(Person person);
	
	void deletePerson(Person person);
}
