package com.pferrot.lendity.need.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.person.PersonService;
import com.pferrot.lendity.utils.JsfUtils;

@ViewController(viewIds={"/public/need/personNeedsList.jspx"})
public class PersonNeedsListController extends AbstractNeedsListController {
	
	private final static Log log = LogFactory.getLog(PersonNeedsListController.class);
	
	private PersonService personService;
	
	private Long personId;
	private String personDisplayName;
	
	public PersonNeedsListController() {
		super();
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
		final String personIdString = JsfUtils.getRequestParameter(PagesURL.PERSON_NEEDS_LIST_PARAM_PERSON_ID);
		if (!StringUtils.isNullOrEmpty(personIdString)) {
			resetFilters();
			setPersonId(Long.valueOf(personIdString));
			setPersonDisplayName(getPersonService().findPersonDisplayName(getPersonId()));
		}
		super.initView();
	}

	@Override
	protected ListWithRowCount getListWithRowCount() {
		return getNeedService().findPersonNeeds(getPersonId(), getSearchString(), getCategoryId(), getFulfilledBoolean(),
				getOrderByField(), getOrderByAscending(), getFirstRow(), getRowsPerPage());
	}

	public String getPersonUrl() {
		return JsfUtils.getFullUrl(
				PagesURL.PERSON_OVERVIEW,
				PagesURL.PERSON_OVERVIEW_PARAM_PERSON_ID,
				getPersonId().toString());
	}
	
	@Override
	protected String getCategoryLinkBaseUrl() {
		return JsfUtils.getFullUrl(PagesURL.PERSON_NEEDS_LIST, PagesURL.NEEDS_SEARCH_PARAM_CATEGORY_ID, PagesURL.NEEDS_SEARCH_PARAM_CATEGORY_ID_TO_REPLACE);
	}
}