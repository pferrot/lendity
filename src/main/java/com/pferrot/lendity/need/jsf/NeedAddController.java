package com.pferrot.lendity.need.jsf;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.model.Need;
import com.pferrot.lendity.utils.JsfUtils;

@ViewController(viewIds={"/auth/need/needAdd.jspx"})
public class NeedAddController extends AbstractNeedAddEditController {
	
	private final static Log log = LogFactory.getLog(NeedAddController.class);
	
	public final static String TITLE_PARAMETER_NAME = "title";
	public final static String NO_CONFIRM_PARAMETER_NAME = "noConfirm";
	
	private boolean noConfirm;

	@InitView
	public void initView() {
		final HttpServletRequest request = JsfUtils.getRequest();
		final String title = request.getParameter(TITLE_PARAMETER_NAME);
		if (!StringUtils.isNullOrEmpty(title)) {
			setTitle(title);
		}
		final String noConfirm = request.getParameter(NO_CONFIRM_PARAMETER_NAME);
		if ("true".equals(noConfirm)) {
			setNoConfirm(true);
		}
	}

	public Long createNeed() {
		Need need = new Need();
		
		need.setTitle(getTitle());
		need.setDescription(getDescription());
		need.setOwner(getNeedService().getCurrentPerson());
				
		return getNeedService().createNeedWithCategory(need, getCategoryId());		
	}
	
	@Override
	public Long processNeed() {
		return createNeed();
	}

	public boolean isNoConfirm() {
		return noConfirm;
	}

	public void setNoConfirm(boolean noConfirm) {
		this.noConfirm = noConfirm;
	}
}
