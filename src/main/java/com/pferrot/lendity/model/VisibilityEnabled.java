package com.pferrot.lendity.model;

public interface VisibilityEnabled {
	
	ItemVisibility getVisibility();
	void setVisibility(ItemVisibility visibility);

	boolean isPublicVisibility();
	boolean isPrivateVisibility();
	boolean isConnectionsVisibility();
}
