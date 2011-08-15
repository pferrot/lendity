package com.pferrot.lendity.item.jsf;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.person.PersonService;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.lendity.utils.UiUtils;

@ViewController(viewIds={"/public/item/personItemsList.jspx"})
public class PersonItemsListController extends AbstractItemsListController {
	
	private final static Log log = LogFactory.getLog(PersonItemsListController.class);
	
	private PersonService personService;
	
	private Long personId;
	private String personDisplayName;
	
	public PersonItemsListController() {
		super();
		// Display available items by default.
		//setBorrowStatus(UiUtils.getLongFromBoolean(Boolean.FALSE));
	}

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

	public String getPersonDisplayName() {
		return personDisplayName;
	}

	public void setPersonDisplayName(String personDisplayName) {
		this.personDisplayName = personDisplayName;
	}

	@InitView
	public void initView() {
		final String personIdString = JsfUtils.getRequestParameter(PagesURL.PERSON_ITEMS_LIST_PARAM_PERSON_ID);
		if (!StringUtils.isNullOrEmpty(personIdString)) {
			resetFilters();
			setPersonId(Long.valueOf(personIdString));
			setPersonDisplayName(getPersonService().findPersonDisplayName(getPersonId()));
		}
		super.initView();
	}

	@Override
	protected ListWithRowCount getListWithRowCount() {
		return getItemService().findPersonItems(getPersonId(), getSearchString(), getCategoryId(), 
				getBorrowStatusBoolean(), getOrderByField(), getOrderByAscending(), getFirstRow(), getRowsPerPage());
	}

	@Override
	public List<SelectItem> getBorrowStatusSelectItems() {
		if (getBorrowStatusSelectItemsInternal() == null) {
			final List<SelectItem> result = new ArrayList<SelectItem>();
			final Locale locale = I18nUtils.getDefaultLocale();
			
			result.add(new SelectItem(UiUtils.getLongFromBoolean(null), I18nUtils.getMessageResourceString("item_availableStatusAll", locale)));
			result.add(new SelectItem(UiUtils.getLongFromBoolean(Boolean.FALSE), I18nUtils.getMessageResourceString("item_availableStatusAvailable", locale)));
			result.add(new SelectItem(UiUtils.getLongFromBoolean(Boolean.TRUE), I18nUtils.getMessageResourceString("item_availableStatusNotAvailable", locale)));
			
			setBorrowStatusSelectItemsInternal(result);
		}		
		return getBorrowStatusSelectItemsInternal();	
	}

	public String getPersonUrl() {
		return JsfUtils.getFullUrl(
				PagesURL.PERSON_OVERVIEW,
				PagesURL.PERSON_OVERVIEW_PARAM_PERSON_ID,
				getPersonId().toString());
	}
	
	@Override
	protected String getCategoryLinkBaseUrl() {
		return JsfUtils.getFullUrl(PagesURL.PERSON_ITEMS_LIST, PagesURL.ITEMS_SEARCH_PARAM_CATEGORY_ID, PagesURL.ITEMS_SEARCH_PARAM_CATEGORY_ID_TO_REPLACE);
	}
}