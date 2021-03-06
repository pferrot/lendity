package com.pferrot.lendity.comment.servlet;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.ObjectNotFoundException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.comment.CommentConsts;
import com.pferrot.lendity.comment.CommentService;
import com.pferrot.lendity.comment.exception.CommentException;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.model.ChildComment;
import com.pferrot.lendity.model.Comment;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.model.SystemComment;
import com.pferrot.lendity.model.WallComment;
import com.pferrot.lendity.person.PersonService;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.HtmlUtils;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.lendity.utils.UiUtils;


/**
 * Servlet to create/read/update/delete comments.
 *
 * @author pferrot
 *
 */
public class CommentController extends AbstractController {
	
	private final static Log log = LogFactory.getLog(CommentController.class);
	
	public final static String ACTION_PARAMETER_NAME = "action";
	public final static String ACTION_CREATE = "create";
	public final static String ACTION_READ = "read";
	public final static String ACTION_UPDATE = "update";
	public final static String ACTION_DELETE = "delete";
	
	public final static String TEXT_PARAMETER_NAME = "text";
	
	public final static String CONTAINER_ITEM_ID_PARAMETER_NAME = "itemID";
	
	public final static String CONTAINER_NEED_ID_PARAMETER_NAME = "needID";
	
	public final static String CONTAINER_LEND_TRANSACTION_ID_PARAMETER_NAME = "lendTransactionID";
	
	public final static String CONTAINER_GROUP_ID_PARAMETER_NAME = "groupID";
	
	public final static String PARENT_COMMENT_ID_PARAMETER_NAME = "parentCommentID";
	
	public final static String WALL_PARAMETER_NAME = "wall";
	public final static String OWN_WALL_PARAMETER_VALUE = "own";
	
	public final static String PUBLIC_COMMENT_PARAMETER_NAME = "publicComment";
	
	private final static int MAX_NB_CHILD_COMMENTS_INITIAL = 3;
	private final static int NB_CHILD_COMMENTS_LOAD_MORE = 10;
	
	private CommentService commentService;
	private PersonService personService;

	public CommentController() {
		super();
	}

	public CommentService getCommentService() {
		return commentService;
	}

	public void setCommentService(CommentService commentService) {
		this.commentService = commentService;
	}
	
