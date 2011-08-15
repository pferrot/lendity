package com.pferrot.lendity.person.jsf;

import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

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
}
