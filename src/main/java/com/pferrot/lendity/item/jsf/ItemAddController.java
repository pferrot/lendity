package com.pferrot.lendity.item.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.model.Item;
import com.pferrot.lendity.model.Need;
import com.pferrot.lendity.need.NeedService;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.JsfUtils;

@ViewController(viewIds={"/auth/item/itemAdd.jspx"})
public class ItemAddController extends AbstractItemAddEditController {
	
	private final static Log log = LogFactory.getLog(ItemAddController.class);

	private NeedService needService;
	private Need need;
	
	public NeedService getNeedService() {
		return needService;
	}

	public void setNeedService(NeedService needService) {
		this.needService = needService;
	}

	public Need getNeed() {
		return need;
	}

	public void setNeed(Need need) {
		this.need = need;
	}

	@InitView
	public void initView() {
		// Default visibility.
//		setVisibilityId(getItemService().getConnectionsVisibilityId());
		String needIdString = null;
		try {
			needIdString = JsfUtils.getRequestParameter(PagesURL.ITEM_ADD_PARAM_NEED_ID);
			if (needIdString != null) {
				final Need needTemp = getNeedService().findNeed(Long.parseLong(needIdString));
				getNeedService().assertCurrentUserAuthorizedToView(needTemp);
				setNeed(needTemp);
				setTitle(needTemp.getTitle());
			}
				
		}
		catch (Exception e) {
			JsfUtils.redirect(PagesURL.ERROR_ACCESS_DENIED);
			if (log.isWarnEnabled()) {
				log.warn("Access denied (item add from need): user = " + PersonUtils.getCurrentPersonDisplayName() + " (" + PersonUtils.getCurrentPersonId() + "), need = " + needIdString);
			}
			return;
		}
	}

	public Long createItem() {
		Item item = new Item();
		
		item.setTitle(getTitle());
		item.setDescription(getDescription());
		item.setOwner(getItemService().getCurrentPerson());
		item.setDeposit(getDeposit());
		item.setRentalFee(getRentalFee());
		item.setToGiveForFree(getToGiveForFree());
		item.setSalePrice(getSalePrice());
				
		return getItemService().createItem(item, getCategoryId(), getVisibilityId(), getNeed());
	}
	
	@Override
	public Long processItem() {
		return createItem();
	}
}