	public PersonService getPersonService() {
		return personService;
	}

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}
	
	/**
	 * Dispatches to the correct CRUD method depending on the "action" (request parameter name).
	 * Possible values for that parameter: "create", "read", "update", "delete".
	 *
	 * @param pRequest
	 * @param pResponse
	 * @return
	 */
	protected ModelAndView handleRequestInternal(final HttpServletRequest pRequest, final HttpServletResponse pResponse) throws Exception {
		try {
			final String action = pRequest.getParameter(ACTION_PARAMETER_NAME);
			if (log.isDebugEnabled()) {
				log.debug("Action: " + action);
			}
			
			Map map = null;
			
			if (ACTION_CREATE.equals(action)) {
				map = create(pRequest, pResponse);	
			}
			else if (ACTION_READ.equals(action)) {
				map = read(pRequest, pResponse);
			}
			else if (ACTION_UPDATE.equals(action)) {
				map = update(pRequest, pResponse);
			}
			else if (ACTION_DELETE.equals(action)) {
				map = delete(pRequest, pResponse);
			}
			else {
				throw new CommentException("Unknonw action: " + action);
			}
			
			return new ModelAndView("commentJsonView", map);
		}
		catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error("Exception for person: " + PersonUtils.getCurrentPersonId(pRequest.getSession()), e);
			}
			throw e;
		}
	}
	
	/**
	 * Delete a comment from the DB if the user is authorized.
	 *
	 * @param pRequest
	 * @param pResponse
	 * @return
	 */
	private Map<String, Object> delete(final HttpServletRequest pRequest, final HttpServletResponse pResponse) {		
		final Map<String, Object> result = new HashMap<String, Object>();
		
		final String commentIdAsString = pRequest.getParameter("commentID");
		// Make the comment ID available in the response.
		result.put("commentID", commentIdAsString);
		
		// Load a given comment.
		if (commentIdAsString != null && commentIdAsString.trim().length() > 0) {
			final Long commentId = Long.parseLong(commentIdAsString);
			try {
				commentService.deleteCommentWithAC(commentId, PersonUtils.getCurrentPersonId(pRequest.getSession()));
			}
			catch (Exception e) {
				result.put("errorMessage", getInternalErrorMessage());
			}
		}		
		return result;
	}
	
	
	/**
	 * Returns a list of maps.
	 * 
	 * Each maps is a representation of a comment (commentId, text, ownerName, ownerUrl,
	 * dateAdded, profilePictureUrl, canEdit)
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return
	 */
	private Map<String, Object> read(final HttpServletRequest pRequest, final HttpServletResponse pResponse) {		
		final Map<String, Object> result = new HashMap<String, Object>();
		
		final String commentIdAsString = pRequest.getParameter("commentID");
		final String parentCommentIdAsString = pRequest.getParameter("parentID");
		// Load a given comment.
		if (commentIdAsString != null && commentIdAsString.trim().length() > 0) {
			final Long commentId = Long.parseLong(commentIdAsString);
			List<Map> comments = new ArrayList<Map>();
			List<Map> childComments = new ArrayList<Map>();
			try {
				final Comment comment = commentService.findCommentWithAC(commentId,
						PersonUtils.getCurrentPersonId(pRequest.getSession()));
				comments.add(getMapForOneComment(comment, pRequest));
				result.put("nb", 1);
			}
			catch (ObjectNotFoundException e) {
				result.put("nb", 0);
			}
			result.put("comments", comments);
			result.put("childComments", childComments);
			result.put("nbExtra", 0);
			result.put("firstResult", 0);
		}
		// Load more child comments.
		else if (parentCommentIdAsString != null && parentCommentIdAsString.trim().length() > 0) {
			final Long parentCommentId = Long.parseLong(parentCommentIdAsString);
			final Long currentOldestTimestamp = Long.parseLong(pRequest.getParameter("currentOldestTimestamp"));
			final ListWithRowCount lwrc = commentService.findChildCommentsByCreationDateDesc(parentCommentId, new Date(currentOldestTimestamp), 0, NB_CHILD_COMMENTS_LOAD_MORE);
			
			populateMultipleChildCommentsMap(result, lwrc, parentCommentId, pRequest);
		}
		// Load multiple comments.
		else {
			final String itemIdAsString = pRequest.getParameter(CONTAINER_ITEM_ID_PARAMETER_NAME);
			final String needIdAsString = pRequest.getParameter(CONTAINER_NEED_ID_PARAMETER_NAME);
			final String lendTransactionIdAsString = pRequest.getParameter(CONTAINER_LEND_TRANSACTION_ID_PARAMETER_NAME);
			final String groupIdAsString = pRequest.getParameter(CONTAINER_GROUP_ID_PARAMETER_NAME);
			final String wall = pRequest.getParameter(WALL_PARAMETER_NAME);
			
			final String firstResultAsString = pRequest.getParameter("firstResult");
			int firstResult = 0;
			if (!StringUtils.isNullOrEmpty(firstResultAsString)) {
				firstResult = Integer.valueOf(firstResultAsString);
			}
			int maxResults = CommentConsts.DEFAULT_NB_COMMENTS_TO_LOAD;
			final String maxResultsAsString = pRequest.getParameter("maxResults");
			if (!StringUtils.isNullOrEmpty(maxResultsAsString)) {
				maxResults = Integer.valueOf(maxResultsAsString);
			}
			ListWithRowCount lwrc = null;
			// Load comments for an item.
			if (itemIdAsString != null && itemIdAsString.trim().length() > 0) {				
				final Long itemId = Long.parseLong(itemIdAsString);
				lwrc = commentService.findItemCommentsWithAC(itemId,
						PersonUtils.getCurrentPersonId(pRequest.getSession()),
						firstResult,
						maxResults);
			}
			// Load comments for a need.
			else if (needIdAsString != null && needIdAsString.trim().length() > 0) {							
				final Long needId = Long.parseLong(needIdAsString);
				lwrc = commentService.findNeedCommentsWithAC(needId,
						PersonUtils.getCurrentPersonId(pRequest.getSession()),
						firstResult,
						maxResults);
			}
			// Load comments for a lend transaction.
			else if (lendTransactionIdAsString != null && lendTransactionIdAsString.trim().length() > 0) {							
				final Long lendTransactionId = Long.parseLong(lendTransactionIdAsString);
				lwrc = commentService.findLendTransactionCommentsWithAC(lendTransactionId,
						PersonUtils.getCurrentPersonId(pRequest.getSession()),
						firstResult,
						maxResults);
			}
			// Load comments for a group.
			else if (groupIdAsString != null && groupIdAsString.trim().length() > 0) {						
				final Long groupId = Long.parseLong(groupIdAsString);
				lwrc = commentService.findGroupCommentsWithAC(groupId,
						PersonUtils.getCurrentPersonId(pRequest.getSession()),
						firstResult,
						maxResults);
			}
			// Load wall comments.
			else if (!StringUtils.isNullOrEmpty(wall)) {	
				final Long currentPersonId = PersonUtils.getCurrentPersonId(pRequest.getSession());
				// Own wall actually corresponds to the "updates" displayed on the homepage, e.g. it also 
				// displays admin comments and comments that connections posted.
				if (OWN_WALL_PARAMETER_VALUE.equals(wall)) {
					lwrc = commentService.findOwnWallCommentsForPerson(currentPersonId, firstResult, maxResults);
				}
				// Someone else's wall or own wall but from the "my profile" or "person overview" pages,
				// but not from the homepage.
				else {
					final Long wallOwnerId = Long.valueOf(wall);
					lwrc = commentService.findOtherWallCommentsForPerson(
																currentPersonId,
																wallOwnerId,
																firstResult,
																maxResults);
				}
			}
			if (lwrc != null) {
				populateMultipleCommentsMap(result, lwrc, firstResult, maxResults, pRequest);
			}
		}
		
		return result;
	}
	
	/**
	 * Populates the given map with the comments information.
	 * 
	 * @param pMap
	 * @param pLwrc
	 * @param pFirstResult
	 * @param pMaxResults
	 * @param pRequest
	 */
	private void populateMultipleCommentsMap(final Map<String, Object> pMap,
			final ListWithRowCount pLwrc,
			final int pFirstResult,
			final int pMaxResults,
			final HttpServletRequest pRequest) {
		long nbExtra = pLwrc.getRowCount() - (pFirstResult + pMaxResults);
		if (nbExtra < 0) {
			nbExtra = 0;
		}
		pMap.put("nbExtra", nbExtra);
		pMap.put("firstResult", pFirstResult);
		pMap.put("nb", pLwrc.getList().size());
		
		final List list = pLwrc.getList();
		final Iterator ite = list.iterator();
		final List<Map> comments = new ArrayList<Map>();
		while (ite.hasNext()) {
			final Comment comment = (Comment)ite.next();
			comments.add(getMapForOneComment(comment, pRequest));
		}
		pMap.put("comments", comments);
	}
	
	private void populateMultipleChildCommentsMap(final Map<String, Object> pMap,
			final ListWithRowCount pLwrc,
			final Long pParentCommentId,
			final HttpServletRequest pRequest) {
		pMap.put("parentCommentId", pParentCommentId.toString());
		
		final long nbExtra = pLwrc.getRowCount() > NB_CHILD_COMMENTS_LOAD_MORE?(pLwrc.getRowCount() - NB_CHILD_COMMENTS_LOAD_MORE):0;
		pMap.put("nbExtraChildComments", nbExtra);
		
		final List<ChildComment> childCommentsList = pLwrc.getList();
		long oldestTimestamp = 0;
		if (!childCommentsList.isEmpty()) {
			oldestTimestamp = childCommentsList.get(childCommentsList.size() - 1).getCreationDate().getTime();
		}
		pMap.put("oldestChildCommentTimestamp", oldestTimestamp);
		
		
		final List<Map> childComments = new ArrayList<Map>();
		for (ChildComment childComment: childCommentsList) {
			childComments.add(getMapForOneComment(childComment, pRequest));
		}
		pMap.put("childComments", childComments);
	}
	
	/**
	 * Returns a map that is the representation of a comment -
	 * the keys are: commentId, text, ownerName, ownerUrl,
	 * dateAdded, profilePictureUrl, canEdit.
	 * 
	 * @param pComment
	 * @param pRequest
	 * @return
	 */
	private Map<String, Object> getMapForOneComment(final Comment pComment, final HttpServletRequest pRequest) {
		final Long currentPersonId = PersonUtils.getCurrentPersonId(pRequest.getSession());
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("commentID", pComment.getId());
		map.put("text", getCommentService().processAllHrefWithPerson(HtmlUtils.getTextWithHrefLinks(HtmlUtils.escapeHtmlAndReplaceCr(pComment.getText())), pComment.getOwner()));
		map.put("textWithoutHref", HtmlUtils.escapeHtmlAndReplaceCr(pComment.getText()));
		map.put("adminComment", pComment.getAdminComment());
		map.put("publicComment", pComment.getPublicComment());
		
		final Person owner = pComment.getOwner();
		if (owner != null) {
			map.put("ownerName", HtmlUtils.escapeHtmlAndReplaceCr(owner.getDisplayName()));		
			map.put("ownerUrl", JsfUtils.getFullUrlWithPrefix(pRequest.getContextPath(),
				                                          PagesURL.PERSON_OVERVIEW,
				                                          PagesURL.PERSON_OVERVIEW_PARAM_PERSON_ID,
				                                          pComment.getOwner().getId().toString()));
		}
		map.put("dateAdded", getDateAsString(pComment.getCreationDate()));
		final Boolean canEdit = currentPersonId != null &&
								! (pComment instanceof SystemComment) &&
								currentPersonId.equals(owner.getId());
		map.put("canEdit", canEdit);
		// Indicate whether that message was posted on someone else's wall.
		Boolean canDelete = canEdit;
		Boolean otherWallComment = Boolean.FALSE;
		WallComment refWallComment = null;
		if (Hibernate.getClass(pComment).isAssignableFrom(WallComment.class)) {
			refWallComment = commentService.findWallComment(pComment.getId()); 			
		}
		else if (Hibernate.getClass(pComment).isAssignableFrom(ChildComment.class)) {
			final ChildComment childComment = commentService.findChildComment(pComment.getId());
			if (Hibernate.getClass(childComment.getParentComment()).isAssignableFrom(WallComment.class)) {
				refWallComment = commentService.findWallComment(childComment.getParentComment().getId());
			} 			
		}
		if (refWallComment != null) {
			Long wallCommentWallOwnerId = null;
			if (refWallComment.getWallOwner() != null) {
				final Person wallOwner = refWallComment.getWallOwner();
				map.put("wallOwnerName", HtmlUtils.escapeHtmlAndReplaceCr(wallOwner.getDisplayName()));		
				map.put("wallOwnerUrl", JsfUtils.getFullUrlWithPrefix(pRequest.getContextPath(),
                        PagesURL.PERSON_OVERVIEW,
                        PagesURL.PERSON_OVERVIEW_PARAM_PERSON_ID,
                        wallOwner.getId().toString()));
				
				wallCommentWallOwnerId = wallOwner.getId();
			}
			canDelete = canEdit || (currentPersonId != null && currentPersonId.equals(wallCommentWallOwnerId));
			
			otherWallComment = refWallComment.getWallOwner() != null; 		
		}
		map.put("canDelete", canDelete);
		map.put("otherWallComment", otherWallComment);
		
		map.put("systemComment", pComment instanceof SystemComment);
		if (pComment instanceof ChildComment) {
			map.put("parentCommentID", ((ChildComment)pComment).getParentComment().getId());
		}
		else {
			final ListWithRowCount childCommentsLwrc = commentService.findChildCommentsByCreationDateDesc(pComment, null, 0, MAX_NB_CHILD_COMMENTS_INITIAL);
			final List<ChildComment> childCommentsList = childCommentsLwrc.getList();
			final List<Map> childComments = new ArrayList<Map>();
			
			for (ChildComment childComment: childCommentsList) {
				childComments.add(getMapForOneComment(childComment, pRequest));
			}
			map.put("childComments", childComments);
			map.put("nbChildComments", childCommentsList.size());
			final long nbExtra = childCommentsLwrc.getRowCount() > MAX_NB_CHILD_COMMENTS_INITIAL?(childCommentsLwrc.getRowCount() - MAX_NB_CHILD_COMMENTS_INITIAL):0;
			map.put("nbExtraChildComments", nbExtra);
			long oldestTimestamp = 0;
			if (!childCommentsList.isEmpty()) {
				oldestTimestamp = childCommentsList.get(childCommentsList.size() - 1).getCreationDate().getTime();
			}
			map.put("oldestChildCommentTimestamp", oldestTimestamp);
			
		}
		map.put("profilePictureUrl",
				personService.getProfileThumbnailSrc(
						owner, 
						true, 
						pRequest.getSession(),
						pRequest.getContextPath()));
		
		
		
		
		return map;
	}
	
	private Map<String, Object> update(final HttpServletRequest pRequest, final HttpServletResponse pResponse) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			final String text = pRequest.getParameter(TEXT_PARAMETER_NAME);
			if (!isValidComment(text)) {
				map.put("errorMessage", getCommentValidationErrorMessage());
			}
			else {
				// Need to pass the session since not in a faces context.
				final Long currentPersonId = PersonUtils.getCurrentPersonId(pRequest.getSession());
				final String commentIdAsString = pRequest.getParameter("commentID");
				final Long commentID = Long.valueOf(commentIdAsString);
				final Boolean publicComment = Boolean.valueOf("true".equals(pRequest.getParameter(PUBLIC_COMMENT_PARAMETER_NAME)));
				
				commentService.updateCommentWithAC(commentID, text, publicComment, currentPersonId);
				
				map = getMapForOneComment(commentService.findComment(commentID), pRequest);
			}
		}
		catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error(e);
			}
			map.put("errorMessage", getInternalErrorMessage());
		}
		
		return map;
		
		
	}

	/**
	 * Creates a comment on the specified container specified by its ID as request parameter
	 * (for item: "itemID", for need: "needID", for lendTransaction: "lendTransactionID").
	 * It is also verified that the comment is valid (max length for instance).
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return
	 */
	private Map<String, Object> create(final HttpServletRequest pRequest, final HttpServletResponse pResponse) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		String parentCommentID = null;
		try {
			final String text = pRequest.getParameter(TEXT_PARAMETER_NAME);
			parentCommentID = pRequest.getParameter(PARENT_COMMENT_ID_PARAMETER_NAME);
			final boolean isChildComment = !StringUtils.isNullOrEmpty(parentCommentID);
			if (!isValidComment(text)) {
				map.put("errorMessage", getCommentValidationErrorMessage());
				if (isChildComment) {
					map.put("parentCommentID", parentCommentID);	
				}
			}
			else {
				// Need to pass the session since not in a faces context.
				final Long currentPersonId = PersonUtils.getCurrentPersonId(pRequest.getSession());
				
				Long commentID = null;
				
				final String itemID = pRequest.getParameter(CONTAINER_ITEM_ID_PARAMETER_NAME);
				final String needID = pRequest.getParameter(CONTAINER_NEED_ID_PARAMETER_NAME);
				final String lendTransactionID = pRequest.getParameter(CONTAINER_LEND_TRANSACTION_ID_PARAMETER_NAME);
				final String groupID = pRequest.getParameter(CONTAINER_GROUP_ID_PARAMETER_NAME);
				final String wall = pRequest.getParameter(WALL_PARAMETER_NAME);
				
				if (!StringUtils.isNullOrEmpty(itemID)) {
					commentID = commentService.createCommentOnItemWithAC(text, Long.valueOf(itemID), currentPersonId);
				}
				else if (!StringUtils.isNullOrEmpty(needID)) {
					commentID = commentService.createCommentOnNeedWithAC(text, Long.valueOf(needID), currentPersonId);
				}
				else if (!StringUtils.isNullOrEmpty(lendTransactionID)) {
					commentID = commentService.createCommentOnLendTransactionWithAC(text, Long.valueOf(lendTransactionID), currentPersonId);
				}
				else if (!StringUtils.isNullOrEmpty(groupID)) {
					commentID = commentService.createCommentOnGroupWithAC(text, Long.valueOf(groupID), currentPersonId);
				}
				else if (!StringUtils.isNullOrEmpty(wall)) {
					if (OWN_WALL_PARAMETER_VALUE.equals(wall) ||
						(currentPersonId != null && wall.equals(currentPersonId.toString()))) {
						final Boolean publicComment = Boolean.valueOf("true".equals(pRequest.getParameter(PUBLIC_COMMENT_PARAMETER_NAME)));
						commentID = commentService.createCommentOnOwnWallWithAC(text, publicComment, currentPersonId);	
					}
					else {
						commentID = commentService.createCommentOnOtherWallWithAC(text, currentPersonId, Long.valueOf(wall), Boolean.FALSE);
					}
				}
				else if (!StringUtils.isNullOrEmpty(parentCommentID)) {
					commentID = commentService.createChildCommentWithAC(text, Long.valueOf(parentCommentID), currentPersonId);
				}
				
				map = getMapForOneComment(commentService.findComment(commentID), pRequest);
			}
		}
		catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error(e);
			}
			map.put("errorMessage", getInternalErrorMessage());
			if (!StringUtils.isNullOrEmpty(parentCommentID)) {
				map.put("parentCommentID", parentCommentID);	
			}
		}
		
		return map;
	}

	/**
	 * Returns true if the comment is valid.
	 *
	 * @param pComment
	 * @return
	 */
	private boolean isValidComment(final String pComment) {
		return pComment != null &&
			   pComment.trim().length() > 0 &&
			   pComment.length() <= CommentConsts.COMMENT_MAX_LENGTH;
	}
	
