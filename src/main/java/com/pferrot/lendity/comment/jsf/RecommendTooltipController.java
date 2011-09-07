package com.pferrot.lendity.comment.jsf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.comment.CommentService;
import com.pferrot.lendity.comment.exception.CommentException;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.model.WallCommentsAddPermission;
import com.pferrot.lendity.person.PersonService;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.lendity.utils.UiUtils;

public class RecommendTooltipController implements Serializable {
	
	private final static Log log = LogFactory.getLog(RecommendTooltipController.class);
	
	private final static String TYPE_ITEM = "item";
	private final static String TYPE_NEED = "need";
	private final static String TYPE_GROUP = "group";
	
	private CommentService commentService;
	private PersonService personService;
	
	private List<SelectItem> personSelectItems;
	private Long personId;
	private Long id;
	private String type;

	public PersonService getPersonService() {
		return personService;
	}

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	public CommentService getCommentService() {
		return commentService;
	}

	public void setCommentService(CommentService commentService) {
		this.commentService = commentService;
	}


	public List<SelectItem> getPersonSelectItems() {
		List<Person> enabledConnections = getPersonService().getCurrentPersonEnabledConnections();
		List<Person> enabledConnectionsAcceptingComments = new ArrayList<Person>();
		// Exclude connections who do not want to receive comments on their wall.
		for (Person connection: enabledConnections) {
			if (!WallCommentsAddPermission.NO_ONE.equals(connection.getWallCommentsAddPermission().getLabelCode())) {
				enabledConnectionsAcceptingComments.add(connection);
			}
		}
		personSelectItems = UiUtils.getSelectItemsForPerson(enabledConnectionsAcceptingComments);
		final Locale locale = I18nUtils.getDefaultLocale();
		personSelectItems.add(0, UiUtils.getPleaseSelectSelectItem(locale));	
		return personSelectItems;	
	}

	public Long getPersonId() {
		return personId;
	}

	public void setPersonId(Long personId) {
		this.personId = personId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String submit() {
		try {
			final Locale locale = I18nUtils.getDefaultLocale();
			String message = null;
			if (TYPE_ITEM.equals(getType())) {
				message = I18nUtils.getMessageResourceString("comment_recommendItem", new Object[]{"{p" + PersonUtils.getCurrentPersonId() + "}", "{i" + getId() + "}"}, locale);
			}
			else if (TYPE_NEED.equals(getType())) {
				message = I18nUtils.getMessageResourceString("comment_recommendNeed", new Object[]{"{p" + PersonUtils.getCurrentPersonId() + "}", "{n" + getId() + "}"}, locale);
				
			}
			else if (TYPE_GROUP.equals(getType())) {
				message = I18nUtils.getMessageResourceString("comment_recommendGroup", new Object[]{"{p" + PersonUtils.getCurrentPersonId() + "}", "{g" + getId() + "}"}, locale);			
			}
			else {
				throw new RuntimeException("Unknown type: " + getType());
			}
			
			getCommentService().createCommentOnOtherWallWithAC(message, PersonUtils.getCurrentPersonId(), getPersonId(), Boolean.FALSE);
			
			JsfUtils.redirect(
					PagesURL.PERSON_OVERVIEW,
					PagesURL.PERSON_OVERVIEW_PARAM_PERSON_ID,
					getPersonId().toString());
		
			// As a redirect is used, this is actually useless.
			return null;
		}
		catch (CommentException e) {
			throw new RuntimeException(e);
		}
	}	
}
