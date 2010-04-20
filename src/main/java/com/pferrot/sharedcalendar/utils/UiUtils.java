package com.pferrot.sharedcalendar.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
	
	private final static Map<Locale, DateFormat> DATE_FORMATS = new HashMap<Locale, DateFormat>();
	
	public static SelectItem getPleaseSelectSelectItem(final Locale locale) {
		final String label = I18nUtils.getMessageResourceString("general_pleaseSelect", locale);
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

	// Since we cannot use Boolean for fields like AbstractItemsListController.borrowStatus because
	// selecting the SelectItem with value null actually sets the value False, we need to use Long.
	public static Boolean getBooleanFromLong(final Long pLong) {
		// Sometimes 0 is set by JSF instead of null...
		if (pLong == null || pLong.longValue() == 0) {
			return null;
		}
		else if (pLong.longValue() == 1) {
			return Boolean.TRUE;
		}
		else if (pLong.longValue() == -1) {
			return Boolean.FALSE;
		}
		else {
			throw new RuntimeException("Unsupported value: " + pLong.intValue());
		}
	}

	// Since we cannot use Boolean for fields like AbstractItemsListController.borrowStatus because
	// selecting the SelectItem with value null actually sets the value False, we need to use Long.
	public static Long getLongFromBoolean(final Boolean pBoolean) {
		if (pBoolean == null) {
			return null;
		}
		else if (pBoolean.booleanValue()) {
			return Long.valueOf(1);
		}
		else {
			return Long.valueOf(-1);
		}		
	}

	public static String getDateAsString(final Date pDate, final Locale pLocale) {
		if (pDate == null) {
			return "";
		}
		return getDateFormat(pLocale).format(pDate);		
	}

	private static DateFormat getDateFormat(final Locale pLocale) {
		if (pLocale == null) {
			if (log.isDebugEnabled()) {
				log.debug("No locale specified, returning default date format");
			}
			return DateFormat.getDateInstance(DateFormat.MEDIUM);
		}
		DateFormat result = DATE_FORMATS.get(pLocale);
		if (result == null) {
			if (log.isDebugEnabled()) {
				log.debug("Date format not available yet. Creating and storing in map.");
			}
			result = DateFormat.getDateInstance(DateFormat.MEDIUM, pLocale);
			DATE_FORMATS.put(pLocale, result);
		}
		return result;
	}
		
}
