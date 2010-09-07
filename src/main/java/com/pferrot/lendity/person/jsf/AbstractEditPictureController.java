package com.pferrot.lendity.person.jsf;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.apache.myfaces.orchestra.viewController.annotations.PreRenderView;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.document.DocumentConsts;
import com.pferrot.lendity.document.DocumentService;
import com.pferrot.lendity.document.DocumentUtils;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.model.Document;
import com.pferrot.lendity.utils.JsfUtils;

public abstract class AbstractEditPictureController  {
	
	private final static Log log = LogFactory.getLog(AbstractEditPictureController.class);

	private DocumentService documentService;
	
	private UploadedFile imageFile;
	private UIComponent imageFileUIComponent;
	private String imageFileTooLargeErrorMessage;
	
	// Used to temporarily store the picture on the file system.
	private String tempFileLocation;
	private String tempFileMimeType;
	private String tempFileOriginalFileName;
	
	// Used to temporarily store the thumbnail on the file system.
	private String tempThumbnailLocation;
	private String tempThumbnailMimeType;
	private String tempThumbnailOriginalFileName;
	
	// Set to TRUE when the user clicks the link to remove the image.
	private Boolean removeCurrentImage;
	
	public DocumentService getDocumentService() {
		return documentService;
	}

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

	public UploadedFile getImageFile() {
		return imageFile;
	}

	public void setImageFile(UploadedFile imageFile) {
		if (imageFile != null) {
			setRemoveCurrentImage(Boolean.FALSE);
		}
		this.imageFile = imageFile;
	}
	
	public UIComponent getImageFileUIComponent() {
		return imageFileUIComponent;
	}

	public void setImageFileUIComponent(UIComponent imageFileUIComponent) {
		this.imageFileUIComponent = imageFileUIComponent;
	}

	public String getImageFileTooLargeErrorMessage() {
		return imageFileTooLargeErrorMessage;
	}

	public void setImageFileTooLargeErrorMessage(String imageFileTooLargeErrorMessage) {
		this.imageFileTooLargeErrorMessage = imageFileTooLargeErrorMessage;
	}
	
	public void validateImageFile(FacesContext context, UIComponent toValidate, Object value) {
		setImageFileUIComponent(toValidate);
	}

	/**
	 * Check for the max file size.
	 */
	@PreRenderView
	public void checkRequestForImageFile1Error() {
		final HttpServletRequest request = JsfUtils.getHttpServletRequest();
		final Object fileUploadException = request.getAttribute("org.apache.myfaces.custom.fileupload.exception");
		if (log.isDebugEnabled()) {
			log.debug("File upload exception: " + fileUploadException);
		}
		if (fileUploadException != null) {
			setImageFileTooLargeErrorMessage(DocumentUtils.getImageValidationErrorMessage(I18nUtils.getDefaultLocale()));
		}
		else {
			setImageFileTooLargeErrorMessage(null);
		}
	}

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

	/**
	 * Authorize the current user to download the temp image from the image system and return
	 * the link to get that file through the document download servlet.
	 *
	 * @return
	 */
	public String getTempFileImgSrc() {
		return getTmpImgSrc(getTempFileLocation(), getTempFileMimeType(), getTempFileOriginalFileName());
	}

	/**
	 * Authorize the current user to download the temp thumbnail from the file system and return
	 * the link to get that file through the document download servlet.
	 *
	 * @return
	 */
	public String getTempThumbnailImgSrc() {
		return getTmpImgSrc(getTempThumbnailLocation(), getTempThumbnailMimeType(), getTempThumbnailOriginalFileName());
	}
	
	/**
	 * Authorize the current user to download the file pFileLocation of the mime-type pMimeType and
	 * with original file name pOriginalFileName and return the link to get that file through the
	 * document download servlet.
	 *  
	 * @param pFileLocation
	 * @param pMimeType
	 * @param pOriginalFileName
	 * @return
	 */
	private String getTmpImgSrc(final String pFileLocation, final String pMimeType, final String pOriginalFileName) {
		getDocumentService().authorizeDownloadOneMinute(JsfUtils.getSession(), Long.valueOf(pFileLocation.hashCode()));
		String[] param1 = {PagesURL.DOCUMENT_DOWNLOAD_PARAM_DOCUMENT_PATH, pFileLocation}; 
		String[] param2 = {PagesURL.DOCUMENT_DOWNLOAD_PARAM_DOCUMENT_MIME_TYPE, pMimeType}; 
		String[] param3 = {PagesURL.DOCUMENT_DOWNLOAD_PARAM_DOCUMENT_ORIGINAL_FILE_NAME, pOriginalFileName}; 
		String[][] params = {param1, param2, param3};
		return JsfUtils.getFullUrl(PagesURL.DOCUMENT_DOWNLOAD, params);		
	}

	protected void errorImageFile() {
		errorImageFile(getImageFileUIComponent());
	}
	
	protected void errorImageFile(final UIComponent pUiComponent) {
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(pUiComponent.getClientId(context), 
				new FacesMessage(DocumentUtils.getImageValidationErrorMessage(I18nUtils.getDefaultLocale())));
	}

	protected Document getImageDocumentFromTempFile() throws FileNotFoundException {
		return getDocFromTempFile(getTempFileLocation(), getTempFileMimeType(), getTempFileOriginalFileName());
	}
	
	protected Document getThumbnailDocumentFromTempFile() throws FileNotFoundException {
		return getDocFromTempFile(getTempThumbnailLocation(), getTempThumbnailMimeType(), getTempThumbnailOriginalFileName());
	}
	
