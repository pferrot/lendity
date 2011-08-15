package com.pferrot.lendity.item.jsf;

import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.model.Item;
import com.pferrot.lendity.utils.JsfUtils;

@ViewController(viewIds={"/auth/item/myItemsList.jspx"})
public class MyItemsListController extends AbstractItemsListController {
	
	private final static Log log = LogFactory.getLog(MyItemsListController.class);
	
	public MyItemsListController() {
		super();
	}

	@InitView
	public void initView() {		
		super.initView();
	}

	@Override
	protected ListWithRowCount getListWithRowCount() {
		return getItemService().findMyItems(getSearchString(), getCategoryId(), 
				getVisibilityId(), getBorrowStatusBoolean(), getLendType(), getOrderByField(), getOrderByAscending(), getFirstRow(), getRowsPerPage());
	}
	
	public String getLendButtonLabel() {
		final Item item = (Item)getTable().getRowData();
		final Locale locale = I18nUtils.getDefaultLocale();
		if (!item.isBorrowed()) {
			return I18nUtils.getMessageResourceString("item_lend", locale);
		}
		else {
			return I18nUtils.getMessageResourceString("item_lendAgain", locale);
		}
	}
	
	@Override
	protected String getCategoryLinkBaseUrl() {
		return JsfUtils.getFullUrl(PagesURL.MY_ITEMS_LIST, PagesURL.ITEMS_SEARCH_PARAM_CATEGORY_ID, PagesURL.ITEMS_SEARCH_PARAM_CATEGORY_ID_TO_REPLACE);
	}
}
