package com.pferrot.lendity.item.jsf;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.apache.myfaces.orchestra.viewController.annotations.PreRenderView;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.document.DocumentUtils;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.utils.JsfUtils;

public abstract class AbstractItemAddEditController extends AbstractObjectAddEditController {
	
	private final static Log log = LogFactory.getLog(AbstractItemAddEditController.class);
	
	
	private UploadedFile imageFile1;
	private UIComponent imageFile1UIComponent;
	private String imageFile1TooLargeErrorMessage;
	
	private UploadedFile imageFile2;
	private UIComponent imageFile2UIComponent;
	private String imageFile2TooLargeErrorMessage;
	
	private UploadedFile imageFile3;
	private UIComponent imageFile3UIComponent;
	private String imageFile3TooLargeErrorMessage;
	
	public UploadedFile getImageFile1() {
		return imageFile1;
	}

	public void setImageFile1(UploadedFile imageFile1) {
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
	
	public UploadedFile getImageFile2() {
		return imageFile2;
	}

	public void setImageFile2(UploadedFile imageFile2) {
		this.imageFile2 = imageFile2;
	}

	public UIComponent getImageFile2UIComponent() {
		return imageFile2UIComponent;
	}

	public void setImageFile2UIComponent(UIComponent imageFile2UIComponent) {
		this.imageFile2UIComponent = imageFile2UIComponent;
	}

	public String getImageFile2TooLargeErrorMessage() {
		return imageFile2TooLargeErrorMessage;
	}

	public void setImageFile2TooLargeErrorMessage(
			String imageFile2TooLargeErrorMessage) {
		this.imageFile2TooLargeErrorMessage = imageFile2TooLargeErrorMessage;
	}

	public UploadedFile getImageFile3() {
		return imageFile3;
	}

	public void setImageFile3(UploadedFile imageFile3) {
		this.imageFile3 = imageFile3;
	}

	public UIComponent getImageFile3UIComponent() {
		return imageFile3UIComponent;
	}

	public void setImageFile3UIComponent(UIComponent imageFile3UIComponent) {
		this.imageFile3UIComponent = imageFile3UIComponent;
	}

	public String getImageFile3TooLargeErrorMessage() {
		return imageFile3TooLargeErrorMessage;
	}

	public void setImageFile3TooLargeErrorMessage(
			String imageFile3TooLargeErrorMessage) {
		this.imageFile3TooLargeErrorMessage = imageFile3TooLargeErrorMessage;
	}

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

	protected void errorImageFile() {
		errorImageFile(getImageFile1UIComponent());
	}
	
	protected void errorImageFile(final UIComponent pUiComponent) {
		String message = "";
		FacesContext context = FacesContext.getCurrentInstance();
		message = I18nUtils.getMessageResourceString("validation_imageIoError", I18nUtils.getDefaultLocale());
		context.addMessage(pUiComponent.getClientId(context), new FacesMessage(message));
	}
	
	public abstract Long processItem();
	
	public String submit() {
		Long itemId = processItem();
		
		JsfUtils.redirect(PagesURL.INTERNAL_ITEM_OVERVIEW, PagesURL.INTERNAL_ITEM_OVERVIEW_PARAM_ITEM_ID, itemId.toString());
	
		// As a redirect is used, this is actually useless.
		return null;
	}
}
