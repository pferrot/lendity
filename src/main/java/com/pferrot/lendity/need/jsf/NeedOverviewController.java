package com.pferrot.lendity.need.jsf;

import java.util.Locale;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.model.Need;
import com.pferrot.lendity.need.NeedService;
import com.pferrot.lendity.need.NeedUtils;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.HtmlUtils;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.lendity.utils.UiUtils;

@ViewController(viewIds={"/auth/need/needOverview.jspx"})
public class NeedOverviewController {
	
	private final static Log log = LogFactory.getLog(NeedOverviewController.class);
	
	private Need need;
	private NeedService needService;
	private Long needId;

	public Need getNeed() {
		return need;
	}

	public void setNeed(Need need) {
		this.need = need;
	}

	public NeedService getNeedService() {
		return needService;
	}

	public void setNeedService(NeedService needService) {
		this.needService = needService;
	}

	public Long getNeedId() {
		return needId;
	}

	public void setNeedId(Long needId) {
		this.needId = needId;
	}

	@InitView
	public void initView() {		
		final String idString = JsfUtils.getRequestParameter(PagesURL.NEED_OVERVIEW_PARAM_NEED_ID);
		Need need = null;
		if (idString != null) {
			setNeedId(Long.parseLong(idString));
			need = getNeedService().findNeed(getNeedId());
			// Access control check.
			if (!getNeedService().isCurrentUserAuthorizedToView(need)) {
				JsfUtils.redirect(PagesURL.ERROR_ACCESS_DENIED);
				if (log.isWarnEnabled()) {
					log.warn("Access denied (need view): user = " + PersonUtils.getCurrentPersonDisplayName() + " (" + PersonUtils.getCurrentPersonId() + "), need = " + getNeedId());
				}
				return;
			}
			setNeed(need);
		}	
	}
	
	public String getOwnerHref() {
		return PersonUtils.getPersonOverviewPageUrl(getNeed().getOwner().getId().toString());
	}
	
	public String getNeedEditHref() {		
		return NeedUtils.getNeedEditPageUrl(getNeed().getId().toString());
	}
	
	public boolean isDescriptionAvailable() {
		final String needDescription = need.getDescription();
		return !StringUtils.isNullOrEmpty(needDescription);
	}

	public boolean isEditAvailable() {
		return needService.isCurrentUserAuthorizedToEdit(need);
	}

	public boolean isDeleteAvailable() {
		return needService.isCurrentUserAuthorizedToDelete(need);
	}

	public boolean isGotItAvailable() {
		return !getNeed().getOwner().getId().equals(PersonUtils.getCurrentPersonId());
	}
	
	public String getGotItHref() {
		return JsfUtils.getFullUrl(PagesURL.INTERNAL_ITEM_ADD, 
				PagesURL.INTERNAL_ITEM_ADD_PARAM_NEED_ID,
				getNeed().getId().toString());
	}
		
	public String getDescription() {
		final String needDescription = need.getDescription();
		if (needDescription != null) {
			return HtmlUtils.escapeHtmlAndReplaceCr(needDescription);
		}
		return "";
	}

	public String getCategoryLabel() {
		if (need != null && need.getCategory() != null) {
			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			return I18nUtils.getMessageResourceString(need.getCategory().getLabelCode(), locale);
		}
		else {
			return "";
		}
	}

	public String getCreationDateLabel() {
		return UiUtils.getDateAsString(need.getCreationDate(), FacesContext.getCurrentInstance().getViewRoot().getLocale());
	}
}
