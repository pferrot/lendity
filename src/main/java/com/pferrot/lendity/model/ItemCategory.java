package com.pferrot.lendity.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.envers.Audited;

@Entity
@DiscriminatorValue("ItemCategory")
@Audited
public class ItemCategory extends ListValue {
	
//	public static final String DVD_LABEL_CODE = "item_categoryDvd";
//	public static final String BLURAY_LABEL_CODE = "item_categoryBluRay";
//	public static final String CD_LABEL_CODE = "item_categoryCd";
//	public static final String BOOK_LABEL_CODE = "item_categoryBook";
//	public static final String VIDEOGAME_OTHER_LABEL_CODE = "item_categoryVideoGameOther";
//	public static final String VIDEOGAME_PS3_LABEL_CODE = "item_categoryVideoGamePS3";
//	public static final String VIDEOGAME_PC_LABEL_CODE = "item_categoryVideoGamePC";
//	public static final String VIDEOGAME_XBOX360_LABEL_CODE = "item_categoryVideoGameXbox360";
//	public static final String VIDEOGAME_WII_LABEL_CODE = "item_categoryVideoGameWii";
	
//	public static final String TOOL_DO_IT_YOURSELF_LABEL_CODE = "item_categoryToolDoItYourself";
//	public static final String TOOL_GARDEN_LABEL_CODE = "item_categoryToolGarden";
//	public static final String ELECTRONIC_LABEL_CODE = "item_categoryElectronic";
//	public static final String SPORT_LABEL_CODE = "item_categorySport";
//	public static final String HOUSEHOLD_ELECTRICAL_LABEL_CODE = "item_categoryHouseholdElectrical";
//	public static final String BOARD_GAME_LABEL_CODE = "item_categoryBoardGame";
//	public static final String MUSIC_INSTRUMENT_LABEL_CODE = "item_categoryMusicInstrument";
//	public static final String BABY_LABEL_CODE = "item_categoryBaby";
//	public static final String COMICS_LABEL_CODE = "item_categoryComics";
	
	public static final String ANIMAL_LABEL_CODE = "item_categoryAnimal";
	public static final String ELECTRONIC_LABEL_CODE = "item_categoryElectronic";
	public static final String BABY_LABEL_CODE = "item_categoryBaby";
	public static final String TOOL_GARDEN_LABEL_CODE = "item_categoryToolGarden";
	public static final String CAMPING_LABEL_CODE = "item_categoryCamping";
	public static final String COSTUME_LABEL_CODE = "item_categoryCostume";
	public static final String COOKING_LABEL_CODE = "item_categoryCooking";
	public static final String MOVIE_LABEL_CODE = "item_categoryMovie";
	public static final String BOARD_GAME_LABEL_CODE = "item_categoryBoardGame";
	public static final String VIDEO_GAME_LABEL_CODE = "item_categoryVideoGame";
	public static final String BOOK_LABEL_CODE = "item_categoryBook";
	public static final String HOUSE_LABEL_CODE = "item_categoryHouse";
	public static final String FURNITURE_LABEL_CODE = "item_categoryFurniture";
	public static final String CD_LABEL_CODE = "item_categoryCd";
	public static final String MUSIC_INSTRUMENT_LABEL_CODE = "item_categoryMusicInstrument";
	public static final String COMPUTER_LABEL_CODE = "item_categoryComputer";
	public static final String TOOL_LABEL_CODE = "item_categoryTool";
	public static final String PHOTOGRAPHY_LABEL_CODE = "item_categoryPhotography";
	public static final String SERVICE_LABEL_CODE = "item_categoryService";
	public static final String COSMETIC_LABEL_CODE = "item_categoryCosmetic";
	public static final String SPORT_LABEL_CODE = "item_categorySport";
	public static final String HOLIDAYS_LABEL_CODE = "item_categoryHolidays";
	public static final String VEHICLE_LABEL_CODE = "item_categoryVehicle";
	public static final String CLOTHING_LABEL_CODE = "item_categoryClothing";
	public static final String OTHER_LABEL_CODE = "item_categoryOther";	
	

	public static final String[] LABEL_CODES = new String[]{
		ANIMAL_LABEL_CODE,
		ELECTRONIC_LABEL_CODE,
		BABY_LABEL_CODE,
		TOOL_GARDEN_LABEL_CODE,
		CAMPING_LABEL_CODE,
		COSTUME_LABEL_CODE,
		COOKING_LABEL_CODE,
		MOVIE_LABEL_CODE,
		BOARD_GAME_LABEL_CODE,
		VIDEO_GAME_LABEL_CODE,
		BOOK_LABEL_CODE,
		HOUSE_LABEL_CODE,
		FURNITURE_LABEL_CODE,
		CD_LABEL_CODE,
		MUSIC_INSTRUMENT_LABEL_CODE,
		COMPUTER_LABEL_CODE,
		TOOL_LABEL_CODE,
		PHOTOGRAPHY_LABEL_CODE,
		SERVICE_LABEL_CODE,
		COSMETIC_LABEL_CODE,
		SPORT_LABEL_CODE,
		HOLIDAYS_LABEL_CODE,
		VEHICLE_LABEL_CODE,
		CLOTHING_LABEL_CODE,
		OTHER_LABEL_CODE	
	};
	
	
	public ItemCategory() {
		super();
	}

	public ItemCategory(final String labelCode) {
		super(labelCode);
	}
}
