package com.pferrot.lendity.item.jsf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.id.IdentityGenerator.GetGeneratedKeysDelegate;

import com.pferrot.core.CoreUtils;
import com.pferrot.core.StringUtils;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.i18n.SelectItemComparator;
import com.pferrot.lendity.item.ItemConsts;
import com.pferrot.lendity.item.ObjektService;
import com.pferrot.lendity.model.Group;
import com.pferrot.lendity.model.Objekt;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.person.PersonService;
import com.pferrot.lendity.utils.UiUtils;

public abstract class AbstractObjektAddEditController {
	
	private final static Log log = LogFactory.getLog(AbstractObjektAddEditController.class);
	
	private List<SelectItem> categoriesSelectItems;
	private Long categoryId;
	private String title;
	private String description;
	private List<SelectItem> visibilitySelectItems;
	private Long visibilityId;	
	private List<SelectItem> authorizedGroupsSelectItems;
	private List<Long> authorizedGroupsIds;
	private PersonService personService;

	protected abstract ObjektService getObjektService();
	
	public PersonService getPersonService() {
		return personService;
	}
	
	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	public List<SelectItem> getCategoriesSelectItems() {
		if (categoriesSelectItems == null) {
			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			categoriesSelectItems = UiUtils.getSelectItemsForListValue(getObjektService().getCategories(), locale);
			categoriesSelectItems.add(0, UiUtils.getPleaseSelectSelectItem(locale));
		}		
		return categoriesSelectItems;	
	}	

	public Long getCategoryId() {
		return categoryId;
	}

	public List<SelectItem> getVisibilitySelectItems() {
		if (visibilitySelectItems == null) {
			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			visibilitySelectItems = UiUtils.getSelectItemsForOrderedListValue(getObjektService().getVisibilities(), locale);
			visibilitySelectItems.add(0, UiUtils.getPleaseSelectSelectItem(locale));
		}		
		return visibilitySelectItems;	
	}

	public Long getVisibilityId() {
		return visibilityId;
	}

	public void setVisibilityId(final Long pVisibilityId) {
		this.visibilityId = UiUtils.getPositiveLongOrNull(pVisibilityId);
	}
	
	public boolean isGroupsAuthorizedAvailable() {
		final Person person = personService.getCurrentPerson();
		return !person.getGroupsOwner().isEmpty() ||
			!person.getGroupsAdministrator().isEmpty() ||
			!person.getGroupsMember().isEmpty();
		
	}
	
	public List<SelectItem> getAuthorizedGroupsSelectItems() {
		if (authorizedGroupsSelectItems == null) {
			Set<Group> groups = new HashSet<Group>();
			final Person person = personService.getCurrentPerson();			
			
			groups.addAll(person.getGroupsOwner());
			groups.addAll(person.getGroupsAdministrator());
			groups.addAll(person.getGroupsMember());
			
			final TreeSet<SelectItem> treeSet = new TreeSet<SelectItem>(new SelectItemComparator());
			for (Group group: groups) {
				final SelectItem selectItem = new SelectItem(group.getId(), group.getTitle());
				treeSet.add(selectItem);
			}
			authorizedGroupsSelectItems = new ArrayList<SelectItem>();
			authorizedGroupsSelectItems.addAll(treeSet);
		}		
		return authorizedGroupsSelectItems;	
	}

	public List<Long> getAuthorizedGroupsIds() {
		return authorizedGroupsIds;
	}
	
	public void setAuthorizedGroupsIdsFromObjekt(final Objekt pObjekt) {
		CoreUtils.assertNotNull(pObjekt);
		Set<Group> groups = pObjekt.getGroupsAuthorized();
		final List<Long> groupsIds = new ArrayList<Long>();
		if (groups != null) {
			for (Group group: groups) {
				groupsIds.add(group.getId());
			}
		}
		setAuthorizedGroupsIds(groupsIds);
	}

	public void setAuthorizedGroupsIds(List<Long> authorizedGroupsIds) {
		this.authorizedGroupsIds = authorizedGroupsIds;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = UiUtils.getPositiveLongOrNull(categoryId);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = StringUtils.getNullIfEmpty(title);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = StringUtils.getNullIfEmpty(description);
	}

	public void validateDescriptionSize(FacesContext context, UIComponent toValidate, Object value) {
		String message = "";
		String description = (String) value;
		if (description != null && description.length() > ItemConsts.MAX_DESCRIPTION_SIZE) {
			((UIInput)toValidate).setValid(false);
			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			message = I18nUtils.getMessageResourceString("validation_maxSizeExceeded", new Object[]{String.valueOf(ItemConsts.MAX_DESCRIPTION_SIZE)}, locale);
			context.addMessage(toValidate.getClientId(context), new FacesMessage(message));
		}
	}
}
