package com.pferrot.lendity.jobs.toreturnemailreminder;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.pferrot.emailsender.manager.MailManager;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.configuration.Configuration;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.jobs.emailsubscriber.TransactionalQuartzJobBean;
import com.pferrot.lendity.lendtransaction.LendTransactionService;
import com.pferrot.lendity.model.LendTransaction;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.lendity.utils.UiUtils;

public class ToReturnEmailReminderJob extends TransactionalQuartzJobBean {
	
	private final static Log log = LogFactory.getLog(ToReturnEmailReminderJob.class);
		
	private MailManager mailManager;
	private LendTransactionService lendTransactionService;	

	public LendTransactionService getLendTransactionService() {
		return lendTransactionService;
	}

	public void setLendTransactionService(
			LendTransactionService lendTransactionService) {
		this.lendTransactionService = lendTransactionService;
	}

	public void setMailManager(MailManager mailManager) {
		this.mailManager = mailManager;
	}

	@Override
	protected void executeTransactional(final JobExecutionContext pJobExecutionContext)
			throws JobExecutionException {
		
		if (log.isWarnEnabled()) {
			log.warn("Executing ToReturnEmailReminderJob");
		}
		try {
			sendEmailForAllTransactions();
		}
		catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error("Exception executing ToReturnEmailReminderJob", e);
			}
			throw new JobExecutionException(e);
		}
		finally {
			if (log.isWarnEnabled()) {
				log.warn("Done executing ToReturnEmailReminderJob");
			}
		}
	}
	
	/**
	 * Prepares and sends out reminder email for all lend transactions
	 * that happen soon. 
	 */
	private void sendEmailForAllTransactions() {
		final List<LendTransaction> transactions = lendTransactionService.findLendTransactionsToReturnSoon();
		for (LendTransaction transaction: transactions) {
			sendForOneTransaction(transaction);
		}
	}
	
	private void sendForOneTransaction(final LendTransaction pLendTransaction) {
		
		if (log.isDebugEnabled()) {
			log.debug("Preparing email for: " + pLendTransaction);
		}
		
		final Locale locale = I18nUtils.getDefaultLocale();
	
		Map<String, String> objects = new HashMap<String, String>();
		objects.put("lendTransactionUrl", getLendTransactionUrl(pLendTransaction));
		objects.put("lenderName", pLendTransaction.getLender().getDisplayName());
		objects.put("lenderFirstName", pLendTransaction.getLender().getFirstName());
		objects.put("lendTransactionTitle", pLendTransaction.getTitle());
		objects.put("startDateLabel", UiUtils.getDateAsString(pLendTransaction.getStartDate(), locale));
		objects.put("endDateLabel", UiUtils.getDateAsString(pLendTransaction.getEndDate(), locale));
		if (pLendTransaction.getBorrower() != null) {
			objects.put("borrowerName", pLendTransaction.getBorrower().getDisplayName());
			objects.put("borrowerFirstName", pLendTransaction.getBorrower().getFirstName());
		}
		else {
			objects.put("borrowerName", pLendTransaction.getBorrowerName());
		}
		objects.put("profileUrl", getProfileUrl());
		objects.put("signature", Configuration.getSiteName());
		objects.put("siteName", Configuration.getSiteName());
		objects.put("siteUrl", Configuration.getRootURL());
		
		Map<String, String> inlineResources = new HashMap<String, String>();
		inlineResources.put("logo", "com/pferrot/lendity/emailtemplate/lendity_logo.png");
		
		// Mail to the lender.
		if (pLendTransaction.getLender() != null &&
			pLendTransaction.getLender().isEnabled()) {
			
			// TODO: localization
			final String velocityTemplateLocation = "com/pferrot/lendity/emailtemplate/reminder/toreturn/lender/fr";
		
			Map<String, String> to = new HashMap<String, String>();
			to.put(pLendTransaction.getLender().getEmail(), pLendTransaction.getLender().getEmail());
			
			mailManager.send(Configuration.getNoReplySenderName(), 
					     	 Configuration.getNoReplyEmailAddress(),
					     	 to,
					         null, 
					         null,
					         Configuration.getSiteName() + ": rappel, un objet doit t'être rendu",
					         objects, 
					         velocityTemplateLocation,
					         inlineResources);
		}

		// Mail to the borrower.
		if (pLendTransaction.getBorrower() != null &&
			pLendTransaction.getBorrower().isEnabled()) {
				
				// TODO: localization
				final String velocityTemplateLocation = "com/pferrot/lendity/emailtemplate/reminder/toreturn/borrower/fr";
			
				Map<String, String> to = new HashMap<String, String>();
				to.put(pLendTransaction.getBorrower().getEmail(), pLendTransaction.getBorrower().getEmail());
				
				mailManager.send(Configuration.getNoReplySenderName(), 
						     	 Configuration.getNoReplyEmailAddress(),
						     	 to,
						         null, 
						         null,
						         Configuration.getSiteName() + ": rappel, objet à rendre",
						         objects, 
						         velocityTemplateLocation,
						         inlineResources);
			}
	}
	
	private String getLendTransactionUrl(final LendTransaction pLendTransaction) {
		return JsfUtils.getFullUrlWithPrefix(
				Configuration.getRootURL(),
				PagesURL.LEND_TRANSACTION_OVERVIEW,
				PagesURL.LEND_TRANSACTION_OVERVIEW_PARAM_LEND_TRANSACTION_ID,
				pLendTransaction.getId().toString());
	}
	
	private String getProfileUrl() {
		return JsfUtils.getFullUrlWithPrefix(Configuration.getRootURL(), PagesURL.MY_PROFILE);
	}
}
