package com.pferrot.lendity.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Called Objekt to not make confusion with the base java class Object.
 * 
 * @author pferrot
 *
 */
public interface Objekt extends Ownable, CategoryEnabled, VisibilityEnabled, Serializable {
	
	String getTitle();
	void setTitle(String title);
	
	String getDescription();
	void setDescription(String description);
	
	Date getCreationDate();
	void setCreationDate(Date creationDate);	
}
