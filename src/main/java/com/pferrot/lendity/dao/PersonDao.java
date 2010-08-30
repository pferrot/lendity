package com.pferrot.lendity.dao;

import java.util.Date;
import java.util.List;

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
	 * @param pEmailExactMatch
	 * @param pEnabled
	 * @param pReceiveNeedsNotification
	 * @param pEmailSubscriber
	 * @param pEmailSubscriberLastSentDateMaxint
	 * @param pFirstResult
	 * @param pMaxResults
	 * @return
	 */
	ListWithRowCount findPersons(Long pPersonId, int pConnectionLink, String pSearchString, Boolean pEmailExactMatch,
			 Boolean pEnabled, Boolean pReceiveNeedsNotification, Boolean pEmailSubscriber, Date pEmailSubscriberLastUpdateMax, int pFirstResult, int pMaxResults);
	
	
	List<Person> findPersonsList(Long pPersonId, int pConnectionLink, String pSearchString, Boolean pEmailExactMatch,
			Boolean pEnabled, Boolean pReceiveNeedsNotification, Boolean pEmailSubscriber, Date pEmailSubscriberLastUpdateMax, int pFirstResult, int pMaxResults);
		
	void updatePerson(Person person);
	
	void deletePerson(Person person);
}
