package com.pferrot.lendity.item;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.ObjectNotFoundException;

import com.pferrot.core.CoreUtils;
import com.pferrot.emailsender.manager.MailManager;
import com.pferrot.lendity.configuration.Configuration;
import com.pferrot.lendity.dao.DocumentDao;
import com.pferrot.lendity.dao.ListValueDao;
import com.pferrot.lendity.document.DocumentService;
import com.pferrot.lendity.group.GroupService;
import com.pferrot.lendity.model.Group;
import com.pferrot.lendity.model.ItemCategory;
import com.pferrot.lendity.model.ItemVisibility;
import com.pferrot.lendity.model.ListValue;
import com.pferrot.lendity.model.Objekt;
import com.pferrot.lendity.model.OrderedListValue;
import com.pferrot.lendity.model.Ownable;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.person.PersonService;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.security.SecurityUtils;

public abstract class ObjektService {

	private final static Log log = LogFactory.getLog(ObjektService.class);
	
	private ListValueDao listValueDao;
	private PersonService personService;
	private MailManager mailManager;
	private DocumentDao documentDao;
	private DocumentService documentService;
	private GroupService groupService;
	
	public GroupService getGroupService() {
		return groupService;
	}

	public void setGroupService(GroupService groupService) {
		this.groupService = groupService;
	}

	public ListValueDao getListValueDao() {
		return listValueDao;
	}

	public void setListValueDao(ListValueDao listValueDao) {
		this.listValueDao = listValueDao;
	}

	public DocumentDao getDocumentDao() {
		return documentDao;
	}

	public void setDocumentDao(DocumentDao documentDao) {
		this.documentDao = documentDao;
	}

	public DocumentService getDocumentService() {
		return documentService;
	}

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

	public PersonService getPersonService() {
		return personService;
	}

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	public MailManager getMailManager() {
		return mailManager;
	}

	public void setMailManager(MailManager mailManager) {
		this.mailManager = mailManager;
	}

	public List<ListValue> getCategories() {
		return listValueDao.findListValue(ItemCategory.class);
	}
	
	public List<OrderedListValue> getVisibilities() {
		return listValueDao.findOrderedListValue(ItemVisibility.class);
	}
		
	protected Long[] getCategoryIds(final Long pCategoryId) {
		return getIds(pCategoryId);
	}
	
	protected Long[] getVisibilityIds(final Long pVisibilityId) {
		return getIds(pVisibilityId);		
	}

	public Long[] getConnectionsAndPublicVisibilityIds() {
		return new Long[]{getConnectionsVisibilityId(), getPublicVisibilityId()};
	}
	
	public Long getConnectionsVisibilityId() {
		return getListValueDao().findListValue(ItemVisibility.CONNECTIONS).getId();
	}
	
	public Long getPublicVisibilityId() {
		return getListValueDao().findListValue(ItemVisibility.PUBLIC).getId();
	}
	
	protected Long[] getIds(final Long pId) {
		Long[] ids = null;
		if (pId != null) {
			ids = new Long[1];
			ids[0] = pId;
		}
		return ids;			
	}
	
	public Person getCurrentPerson() {
		return getPersonService().getCurrentPerson();
	}
	
	public ListValue getListValue(final Long pListValueId) {
		CoreUtils.assertNotNull(pListValueId);
		final ListValue listValue = listValueDao.findListValue(pListValueId);
		if (listValue == null) {
			throw new ObjectNotFoundException(pListValueId, ListValue.class.getName());
		}
		return listValue;		
	}

	public boolean isCurrentUserOwner(final Ownable pOwnable) {
		final Person currentPerson = getCurrentPerson();
		if (currentPerson == null) {
			return false;
		}
		return currentPerson.equals(pOwnable.getOwner());
	}
	
	public String getThumbnail1Src(final Objekt pObjekt, final boolean pAuthorizeDocumentAccess) {
		return getThumbnail1Src(pObjekt, pAuthorizeDocumentAccess, JsfUtils.getSession(), JsfUtils.getContextRoot());		
	}

