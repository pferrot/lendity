package com.pferrot.lendity.potentialconnection;

import java.util.Comparator;

import com.pferrot.lendity.model.PotentialConnection;

public class PotentialConnectionEmailCaseInsensitiveComparator implements Comparator<PotentialConnection> {

	public int compare(PotentialConnection pPc1, PotentialConnection pPc2) {
		if (pPc1 == pPc2) {
			return 0;
		}
		else if (pPc1 == null) {
			return -1;
		}
		else if (pPc2 == null) {
			return 1;
		}
		return pPc1.getEmail().toLowerCase().compareTo(pPc2.getEmail().toLowerCase());
	}

}
