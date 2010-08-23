package com.pferrot.lendity.need.jsf;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.need.NeedService;
import com.pferrot.lendity.utils.JsfUtils;

public class DeleteNeedTooltipController implements Serializable {
	
	private final static Log log = LogFactory.getLog(DeleteNeedTooltipController.class);
	
	private NeedService needService;
	
	private Long needId;

	public NeedService getNeedService() {
		return needService;
	}

	public void setNeedService(NeedService needService) {
		this.needService = needService;
	}

	public Long getNeedId() {
		return needId;
	}

	public void setNeedId(Long needId) {
		this.needId = needId;
	}

	public String submit() {
		deleteNeed();
		
		JsfUtils.redirect(PagesURL.MY_NEEDS_LIST);
	
		// As a redirect is used, this is actually useless.
		return null;
	}

	private void deleteNeed() {
		getNeedService().deleteNeed(getNeedId());		
	}	
}
