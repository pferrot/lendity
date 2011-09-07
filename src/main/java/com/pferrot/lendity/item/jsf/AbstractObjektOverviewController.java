package com.pferrot.lendity.item.jsf;

import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.geolocation.GeoLocationUtils;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.item.ObjektService;
import com.pferrot.lendity.model.Objekt;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.HtmlUtils;
import com.pferrot.lendity.utils.UiUtils;
import com.pferrot.security.SecurityUtils;

public abstract class AbstractObjektOverviewController {

	private final static Log log = LogFactory.getLog(AbstractObjektOverviewController.class);

	protected abstract Objekt getObjekt();
	protected abstract ObjektService getObjektService();
	
	
	public String getCategoryLabel() {
		if (getObjekt() != null && getObjekt().getCategory() != null) {
			final Locale locale = I18nUtils.getDefaultLocale();
			return I18nUtils.getMessageResourceString(getObjekt().getCategory().getLabelCode(), locale);
		}
		else {
			return "";
		}
	}	

	public String getDescription() {
		final String itemDescription = getObjekt().getDescription();
		if (itemDescription != null) {
			return HtmlUtils.getTextWithHrefLinks(HtmlUtils.escapeHtmlAndReplaceCr(itemDescription));
		}
		return "";
	}

	public boolean isDescriptionAvailable() {
		final String itemDescription = getObjekt().getDescription();
		return !StringUtils.isNullOrEmpty(itemDescription);
	}	
	
	public boolean isOwner() {
		return getObjektService().isCurrentUserOwner(getObjekt());
	}
	
	public boolean isGroupsAuthorized() {
		return !getObjekt().getGroupsAuthorized().isEmpty();
	}
	
	public String getCreationDateLabel() {
		return UiUtils.getDateAsString(getObjekt().getCreationDate(), I18nUtils.getDefaultLocale());
	}

	public String getVisibilityLabel() {
		if (getObjekt() != null && getObjekt().getVisibility() != null) {
			final Locale locale = I18nUtils.getDefaultLocale();
			return I18nUtils.getMessageResourceString(getObjekt().getVisibility().getLabelCode(), locale);
		}
		else {
			return "";
		}
	}
	
	public String getOwnerHref() {
		return PersonUtils.getPersonOverviewPageUrl(getObjekt().getOwner().getId().toString());
	}

	public boolean isEditAvailable() {
		return getObjektService().isCurrentUserAuthorizedToEdit(getObjekt());
	}

	public boolean isDeleteAvailable() {
		return getObjektService().isCurrentUserAuthorizedToDelete(getObjekt());
	}
	
	public String getDistanceLabel() {
		return GeoLocationUtils.getApproxDistanceKm(PersonUtils.getCurrentPersonAddressHomeLatitude(),
													PersonUtils.getCurrentPersonAddressHomeLongitude(),
													getObjekt().getOwner().getAddressHomeLatitude(),
													getObjekt().getOwner().getAddressHomeLongitude());
	}
	
	public boolean isFacebookLikeButtonAvailable() {
		return getObjekt().isPublicVisibility();
	}
	
	public boolean isRecommendAvailable() {
		return SecurityUtils.isLoggedIn();
	}
}
