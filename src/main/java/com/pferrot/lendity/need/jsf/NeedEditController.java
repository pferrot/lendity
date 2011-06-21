package com.pferrot.lendity.need.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.model.ItemCategory;
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
			Need need = null;
			if (idString != null) {
				need = getNeedService().findNeed(Long.parseLong(idString));
				// Access control check.
				if (!getNeedService().isCurrentUserAuthorizedToEdit(need)) {
					JsfUtils.redirect(PagesURL.ERROR_ACCESS_DENIED);
					if (log.isWarnEnabled()) {
						log.warn("Access denied (need edit): user = " + PersonUtils.getCurrentPersonDisplayName() + " (" + PersonUtils.getCurrentPersonId() + "), need = " + idString);
					}
					return;
				}
				else {
					setNeed(need);
				}
			}
			// Not found or no ID specified.
			if (getNeed() == null) {
				JsfUtils.redirect(PagesURL.NEEDS_SEARCH);
				return;
			}
		}
		catch (Exception e) {
			//TODO display standard error page instead.
			JsfUtils.redirect(PagesURL.NEEDS_SEARCH);
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
		setVisibilityId(pNeed.getVisibility().getId());
		final ItemCategory category = pNeed.getCategory();
		if (category != null) {
			setCategoryId(category.getId());
		}
		setAuthorizedGroupsIdsFromObjekt(pNeed);
	}	

	public Long updateNeed() {		
		getNeed().setTitle(getTitle());
		getNeed().setDescription(getDescription());
		getNeedService().updateNeed(getNeed(), getCategoryId(), getVisibilityId(), getAuthorizedGroupsIds());

		return getNeed().getId();
	}

	public String getNeedOverviewHref() {		
		return NeedUtils.getNeedOverviewPageUrl(getNeed().getId().toString());
	}	

	@Override
	public Long processNeed() {
		return updateNeed();
	}	
}