	public String getThumbnail1Src(final Objekt pObjekt, final boolean pAuthorizeDocumentAccess,
			final HttpSession pSession, final String pUrlPrefix) {
		final ItemCategory category = pObjekt.getCategory();
		final String categoryLabelCode = category.getLabelCode();
		
		if (ItemCategory.BABY_LABEL_CODE.equals(categoryLabelCode)) {
			return JsfUtils.getFullUrlWithPrefix(pUrlPrefix, ItemConsts.CATEGORY_BABY_THUMBNAIL_URL);
		}
		else if (ItemCategory.BLURAY_LABEL_CODE.equals(categoryLabelCode)) {
			return JsfUtils.getFullUrlWithPrefix(pUrlPrefix, ItemConsts.CATEGORY_BLURAY_THUMBNAIL_URL);
		}
		else if (ItemCategory.BOARD_GAME_LABEL_CODE.equals(categoryLabelCode)) {
			return JsfUtils.getFullUrlWithPrefix(pUrlPrefix, ItemConsts.CATEGORY_BOARDGAME_THUMBNAIL_URL);
		}
		else if (ItemCategory.BOOK_LABEL_CODE.equals(categoryLabelCode)) {
			return JsfUtils.getFullUrlWithPrefix(pUrlPrefix, ItemConsts.CATEGORY_BOOK_THUMBNAIL_URL);
		}
		else if (ItemCategory.CD_LABEL_CODE.equals(categoryLabelCode)) {
			return JsfUtils.getFullUrlWithPrefix(pUrlPrefix, ItemConsts.CATEGORY_CD_THUMBNAIL_URL);
		}
		else if (ItemCategory.COMICS_LABEL_CODE.equals(categoryLabelCode)) {
			return JsfUtils.getFullUrlWithPrefix(pUrlPrefix, ItemConsts.CATEGORY_COMICS_THUMBNAIL_URL);
		}
		else if (ItemCategory.DVD_LABEL_CODE.equals(categoryLabelCode)) {
			return JsfUtils.getFullUrlWithPrefix(pUrlPrefix, ItemConsts.CATEGORY_DVD_THUMBNAIL_URL);
		}
		else if (ItemCategory.ELECTRONIC_LABEL_CODE.equals(categoryLabelCode)) {
			return JsfUtils.getFullUrlWithPrefix(pUrlPrefix, ItemConsts.CATEGORY_ELECTRONIC_THUMBNAIL_URL);
		}
		else if (ItemCategory.HOUSEHOLD_ELECTRICAL_LABEL_CODE.equals(categoryLabelCode)) {
			return JsfUtils.getFullUrlWithPrefix(pUrlPrefix, ItemConsts.CATEGORY_HOUSEHOLDELECTRICAL_THUMBNAIL_URL);
		}
		else if (ItemCategory.MUSIC_INSTRUMENT_LABEL_CODE.equals(categoryLabelCode)) {
			return JsfUtils.getFullUrlWithPrefix(pUrlPrefix, ItemConsts.CATEGORY_MUSICINSTRUMENT_THUMBNAIL_URL);
		}
		else if (ItemCategory.OTHER_LABEL_CODE.equals(categoryLabelCode)) {
			return JsfUtils.getFullUrlWithPrefix(pUrlPrefix, ItemConsts.CATEGORY_OTHER_THUMBNAIL_URL);
		}
		else if (ItemCategory.SPORT_LABEL_CODE.equals(categoryLabelCode)) {
			return JsfUtils.getFullUrlWithPrefix(pUrlPrefix, ItemConsts.CATEGORY_SPORT_THUMBNAIL_URL);
		}
		else if (ItemCategory.TOOL_DO_IT_YOURSELF_LABEL_CODE.equals(categoryLabelCode)) {
			return JsfUtils.getFullUrlWithPrefix(pUrlPrefix, ItemConsts.CATEGORY_TOOLDOITYOURSELF_THUMBNAIL_URL);
		}
		else if (ItemCategory.TOOL_GARDEN_LABEL_CODE.equals(categoryLabelCode)) {
			return JsfUtils.getFullUrlWithPrefix(pUrlPrefix, ItemConsts.CATEGORY_TOOLGARDEN_THUMBNAIL_URL);
		}
		else if (ItemCategory.VIDEOGAME_OTHER_LABEL_CODE.equals(categoryLabelCode)) {
			return JsfUtils.getFullUrlWithPrefix(pUrlPrefix, ItemConsts.CATEGORY_VIDEOGAMEOTHER_THUMBNAIL_URL);
		}
		else if (ItemCategory.VIDEOGAME_PC_LABEL_CODE.equals(categoryLabelCode)) {
			return JsfUtils.getFullUrlWithPrefix(pUrlPrefix, ItemConsts.CATEGORY_VIDEOGAMEPC_THUMBNAIL_URL);
		}
		else if (ItemCategory.VIDEOGAME_PS3_LABEL_CODE.equals(categoryLabelCode)) {
			return JsfUtils.getFullUrlWithPrefix(pUrlPrefix, ItemConsts.CATEGORY_VIDEOGAMEPS3_THUMBNAIL_URL);
		}
		else if (ItemCategory.VIDEOGAME_WII_LABEL_CODE.equals(categoryLabelCode)) {
			return JsfUtils.getFullUrlWithPrefix(pUrlPrefix, ItemConsts.CATEGORY_VIDEOGAMEWII_THUMBNAIL_URL);
		}
		else if (ItemCategory.VIDEOGAME_XBOX360_LABEL_CODE.equals(categoryLabelCode)) {
			return JsfUtils.getFullUrlWithPrefix(pUrlPrefix, ItemConsts.CATEGORY_VIDEOGAMEXBOX360_THUMBNAIL_URL);
		}
		
		return null;				
	}
	
	public String getFacebookLikeImageSrc(final Objekt pObjekt, final boolean pAuthorizeDocumentAccess) {
		return getFacebookLikeImageSrc(pObjekt, pAuthorizeDocumentAccess, JsfUtils.getSession(), JsfUtils.getContextRoot());		
	}
	
