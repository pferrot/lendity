package com.pferrot.lendity.person.jsf;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;
import org.springframework.security.AccessDeniedException;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.image.jsf.AbstractEditPictureController;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.person.PersonConsts;
import com.pferrot.lendity.person.PersonService;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.JsfUtils;

@ViewController(viewIds={"/auth/person/personEditPicture.jspx"})
public class PersonEditPictureController  extends AbstractEditPictureController {
	
	private final static Log log = LogFactory.getLog(PersonEditPictureController.class);
	
	private static final int IMAGE_MAX_HEIGHT = 200;
	private static final int IMAGE_MAX_WIDTH = 200;
	
	private static final int THUMBNAIL_MAX_HEIGHT = 45;
	private static final int THUMBNAIL_MAX_WIDTH = 45;
	
	private PersonService personService;
	
	private Person person;

	public PersonService getPersonService() {
		return personService;
	}

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	@InitView
	public void initView() {
		// Read the person ID from the request parameter and load the correct person.
		try {
			final String personIdString = JsfUtils.getRequestParameter(PagesURL.PERSON_EDIT_PICTURE_PARAM_PERSON_ID);
			if (personIdString != null) {
				final Long personId = Long.parseLong(personIdString);
				person = getPersonService().findPerson(personId);				
				// Access control check.
				if (!getPersonService().isCurrentUserAuthorizedToEdit(person)) {
					JsfUtils.redirect(PagesURL.ERROR_ACCESS_DENIED);
					if (log.isWarnEnabled()) {
						log.warn("Access denied (person edit): user = " + PersonUtils.getCurrentPersonDisplayName() + " (" + PersonUtils.getCurrentPersonId() + "), person = " + personIdString);
					}
					return;
				}
				setPerson(person);
			}
			// Person not found or no person ID specified.
			if (getPerson() == null) {
				JsfUtils.redirect(PagesURL.PERSONS_LIST);
			}
		}
		catch (AccessDeniedException ade) {
			throw ade;
		}
		catch (Exception e) {
			//TODO display standard error page instead.
			JsfUtils.redirect(PagesURL.PERSONS_LIST);
		}		
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person pPerson) {
		person = pPerson;
	}

	public Long updatePerson() {
		try {				
			if (getTempFileLocation() != null) {
				getPersonService().updatePersonPicture(getPerson(), getImageDocumentFromTempFile(), getThumbnailDocumentFromTempFile());
			}
			else if (Boolean.TRUE.equals(getRemoveCurrentImage())) {
				getPersonService().updatePersonPicture(getPerson(), null, null);
			}			
			return getPerson().getId();
		}
		catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}
	
	public String getMyProfileHref() {
		return JsfUtils.getFullUrl(PagesURL.MY_PROFILE);
	}

	public String submit() {
		Long personId = updatePerson();
		
		if (personId != null) {
			JsfUtils.redirect(PagesURL.MY_PROFILE);
		}
	
		// Return to the same page.
		return "error";
	}
	
	public String getImgSrc() {
		if (Boolean.TRUE.equals(getRemoveCurrentImage())) {
			return JsfUtils.getFullUrl(PersonConsts.DUMMY_PROFILE_PICTURE_URL);
		}
		else if (getTempFileLocation() != null) {
			return getTempFileImgSrc();
		}
		else {
			return personService.getProfilePictureSrc(getPerson(), true);
		}
	}
	
	public String getThumbnailSrc() {
		if (Boolean.TRUE.equals(getRemoveCurrentImage())) {
			return JsfUtils.getFullUrl(PersonConsts.DUMMY_PROFILE_THUMBNAIL_URL);
		}
		else if (getTempThumbnailLocation() != null) {
			return getTempThumbnailImgSrc();			
		}
		else {
			return personService.getProfileThumbnailSrc(getPerson(), true);
		}
	}

	@Override
	protected int getImageMaxHeight() {
		return IMAGE_MAX_HEIGHT;
	}

	@Override
	protected int getImageMaxWidth() {
		return IMAGE_MAX_WIDTH;
	}

	@Override
	protected int getThumbnailMaxHeight() {
		return THUMBNAIL_MAX_HEIGHT;
	}

	@Override
	protected int getThumbnailMaxWidth() {
		return THUMBNAIL_MAX_WIDTH;
	}

	@Override
	public String getCancelHref() {
		return getMyProfileHref();
	}

	@Override
	public boolean isExistingImage() {
		return getPerson().getImage() != null;
	}
}
