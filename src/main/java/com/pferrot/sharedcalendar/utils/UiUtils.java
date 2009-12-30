package com.pferrot.sharedcalendar.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.sharedcalendar.i18n.I18nUtils;
import com.pferrot.sharedcalendar.i18n.SelectItemComparator;
import com.pferrot.sharedcalendar.model.ListValue;
import com.pferrot.sharedcalendar.model.OrderedListValue;

public class UiUtils {
	
	private final static Log log = LogFactory.getLog(UiUtils.class);
	
	public static SelectItem getPleaseSelectSelectItem(final Locale locale) {
		final String label = I18nUtils.getMessageResourceString("ui_please_select", locale);
		final SelectItem si = new SelectItem(null, label);
		return si;
	}
	
	public static List<SelectItem> getSelectItemsForOrderedListValue(final List<OrderedListValue> list,
																     final Locale locale) {
		final List<SelectItem> result = new ArrayList<SelectItem>();
		for (OrderedListValue olv: list) {
			final SelectItem selectItem = new SelectItem(olv.getId(), I18nUtils.getMessageResourceString(olv.getLabelCode(), locale));
			result.add(selectItem);
		}
		return result;		
	}
	
	public static List<SelectItem> getSelectItemsForListValue(final List<ListValue> list,
															  final Locale locale) {
		final TreeSet<SelectItem> treeSet = new TreeSet<SelectItem>(new SelectItemComparator());
		for (ListValue lv: list) {
			final SelectItem selectItem = new SelectItem(lv.getId(), I18nUtils.getMessageResourceString(lv.getLabelCode(), locale));
			treeSet.add(selectItem);
		}
		final List result = new ArrayList<SelectItem>();
		result.addAll(treeSet);
		return result; 
	}
}
