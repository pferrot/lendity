package com.pferrot.lendity.lendtransaction.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.person.PersonService;
import com.pferrot.lendity.utils.JsfUtils;

public class LendTransactionsForItemAndPersonListController extends AbstractLendTransactionsListController {

	private final static Log log = LogFactory.getLog(LendTransactionsForItemAndPersonListController.class);
	
	private Long personId;
	private String personDisplayName;
	
	private Long itemId;
	private String itemTitle;
	
	private PersonService personService;
	
	public PersonService getPersonService() {
		return personService;
	}

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}	

	public Long getPersonId() {
		return personId;
	}

	public void setPersonId(Long personId) {
		this.personId = personId;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public String getPersonDisplayName() {
		return personDisplayName;
	}

	public void setPersonDisplayName(String personDisplayName) {
		this.personDisplayName = personDisplayName;
	}

	public String getItemTitle() {
		return itemTitle;
	}

	public void setItemTitle(String itemTitle) {
		this.itemTitle = itemTitle;
	}

	@Override
	protected ListWithRowCount getListWithRowCount() {
		return getLendTransactionService().findLendTransactionsForItemAndPerson(getItemId(), getPersonId(), getStatusId(), getFirstRow(), getRowsPerPage());
	}

	@InitView
	public void initView() {
		super.initView();
		final String personIdString = getPersonIdString();
		if (!StringUtils.isNullOrEmpty(personIdString)) {
			setPersonId(Long.valueOf(personIdString));
			setPersonDisplayName(getPersonService().findPersonDisplayName(getPersonId()));
		}

		final String itemIdString = JsfUtils.getRequestParameter(PagesURL.LEND_TRANSACTIONS_FOR_ITEM_AND_PERSON_LIST_PARAM_ITEM_ID);
		if (!StringUtils.isNullOrEmpty(itemIdString)) {
			setItemId(Long.valueOf(itemIdString));
			setItemTitle(getItemService().findItemTitle(getItemId()));
		}
	}

	protected String getPersonIdString() {
		return JsfUtils.getRequestParameter(PagesURL.LEND_TRANSACTIONS_FOR_ITEM_AND_PERSON_LIST_PARAM_PERSON_ID);
	}

	public String getPersonUrl() {
		return JsfUtils.getFullUrl(
				PagesURL.PERSON_OVERVIEW,
				PagesURL.PERSON_OVERVIEW_PARAM_PERSON_ID,
				getPersonId().toString());
	}

	public String getItemUrl() {
		return JsfUtils.getFullUrl(
				PagesURL.ITEM_OVERVIEW,
				PagesURL.ITEM_OVERVIEW_PARAM_ITEM_ID,
				getItemId().toString());
	}
}
