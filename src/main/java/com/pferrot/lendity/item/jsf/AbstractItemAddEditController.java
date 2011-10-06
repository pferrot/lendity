package com.pferrot.lendity.item.jsf;

import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.item.ItemService;
import com.pferrot.lendity.item.ObjektService;
import com.pferrot.lendity.model.ListValue;
import com.pferrot.lendity.utils.JsfUtils;

public abstract class AbstractItemAddEditController extends AbstractObjektAddEditController {
	
	private final static Log log = LogFactory.getLog(AbstractItemAddEditController.class);
	
	private ItemService itemService;
	
	private Boolean toGiveForFree;
	private Double salePrice;
	private Double deposit;
	private Double rentalFee;
	
	public ItemService getItemService() {
		return itemService;
	}

	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}
	
	@Override
	protected ObjektService getObjektService() {
		return getItemService();
	}

	public abstract Long processItem() throws Exception;

	public Boolean getToGiveForFree() {
		if (toGiveForFree == null) {
			return Boolean.FALSE;
		}
		return toGiveForFree;
	}

	public void setToGiveForFree(Boolean toGiveForFree) {
		this.toGiveForFree = toGiveForFree;
	}

	public Double getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(Double salePrice) {
		if (salePrice != null && salePrice.doubleValue() <= 0) {
			salePrice = null;
		}
		this.salePrice = salePrice;
	}

	public Double getDeposit() {
		return deposit;
	}

	public void setDeposit(Double deposit) {
		if (deposit != null && deposit.doubleValue() <= 0) {
			deposit = null;
		}
		this.deposit = deposit;
	}

	public Double getRentalFee() {
		return rentalFee;
	}

	public void setRentalFee(Double rentalFee) {
		if (rentalFee != null && rentalFee.doubleValue() <= 0) {
			rentalFee = null;
		}
		this.rentalFee = rentalFee;
	}

	public void validateRentalFee(FacesContext context, UIComponent toValidate, Object value) {
		String message = "";
		Double rentalFee = (Double) value;
		
		final UIComponent categoryComponent = toValidate.findComponent("category");
		final EditableValueHolder categoryEditableValueHolder = (EditableValueHolder)categoryComponent;
		final Long categoryId = (Long)categoryEditableValueHolder.getValue();
		
		if (categoryId != null &&
				rentalFee != null &&
			!getItemService().isRentalAllowed(categoryId)) {
			((UIInput)toValidate).setValid(false);
			final Locale locale = I18nUtils.getDefaultLocale();
			final ListValue category = getObjektService().getListValueDao().findListValue(categoryId);
			final String categoryLabel = I18nUtils.getMessageResourceString(category.getLabelCode(), locale);
			message = I18nUtils.getMessageResourceString("validation_rentalFeeNotAllowedIntellectualProperty", new Object[]{categoryLabel}, locale);
			context.addMessage(toValidate.getClientId(context), new FacesMessage(message));			
		}
	}
	
	public String submit() {
		try {
			Long itemId = processItem();
			
			JsfUtils.redirect(PagesURL.ITEM_OVERVIEW, PagesURL.ITEM_OVERVIEW_PARAM_ITEM_ID, itemId.toString());
		
			// As a redirect is used, this is actually useless.
			return null;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
