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
import com.pferrot.lendity.model.Comment;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.model.SystemComment;
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
		final String itemIdAsString = pRequest.getParameter(CONTAINER_ITEM_ID_PARAMETER_NAME);
		final String needIdAsString = pRequest.getParameter(CONTAINER_NEED_ID_PARAMETER_NAME);
		final String lendTransactionIdAsString = pRequest.getParameter(CONTAINER_LEND_TRANSACTION_ID_PARAMETER_NAME);
		final String groupIdAsString = pRequest.getParameter(CONTAINER_GROUP_ID_PARAMETER_NAME);
		// Load a given comment.
		if (commentIdAsString != null && commentIdAsString.trim().length() > 0) {
			final Long commentId = Long.parseLong(commentIdAsString);
			List<Map> comments = new ArrayList<Map>();
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
			result.put("nbExtra", 0);
			result.put("firstResult", 0);
		}
		// Load comments for an item.
		else if (itemIdAsString != null && itemIdAsString.trim().length() > 0) {
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
			
			final Long itemId = Long.parseLong(itemIdAsString);
			final ListWithRowCount lwrc = commentService.findItemCommentsWithAC(itemId,
					PersonUtils.getCurrentPersonId(pRequest.getSession()),
					firstResult,
					maxResults);
			
			populateMultipleCommentsMap(result, lwrc, firstResult, maxResults, pRequest);
		}
		// Load comments for a need.
		else if (needIdAsString != null && needIdAsString.trim().length() > 0) {			
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
			
			final Long needId = Long.parseLong(needIdAsString);
			final ListWithRowCount lwrc = commentService.findNeedCommentsWithAC(needId,
					PersonUtils.getCurrentPersonId(pRequest.getSession()),
					firstResult,
					maxResults);
			
			populateMultipleCommentsMap(result, lwrc, firstResult, maxResults, pRequest);
		}
		// Load comments for a lend transaction.
		else if (lendTransactionIdAsString != null && lendTransactionIdAsString.trim().length() > 0) {			
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
			
			final Long lendTransactionId = Long.parseLong(lendTransactionIdAsString);
			final ListWithRowCount lwrc = commentService.findLendTransactionCommentsWithAC(lendTransactionId,
					PersonUtils.getCurrentPersonId(pRequest.getSession()),
					firstResult,
					maxResults);
			
			populateMultipleCommentsMap(result, lwrc, firstResult, maxResults, pRequest);
		}
		// Load comments for a group.
		else if (groupIdAsString != null && groupIdAsString.trim().length() > 0) {			
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
			
			final Long groupId = Long.parseLong(groupIdAsString);
			final ListWithRowCount lwrc = commentService.findGroupCommentsWithAC(groupId,
					PersonUtils.getCurrentPersonId(pRequest.getSession()),
					firstResult,
					maxResults);
			
			populateMultipleCommentsMap(result, lwrc, firstResult, maxResults, pRequest);
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
		Map<String, Object> map= new HashMap<String, Object>();
		
		map.put("commentID", pComment.getId());
		map.put("text", HtmlUtils.escapeHtmlAndReplaceCr(pComment.getText()));
		final Person owner = pComment.getOwner();
		if (owner != null) {
			map.put("ownerName", HtmlUtils.escapeHtmlAndReplaceCr(owner.getDisplayName()));		
			map.put("ownerUrl", JsfUtils.getFullUrlWithPrefix(pRequest.getContextPath(),
				                                          PagesURL.PERSON_OVERVIEW,
				                                          PagesURL.PERSON_OVERVIEW_PARAM_PERSON_ID,
				                                          pComment.getOwner().getId().toString()));
		}
		map.put("dateAdded", getDateAsString(pComment.getCreationDate()));
		final Long currentPersonId = PersonUtils.getCurrentPersonId(pRequest.getSession());
		final Boolean canEdit = currentPersonId != null &&
								! (pComment instanceof SystemComment) &&
								PersonUtils.getCurrentPersonId(pRequest.getSession()).equals(owner.getId());
		map.put("canEdit", canEdit);
		map.put("systemComment", pComment instanceof SystemComment);
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
				
				commentService.updateCommentWithAC(commentID, text, currentPersonId);
				
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
		try {
			final String text = pRequest.getParameter(TEXT_PARAMETER_NAME);
			if (!isValidComment(text)) {
				map.put("errorMessage", getCommentValidationErrorMessage());
			}
			else {
				// Need to pass the session since not in a faces context.
				final Long currentPersonId = PersonUtils.getCurrentPersonId(pRequest.getSession());
				
				Long commentID = null;
				
				final String itemID = pRequest.getParameter(CONTAINER_ITEM_ID_PARAMETER_NAME);
				final String needID = pRequest.getParameter(CONTAINER_NEED_ID_PARAMETER_NAME);
				final String lendTransactionID = pRequest.getParameter(CONTAINER_LEND_TRANSACTION_ID_PARAMETER_NAME);
				final String groupID = pRequest.getParameter(CONTAINER_GROUP_ID_PARAMETER_NAME);
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
	 * Returns true if the comment is valid.
	 *
	 * @param pComment
	 * @return
	 */
	private boolean isValidComment(final String pComment) {
		return pComment != null &&
			!pComment.equals(getAddCommentDefaultText()) &&
			pComment.length() <= CommentConsts.COMMENT_MAX_LENGTH;
	}

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
		return HtmlUtils.escapeHtmlAndReplaceCr(
				I18nUtils.getMessageResourceString("comment_addComment", locale));
	}
}
