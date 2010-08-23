package com.pferrot.lendity.need.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.model.Need;
import com.pferrot.lendity.need.NeedUtils;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.JsfUtils;

@ViewController(viewIds={"/auth/need/needEdit.jspx"})
public class NeedEditController extends AbstractNeedAddEditController {
	
	private final static Log log = LogFactory.getLog(NeedEditController.class);
	
	private Need need;

	@InitView
	public void initView() {
		// Read the ID from the request parameter and load the correct item.
		try {
			final String idString = JsfUtils.getRequestParameter(PagesURL.NEED_EDIT_PARAM_NEED_ID);
			if (idString != null) {
				setNeed(getNeedService().findNeed(Long.parseLong(idString)));
				// Access control check.
				if (!getNeedService().isCurrentUserAuthorizedToEdit(getNeed())) {
					JsfUtils.redirect(PagesURL.ERROR_ACCESS_DENIED);
					if (log.isWarnEnabled()) {
						log.warn("Access denied (need edit): user = " + PersonUtils.getCurrentPersonDisplayName() + " (" + PersonUtils.getCurrentPersonId() + "), need = " + idString);
					}
					return;
				}
			}
			// Not found or no ID specified.
			if (getNeed() == null) {
				JsfUtils.redirect(PagesURL.MY_CONNECTIONS_NEEDS_LIST);
			}
		}
		catch (Exception e) {
			//TODO display standard error page instead.
			JsfUtils.redirect(PagesURL.MY_CONNECTIONS_NEEDS_LIST);
		}		
	}

	public Need getNeed() {
		return need;
	}
	
	private void setNeed(final Need pNeed) {
		need = pNeed;

		// Initialize the model to be edited.
		setTitle(pNeed.getTitle());
		setDescription(pNeed.getDescription());
		setCategoryId(pNeed.getCategory().getId());
	}	

	public Long updateNeed() {		
		getNeed().setTitle(getTitle());
		getNeed().setDescription(getDescription());
		getNeedService().updateNeedWithCategory(getNeed(), getCategoryId());

		return getNeed().getId();
	}

	public String getNeedOverviewHref() {		
		return NeedUtils.getNeedOverviewPageUrl(need.getId().toString());
	}	

	@Override
	public Long processNeed() {
		return updateNeed();
	}	
}
