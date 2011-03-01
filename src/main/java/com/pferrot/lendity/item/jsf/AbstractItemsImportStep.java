package com.pferrot.lendity.item.jsf;

public abstract class AbstractItemsImportStep {
	
	private ItemsImportController itemsImportController;

	public ItemsImportController getItemsImportController() {
		return itemsImportController;
	}

	public void setItemsImportController(
			ItemsImportController itemsImportController) {
		this.itemsImportController = itemsImportController;
	}
}
