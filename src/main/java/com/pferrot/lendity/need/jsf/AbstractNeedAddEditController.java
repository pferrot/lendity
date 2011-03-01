package com.pferrot.lendity.need.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.item.ObjektService;
import com.pferrot.lendity.item.jsf.AbstractObjektAddEditController;
import com.pferrot.lendity.need.NeedService;
import com.pferrot.lendity.utils.JsfUtils;

public abstract class AbstractNeedAddEditController extends AbstractObjektAddEditController {

	private final static Log log = LogFactory.getLog(AbstractNeedAddEditController.class);
	
	private NeedService needService;
	
	public NeedService getNeedService() {
		return needService;
	}

	public void setNeedService(NeedService needService) {
		this.needService = needService;
	}

	@Override
	protected ObjektService getObjektService() {
		return getNeedService();
	}

	public abstract Long processNeed();
	
	public String submit() {
		Long needId = processNeed();
		
		JsfUtils.redirect(PagesURL.NEED_OVERVIEW, PagesURL.NEED_OVERVIEW_PARAM_NEED_ID, needId.toString());
	
		// As a redirect is used, this is actually useless.
		return null;
	}
}
