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
public class PersonEditPictureController  {
	
	private final static Log log = LogFactory.getLog(PersonEditPictureController.class);
	
	private static final int MAX_PROFILE_PICTURE_HEIGHT = 200;
	private static final int MAX_PROFILE_PICTURE_WIDTH = 200;
	
	private static final int MAX_PROFILE_THUMBNAIL_HEIGHT = 45;
	private static final int MAX_PROFILE_THUMBNAIL_WIDTH = 45;
	
	private PersonService personService;
	private DocumentService documentService;
	
	private Person person;

	private UploadedFile imageFile1;
	private UIComponent imageFile1UIComponent;
	private String imageFile1TooLargeErrorMessage;
	
	private String tempFileLocation;
	private String tempFileMimeType;
	private String tempFileOriginalFileName;
	
	private String tempThumbnailLocation;
	private String tempThumbnailMimeType;
	private String tempThumbnailOriginalFileName;
	
	private Boolean removeCurrentImage;
	
	public PersonService getPersonService() {
		return personService;
	}

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	public DocumentService getDocumentService() {
		return documentService;
	}

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

	public UploadedFile getImageFile1() {
		return imageFile1;
	}

	public void setImageFile1(UploadedFile imageFile1) {
		if (imageFile1 != null) {
			setRemoveCurrentImage(Boolean.FALSE);
		}
		this.imageFile1 = imageFile1;
	}
	
	public UIComponent getImageFile1UIComponent() {
		return imageFile1UIComponent;
	}

	public void setImageFile1UIComponent(UIComponent imageFile1UIComponent) {
		this.imageFile1UIComponent = imageFile1UIComponent;
	}

	public String getImageFile1TooLargeErrorMessage() {
		return imageFile1TooLargeErrorMessage;
	}

	public void setImageFileTooLargeErrorMessage(String imageFile1TooLargeErrorMessage) {
		this.imageFile1TooLargeErrorMessage = imageFile1TooLargeErrorMessage;
	}
	
	public void validateImageFile1(FacesContext context, UIComponent toValidate, Object value) {
		setImageFile1UIComponent(toValidate);
	}

//	@PreRenderView
//	public void checkRequestForImageFile1Error() {
//		final HttpServletRequest request = JsfUtils.getHttpServletRequest();
//		final Object fileUploadException = request.getAttribute("org.apache.myfaces.custom.fileupload.exception");
//		if (log.isDebugEnabled()) {
//			log.debug("File upload exception: " + fileUploadException);
//		}
//		if (fileUploadException != null) {
//			setImageFileTooLargeErrorMessage(DocumentUtils.getImageValidationErrorMessage(I18nUtils.getDefaultLocale()));
//		}
//		else {
//			setImageFileTooLargeErrorMessage(null);
//		}
//	}

	public Boolean getRemoveCurrentImage() {
		return removeCurrentImage;
	}

	public void setRemoveCurrentImage(Boolean removeCurrentImage) {
		this.removeCurrentImage = removeCurrentImage;
	}

	public String getTempFileLocation() {
		return tempFileLocation;
	}

	public void setTempFileLocation(String tempFileLocation) {
		this.tempFileLocation = tempFileLocation;
	}

	public String getTempFileMimeType() {
		return tempFileMimeType;
	}

	public void setTempFileMimeType(String tempFileMimeType) {
		this.tempFileMimeType = tempFileMimeType;
	}

	public String getTempFileOriginalFileName() {
		return tempFileOriginalFileName;
	}

	public void setTempFileOriginalFileName(String tempFileOriginalFileName) {
		this.tempFileOriginalFileName = tempFileOriginalFileName;
	}
	
	public String getTempThumbnailLocation() {
		return tempThumbnailLocation;
	}

	public void setTempThumbnailLocation(String tempThumbnailLocation) {
		this.tempThumbnailLocation = tempThumbnailLocation;
	}

	public String getTempThumbnailMimeType() {
		return tempThumbnailMimeType;
	}

	public void setTempThumbnailMimeType(String tempThumbnailMimeType) {
		this.tempThumbnailMimeType = tempThumbnailMimeType;
	}

	public String getTempThumbnailOriginalFileName() {
		return tempThumbnailOriginalFileName;
	}

	public void setTempThumbnailOriginalFileName(
			String tempThumbnailOriginalFileName) {
		this.tempThumbnailOriginalFileName = tempThumbnailOriginalFileName;
	}

	public String getTempFileImgSrc() {
		getDocumentService().authorizeDownloadOneMinute(JsfUtils.getSession(), Long.valueOf(getTempFileLocation().hashCode()));
		String[] param1 = {PagesURL.DOCUMENT_DOWNLOAD_PARAM_DOCUMENT_PATH, getTempFileLocation()}; 
		String[] param2 = {PagesURL.DOCUMENT_DOWNLOAD_PARAM_DOCUMENT_MIME_TYPE, getTempFileMimeType()}; 
		String[] param3 = {PagesURL.DOCUMENT_DOWNLOAD_PARAM_DOCUMENT_ORIGINAL_FILE_NAME, getTempFileOriginalFileName()}; 
		String[][] params = {param1, param2, param3};
		return JsfUtils.getFullUrl(PagesURL.DOCUMENT_DOWNLOAD, params);
	}

