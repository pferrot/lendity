package com.pferrot.lendity.group.jsf;

import java.io.Serializable;
import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.group.GroupConsts;
import com.pferrot.lendity.group.GroupService;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.person.PersonService;
import com.pferrot.lendity.utils.JsfUtils;

public abstract class AbstractGroupAddEditController implements Serializable {
	
	private final static Log log = LogFactory.getLog(AbstractGroupAddEditController.class);
	
	private GroupService groupService;
	private PersonService personService;
	
	private String title;
	private String description;
	private Boolean validateMembership;
	private Boolean onlyMembersCanSeeComments;
	private String password;
	
	
	public GroupService getGroupService() {
		return groupService;
	}

	public void setGroupService(GroupService groupService) {
		this.groupService = groupService;
	}

	public PersonService getPersonService() {
		return personService;
	}

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getValidateMembership() {
		return validateMembership;
	}

	public void setValidateMembership(Boolean validateMembership) {
		this.validateMembership = validateMembership;
	}

	public Boolean getOnlyMembersCanSeeComments() {
		return onlyMembersCanSeeComments;
	}

	public void setOnlyMembersCanSeeComments(Boolean onlyMembersCanSeeComments) {
		this.onlyMembersCanSeeComments = onlyMembersCanSeeComments;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void validateDescriptionSize(FacesContext context, UIComponent toValidate, Object value) {
		String message = "";
		String description = (String) value;
		if (description != null && description.length() > GroupConsts.MAX_DESCRIPTION_SIZE) {
			((UIInput)toValidate).setValid(false);
			final Locale locale = I18nUtils.getDefaultLocale();
			message = I18nUtils.getMessageResourceString("validation_maxSizeExceeded", new Object[]{String.valueOf(GroupConsts.MAX_DESCRIPTION_SIZE)}, locale);
			context.addMessage(toValidate.getClientId(context), new FacesMessage(message));
		}
	}

	public String submit() {
		Long groupId = processGroup();
		
		JsfUtils.redirect(PagesURL.GROUP_OVERVIEW, PagesURL.GROUP_OVERVIEW_PARAM_GROUP_ID, groupId.toString());
	
		// As a redirect is used, this is actually useless.
		return null;
	}
	
	public abstract Long processGroup();
}
