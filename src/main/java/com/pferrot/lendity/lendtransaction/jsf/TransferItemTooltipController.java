package com.pferrot.lendity.lendtransaction.jsf;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.lendtransaction.LendTransactionWithCommentService;
import com.pferrot.lendity.utils.JsfUtils;

public class TransferItemTooltipController implements Serializable {
	
	private final static Log log = LogFactory.getLog(TransferItemTooltipController.class);
	
	private LendTransactionWithCommentService lendTransactionWithCommentService;
	
	private Long lendTransactionId;

	public LendTransactionWithCommentService getLendTransactionWithCommentService() {
		return lendTransactionWithCommentService;
	}

	public void setLendTransactionWithCommentService(
			LendTransactionWithCommentService lendTransactionWithCommentService) {
		this.lendTransactionWithCommentService = lendTransactionWithCommentService;
	}

	public Long getLendTransactionId() {
		return lendTransactionId;
	}

	public void setLendTransactionId(Long lendTransactionId) {
		this.lendTransactionId = lendTransactionId;
	}

	public String submit() {
		try {
			getLendTransactionWithCommentService().updateGiveOrSellItem(getLendTransactionId());		
			
			JsfUtils.redirect(
					PagesURL.LEND_TRANSACTION_OVERVIEW,
					PagesURL.LEND_TRANSACTION_OVERVIEW_PARAM_LEND_TRANSACTION_ID,
					lendTransactionId.toString());
		
			// As a redirect is used, this is actually useless.
			return null;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
