package com.pferrot.lendity.person.jsf;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;
import org.springframework.security.AccessDeniedException;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.document.DocumentConsts;
import com.pferrot.lendity.document.DocumentService;
import com.pferrot.lendity.document.DocumentUtils;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.model.Document;
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
			final String personIdString = JsfUtils.getRequestParameter(PagesURL.PERSON_EDIT_PARAM_PERSON_ID);
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
		finally {
//			if (fis1 != null) {
//				try {
//					fis1.close();
//				} catch (IOException e) {}
//			}
//			if (fis2 != null) {
//				try {
//					fis2.close();
//				} catch (IOException e) {}
//			}
		}
	}
	
	public String getPersonOverviewHref() {		
		return PersonUtils.getPersonOverviewPageUrl(person.getId().toString());
	}

	public String submit() {
		Long personId = updatePerson();
		
		if (personId != null) {
			JsfUtils.redirect(PagesURL.PERSON_OVERVIEW, PagesURL.PERSON_OVERVIEW_PARAM_PERSON_ID, personId.toString());
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
	protected int getPictureMaxHeight() {
		return IM
	}

	@Override
	protected int getPictureMaxWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected int getThumbnailMaxHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected int getThumbnailMaxWidth() {
		// TODO Auto-generated method stub
		return 0;
	}
}
