package com.pferrot.lendity.need.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.lendity.model.Need;

@ViewController(viewIds={"/auth/need/needAdd.jspx"})
public class NeedAddController extends AbstractNeedAddEditController {
	
	private final static Log log = LogFactory.getLog(NeedAddController.class);

//	@InitView
//	public void initView() {
//	}

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
}
