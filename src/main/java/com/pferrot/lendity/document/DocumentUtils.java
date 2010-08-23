package com.pferrot.lendity.document;

import java.util.Locale;

import com.pferrot.lendity.i18n.I18nUtils;

public class DocumentUtils {

	/**
	 * Supported types for now: PNG, JPEG and GIF.
	 *
	 * @param pMimeType
	 * @return
	 */
	public static final boolean isSupportedImageMimeType(final String pMimeType) {
		return DocumentConsts.MIME_TYPE_IMAGE_GIF.equals(pMimeType)	||
			DocumentConsts.MIME_TYPE_IMAGE_JPEG.equals(pMimeType)	||
			DocumentConsts.MIME_TYPE_IMAGE_PNG.equals(pMimeType);
	}
	
	public static String getImageValidationErrorMessage(final Locale pLocale) {
		String message = "";
		message = I18nUtils.getMessageResourceString("validation_imageIoError", pLocale);
		return message;
	}

//	public static String getImageAccessCode(final Long pDocumentId) {
//		if (pDocumentId == null) {
//			return null;
//		}
//	}
}
