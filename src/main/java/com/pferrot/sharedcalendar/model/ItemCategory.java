package com.pferrot.sharedcalendar.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.pferrot.sharedcalendar.model.ListValue;

@Entity
@DiscriminatorValue("ItemCategory")
public class ItemCategory extends ListValue {
	
	public static final String DVD_LABEL_CODE = "item_categoryDvd";
	public static final String BLURAY_LABEL_CODE = "item_categoryBluRay";
	public static final String HDDVD_LABEL_CODE = "item_categoryHdDvd";
	public static final String CD_LABEL_CODE = "item_categoryCd";
	public static final String BOOK_LABEL_CODE = "item_categoryBook";
	public static final String COMICS_LABEL_CODE = "item_categoryComics";
	public static final String VIDEOGAME_OTHER_LABEL_CODE = "item_categoryVideoGameOther";
	public static final String VIDEOGAME_PSP_LABEL_CODE = "item_categoryVideoGamePSP";
	public static final String VIDEOGAME_PS1_LABEL_CODE = "item_categoryVideoGamePS1";
	public static final String VIDEOGAME_PS2_LABEL_CODE = "item_categoryVideoGamePS2";
	public static final String VIDEOGAME_PS3_LABEL_CODE = "item_categoryVideoGamePS3";
	public static final String VIDEOGAME_PC_LABEL_CODE = "item_categoryVideoGamePC";
	public static final String VIDEOGAME_XBOX_LABEL_CODE = "item_categoryVideoGameXbox";
	public static final String VIDEOGAME_XBOX360_LABEL_CODE = "item_categoryVideoGameXbox360";
	public static final String VIDEOGAME_WII_LABEL_CODE = "item_categoryVideoGameWii";
	public static final String TOOL_LABEL_CODE = "item_categoryTool";
	public static final String OTHER_LABEL_CODE = "item_categoryOther";
	
	
	public static final String[] LABEL_CODES = new String[]{
		DVD_LABEL_CODE,
		BLURAY_LABEL_CODE,
		HDDVD_LABEL_CODE,
		CD_LABEL_CODE,
		BOOK_LABEL_CODE,
		COMICS_LABEL_CODE,
		VIDEOGAME_OTHER_LABEL_CODE,
		VIDEOGAME_PSP_LABEL_CODE,
		VIDEOGAME_PS1_LABEL_CODE,
		VIDEOGAME_PS2_LABEL_CODE,
		VIDEOGAME_PS3_LABEL_CODE,
		VIDEOGAME_PC_LABEL_CODE,
		VIDEOGAME_XBOX_LABEL_CODE,
		VIDEOGAME_XBOX360_LABEL_CODE,
		VIDEOGAME_WII_LABEL_CODE,
		TOOL_LABEL_CODE,
		OTHER_LABEL_CODE		
	};
	
	
	public ItemCategory() {
		super();
	}

	public ItemCategory(final String labelCode) {
		super(labelCode);
	}
}
