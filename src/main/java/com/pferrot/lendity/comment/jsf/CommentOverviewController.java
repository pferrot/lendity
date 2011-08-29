package com.pferrot.lendity.comment.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;
import org.springframework.security.AccessDeniedException;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.comment.CommentService;
import com.pferrot.lendity.comment.exception.CommentException;
import com.pferrot.lendity.model.Comment;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.JsfUtils;

@ViewController(viewIds={"/auth/comment/commentOverview.jspx"})
public class CommentOverviewController {
	
	private final static Log log = LogFactory.getLog(CommentOverviewController.class);
	
	private CommentService commentService;
	
	private Comment comment;
	private Long commentId;	
	
	public Long getCommentId() {
		return commentId;
	}
	
	public void setCommentId(Long commentId) {
		this.commentId = commentId;
	}
	
	public Comment getComment() {
		return comment;
	}

	public void setComment(Comment comment) {
		this.comment = comment;
	}

	public CommentService getCommentService() {
		return commentService;
	}
	
	public void setCommentService(CommentService commentService) {
		this.commentService = commentService;
	}
	
	@InitView
	public void initView() {
		// Read the comment ID from the request parameter and load the correct comment.
		try {
			final String commentIdString = JsfUtils.getRequestParameter(PagesURL.COMMENT_OVERVIEW_PARAM_COMMENT_ID);
			Comment comment = null;
			if (commentIdString != null) {
				setCommentId(Long.parseLong(commentIdString));
				comment = getCommentService().findComment(getCommentId());
				// Access control check.
				if (!getCommentService().isCurrentUserAuthorizedToView(comment)) {
					JsfUtils.redirect(PagesURL.ERROR_ACCESS_DENIED);
					if (log.isWarnEnabled()) {
						log.warn("Access denied (comment view): user = " + PersonUtils.getCurrentPersonDisplayName() + " (" + PersonUtils.getCurrentPersonId() + "), comment = " + commentIdString);
					}
					return;
				}
				setComment(comment);
			}
		}
		catch (AccessDeniedException ade) {
			throw ade;
		}
		catch (Exception e) {
			JsfUtils.redirect(PagesURL.ERROR_PAGE_NOT_FOUND);
		}		
	}
	
	public boolean isCurrentUserAuthorizedToReply() {
		return getCommentService().isCurrentUserAuthorizedToAddChildComment(getComment());
	}
	
	public String getContainerUrl() {
		try {
			return getCommentService().getCommentContainerUrl(getComment());
		}
		catch (CommentException e) {
			throw new RuntimeException(e);
		}		
	}
	
	public String getContainerTitle() {
		try {
			return getCommentService().getCommentContainerTitle(getComment());
		}
		catch (CommentException e) {
			throw new RuntimeException(e);
		}
	}
	

}
