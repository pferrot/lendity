package com.pferrot.lendity.need.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.item.ObjektService;
import com.pferrot.lendity.item.jsf.AbstractObjektOverviewController;
import com.pferrot.lendity.model.Need;
import com.pferrot.lendity.model.Objekt;
import com.pferrot.lendity.need.NeedService;
import com.pferrot.lendity.need.NeedUtils;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.lendity.utils.NavigationUtils;
import com.pferrot.security.SecurityUtils;

@ViewController(viewIds={"/public/need/needOverview.jspx"})
public class NeedOverviewController extends AbstractObjektOverviewController {
	
	private final static Log log = LogFactory.getLog(NeedOverviewController.class);
	
	private Need need;
	private NeedService needService;
	private Long needId;

	public Need getNeed() {
		return need;
	}

	public void setNeed(Need need) {
		this.need = need;
	}

	public NeedService getNeedService() {
		return needService;
	}

	public void setNeedService(NeedService needService) {
		this.needService = needService;
	}

	@Override
	protected Objekt getObjekt() {
		return getNeed();
	}

	@Override
	protected ObjektService getObjektService() {
		return getNeedService();
	}

	public Long getNeedId() {
		return needId;
	}

	public void setNeedId(Long needId) {
		this.needId = needId;
	}

	@InitView
	public void initView() {		
		final String idString = JsfUtils.getRequestParameter(PagesURL.NEED_OVERVIEW_PARAM_NEED_ID);
		Need need = null;
		if (idString != null) {
			setNeedId(Long.parseLong(idString));
			need = getNeedService().findNeed(getNeedId());
			// Access control check.
			if (!getNeedService().isCurrentUserAuthorizedToView(need)) {
				if (SecurityUtils.isLoggedIn()) {
					JsfUtils.redirect(PagesURL.ERROR_ACCESS_DENIED);
					if (log.isWarnEnabled()) {
						log.warn("Access denied (need view): user = " + PersonUtils.getCurrentPersonDisplayName() + " (" + PersonUtils.getCurrentPersonId() + "), item = " + getNeedId());
					}
				}
				// If the user is not logged in, there is a chance he can access the page once logged in,
				// so we need to redirect him and not just show the access denied page.
				else {
					NavigationUtils.redirectToCurrentPageThroughLogin();
				}
				return;
			}
			setNeed(need);
		}	
	}
	
	public String getNeedEditHref() {		
		return NeedUtils.getNeedEditPageUrl(getNeed().getId().toString());
	}

	public boolean isGotItAvailable() {
		return !getNeed().getOwner().getId().equals(PersonUtils.getCurrentPersonId());
	}
	
	public String getGotItHref() {
		return JsfUtils.getFullUrl(PagesURL.ITEM_ADD, 
				PagesURL.ITEM_ADD_PARAM_NEED_ID,
				getNeed().getId().toString());
	}
}
