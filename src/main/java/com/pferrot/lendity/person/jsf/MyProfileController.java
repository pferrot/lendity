package com.pferrot.lendity.person.jsf;

import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.JsfUtils;

@ViewController(viewIds={"/auth/person/myProfile.jspx"})
public class MyProfileController {
	
	@InitView
	public void initView() {
		JsfUtils.redirect(PagesURL.PERSON_OVERVIEW, PagesURL.PERSON_OVERVIEW_PARAM_PERSON_ID, String.valueOf(PersonUtils.getCurrentPersonId()));
	}

}
