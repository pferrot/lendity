package com.pferrot.lendity.lendrequest.jsf;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.lendity.i18n.I18nConsts;
import com.pferrot.lendity.lendrequest.LendRequestService;

public class RequestLendTooltipController implements Serializable {
	
	private final static Log log = LogFactory.getLog(RequestLendTooltipController.class);
	
	private LendRequestService lendRequestService;
	
	private Long itemId;
	
	// 1 == my connections items page
	// 2 == item overview page
	// 3 == homepage
	// 4 == person items list
	private Long redirectId;
	
	private Date startDate;
	private Date endDate;

	public  LendRequestService getLendRequestService() {
		return lendRequestService;
	}

	public void setLendRequestService(LendRequestService lendRequestService) {
		this.lendRequestService = lendRequestService;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public Long getRedirectId() {
		return redirectId;
	}

	public void setRedirectId(Long redirectId) {
		this.redirectId = redirectId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setStartDateAsString(final String pStartDateAsString) {
		try {
			if (StringUtils.isNullOrEmpty(pStartDateAsString)) {
				setStartDate(null);
			}
			else {
				setStartDate(I18nConsts.DATE_FORMAT.parse(pStartDateAsString));
			}
		}
		catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String getStartDateAsString() {
		if (getStartDate() == null) {
			return "";
		}
		else {
			return I18nConsts.DATE_FORMAT.format(getStartDate());
		}
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	public void setEndDateAsString(final String pEndDateAsString) {
		try {
			if (StringUtils.isNullOrEmpty(pEndDateAsString)) {
				setEndDate(null);
			}
			else {
				setEndDate(I18nConsts.DATE_FORMAT.parse(pEndDateAsString));
			}
		}
		catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String getEndDateAsString() {
		if (getEndDate() == null) {
			return "";
		}
		else {
			return I18nConsts.DATE_FORMAT.format(getEndDate());
		}
	}

	public String submit() {
		final Long[] ids = requestLend();
		//final Long lendRequestId = ids[0];
		final Long lendTransactionId = ids[1];
		
//		if (getRedirectId().longValue() == 1) {
//			JsfUtils.redirect(PagesURL.ITEMS_SEARCH);
//		}
//		else if (getRedirectId().longValue() == 2) {
//			JsfUtils.redirect(PagesURL.ITEM_OVERVIEW, PagesURL.ITEM_OVERVIEW_PARAM_ITEM_ID, getItemId().toString());
//		}
//		else if (getRedirectId().longValue() == 3) {
//			JsfUtils.redirect(PagesURL.HOME);
//		}
//		else if (getRedirectId().longValue() == 4) {
//			JsfUtils.redirect(PagesURL.PERSON_ITEMS_LIST);
//		}
		
		JsfUtils.redirect(
				PagesURL.LEND_TRANSACTION_OVERVIEW,
				PagesURL.LEND_TRANSACTION_OVERVIEW_PARAM_LEND_TRANSACTION_ID,
				lendTransactionId.toString());
	
		// As a redirect is used, this is actually useless.
		return null;
	}

	private Long[] requestLend() {
		try {
			return getLendRequestService().createLendRequestFromCurrentUser(getItemId(), getStartDate(), getEndDate());
		} 
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
