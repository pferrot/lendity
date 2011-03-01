package com.pferrot.lendity.item.jsf;

import java.util.Locale;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.model.Item;
import com.pferrot.lendity.utils.JsfUtils;

@ViewController(viewIds={"/auth/item/myItemsList.jspx"})
public class MyItemsListController extends AbstractItemsListController {
	
	private final static Log log = LogFactory.getLog(MyItemsListController.class);
	
	public final static String FORCE_VIEW_PARAM_NAME = "view";
	public final static String FORCE_VIEW_ALL_LENT_ITEMS_BY_NAME = "lentByN";
	
	public MyItemsListController() {
		super();
	}


	@InitView
	public void initView() {		
		final String orderBy = JsfUtils.getRequestParameter(FORCE_VIEW_PARAM_NAME);
		if (FORCE_VIEW_ALL_LENT_ITEMS_BY_NAME.equals(orderBy)) {
			resetFilters();			
		}
	}

	@Override
	protected ListWithRowCount getListWithRowCount() {
		return getItemService().findMyItems(getSearchString(), getCategoryId(), 
				getVisibilityId(), getBorrowStatusBoolean(), getLendType(), getOrderByField(), getOrderByAscending(), getFirstRow(), getRowsPerPage());
	}
	
	public String getLendButtonLabel() {
		final Item item = (Item)getTable().getRowData();
		final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		if (!item.isBorrowed()) {
			return I18nUtils.getMessageResourceString("item_lend", locale);
		}
		else {
			return I18nUtils.getMessageResourceString("item_lendAgain", locale);
		}
	}
}
