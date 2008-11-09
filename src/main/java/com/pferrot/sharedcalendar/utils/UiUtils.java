package com.pferrot.sharedcalendar.utils;

import javax.faces.model.SelectItem;

public class UiUtils {
	
	public static SelectItem getPleaseSelectSelectItem() {
		final SelectItem si = new SelectItem(null, "ui_please_select");
		return si;
	}

}
