package com.pferrot.lendity.need;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.CoreUtils;
import com.pferrot.lendity.dao.NeedDao;
import com.pferrot.lendity.dao.PersonDao;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.item.ObjectService;
import com.pferrot.lendity.model.ItemCategory;
import com.pferrot.lendity.model.Need;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.ListValueUtils;

public class NeedService extends ObjectService {
	
	private final static Log log = LogFactory.getLog(NeedService.class);
	
	private NeedDao needDao;
	
	public NeedDao getNeedDao() {
		return needDao;
	}

	public void setNeedDao(NeedDao needDao) {
		this.needDao = needDao;
	}

	public Need findNeed(final Long pNeedId) {
		return needDao.findNeed(pNeedId);
	}
	
	public ListWithRowCount findMyNeeds(final String pTitle, final Long pCategoryId, final String pOrderBy,
			final Boolean pOrderByAscending, final int pFirstResult, final int pMaxResults) {
		final Long currentPersonId = PersonUtils.getCurrentPersonId();
		CoreUtils.assertNotNull(currentPersonId);
		final Long[] personIds = new Long[]{currentPersonId};
		
		return needDao.findNeeds(personIds, Boolean.TRUE, pTitle, getCategoryIds(pCategoryId), pOrderBy, pOrderByAscending, pFirstResult, pMaxResults);
	}

	public ListWithRowCount findMyConnectionsNeeds(final Long pConnectionId, final String pTitle, final Long pCategoryId, final String pOrderBy,
			final Boolean pOrderByAscending, final int pFirstResult, final int pMaxResults) {
		Long[] connectionsIds = getPersonService().getCurrentPersonConnectionIds(pConnectionId);
		if (connectionsIds == null || connectionsIds.length == 0) {
			return ListWithRowCount.emptyListWithRowCount();
		}
		return needDao.findNeeds(connectionsIds, Boolean.TRUE, pTitle, getCategoryIds(pCategoryId), pOrderBy, pOrderByAscending, pFirstResult, pMaxResults);
	}

	public ListWithRowCount findMyLatestConnectionsNeeds() {
		Long[] connectionsIds = getPersonService().getCurrentPersonConnectionIds(null);
		if (connectionsIds == null || connectionsIds.length == 0) {
			return ListWithRowCount.emptyListWithRowCount();
		}
		return needDao.findNeeds(connectionsIds, Boolean.TRUE, null, null, "creationDate", Boolean.FALSE, 0, 5);
	}
	
	public void deleteNeed(final Long pNeedId) {
		deleteNeed(needDao.findNeed(pNeedId));
	}
	
	public void deleteNeed(final Need pNeed) {
		needDao.deleteNeed(pNeed);
	}
	
	public Long createNeed(final Need pNeed) {
		pNeed.setCreationDate(new Date());
		return needDao.createNeed(pNeed);
	}
	
	public Long createNeedWithCategory(final Need pNeed, final Long pCategoryId) {
		pNeed.setCategory((ItemCategory) ListValueUtils.getListValueFromId(pCategoryId, getListValueDao()));
		return createNeed(pNeed);
	}
	
	public void updateNeed(final Need pNeed) {
		needDao.updateNeed(pNeed);
	}

	public void updateNeedWithCategory(final Need pNeed, final Long pCategoryId) {
		assertCurrentUserAuthorizedToEdit(pNeed);
		pNeed.setCategory((ItemCategory) ListValueUtils.getListValueFromId(pCategoryId, getListValueDao()));
		updateNeed(pNeed);
	}
	
	public List<Person> getCurrentPersonEnabledConnections() {
		final ListWithRowCount listWithRowCount = getPersonDao().findPersons(PersonUtils.getCurrentPersonId(), PersonDao.CONNECTIONS_LINK, null, Boolean.TRUE, true, null, null, 0, 0);
		return listWithRowCount.getList();
	}
	
	/////////////////////////////////////////////////////////
	// Access control
	
	public Person getCurrentPerson() {
		return getPersonDao().findPerson(PersonUtils.getCurrentPersonId());
	}	
	
	public boolean isCurrentUserAuthorizedToView(final Need pNeed) {
		CoreUtils.assertNotNull(pNeed);
		if (isCurrentUserAuthorizedToEdit(pNeed)) {
			return true;
		}
		return pNeed.getOwner() != null &&
			pNeed.getOwner().getConnections() != null &&
			pNeed.getOwner().getConnections().contains(getCurrentPerson());
	}
	
	public void assertCurrentUserAuthorizedToView(final Need pNeed) {
		if (!isCurrentUserAuthorizedToView(pNeed)) {
			throw new SecurityException("Current user is not authorized to view need");
		}
	}

	public boolean isCurrentUserAuthorizedToEdit(final Need pNeed) {
		CoreUtils.assertNotNull(pNeed);
		final Person currentPerson = getCurrentPerson();
		if (currentPerson == null) {
			return false;
		}
		if (currentPerson.getUser() != null &&
		    currentPerson.getUser().isAdmin()) {
			return true;
		}
		return currentPerson.equals(pNeed.getOwner());
	}

	public void assertCurrentUserAuthorizedToEdit(final Need pNeed) {
		if (!isCurrentUserAuthorizedToEdit(pNeed)) {
			throw new SecurityException("Current user is not authorized to edit need");
		}
	}

	public boolean isCurrentUserAuthorizedToAdd() {
		final Person currentPerson = getCurrentPerson();
		if (currentPerson != null && currentPerson.getUser() != null) {
			return true;
		}
		return false;
	}

	public void assertCurrentUserAuthorizedToAdd() {
		if (!isCurrentUserAuthorizedToAdd()) {
			throw new SecurityException("Current user is not authorized to add need");
		}
	}

	public boolean isCurrentUserAuthorizedToDelete(final Need pNeed) {
		return isCurrentUserAuthorizedToEdit(pNeed);
	}

	public void assertCurrentUserAuthorizedToDelete(final Need pNeed) {
		if (!isCurrentUserAuthorizedToDelete(pNeed)) {
			throw new SecurityException("Current user is not authorized to delete need");
		}
	}
	
	public boolean isCurrentUserOwner(final Need pNeed) {
		final Person currentPerson = getCurrentPerson();
		if (currentPerson == null) {
			return false;
		}
		return currentPerson.equals(pNeed.getOwner());
	}

}