	public String getTempThumbnailImgSrc() {
		getDocumentService().authorizeDownloadOneMinute(JsfUtils.getSession(), Long.valueOf(getTempThumbnailLocation().hashCode()));
		String[] param1 = {PagesURL.DOCUMENT_DOWNLOAD_PARAM_DOCUMENT_PATH, getTempThumbnailLocation()}; 
		String[] param2 = {PagesURL.DOCUMENT_DOWNLOAD_PARAM_DOCUMENT_MIME_TYPE, getTempThumbnailMimeType()}; 
		String[] param3 = {PagesURL.DOCUMENT_DOWNLOAD_PARAM_DOCUMENT_ORIGINAL_FILE_NAME, getTempThumbnailOriginalFileName()}; 
		String[][] params = {param1, param2, param3};
		return JsfUtils.getFullUrl(PagesURL.DOCUMENT_DOWNLOAD, params);
	}

	protected void errorImageFile() {
		errorImageFile(getImageFile1UIComponent());
	}
	
	protected void errorImageFile(final UIComponent pUiComponent) {
		String message = "";
		FacesContext context = FacesContext.getCurrentInstance();
		message = I18nUtils.getMessageResourceString("validation_imageProfileIoError", I18nUtils.getDefaultLocale());
		context.addMessage(pUiComponent.getClientId(context), new FacesMessage(message));
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
		FileInputStream fis1 = null;
		FileInputStream fis2 = null;
		try {				
			if (getTempFileLocation() != null) {
				final Document image1 = new Document();
				final File file1 = new File(getTempFileLocation());
				image1.setMimeType(getTempFileMimeType());
				image1.setSize(file1.length());
				image1.setName(getTempFileOriginalFileName());
				fis1 = new FileInputStream(file1);
				image1.setInputStream(fis1);
				
				final Document thumbnail = new Document();
				final File file2 = new File(getTempThumbnailLocation());
				thumbnail.setMimeType(getTempThumbnailMimeType());
				thumbnail.setSize(file2.length());
				thumbnail.setName(getTempFileOriginalFileName());
				fis2 = new FileInputStream(file2);				
				thumbnail.setInputStream(fis2);
				
				getPersonService().updatePersonPicture(getPerson(), image1, thumbnail);
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
	
	/**
	 * Returns true if OK, false otherwise.
	 *
	 * @return
	 */
	private boolean validateImage() {
		if (getImageFile1() != null) {
			final String mimeType = getImageFile1().getContentType();
			if (! DocumentUtils.isSupportedImageMimeType(mimeType)) {
				errorImageFile();
				return false;
			}
			final long size = getImageFile1().getSize();
			if (size > DocumentConsts.MAX_PROFILE_IMAGE_SIZE) {
				errorImageFile();
				return false;
			}
		}
		return true;
	}

	public String getPersonOverviewHref() {		
		return PersonUtils.getPersonOverviewPageUrl(person.getId().toString());
	}
	
	private void saveImageInTempFile() throws IOException {
		
		OutputStream out = null;
		try {
			final InputStream is = getImageFile1().getInputStream();
			final BufferedImage originalBufferedImage = ImageIO.read(is);
			
			BufferedImage targetBufferedImage = DocumentUtils.getHighQualityScaledInstance(originalBufferedImage, MAX_PROFILE_PICTURE_HEIGHT, MAX_PROFILE_PICTURE_WIDTH);
			File tempFile = File.createTempFile("lendity-", ".tmp");
			ImageIO.write(targetBufferedImage, "jpg", tempFile);
			setTempFileLocation(tempFile.getAbsolutePath());
			setTempFileMimeType(DocumentConsts.MIME_TYPE_IMAGE_JPEG);
			setTempFileOriginalFileName(getImageFile1().getName());
			if (log.isDebugEnabled()) {
				log.debug("Saved profile picture into temp file: " + getTempFileLocation());
			}
			
			targetBufferedImage = DocumentUtils.getHighQualityScaledInstance(originalBufferedImage, MAX_PROFILE_THUMBNAIL_HEIGHT, MAX_PROFILE_THUMBNAIL_WIDTH);
			tempFile = File.createTempFile("lendity-", ".tmp");
			ImageIO.write(targetBufferedImage, "jpg", tempFile);
			setTempThumbnailLocation(tempFile.getAbsolutePath());
			setTempThumbnailMimeType(DocumentConsts.MIME_TYPE_IMAGE_JPEG);
			setTempThumbnailOriginalFileName(getImageFile1().getName());
			if (log.isDebugEnabled()) {
				log.debug("Saved profile thumbnail into temp file: " + getTempThumbnailLocation());
			}
		}
		finally {
			if (out != null) {
				out.close();
			}
		}
	}
	
	public String processImage() {
		try {
			if (validateImage()) {
				saveImageInTempFile();
				return "success";
			}
			else {
				return "error";
			}
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String removeCurrentImage() {
		setRemoveCurrentImage(Boolean.TRUE);
		setTempFileLocation(null);
		return "success";
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
		if (Boolean.TRUE.equals(removeCurrentImage)) {
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
		if (Boolean.TRUE.equals(removeCurrentImage)) {
			return JsfUtils.getFullUrl(PersonConsts.DUMMY_PROFILE_THUMBNAIL_URL);
		}
		else if (getTempThumbnailLocation() != null) {
			return getTempThumbnailImgSrc();			
		}
		else {
			return personService.getProfileThumbnailSrc(getPerson(), true);
		}
	}
}
