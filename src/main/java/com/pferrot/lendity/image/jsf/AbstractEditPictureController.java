package com.pferrot.lendity.image.jsf;

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
import com.pferrot.lendity.document.exception.DocumentException;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.model.Document;
import com.pferrot.lendity.utils.JsfUtils;

/**
 * Abstract class for handling the upload of an image.
 *
 * @author pferrot
 *
 */
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
		final File file1 = new File(pFileLocation);
		document.setMimeType(pMimeType);
		document.setSize(file1.length());
		document.setName(pOriginalFileName);
		final FileInputStream fis = new FileInputStream(file1);
		document.setInputStream(fis);
		return document;
	}
	
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
	protected abstract int getImageMaxHeight();
	
	/**
	 * Max height for the image.
	 */
	protected abstract int getImageMaxWidth();
	
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
	 * @throws DocumentException 
	 */
	protected void saveImageInTempFiles() throws IOException, DocumentException {		
		OutputStream out = null;
		InputStream is = null;
		try {
			is = getImageFile().getInputStream();
			final BufferedImage originalBufferedImage = ImageIO.read(is);
			final String mimeType = getImageFile().getContentType();
			
			BufferedImage targetBufferedImage = DocumentUtils.getHighQualityScaledInstance(originalBufferedImage, getImageMaxHeight(), getImageMaxWidth());
			File tempFile = File.createTempFile("lendity-", ".tmp");
			ImageIO.write(targetBufferedImage, DocumentUtils.getFormat(mimeType), tempFile);
			setTempFileLocation(tempFile.getAbsolutePath());
			setTempFileMimeType(mimeType);
			setTempFileOriginalFileName(getImageFile().getName());
			if (log.isDebugEnabled()) {
				log.debug("Saved picture into temp file: " + getTempFileLocation());
			}
			
			targetBufferedImage = DocumentUtils.getHighQualityScaledInstance(originalBufferedImage, getThumbnailMaxHeight(), getThumbnailMaxWidth());
			tempFile = File.createTempFile("lendity-", ".tmp");
			ImageIO.write(targetBufferedImage, DocumentUtils.getFormat(mimeType), tempFile);
			setTempThumbnailLocation(tempFile.getAbsolutePath());
			setTempThumbnailMimeType(mimeType);
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
		} catch (DocumentException e) {
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
	
	/**
	 * The page to go to when clicking cancel.
	 *
	 * @return
	 */
	public abstract String getCancelHref();
	
	/**
	 * Returns true if the image exists, false otherwise.
	 *
	 * @return
	 */
	public abstract boolean isExistingImage();
}
