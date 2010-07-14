package com.pferrot.lendity.item.jsf;

public abstract class AbstractInternalItemsImportStep {
	
	private InternalItemsImportController internalItemsImportController;

	public InternalItemsImportController getInternalItemsImportController() {
		return internalItemsImportController;
	}

	public void setInternalItemsImportController(
			InternalItemsImportController internalItemsImportController) {
		this.internalItemsImportController = internalItemsImportController;
	}
}
