package com.pferrot.lendity.group.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.lendity.model.Group;

@ViewController(viewIds={"/auth/group/groupAdd.jspx"})
public class GroupAddController extends AbstractGroupAddEditController {
	
	private final static Log log = LogFactory.getLog(GroupAddController.class);

	public Long createGroup() {
		Group group = new Group();
		
		group.setTitle(getTitle());
		group.setDescription(getDescription());
		group.setValidateMembership(getValidateMembership());
		group.setOnlyMembersCanSeeComments(getOnlyMembersCanSeeComments());
		group.setPassword(getPassword());
		group.setOwner(getPersonService().getCurrentPerson());			
		return getGroupService().createGroup(group);
	}
	
	@Override
	public Long processGroup() {
		return createGroup();
	}
}
