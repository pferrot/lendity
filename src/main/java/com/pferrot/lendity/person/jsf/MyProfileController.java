package com.pferrot.lendity.person.jsf;

import java.util.Locale;

import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.person.PersonUtils;

@ViewController(viewIds={"/auth/person/myProfile.jspx"})
public class MyProfileController extends PersonOverviewController {
	
	@Override
	protected String getPersonIdString() {
		return String.valueOf(PersonUtils.getCurrentPersonId());
	}
	
	@Override
	public boolean isShowLinksToObjekts() {
		return false;
	}

	@Override
	public boolean isShowConnectionsAndGroups() {
		return false;
	}
	
	@Override
	public String getPageTitle() {
		final Locale locale = I18nUtils.getDefaultLocale();
		return I18nUtils.getMessageResourceString("menu_profile", locale);
	}
	
	@Override
	public boolean isEditAvailable() {
		return true;
	}
}
