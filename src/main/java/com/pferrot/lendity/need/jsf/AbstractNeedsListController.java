package com.pferrot.lendity.need.jsf;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.item.ItemConsts;
import com.pferrot.lendity.item.ObjektService;
import com.pferrot.lendity.item.jsf.AbstractObjektsListController;
import com.pferrot.lendity.model.Need;
import com.pferrot.lendity.model.Objekt;
import com.pferrot.lendity.need.NeedService;
import com.pferrot.lendity.need.NeedUtils;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.lendity.utils.UiUtils;
import com.pferrot.security.SecurityUtils;

public abstract class AbstractNeedsListController extends AbstractObjektsListController {
	
	private final static Log log = LogFactory.getLog(AbstractNeedsListController.class);
	
	private NeedService needService;
	
	private List<SelectItem> fulfilledSelectItems;
	// null = not fulfilled
	// 1 = fulfilled
	// 2 = all
	private Long fulfilled;
	
	public AbstractNeedsListController() {
		super();
	}

	public void setNeedService(final NeedService pNeedService) {
		this.needService = pNeedService;
	}
	
	public NeedService getNeedService() {
		return needService;
	}
	
	@Override
	protected ObjektService getObjektService() {
		return getNeedService();
	}

	@Override
	protected String getOrderBySelectItemsByCreationDateLabel() {
		final Locale locale = I18nUtils.getDefaultLocale();
    	return I18nUtils.getMessageResourceString("need_orderByCreationDateDesc", locale);
	}
	
	public String getNeedOverviewHref() {
		final Need need = (Need)getTable().getRowData();
		return NeedUtils.getNeedOverviewPageUrl(need.getId().toString());		
	}

	public String getGotItHref() {
		final Need need = (Need)getTable().getRowData();
		return JsfUtils.getFullUrl(PagesURL.ITEM_ADD, 
				PagesURL.ITEM_ADD_PARAM_NEED_ID,
				need.getId().toString());
	}

	public boolean isFilteredList() {
		return !StringUtils.isNullOrEmpty(getSearchString()) ||
			getMaxDistance() != null ||
			getCategoryId() != null ||
			getOwnerType() != null ||
			getVisibilityId() != null ||
			getFulfilled() != null;
	}

	public boolean isShowAdvancedSearch() {
		return getCategoryId() != null || 
			getMaxDistance() != null ||
			getOwnerType() != null ||
			getVisibilityId() != null ||
			getOrderBy() != null ||
			getFulfilled() != null;
	}

	public boolean isGotItAvailable() {
		if (!SecurityUtils.isLoggedIn()) {
			return false;
		}
		else {
			final Need need = (Need)getTable().getRowData();
			return !need.getOwner().getId().equals(PersonUtils.getCurrentPersonId());
		}
	}
	
	public List<SelectItem> getFulfilledSelectItems() {
		if (fulfilledSelectItems == null) {
			final List<SelectItem> result = new ArrayList<SelectItem>();
			final Locale locale = I18nUtils.getDefaultLocale();
			
			result.add(new SelectItem(null, I18nUtils.getMessageResourceString("need_fulfilledNo", locale)));
			result.add(new SelectItem(new Long(1), I18nUtils.getMessageResourceString("need_fulfilledYes", locale)));
			result.add(new SelectItem(new Long(2), I18nUtils.getMessageResourceString("need_fulfilledAll", locale)));
			
			fulfilledSelectItems = result;
		}		
		return fulfilledSelectItems;	
	}

	public Long getFulfilled() {
		return fulfilled;
	}
	
	public Boolean getFulfilledBoolean() {
		Boolean fulfilledBoolean = null;
	    if (getFulfilled() == null) {
	    	fulfilledBoolean = Boolean.FALSE;
	    }
	    else if (getFulfilled().longValue() == 1) {
	    	fulfilledBoolean = Boolean.TRUE;
	    }
	    return fulfilledBoolean;
	}

	public void setFulfilled(Long fulfilled) {
		this.fulfilled = UiUtils.getPositiveLongOrNull(fulfilled);
	}
	
	public void fulfilled(final ValueChangeEvent pEevent) {
    	final Long fulfilled = (Long) ((HtmlSelectOneMenu) pEevent.getComponent()).getValue();
        setFulfilled(fulfilled);
        // loadDataList() is not called by getList() when the page is submitted with the onchange
        // event on the h:selectOneMenu. Not sure why!?
        reloadList();
    }
	
	@Override
	public List<SelectItem> getOwnerTypeSelectItems() {
		final List<SelectItem> result = new ArrayList<SelectItem>();
		final Locale locale = I18nUtils.getDefaultLocale();
		
		result.add(new SelectItem(null, I18nUtils.getMessageResourceString("need_ownerTypeAll", locale)));
		result.add(new SelectItem(Long.valueOf(ItemConsts.OWNER_TYPE_CONNECTIONS), I18nUtils.getMessageResourceString("item_ownerTypeConnections", locale)));
		
		return result;
	}
}
