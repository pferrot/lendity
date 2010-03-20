package com.pferrot.sharedcalendar.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.pferrot.sharedcalendar.model.ListValue;

@Entity
@DiscriminatorValue("ItemCategory")
public class ItemCategory extends ListValue {
	
	public static final String DVD_LABEL_CODE = "item_category_dvd";
	public static final String CD_LABEL_CODE = "item_category_cd";
	public static final String BOOK_LABEL_CODE = "item_category_book";
	
	public ItemCategory() {
		super();
	}

	public ItemCategory(final String labelCode) {
		super(labelCode);
	}
}
