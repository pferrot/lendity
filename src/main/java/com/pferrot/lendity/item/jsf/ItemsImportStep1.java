package com.pferrot.lendity.item.jsf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.PreRenderView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.item.ItemService;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.lendity.utils.StringCaseInsensitiveComparator;
import com.pferrot.lendity.utils.UiUtils;
import com.pferrot.lendity.utils.file.FileConsts;
import com.pferrot.lendity.utils.file.exception.NotUtf8FileException;

@ViewController(viewIds={"/auth/item/itemsImport.jspx"})
public class ItemsImportStep1 extends AbstractItemsImportStep {
	
	private final static Log log = LogFactory.getLog(ItemsImportStep1.class);
	
	private final static String TEXT_PLAIN_MIME_TYPE = "text/plain";
	
	private final static int MAX_TITLE_LENGTH = 255;
	private final static int MAX_NB_ITEMS_TO_IMPORT = 1000;
	
	private ItemService itemService;
	
	private UIComponent fileUIComponent;
	
	private String fileTooLargeErrorMessage;
	
	public ItemService getItemService() {
		return itemService;
	}

	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}
	
	public String getFileTooLargeErrorMessage() {
		return fileTooLargeErrorMessage;
	}

	public void setFileTooLargeErrorMessage(String fileTooLargeErrorMessage) {
		this.fileTooLargeErrorMessage = fileTooLargeErrorMessage;
	}

	@PreRenderView
	public void checkRequestForFileError() {
		final HttpServletRequest request = JsfUtils.getHttpServletRequest();
		final Object fileUploadException = request.getAttribute("org.apache.myfaces.custom.fileupload.exception");
		if (log.isDebugEnabled()) {
			log.debug("File upload exception: " + fileUploadException);
		}
		if (fileUploadException != null) {
			setFileTooLargeErrorMessage(UiUtils.getFileTooLargeErrorMessageFromResource(I18nUtils.getDefaultLocale()));
		}
		else {
			setFileTooLargeErrorMessage(null);
		}
	}

	protected Collection<String> processFile(final InputStream pIs, final String pEncoding) throws IOException, NotUtf8FileException {
		final InputStreamReader isr = new InputStreamReader(pIs, pEncoding);
		final BufferedReader bufferedReader = new BufferedReader(isr);
		String line = null;
		int counter = 0;
		
		final Collection<String> allItems = new ArrayList<String>();
		// First just read the file - no db access.
		while ((line = bufferedReader.readLine()) != null) {
			if (log.isDebugEnabled()) {
				log.debug(line);
			}
			if (!StringUtils.isNullOrEmpty(line)) {
				// Fix bug when the first character of the first line is the BOM (Byte Order Mark), unicode = 65279.
				int firstCharUnicode = line.charAt(0);
				if (firstCharUnicode == 65279) {
					if (line.length() > 1) {
						line = line.substring(1);
					}
					else {
						line = null;
					}
				}
				// Still not null or empty !?
				if (!StringUtils.isNullOrEmpty(line)) {
					if (line.contains(FileConsts.UNICODE_REPLACEMENT_CHARACTER)) {
						if (log.isWarnEnabled()) {
							log.warn("Line contains Unicode Replacement Character: " + line);
						}
						throw new NotUtf8FileException();
					}
					counter++;
					if (counter > MAX_NB_ITEMS_TO_IMPORT) {
						tooManyEntriesInFile(MAX_NB_ITEMS_TO_IMPORT);
						return null;
					}
					allItems.add(line);
				}
			}
		}
		return allItems;
	}
	
	public String submit() {
		try {
			if (log.isDebugEnabled()) {
				log.debug("File type: " + getItemsImportController().getUploadFile().getContentType());
				log.debug("File name: " + getItemsImportController().getUploadFile().getName());
				log.debug("File size: " + getItemsImportController().getUploadFile().getSize() + " bytes");
			}
			
			final String mimeType = getItemsImportController().getUploadFile().getContentType();
			if (! TEXT_PLAIN_MIME_TYPE.equals(mimeType)) {
				notTextFile();
				return "error";
			}
		
		
			final InputStream is = getItemsImportController().getUploadFile().getInputStream();
			
			is.mark(10000000);
			Collection<String> allItems = new ArrayList<String>();
			try {
				allItems = processFile(is, "UTF-8");
			}
			catch (NotUtf8FileException e) {
				is.reset();
				allItems = processFile(is, "ISO-8859-1");
			}
			
			if (allItems == null) {
				return "error";
			}		
			Set<String> validItemsToImport = new TreeSet<String>(new StringCaseInsensitiveComparator());
			Set<String> alreadyExistItemsToImport = new TreeSet<String>(new StringCaseInsensitiveComparator());
			Set<String> titleTooLongItemsToImport = new TreeSet<String>(new StringCaseInsensitiveComparator());
			
			// Now check if already exist or size.
			for (String oneItem: allItems) {
				if (oneItem.length() > MAX_TITLE_LENGTH) {
					titleTooLongItemsToImport.add(oneItem);
				}
				else if (getItemService().findMyItems(oneItem, null, null, null, null, null, null, 0, 1).getRowCount() > 0) {
					alreadyExistItemsToImport.add(oneItem);
				}
				else {
					validItemsToImport.add(oneItem);
				}
			}
			
			getItemsImportController().setValidItemsToImport(validItemsToImport);
			getItemsImportController().setAlreadyExistItemsToImport(alreadyExistItemsToImport);
			getItemsImportController().setTitleTooLongItemsToImport(titleTooLongItemsToImport);
			return "success";
			
		} 
		catch (Exception e) {
			ioException();
			return "error";
		}
    }
	
	public UIComponent getFileUIComponent() {
		return fileUIComponent;
	}

	public void setFileUIComponent(UIComponent fileUIComponent) {
		this.fileUIComponent = fileUIComponent;
	}

	private void ioException() {
		String message = "";
		FacesContext context = FacesContext.getCurrentInstance();
		final Locale locale = I18nUtils.getDefaultLocale();
		message = I18nUtils.getMessageResourceString("validation_importIoError", locale);
		context.addMessage(getFileUIComponent().getClientId(context), new FacesMessage(message));
	}
	
	private void notTextFile() {
		String message = "";
		FacesContext context = FacesContext.getCurrentInstance();
		final Locale locale = I18nUtils.getDefaultLocale();
		message = I18nUtils.getMessageResourceString("validation_importIoError", locale);
		context.addMessage(getFileUIComponent().getClientId(context), new FacesMessage(message));
	}
	
	private void tooManyEntriesInFile(final int pMaxNbEntries) {
		String message = "";
		FacesContext context = FacesContext.getCurrentInstance();
		final Locale locale = I18nUtils.getDefaultLocale();
		message = I18nUtils.getMessageResourceString("validation_maxNbItemsToImportExceeded",
				new Object[]{String.valueOf(pMaxNbEntries)}, locale);
		context.addMessage(getFileUIComponent().getClientId(context), new FacesMessage(message));
		
	}
	
	public void validateFile(FacesContext context, UIComponent toValidate, Object value) {
		setFileUIComponent(toValidate);
	}
}
