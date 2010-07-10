package com.pferrot.lendity.item.jsf;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.utils.JsfUtils;

public abstract class AbstractExternalItemAddEditController extends AbstractItemAddEditController {
	
	private final static Log log = LogFactory.getLog(AbstractExternalItemAddEditController.class);
	private final static DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
	
	private String ownerName;
	private Date borrowDate = new Date();
	
	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	
	public Date getBorrowDate() {
		return borrowDate;
	}

	public void setBorrowDate(Date borrowDate) {
		this.borrowDate = borrowDate;
	}

	public void setBorrowDateAsString(String borrowDateAsString) {
		try {
			setBorrowDate(DATE_FORMAT.parse(borrowDateAsString));
		}
		catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String getBorrowDateAsString() {
		return DATE_FORMAT.format(getBorrowDate());
	}

	public String submit() {
		Long itemId = processItem();
		
		JsfUtils.redirect(PagesURL.EXTERNAL_ITEM_OVERVIEW, PagesURL.EXTERNAL_ITEM_OVERVIEW_PARAM_ITEM_ID, itemId.toString());
	
		// As a redirect is used, this is actually useless.
		return null;
	}
}