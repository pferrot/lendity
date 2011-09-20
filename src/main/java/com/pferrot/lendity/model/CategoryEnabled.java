package com.pferrot.lendity.model;

import java.util.Set;

public interface CategoryEnabled {	
	Set<ItemCategory> getCategories();
	void setCategories(Set<ItemCategory> categories);
}
