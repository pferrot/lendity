package com.pferrot.lendity.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.pferrot.lendity.model.ListValue;

@Entity
@DiscriminatorValue("ItemCategory")
public class ItemCategory extends ListValue {
	
	public static final String DVD_LABEL_CODE = "item_categoryDvd";
	public static final String BLURAY_LABEL_CODE = "item_categoryBluRay";
	public static final String HDDVD_LABEL_CODE = "item_categoryHdDvd";
	public static final String CD_LABEL_CODE = "item_categoryCd";
	public static final String BOOK_LABEL_CODE = "item_categoryBook";
	public static final String VIDEOGAME_OTHER_LABEL_CODE = "item_categoryVideoGameOther";
	public static final String VIDEOGAME_PS3_LABEL_CODE = "item_categoryVideoGamePS3";
	public static final String VIDEOGAME_PC_LABEL_CODE = "item_categoryVideoGamePC";
	public static final String VIDEOGAME_XBOX360_LABEL_CODE = "item_categoryVideoGameXbox360";
	public static final String VIDEOGAME_WII_LABEL_CODE = "item_categoryVideoGameWii";
	public static final String TOOL_LABEL_CODE = "item_categoryTool";
	public static final String OTHER_LABEL_CODE = "item_categoryOther";
	
	public static final String TOOL_DO_IT_YOURSELF_LABEL_CODE = "item_categoryToolDoItYourself";
	public static final String TOOL_GARDEN_LABEL_CODE = "item_categoryToolGarden";
	public static final String VEHICLE_LABEL_CODE = "item_categoryVehicle";
	public static final String FURNITURE_LABEL_CODE = "item_categoryFurniture";
	public static final String ELECTRONIC_LABEL_CODE = "item_categoryElectronic";
	public static final String SPORT_LABEL_CODE = "item_categorySport";
	public static final String HOUSEHOLD_ELECTRICAL_LABEL_CODE = "item_categoryHouseholdElectrical";
	public static final String BOARD_GAME_LABEL_CODE = "item_categoryBoardGame";
	public static final String MUSIC_INSTRUMENT_LABEL_CODE = "item_categoryMusicInstrument";
	public static final String CLOTHS_LABEL_CODE = "item_categoryCloth";
	public static final String BABY_LABEL_CODE = "item_categoryBaby";

	public static final String[] LABEL_CODES = new String[]{
		DVD_LABEL_CODE,
		BLURAY_LABEL_CODE,
		HDDVD_LABEL_CODE,
		CD_LABEL_CODE,
		BOOK_LABEL_CODE,
		VIDEOGAME_OTHER_LABEL_CODE,
		VIDEOGAME_PS3_LABEL_CODE,
		VIDEOGAME_PC_LABEL_CODE,
		VIDEOGAME_XBOX360_LABEL_CODE,
		VIDEOGAME_WII_LABEL_CODE,
		TOOL_LABEL_CODE,
		OTHER_LABEL_CODE,
		TOOL_DO_IT_YOURSELF_LABEL_CODE,
		TOOL_GARDEN_LABEL_CODE,
		VEHICLE_LABEL_CODE,
		FURNITURE_LABEL_CODE,
		ELECTRONIC_LABEL_CODE,
		SPORT_LABEL_CODE,
		HOUSEHOLD_ELECTRICAL_LABEL_CODE,
		BOARD_GAME_LABEL_CODE,
		MUSIC_INSTRUMENT_LABEL_CODE,
		CLOTHS_LABEL_CODE,
		BABY_LABEL_CODE
	};
	
	
	public ItemCategory() {
		super();
	}

	public ItemCategory(final String labelCode) {
		super(labelCode);
	}
}
