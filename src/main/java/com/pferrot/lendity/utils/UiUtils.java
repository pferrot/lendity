package com.pferrot.lendity.utils;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;

import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.i18n.SelectItemComparator;
import com.pferrot.lendity.model.ListValue;
import com.pferrot.lendity.model.OrderedListValue;
import com.pferrot.lendity.model.Person;

public class UiUtils {
	
	private final static Log log = LogFactory.getLog(UiUtils.class);
	
	private final static Map<Locale, DateFormat> DATE_FORMATS = new HashMap<Locale, DateFormat>();
	private final static Map<Locale, DateFormat> DATE_TIME_FORMATS = new HashMap<Locale, DateFormat>();
	
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
	
	public static List<SelectItem> getSelectItemsForListValue(final List<ListValue> pList,
															  final Locale pLocale) {
		if (pList == null) {
			return Collections.EMPTY_LIST;
		}
		final TreeSet<SelectItem> treeSet = new TreeSet<SelectItem>(new SelectItemComparator());
		for (ListValue lv: pList) {
			final SelectItem selectItem = new SelectItem(lv.getId(), I18nUtils.getMessageResourceString(lv.getLabelCode(), pLocale));
			treeSet.add(selectItem);
		}
		final List result = new ArrayList<SelectItem>();
		result.addAll(treeSet);
		return result; 
	}

	/**
	 * Will only consider ENABLED persons.
	 *
	 * @param pList
	 * @return
	 */
	public static List<SelectItem> getSelectItemsForPerson(final Collection<Person> pList) {
		if (pList == null) {
			return Collections.EMPTY_LIST;
		}
		final TreeSet<SelectItem> treeSet = new TreeSet<SelectItem>(new SelectItemComparator());
		for (Person person: pList) {
			if (person.isEnabled()) {
				final SelectItem selectItem = new SelectItem(person.getId(), person.getDisplayName());
				treeSet.add(selectItem);
			}
		}
		final List result = new ArrayList<SelectItem>();
		result.addAll(treeSet);
		return result; 
	}

	// Since we cannot use Boolean for fields like AbstractItemsListController.borrowStatus because
	// selecting the SelectItem with value null actually sets the value False, we need to use Long.
	// 1 is TRUE
	// 2 is FALSE
	public static Boolean getBooleanFromLong(final Long pLong) {
		if (pLong == null) {
			return null;
		}
		else if (pLong.longValue() == 1) {
			return Boolean.TRUE;
		}
		else if (pLong.longValue() == 2) {
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
			return Long.valueOf(2);
		}		
	}

	public static Long getPositiveLongOrNull(final Long pInput) {
		if (pInput == null || pInput.longValue() <= 0) {
			return null;
		}
		return pInput;
	}
	
	public static Integer getPositiveIntegerOrNull(final Integer pInput) {
		if (pInput == null || pInput.longValue() <= 0) {
			return null;
		}
		return pInput;
	}
	
	public static String getDateTimeAsString(final Date pDate, final Locale pLocale) {
		if (pDate == null) {
			return "";
		}
		return getDateTimeFormat(pLocale).format(pDate);		
	}

	public static String getDateAsString(final Date pDate, final Locale pLocale) {
		if (pDate == null) {
			return "";
		}
		return getDateFormat(pLocale).format(pDate);		
	}
	
	public static String getListValueLabel(final ListValue pListValue, final Locale pLocale) {
		if (pListValue == null) {
			return "";
		}
		return I18nUtils.getMessageResourceString(pListValue.getLabelCode(), I18nUtils.getDefaultLocale());
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
	
	private static DateFormat getDateTimeFormat(final Locale pLocale) {
		if (pLocale == null) {
			if (log.isDebugEnabled()) {
				log.debug("No locale specified, returning default date time format");
			}
			return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
		}
		DateFormat result = DATE_TIME_FORMATS.get(pLocale);
		if (result == null) {
			if (log.isDebugEnabled()) {
				log.debug("Date time format not available yet. Creating and storing in map.");
			}
			result = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, pLocale);
			DATE_TIME_FORMATS.put(pLocale, result);
		}
		return result;
	}	

	public static String getFileTooLargeErrorMessageFromResource(final Locale pLocale) {
		String message = "";
		message = I18nUtils.getMessageResourceString("validation_fileTooLarge", pLocale);
		return message;
	}
		
}
