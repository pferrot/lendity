package com.pferrot.lendity.need.jsf;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.model.Need;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.lendity.utils.UiUtils;

@ViewController(viewIds={"/auth/need/needAdd.jspx"})
public class NeedAddController extends AbstractNeedAddEditController {
	
	private final static Log log = LogFactory.getLog(NeedAddController.class);
	
	public final static String TITLE_PARAMETER_NAME = "title";
	
	// Set true if an email should be sent to connections about that need.
	private Boolean sendEmail;
	
	private List<SelectItem> sendEmailSelectItems;

	@InitView
	public void initView() {
		final HttpServletRequest request = JsfUtils.getRequest();
		final String title = request.getParameter(TITLE_PARAMETER_NAME);
		if (!StringUtils.isNullOrEmpty(title)) {
			setTitle(title);
		}
	}

	public Long createNeed() {
		Need need = new Need();
		
		need.setTitle(getTitle());
		need.setDescription(getDescription());
		need.setOwner(getNeedService().getCurrentPerson());
				
		return getNeedService().createNeed(need, getCategoriesIds(), getVisibilityId(), getAuthorizedGroupsIds(), isSendEmailBoolean());		
	}
	
	@Override
	public Long processNeed() {
		return createNeed();
	}
	
	public Boolean getSendEmail() {
		return sendEmail;
	}

	public void setSendEmail(Boolean sendEmail) {
		this.sendEmail = sendEmail;
	}
	
	public boolean isSendEmailBoolean() {
		return Boolean.TRUE.equals(getSendEmail());
	}
	
	public List<SelectItem> getSendEmailSelectItems() {
		if (sendEmailSelectItems == null) {
			final Locale locale = I18nUtils.getDefaultLocale();
			sendEmailSelectItems = new ArrayList<SelectItem>();
			sendEmailSelectItems.add(UiUtils.getPleaseSelectSelectItem(locale));
			sendEmailSelectItems.add(new SelectItem(Boolean.TRUE, I18nUtils.getMessageResourceString("need_sendEmailYes", locale)));
			sendEmailSelectItems.add(new SelectItem(Boolean.FALSE, I18nUtils.getMessageResourceString("need_sendEmailNo", locale)));
		}		
		return sendEmailSelectItems;	
	}
}
