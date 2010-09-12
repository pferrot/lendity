package com.pferrot.lendity.need.jsf;

import java.util.Locale;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.item.jsf.AbstractObjectsListController;
import com.pferrot.lendity.model.Need;
import com.pferrot.lendity.need.NeedService;
import com.pferrot.lendity.need.NeedUtils;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.UiUtils;

public abstract class AbstractNeedsListController extends AbstractObjectsListController {
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
	
	

//	public boolean isEditAvailable() {
//		final Need need = (Need)getTable().getRowData();
//		return getNeedService().isCurrentUserAuthorizedToEdit(need);
//	}
//
//	public boolean isAddAvailable() {
//		return getNeedService().isCurrentUserAuthorizedToAdd();
//	}

//	public boolean isDeleteAvailable() {
//		final Need need = (Need)getTable().getRowData();
//		return getNeedService().isCurrentUserAuthorizedToDelete(need);
//	}

	@Override
	protected String getOrderBySelectItemsByCreationDateLabel() {
		final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
    	return I18nUtils.getMessageResourceString("need_orderByCreationDateDesc", locale);
	}

	@Override
	public boolean isOwner() {
		final Need need = (Need)getTable().getRowData();
		return getNeedService().isCurrentUserOwner(need);
	}
	
	@Override
	public String getOwnerLabel() {
		final Need need = (Need)getTable().getRowData();
		return need.getOwner().getDisplayName();
	}
	
	@Override
	public String getCreationDateLabel() {
		final Need need = (Need)getTable().getRowData();
		return UiUtils.getDateAsString(need.getCreationDate(), FacesContext.getCurrentInstance().getViewRoot().getLocale());
	}

	@Override
	public String getOwnerHref() {
		final Need need = (Need)getTable().getRowData();
		return PersonUtils.getPersonOverviewPageUrl(need.getOwner().getId().toString());
	}
	
	public String getNeedOverviewHref() {
		final Need need = (Need)getTable().getRowData();
		return NeedUtils.getNeedOverviewPageUrl(need.getId().toString());		
	}
}
