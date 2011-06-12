package com.pferrot.lendity.need.jsf;

import java.util.Locale;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.item.ObjektService;
import com.pferrot.lendity.item.jsf.AbstractObjektsListController;
import com.pferrot.lendity.model.Need;
import com.pferrot.lendity.need.NeedService;
import com.pferrot.lendity.need.NeedUtils;
import com.pferrot.lendity.utils.JsfUtils;

public abstract class AbstractNeedsListController extends AbstractObjektsListController {
	
	private final static Log log = LogFactory.getLog(AbstractNeedsListController.class);
	
	private NeedService needService;	
	
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
		final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
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
			getVisibilityId() != null;
	}

	public boolean isShowAdvancedSearch() {
		return getCategoryId() != null || 
			getMaxDistance() != null ||
			getOwnerType() != null ||
			getVisibilityId() != null ||
			getOrderBy() != null;
	}

	public String getImage1Src() {
		final Need need = (Need)getTable().getRowData();
		return getNeedService().getNeedPicture1Src(need, true);
	}
	
	public String getThumbnail1Src() {
		final Need need = (Need)getTable().getRowData();
		return getNeedService().getNeedThumbnail1Src(need, true);
	}
}
