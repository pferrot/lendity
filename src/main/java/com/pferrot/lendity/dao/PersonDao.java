package com.pferrot.lendity.dao;

import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.model.Person;

public interface PersonDao {
	
	int UNSPECIFIED_LINK = 0;
	int CONNECTIONS_LINK = 1;
	int BANNED_PERSONS_LINK = 2;
	int BANNED_BY_PERSONS_LINK = 3;
	
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
	ListWithRowCount findPersons(Long pPersonId, int pConnectionLink, String pSearchString, Boolean pEnabled, boolean pEmailExactMatch, int pFirstResult, int pMaxResults);
		
	void updatePerson(Person person);
	
	void deletePerson(Person person);
}
