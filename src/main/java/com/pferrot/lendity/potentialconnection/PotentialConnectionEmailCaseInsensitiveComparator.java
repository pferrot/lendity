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
		else if (pPc1.getEmail() != null && pPc2.getEmail() != null) {
			return pPc1.getEmail().toLowerCase().compareTo(pPc2.getEmail().toLowerCase());
		}
		else if (pPc1.getEmail() != null) {
			return -1;
		}
		else if (pPc2.getEmail() != null) {
			return 1;
		}
		else if (pPc1.getConnectionId() != null && pPc2.getConnectionId() != null) {
			return pPc1.getConnectionId().compareTo(pPc2.getConnectionId());
		}
		else if (pPc1.getConnectionId() != null) {
			return -1;
		}
		else if (pPc2.getConnectionId() != null) {
			return 1;
		}
		else if (pPc1.getName() != null && pPc2.getName() != null) {
			return pPc1.getName().toLowerCase().compareTo(pPc2.getName().toLowerCase());
		}
		else if (pPc1.getName() != null) {
			return -1;
		}
		else if (pPc2.getName() != null) {
			return 1;
		}
		// Both have no email and no name.
		return 0;		
	}

}
