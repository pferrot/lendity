package com.pferrot.lendity.item.jsf;

import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.comment.CommentService;
import com.pferrot.lendity.comment.exception.CommentException;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.model.Item;
import com.pferrot.lendity.model.ItemVisibility;
import com.pferrot.lendity.model.Need;
import com.pferrot.lendity.need.NeedService;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.JsfUtils;

@ViewController(viewIds={"/auth/item/itemAdd.jspx"})
public class ItemAddController extends AbstractItemAddEditController {
	
	private final static Log log = LogFactory.getLog(ItemAddController.class);

	private NeedService needService;
	private CommentService commentService;
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

	public Long createItem() throws CommentException {
		Item item = new Item();
		
		item.setTitle(getTitle());
		item.setDescription(getDescription());
		item.setOwner(getItemService().getCurrentPerson());
		item.setDeposit(getDeposit());
		item.setRentalFee(getRentalFee());
		item.setToGiveForFree(getToGiveForFree());
		item.setSalePrice(getSalePrice());
				
		final Long id = getItemService().createItem(item, getCategoriesIds(), getVisibilityId(), getNeed(), getAuthorizedGroupsIds());
		
		// Post a comment on the wall.
		final String visibilityLabelCode = item.getVisibility().getLabelCode();
		if (!ItemVisibility.PRIVATE.equals(visibilityLabelCode)) {
			Boolean publicComment = ItemVisibility.PUBLIC.equals(visibilityLabelCode);
			final Locale locale = I18nUtils.getDefaultLocale();			
			final String text = I18nUtils.getMessageResourceString("item_itemAddedWallComment", new Object[]{"{i" + id + "}"}, locale);
			getCommentService().createCommentOnOwnWallWithAC(text, publicComment, item.getOwner().getId());
		}
		
		return id;
	}
	
	@Override
	public Long processItem() throws Exception {
		return createItem();
	}

	public CommentService getCommentService() {
		return commentService;
	}

	public void setCommentService(CommentService commentService) {
		this.commentService = commentService;
	}
}