//	private boolean isValidChildComment(final String pComment) {
//		return pComment != null &&
//			!pComment.equals(getAddChildCommentDefaultText()) &&
//			pComment.length() <= CommentConsts.COMMENT_MAX_LENGTH;
//	}

	private String getCommentValidationErrorMessage() {
		final Locale locale = I18nUtils.getDefaultLocale();
		return HtmlUtils.escapeHtmlAndReplaceCr(
				I18nUtils.getMessageResourceString("validation_commentError", new Object[]{new Integer(CommentConsts.COMMENT_MAX_LENGTH)}, locale));
	}
	
	private String getInternalErrorMessage() {
		final Locale locale = I18nUtils.getDefaultLocale();
		return HtmlUtils.escapeHtmlAndReplaceCr(I18nUtils.getMessageResourceString("error_internalError", locale));
	}
	
	private String getDateAsString(final Date pDate) {
		final Locale locale = I18nUtils.getDefaultLocale();
		return HtmlUtils.escapeHtmlAndReplaceCr(UiUtils.getDateTimeAsString(pDate, locale));
	}
	
	private String getAddCommentDefaultText() {
		final Locale locale = I18nUtils.getDefaultLocale();
		return I18nUtils.getMessageResourceString("comment_addComment", locale);
	}
	
	private String getAddChildCommentDefaultText() {
		final Locale locale = I18nUtils.getDefaultLocale();
		return I18nUtils.getMessageResourceString("comment_addReply", locale);
	}
}
