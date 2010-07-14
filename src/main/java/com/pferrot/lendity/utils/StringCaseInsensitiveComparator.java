package com.pferrot.lendity.utils;

import java.util.Comparator;

public class StringCaseInsensitiveComparator implements Comparator<String> {

	public int compare(String pText1, String pText2) {
		if (pText1 == pText2) {
			return 0;
		}
		else if (pText1 == null) {
			return -1;
		}
		else if (pText2 == null) {
			return 1;
		}
		return pText1.toLowerCase().compareTo(pText2.toLowerCase());
	}

}