	/**
	 * Returns a document from a file on the file system.
	 * 
	 * @param pFileLocation
	 * @param pMimeType
	 * @param pOriginalFileName
	 * @return
	 * @throws FileNotFoundException 
	 */
	private Document getDocFromTempFile(final String pFileLocation, final String pMimeType, final String pOriginalFileName) throws FileNotFoundException {
		final Document document = new Document();
		final File file1 = new File(getTempFileLocation());
		document.setMimeType(getTempFileMimeType());
		document.setSize(file1.length());
		document.setName(getTempFileOriginalFileName());
		final FileInputStream fis1 = new FileInputStream(file1);
		document.setInputStream(fis1);
		return document;
	}
	
//	public Long updatePerson() {
//		try {				
//			if (getTempFileLocation() != null) {
//				getPersonService().updatePersonPicture(getPerson(), getImageDocumentFromTempFile(), getThumbnailDocumentFromTempFile());
//			}
//			else if (Boolean.TRUE.equals(getRemoveCurrentImage())) {
//				getPersonService().updatePersonPicture(getPerson(), null, null);
//			}			
//			return getPerson().getId();
//		}
//		catch (IOException ioe) {
//			throw new RuntimeException(ioe);
//		}
//		finally {
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
//		}
//	}
	
	/**
	 * Returns true if the uploaded image is of a supported mime type, false otherwise.
	 * If not a supported mime type, error message is placed in the context.
	 *
	 * @return
	 */
	protected boolean validateImage() {
		if (getImageFile() != null) {
			final String mimeType = getImageFile().getContentType();
			if (! DocumentUtils.isSupportedImageMimeType(mimeType)) {
				errorImageFile();
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Max width for the image.
	 */
	protected abstract int getPictureMaxHeight();
	
	/**
	 * Max height for the image.
	 */
	protected abstract int getPictureMaxWidth();
	
	/**
	 * Max width for the thumbnail.
	 */
	protected abstract int getThumbnailMaxHeight();
	
	/**
	 * Max height for the thumbnail.
	 */
	protected abstract int getThumbnailMaxWidth();
	
	/**
	 * Create two temporary files: the image and the thumbnail.
	 * 
	 * @throws IOException
	 */
	protected void saveImageInTempFiles() throws IOException {		
		OutputStream out = null;
		InputStream is = null;
		try {
			is = getImageFile().getInputStream();
			final BufferedImage originalBufferedImage = ImageIO.read(is);
			
			BufferedImage targetBufferedImage = DocumentUtils.getHighQualityScaledInstance(originalBufferedImage, getPictureMaxHeight(), getPictureMaxWidth());
			File tempFile = File.createTempFile("lendity-", ".tmp");
			ImageIO.write(targetBufferedImage, "jpg", tempFile);
			setTempFileLocation(tempFile.getAbsolutePath());
			setTempFileMimeType(DocumentConsts.MIME_TYPE_IMAGE_JPEG);
			setTempFileOriginalFileName(getImageFile().getName());
			if (log.isDebugEnabled()) {
				log.debug("Saved picture into temp file: " + getTempFileLocation());
			}
			
			targetBufferedImage = DocumentUtils.getHighQualityScaledInstance(originalBufferedImage, getThumbnailMaxHeight(), getThumbnailMaxWidth());
			tempFile = File.createTempFile("lendity-", ".tmp");
			ImageIO.write(targetBufferedImage, "jpg", tempFile);
			setTempThumbnailLocation(tempFile.getAbsolutePath());
			setTempThumbnailMimeType(DocumentConsts.MIME_TYPE_IMAGE_JPEG);
			setTempThumbnailOriginalFileName(getImageFile().getName());
			if (log.isDebugEnabled()) {
				log.debug("Saved thumbnail into temp file: " + getTempThumbnailLocation());
			}
		}
		finally {
			if (out != null) {
				out.close();
			}
			if (is != null) {
				is.close();
			}
		}
	}
	
	/**
	 * Validate the image and save it in two temp files (image and
	 * thumbnail) if ok.
	 * 
	 * @return
	 */
	public String processImage() {
		try {
			if (validateImage()) {
				saveImageInTempFiles();
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
	
	/**
	 * When the user clicks the link to remove the current image.
	 *
	 * @return
	 */
	public String removeCurrentImage() {
		setRemoveCurrentImage(Boolean.TRUE);
		setTempFileLocation(null);
		return "success";
	}

//	public String submit() {
//		Long personId = updatePerson();
//		
//		if (personId != null) {
//			JsfUtils.redirect(PagesURL.PERSON_OVERVIEW, PagesURL.PERSON_OVERVIEW_PARAM_PERSON_ID, personId.toString());
//		}
//	
//		// Return to the same page.
//		return "error";
//	}
	
//	public String getImgSrc() {
//		if (Boolean.TRUE.equals(removeCurrentImage)) {
//			return JsfUtils.getFullUrl(PersonConsts.DUMMY_PROFILE_PICTURE_URL);
//		}
//		else if (getTempFileLocation() != null) {
//			return getTempFileImgSrc();
//		}
//		else {
//			return personService.getProfilePictureSrc(getPerson(), true);
//		}
//	}
//	
//	public String getThumbnailSrc() {
//		if (Boolean.TRUE.equals(removeCurrentImage)) {
//			return JsfUtils.getFullUrl(PersonConsts.DUMMY_PROFILE_THUMBNAIL_URL);
//		}
//		else if (getTempThumbnailLocation() != null) {
//			return getTempThumbnailImgSrc();			
//		}
//		else {
//			return personService.getProfileThumbnailSrc(getPerson(), true);
//		}
//	}
}
