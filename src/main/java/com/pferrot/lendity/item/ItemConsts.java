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
}
