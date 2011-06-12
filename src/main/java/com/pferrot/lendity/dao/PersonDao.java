package com.pferrot.lendity.dao;

import java.util.Date;
import java.util.List;

import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.dao.bean.PersonDaoQueryBean;
import com.pferrot.lendity.model.Person;

public interface PersonDao {
	
	int UNSPECIFIED_LINK = 0;
	int CONNECTIONS_LINK = 1;
	int BANNED_PERSONS_LINK = 2;
	int BANNED_BY_PERSONS_LINK = 3;
	
	Long createPerson(Person person);
	
	Person findPerson(Long personId);
	Person findPersonFromUsername(String username);
	Person findPersonFromDisplayName(String displayName);
	
	/**
	 * pConnectionLink can be null, "connections" or "bannedPersons". If not null, then pPersonId must be set.
	 * It will then return the connections / banned persons for that person.
	 * If max distance is specified, then origin latitude and longitude must be specified as well.
	 *
	 * @param pPersonId
	 * @param pConnectionLink
	 * @param pSearchString
	 * @param pEmailExactMatch
	 * @param pEnabled
	 * @param pReceiveNeedsNotifications
	 * @param pEmailSubscriber
	 * @param pEmailSubscriberLastSentDateMaxint
	 * @param pMaxDistance
	 * @param pOriginLatitude
	 * @param pOriginLongitude
	 * @param pFirstResult
	 * @param pMaxResults
	 * @return
	 * @deprecated
	 */
	ListWithRowCount findPersons(Long pPersonId, int pConnectionLink, String pSearchString, Boolean pEmailExactMatch,
			 Boolean pEnabled, Boolean pReceiveNeedsNotifications, Boolean pEmailSubscriber, Date pEmailSubscriberLastUpdateMax,
			 Double pMaxDistance, Double pOriginLatitude, Double pOriginLongitude,
			 int pFirstResult, int pMaxResults);
	
	ListWithRowCount findPersons(PersonDaoQueryBean pQueryBean);
	
	
	/**
	 * 
	 * @param pPersonId
	 * @param pConnectionLink
	 * @param pSearchString
	 * @param pEmailExactMatch
	 * @param pEnabled
	 * @param pReceiveNeedsNotifications
	 * @param pEmailSubscriber
	 * @param pEmailSubscriberLastUpdateMax
	 * @param pMaxDistance
	 * @param pOriginLatitude
	 * @param pOriginLongitude
	 * @param pFirstResult
	 * @param pMaxResults
	 * @return
	 * @deprecated
	 */
	List<Person> findPersonsList(Long pPersonId, int pConnectionLink, String pSearchString, Boolean pEmailExactMatch,
			Boolean pEnabled, Boolean pReceiveNeedsNotifications, Boolean pEmailSubscriber, Date pEmailSubscriberLastUpdateMax,
			Double pMaxDistance, Double pOriginLatitude, Double pOriginLongitude,
			int pFirstResult, int pMaxResults);
	
	List<Person> findPersonsList(PersonDaoQueryBean pQueryBean);
	long countPersons(PersonDaoQueryBean pQueryBean);
		
	void updatePerson(Person person);
	
	void deletePerson(Person person);

	ListWithRowCount findGroupMembers(Long pGroupId, int pFirstResult, int pMaxResults);
	long countGroupMembers(Long pGroupId);
	ListWithRowCount findGroupAdministrators(Long pGroupId, int pFirstResult, int pMaxResults);
	long countGroupAdministrators(Long pGroupId);
}
