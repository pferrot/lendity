package com.pferrot.lendity.item;

public interface ItemConsts {

	int NB_ITEMS_PER_PAGE = 20;
	int NB_CHARACTERS_DESCRIPTION_IN_LISTS = 100;
	int MAX_DESCRIPTION_SIZE = 1333;
	
	Long LEND_TYPE_LEND = Long.valueOf(2);
	Long LEND_TYPE_RENT = Long.valueOf(3);
	Long LEND_TYPE_SELL = Long.valueOf(4);
	Long LEND_TYPE_GIVE_FOR_FREE = Long.valueOf(5);
	
	Long OWNER_TYPE_CONNECTIONS = Long.valueOf(1);
	
	String CATEGORY_BABY_THUMBNAIL_URL = "/public/images/icons/categories/baby.gif";
	String CATEGORY_BLURAY_THUMBNAIL_URL = "/public/images/icons/categories/bluray.gif";
	String CATEGORY_BOARDGAME_THUMBNAIL_URL = "/public/images/icons/categories/boardgame.gif";
	String CATEGORY_BOOK_THUMBNAIL_URL = "/public/images/icons/categories/book.gif";
	String CATEGORY_CD_THUMBNAIL_URL = "/public/images/icons/categories/cd.gif";
	String CATEGORY_COMICS_THUMBNAIL_URL = "/public/images/icons/categories/comics.gif";
	String CATEGORY_DVD_THUMBNAIL_URL = "/public/images/icons/categories/dvd.gif";
	String CATEGORY_ELECTRONIC_THUMBNAIL_URL = "/public/images/icons/categories/electronic.gif";
	String CATEGORY_HOUSEHOLDELECTRICAL_THUMBNAIL_URL = "/public/images/icons/categories/householdelectrical.gif";
	String CATEGORY_MUSICINSTRUMENT_THUMBNAIL_URL = "/public/images/icons/categories/musicinstrument.gif";
	String CATEGORY_OTHER_THUMBNAIL_URL = "/public/images/icons/categories/other.gif";
	String CATEGORY_SPORT_THUMBNAIL_URL = "/public/images/icons/categories/sport.gif";
	String CATEGORY_TOOLDOITYOURSELF_THUMBNAIL_URL = "/public/images/icons/categories/tooldoityourself.gif";
	String CATEGORY_TOOLGARDEN_THUMBNAIL_URL = "/public/images/icons/categories/toolgarden.gif";
	String CATEGORY_VIDEOGAMEOTHER_THUMBNAIL_URL = "/public/images/icons/categories/videogameother.gif";
	String CATEGORY_VIDEOGAMEPC_THUMBNAIL_URL = "/public/images/icons/categories/videogamepc.gif";
	String CATEGORY_VIDEOGAMEPS3_THUMBNAIL_URL = "/public/images/icons/categories/videogameps3.gif";
	String CATEGORY_VIDEOGAMEWII_THUMBNAIL_URL = "/public/images/icons/categories/videogamewii.gif";
	String CATEGORY_VIDEOGAMEXBOX360_THUMBNAIL_URL = "/public/images/icons/categories/videogamexbox360.gif";

	String FACEBOOK_LIKE_DEFAULT_IMAGE_URL = "/public/images/login/objet_dispo.png";
}