	public abstract String getFacebookLikeImageSrc(final Objekt pObjekt, final boolean pAuthorizeDocumentAccess,
			final HttpSession pSession, final String pUrlPrefix);
	
	public boolean isVisibilityAllowed(final Long pVisibilityId, final Long pCategoryId) {
		CoreUtils.assertNotNull(pVisibilityId);
		CoreUtils.assertNotNull(pCategoryId);
		
		final ListValue visibility = getListValueDao().findListValue(pVisibilityId);
		final ListValue category = getListValueDao().findListValue(pCategoryId);
		
		if (ItemVisibility.PUBLIC.equals(visibility.getLabelCode()) &&
			Configuration.getCategoriesNotAllowedPublicVisibility().contains(category.getLabelCode())) {
			return false;
		}		
		return true;
	}

	/////////////////////////////////////////////////////////
	// Access control
	
	public boolean isCurrentUserAuthorizedToView(final Objekt pObjekt) {
		return isUserAuthorizedToView(getCurrentPerson(), pObjekt);
	}

	public boolean isUserAuthorizedToView(final Person pPerson, final Objekt pObjekt) {
		CoreUtils.assertNotNull(pObjekt);
		if (!SecurityUtils.isLoggedIn()) {
			if (pObjekt.isPublicVisibility()) {
				return true;
			}
			return false;
		}
		else {
			if (isUserAuthorizedToEdit(pPerson, pObjekt)) {
				return true;
			}
			// Connections can view.
			else if (// Public visibility.
				pObjekt.isPublicVisibility() ||
				// Connection visibility.
			    (pObjekt.isConnectionsVisibility() && 
	    		 pObjekt.getOwner() != null &&
	    		 pObjekt.getOwner().getConnections() != null &&
	    		 pObjekt.getOwner().getConnections().contains(pPerson))) {
				return true;
			}
			final Set<Group> groupsAuthorized = pObjekt.getGroupsAuthorized();
			if (groupsAuthorized.isEmpty()) {
				return false;
			}
			final List<Group> groupsPerson = getGroupService().findPersonGroupsWhereOwnerOrAdministratorOrMemberList(pPerson.getId(), null, 0, 0);
			if (groupsPerson.isEmpty()) {
				return false;
			}
			// We should certainly improve this when we have time...
			for (Group groupAuthorized: groupsAuthorized) {
				for(Group groupPerson: groupsPerson) {
					if (groupAuthorized.equals(groupPerson)) {
						return true;
					}
				}
			}
			return false;
		}
	}
	
	public void assertCurrentUserAuthorizedToView(final Objekt pObjekt) {
		if (!isCurrentUserAuthorizedToView(pObjekt)) {
			throw new SecurityException("Current user is not authorized to view objekt");
		}
	}
	
	public void assertUserAuthorizedToView(final Person pPerson, final Objekt pObjekt) {
		if (!isUserAuthorizedToView(pPerson, pObjekt)) {
			throw new SecurityException("User is not authorized to view objekt");
		}
	}
	
	public boolean isCurrentUserAuthorizedToEdit(final Objekt pObjekt) {
		return isUserAuthorizedToEdit(getCurrentPerson(), pObjekt);
	}
	
	public boolean isUserAuthorizedToEdit(final Person pPerson, final Objekt pObjekt) {
		CoreUtils.assertNotNull(pObjekt);
		if (!SecurityUtils.isLoggedIn()) {
			return false;
		}
		else if (pPerson == null || pPerson.getUser() == null) {
			return false;
		}
		else if (pPerson.getUser() != null &&
			pPerson.getUser().isAdmin()) {
			return true;
		}
		else if (pPerson.equals(pObjekt.getOwner())) {
			return true;
		}			
		return false;
	}

	public void assertCurrentUserAuthorizedToEdit(final Objekt pObjekt) {
		if (!isCurrentUserAuthorizedToEdit(pObjekt)) {
			throw new SecurityException("Current user is not authorized to edit objekt");
		}
	}

	public boolean isCurrentUserAuthorizedToAdd() {
		final Person currentPerson = getCurrentPerson();
		if (currentPerson != null && currentPerson.getUser() != null) {
			return true;
		}
		return false;
	}

	public void assertCurrentUserAuthorizedToAdd(final Objekt pObjekt) {
		if (!isCurrentUserAuthorizedToAdd()) {
			throw new SecurityException("Current user is not authorized to add objekt");
		}
	}

	public boolean isCurrentUserAuthorizedToDelete(final Objekt pObjekt) {
		return isCurrentUserAuthorizedToEdit(pObjekt);
	}

	public void assertCurrentUserAuthorizedToDelete(final Objekt pObjekt) {
		if (!isCurrentUserAuthorizedToDelete(pObjekt)) {
			throw new SecurityException("Current user is not authorized to delete objekt");
		}
	}

	// Access control
	/////////////////////////////////////////////////////////
	
}
