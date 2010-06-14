package com.pferrot.lendity.i18n;

import java.util.Comparator;

import javax.faces.model.SelectItem;

/**
 * SelectItem comparator for the user interface: a SelectItem is smaller
 * than another if its label (case insensitive) is smaller (i.e. useful 
 * to sort by alphabetical order).
 * 
 * @author Patrice
 *
 */
public class SelectItemComparator implements Comparator<SelectItem> {

	public int compare(final SelectItem o1, final SelectItem o2) {
		if (o1 == o2) {
			return 0;
		}
		else if (o1 == null) {
			return -1;
		}
		// They are BOTH not null.
		final String o1Label = o1.getLabel();
		final String o2Label = o2.getLabel();
		if (o1Label == o2Label) {
			return 0;
		}
		else if (o1Label == null) {
			return -1;
		}
		// Both labels are not null.
		return o1Label.compareToIgnoreCase(o2Label);
	}
}
