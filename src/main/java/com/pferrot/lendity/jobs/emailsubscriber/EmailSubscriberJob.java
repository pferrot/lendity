package com.pferrot.lendity.jobs.emailsubscriber;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import com.pferrot.lendity.connectionrequest.ConnectionRequestService;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.item.ItemService;
import com.pferrot.lendity.item.jsf.MyConnectionsItemsListController;
import com.pferrot.lendity.model.ConnectionRequest;
import com.pferrot.lendity.model.InternalItem;
import com.pferrot.lendity.model.Need;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.need.NeedService;
import com.pferrot.lendity.need.jsf.MyConnectionsNeedsListController;
import com.pferrot.lendity.person.PersonService;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.lendity.utils.UiUtils;

public class EmailSubscriberJob extends TransactionalQuartzJobBean {
	
	private final static Log log = LogFactory.getLog(EmailSubscriberJob.class);
	
	private int frequencyInDays;
	
	private MailManager mailManager;
	private ItemService itemService;
	private NeedService needService;
	private ConnectionRequestService connectionRequestService;
	private PersonService personService;

	public void setMailManager(MailManager mailManager) {
		this.mailManager = mailManager;
	}

	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}

	public void setNeedService(NeedService needService) {
		this.needService = needService;
	}

	public void setConnectionRequestService(
			ConnectionRequestService connectionRequestService) {
		this.connectionRequestService = connectionRequestService;
	}

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	public int getFrequencyInDays() {
		return frequencyInDays;
	}

	public void setFrequencyInDays(int frequencyInDays) {
		this.frequencyInDays = frequencyInDays;
	}

	@Override
	protected void executeTransactional(final JobExecutionContext pJobExecutionContext)
			throws JobExecutionException {
		
		if (log.isWarnEnabled()) {
			log.warn("Executing EmailSubscriberJob");
		}
		try {
			

			// This is not needed: the mapping is done automatically by Spring!
//			final SchedulerContext schedularContext = pJobExecutionContext.getScheduler().getContext();  
//			setMailManager((MailManager)schedularContext.get("mailManager"));
//			setItemService((ItemService)schedularContext.get("itemService"));
//			setConnectionRequestService((ConnectionRequestService)schedularContext.get("connectionRequestService"));
//			setPersonService((PersonService)schedularContext.get("personService"));
			
			sendEmailToAllUsers();
		}
		catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error("Exception executing EmailSubscriberJob", e);
			}
			throw new JobExecutionException(e);
		}
		finally {
			if (log.isWarnEnabled()) {
				log.warn("Done executing EmailSubscriberJob");
			}
		}
	}
	
	/**
	 * Prepares and sends out the email to all users who
	 * subscribed and who did not get the email for
	 * <code>frequencyInDays</code> days at least.
	 */
	private void sendEmailToAllUsers() {
		// Only retrieve users that have been last updated before that date.
		Calendar lastUpdateMaxDate = Calendar.getInstance();
		lastUpdateMaxDate.add(Calendar.DAY_OF_MONTH, -getFrequencyInDays());
		// 2 loops to avoid loading too many records from the DB at the same time.
		for (int i = 0; i < 20; i++) {
			final List<Person> emailSubscribers = personService.findEmailSubscribers(new Date(lastUpdateMaxDate.getTimeInMillis()), 25);
			if (emailSubscribers.isEmpty()) {
				return;
			}
			for (Person emailSubscriber: emailSubscribers) {
				sendEmailToOneUser(emailSubscriber);
			}
		}
	}
	
	private void sendEmailToOneUser(final Person pPerson) {
		
		if (log.isDebugEnabled()) {
			log.debug("Preparing email for: " + pPerson);
		}
		
		final Locale locale = I18nUtils.getDefaultLocale();
		
		Date latestUpdate = pPerson.getEmailSubscriberLastUpdate();
		String latestUpdateDateLabel = null;
		if (latestUpdate == null) {
			Calendar longTimeAgo = Calendar.getInstance();
			longTimeAgo.set(1900, 1, 1);
			latestUpdate = longTimeAgo.getTime();
		}
		else {
			latestUpdateDateLabel = UiUtils.getDateAsString(latestUpdate, locale);
		}
		
		Map<String, String> objects = new HashMap<String, String>();
		objects.put("latestUpdateDateLabel", latestUpdateDateLabel);
		
		// Items.
		final ListWithRowCount items = itemService.findPersonLatestConnectionsItemsSince(pPerson, latestUpdate);
		if (log.isDebugEnabled()) {
			log.debug("Items: " + items.getRowCount());
		}
		List list = items.getList();
		Iterator ite = list.iterator();
		int itemsCounter = 0;
		while (ite.hasNext()) {
			itemsCounter++;
			final InternalItem item = (InternalItem)ite.next();
			objects.put("itemTitle" + itemsCounter, item.getTitle());
			objects.put("itemDetails" + itemsCounter, getItemDetails(item));
			objects.put("itemUrl" + itemsCounter, getItemUrl(item));
		}
		long nbExtra = items.getRowCount() > itemsCounter ? items.getRowCount()-itemsCounter : 0;
		if (nbExtra > 0) {
			objects.put("itemNbExtra", String.valueOf(nbExtra));
		}
		objects.put("itemUrl", getConnectionsItemsByDateUrl());
		
		// Needs.
		final ListWithRowCount needs = needService.findPersonLatestConnectionsNeedsSince(pPerson, latestUpdate);
		if (log.isDebugEnabled()) {
			log.debug("Needs: " + needs.getRowCount());
		}
		list = needs.getList();
		ite = list.iterator();
		int needsCounter = 0;
		while (ite.hasNext()) {
			needsCounter++;
			final Need need = (Need)ite.next();
			objects.put("needTitle" + needsCounter, need.getTitle());
			objects.put("needDetails" + needsCounter, getNeedDetails(need));
			objects.put("needUrl" + needsCounter, getNeedUrl(need));
		}
		nbExtra = needs.getRowCount() > needsCounter ? needs.getRowCount()-needsCounter : 0;
		if (nbExtra > 0) {
			objects.put("needNbExtra", String.valueOf(nbExtra));
		}
		objects.put("needUrl", getConnectionsNeedsByDateUrl());
		
		// Connection updates.
		final ListWithRowCount connectionsUpdates = connectionRequestService.findPersonConnectionsUpdatesSince(pPerson, latestUpdate);
		if (log.isDebugEnabled()) {
			log.debug("Connections updates: " + connectionsUpdates.getRowCount());
		}
		list = connectionsUpdates.getList();
		ite = list.iterator();
		int connectionsUpdatesCounter = 0;
		while (ite.hasNext()) {
			connectionsUpdatesCounter++;
			final ConnectionRequest connectionRequest = (ConnectionRequest)ite.next();
			objects.put("requester" + connectionsUpdatesCounter, connectionRequest.getRequester().getDisplayName());
			objects.put("requesterUrl" + connectionsUpdatesCounter, getPersonUrl(connectionRequest.getRequester()));
			objects.put("connection" + connectionsUpdatesCounter, connectionRequest.getConnection().getDisplayName());
			objects.put("connectionUrl" + connectionsUpdatesCounter, getPersonUrl(connectionRequest.getConnection()));
			objects.put("connectionUpdateDetails" + connectionsUpdatesCounter, getConnectionUpdateDetails(connectionRequest));
		}
		nbExtra = connectionsUpdates.getRowCount() > connectionsUpdatesCounter ? connectionsUpdates.getRowCount()-connectionsUpdatesCounter : 0;
		if (nbExtra > 0) {
			objects.put("connectionUpdateNbExtra", String.valueOf(nbExtra));
		}
		objects.put("connectionUpdateUrl", getConnectionsUpdatesUrl());
		
		// Borrowed items.
//		final ListWithRowCount borrowedItems = itemService.findBorrowedItems(pPerson.getId(), null, null, null, 0, 5);
//		System.out.println("Borrowed items: " + borrowedItems.getRowCount());
//		if (borrowedItems.getRowCount() > 0) {
//			objects.put("borrowedItemNb", String.valueOf(borrowedItems.getRowCount()));
//		}
//		objects.put("borrowedItemUrl", getBorrowedItemsUrl());
		
		// Lent items.
//		final ListWithRowCount lentItems = itemService.findItems(pPerson.getId(), null, null, null, Boolean.TRUE, null, null, 0, 5);
//		System.out.println("Lent items: " + lentItems.getRowCount());
//		if (lentItems.getRowCount() > 0) {
//			objects.put("lentItemNb", String.valueOf(lentItems.getRowCount()));
//		}
//		objects.put("lentItemUrl", getLentItemsUrl());
		
		// Do not send the email if nothing to say...
		if (itemsCounter > 0 ||
			connectionsUpdatesCounter > 0) {
		
			if (log.isDebugEnabled()) {
				log.debug("Sending email to: " + pPerson);
			}
			
			objects.put("firstName", pPerson.getFirstName());
			objects.put("profileUrl", getProfileUrl());
			objects.put("signature", Configuration.getSiteName());
			objects.put("siteName", Configuration.getSiteName());
			objects.put("siteUrl", Configuration.getRootURL());
			
			// TODO: localization
			final String velocityTemplateLocation = "com/pferrot/lendity/emailtemplate/weeklyupdate/fr";
			
			Map<String, String> to = new HashMap<String, String>();
			to.put(pPerson.getEmail(), pPerson.getEmail());
			
			Map<String, String> inlineResources = new HashMap<String, String>();
			inlineResources.put("logo", "com/pferrot/lendity/emailtemplate/lendity_logo.gif");
			
			final String currentDateLabel = UiUtils.getDateAsString(new Date(), locale);
			
			mailManager.send(Configuration.getNoReplySenderName(), 
					         Configuration.getNoReplyEmailAddress(),
					         to,
					         null, 
					         null,
					         Configuration.getSiteName() + ": updates " + currentDateLabel,
					         objects, 
					         velocityTemplateLocation,
					         inlineResources);
		}
		else {
			if (log.isDebugEnabled()) {
				log.debug("No update for, not sending email: " + pPerson);
			}
		}
		// Update user in any case, so that the same user is not verified everyday even if there is
		// nothing to send to him.
		pPerson.setEmailSubscriberLastUpdate(new Date());
		personService.updatePersonPrivileged(pPerson);
	}
	
	private String getItemDetails(final InternalItem pItem) {
		if (pItem == null) {
			return "";
		}
		final Locale locale = I18nUtils.getDefaultLocale();
		final String param1 = UiUtils.getListValueLabel(pItem.getCategory(), locale) ;
		final String param2 = pItem.getOwner().getDisplayName() ;
		final String param3 = UiUtils.getDateAsString(pItem.getCreationDate(), locale);
		return I18nUtils.getMessageResourceString("home_latestConnectionItemsDetails", new Object[]{param1, param2, param3}, locale);	
	}
	
	private String getNeedDetails(final Need pNeed) {
		if (pNeed == null) {
			return "";
		}
		final Locale locale = I18nUtils.getDefaultLocale();
		final String param1 = pNeed.getOwner().getDisplayName() ;
		final String param2 = UiUtils.getDateAsString(pNeed.getCreationDate(), locale);
		return I18nUtils.getMessageResourceString("home_latestConnectionNeedsDetails", new Object[]{param1, param2}, locale);	
	}
	
	private String getConnectionUpdateDetails(final ConnectionRequest pConnectionRequest) {
		if (pConnectionRequest == null) {
			return "";
		}
		final Locale locale = I18nUtils.getDefaultLocale();
		final String param1 = UiUtils.getDateAsString(pConnectionRequest.getResponseDate(), locale) ;
		return I18nUtils.getMessageResourceString("home_connectionsUpdatesDetails", new Object[]{param1}, locale);		
	}
	
	private String getItemUrl(final InternalItem pItem) {
		return JsfUtils.getFullUrlWithPrefix(
				Configuration.getRootURL(),
				PagesURL.INTERNAL_ITEM_OVERVIEW,
				PagesURL.INTERNAL_ITEM_OVERVIEW_PARAM_ITEM_ID,
				pItem.getId().toString());
	}
	
	private String getNeedUrl(final Need pNeed) {
		return JsfUtils.getFullUrlWithPrefix(
				Configuration.getRootURL(),
				PagesURL.NEED_OVERVIEW,
				PagesURL.NEED_OVERVIEW_PARAM_NEED_ID,
				pNeed.getId().toString());
	}
	
	private String getPersonUrl(final Person pPerson) {
		return JsfUtils.getFullUrlWithPrefix(
				Configuration.getRootURL(),
				PagesURL.PERSON_OVERVIEW,
				PagesURL.PERSON_OVERVIEW_PARAM_PERSON_ID,
				pPerson.getId().toString());		
	}
	
	private String getConnectionsUpdatesUrl() {
		return JsfUtils.getFullUrlWithPrefix(Configuration.getRootURL(), PagesURL.MY_CONNECTIONS_UPDATES_LIST);
		
	}
	
	private String getConnectionsItemsByDateUrl() {
		return JsfUtils.getFullUrlWithPrefix(
				Configuration.getRootURL(),
				PagesURL.MY_CONNECTIONS_ITEMS_LIST,
				MyConnectionsItemsListController.FORCE_VIEW_PARAM_NAME,
				MyConnectionsItemsListController.FORCE_VIEW_ALL_BY_CREATION_DATE_VALUE);
	}
	
	private String getConnectionsNeedsByDateUrl() {
		return JsfUtils.getFullUrlWithPrefix(
				Configuration.getRootURL(),
				PagesURL.MY_CONNECTIONS_NEEDS_LIST,
				MyConnectionsNeedsListController.FORCE_VIEW_PARAM_NAME,
				MyConnectionsNeedsListController.FORCE_VIEW_ALL_BY_CREATION_DATE_VALUE);
	}
	
//	private String getBorrowedItemsUrl() {
//		return JsfUtils.getFullUrlWithPrefix(Configuration.getRootURL(), PagesURL.MY_BORROWED_ITEMS_LIST);
//	}
//	
//	private String getLentItemsUrl() {
//		return JsfUtils.getFullUrlWithPrefix(
//				Configuration.getRootURL(),
//				PagesURL.MY_ITEMS_LIST,
//				MyItemsListController.FORCE_VIEW_PARAM_NAME,
//				MyItemsListController.FORCE_VIEW_ALL_LENT_ITEMS_BY_NAME);
//	}
	
	private String getProfileUrl() {
		return JsfUtils.getFullUrlWithPrefix(Configuration.getRootURL(), PagesURL.MY_PROFILE);
	}
}
