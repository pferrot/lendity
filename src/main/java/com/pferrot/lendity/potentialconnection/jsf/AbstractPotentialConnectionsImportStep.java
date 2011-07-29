package com.pferrot.lendity.potentialconnection.jsf;

public abstract class AbstractPotentialConnectionsImportStep {
	
	private PotentialConnectionsImportController potentialConnectionsImportController;

	public PotentialConnectionsImportController getPotentialConnectionsImportController() {
		return potentialConnectionsImportController;
	}

	public void setPotentialConnectionsImportController(
			PotentialConnectionsImportController potentialConnectionsImportController) {
		this.potentialConnectionsImportController = potentialConnectionsImportController;
	}
}
