package com.pferrot.lendity.comment;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;

import com.pferrot.core.CoreUtils;
import com.pferrot.emailsender.manager.MailManager;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.comment.exception.CommentException;
import com.pferrot.lendity.configuration.Configuration;
import com.pferrot.lendity.dao.CommentDao;
import com.pferrot.lendity.dao.PersonDao;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.group.GroupService;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.item.ItemService;
import com.pferrot.lendity.lendtransaction.LendTransactionService;
import com.pferrot.lendity.model.ChildComment;
import com.pferrot.lendity.model.Comment;
import com.pferrot.lendity.model.Commentable;
import com.pferrot.lendity.model.CommentableWithOwner;
import com.pferrot.lendity.model.Group;
import com.pferrot.lendity.model.GroupComment;
import com.pferrot.lendity.model.Item;
import com.pferrot.lendity.model.ItemComment;
import com.pferrot.lendity.model.LendTransaction;
import com.pferrot.lendity.model.LendTransactionComment;
import com.pferrot.lendity.model.LendTransactionSystemComment;
import com.pferrot.lendity.model.Need;
import com.pferrot.lendity.model.NeedComment;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.model.SystemComment;
import com.pferrot.lendity.model.WallComment;
import com.pferrot.lendity.model.WallCommentsAddPermission;
import com.pferrot.lendity.model.WallCommentsVisibility;
import com.pferrot.lendity.need.NeedService;
import com.pferrot.lendity.person.PersonService;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.HtmlUtils;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.security.SecurityUtils;

public class CommentService {

	private final static Log log = LogFactory.getLog(CommentService.class);
	
	private CommentDao commentDao;
	private ItemService itemService;
	private NeedService needService;
	private LendTransactionService lendTransactionService;
	private GroupService groupService;
	private PersonDao personDao;
	private MailManager mailManager;
	private PersonService personService;

	public void setMailManager(final MailManager pMailManager) {
		this.mailManager = pMailManager;
	}
	
	public MailManager getMailManager() {
		return mailManager;
	}

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}
	
	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}

	public void setNeedService(NeedService needService) {
		this.needService = needService;
	}

	public void setLendTransactionService(
			LendTransactionService lendTransactionService) {
		this.lendTransactionService = lendTransactionService;
	}

	
	public void setGroupService(GroupService groupService) {
		this.groupService = groupService;
	}

	public void setCommentDao(final CommentDao commentDao) {
		this.commentDao = commentDao;
	}
	
	public void setPersonDao(final PersonDao pPersonDao) {
		this.personDao = pPersonDao;
	}
	
	public Comment findComment(final Long pCommentId) {
		return commentDao.findComment(pCommentId);
	}
	
	public WallComment findWallComment(final Long pWallCommentId) {
		return commentDao.findWallComment(pWallCommentId);
	}
	
	public ChildComment findChildComment(final Long pChildCommentId) {
		return commentDao.findChildComment(pChildCommentId);
	}
	
	/**
	 * Returns a comment given its ID plus validates if the specified person is
	 * allowed to read that comment.
	 * 
	 * @param pCommentId
	 * @param pCurrentPersonId
	 * @return
	 */
	public Comment findCommentWithAC(final Long pCommentId, final Long pCurrentPersonId) {
		Person currentPerson = null;
		if (pCurrentPersonId != null) {
			currentPerson = personService.findPerson(pCurrentPersonId);
		}
		final Comment comment = commentDao.findComment(pCommentId);
		assertUserAuthorizedToView(currentPerson, comment);
		return comment;
	}
	
	/**
	 * Returns comments but validates that the specified user is authorized to view those
	 * comments.
	 * 
	 * @param pItemId
	 * @param pCurrentPersonId
	 * @param pFirstResult
	 * @param pMaxResults
	 * @return
	 */
	public ListWithRowCount findItemCommentsWithAC(final Long pItemId, final Long pCurrentPersonId, final int pFirstResult, final int pMaxResults) {
		final Item item = itemService.findItem(pItemId);
		Person currentPerson = null;
		if (pCurrentPersonId != null) {
			currentPerson = personService.findPerson(pCurrentPersonId);
		}
		itemService.assertUserAuthorizedToView(currentPerson, item);		
		return commentDao.findItemComments(item, pFirstResult, pMaxResults);
	}
	
	public ListWithRowCount findItemComments(final Long pItemId, final int pFirstResult, final int pMaxResults) {
		final Item item = itemService.findItem(pItemId);
		return commentDao.findItemComments(item, pFirstResult, pMaxResults);
	}
	
	/**
	 * Replaces {iXYZ}, {nXYZ}, {gXYZ}, {pXYZ} strings with labels and href links to the
	 * corresponding objects.
	 * 
	 * @param pText
	 * @param pPerson
	 * @return
	 */
	public String processAllHrefWithPerson(final String pText, final Person pPerson) {
		String result = itemService.processItemHref(pText, pPerson);
		result = needService.processNeedHref(result, pPerson);
		result = groupService.processGroupHref(result, pPerson);
		result = personService.processPersonHref(result, pPerson);
		
		return result;
	}
	
	/**
	 * Replaces {iXYZ}, {nXYZ}, {gXYZ}, {pXYZ} strings with labels but NO href links.
	 * 
	 * @param pText
	 * @param pPerson
	 * @return
	 */
	public String processAllNoHrefWithPerson(final String pText, final Person pPerson) {
		String result = itemService.processItemNoHref(pText, pPerson);
		result = needService.processNeedNoHref(result, pPerson);
		result = groupService.processGroupNoHref(result, pPerson);
		result = personService.processPersonNoHref(result, pPerson);
		
		return result;
	}
	
	/**
	 * Returns comments but validates that the specified user is authorized to view those
	 * comments.
	 * 
	 * @param pNeedId
	 * @param pCurrentPersonId
	 * @param pFirstResult
	 * @param pMaxResults
	 * @return
	 */
	public ListWithRowCount findNeedCommentsWithAC(final Long pNeedId, final Long pCurrentPersonId, final int pFirstResult, final int pMaxResults) {
		final Need need = needService.findNeed(pNeedId);
		Person currentPerson = null;
		if (pCurrentPersonId != null) {
			currentPerson = personService.findPerson(pCurrentPersonId);
		}
		needService.assertUserAuthorizedToView(currentPerson, need);		
		return commentDao.findNeedComments(need, pFirstResult, pMaxResults);
	}
	
	public ListWithRowCount findNeedComments(final Long pNeedId, final int pFirstResult, final int pMaxResults) {
		final Need need = needService.findNeed(pNeedId);
		return commentDao.findNeedComments(need, pFirstResult, pMaxResults);
	}

	/**
	 * Returns comments but validates that the specified user is authorized to view those
	 * comments.
	 * 
	 * @param pLendTransactionId
	 * @param pCurrentPersonId
	 * @param pFirstResult
	 * @param pMaxResults
	 * @return
	 */
	public ListWithRowCount findLendTransactionCommentsWithAC(final Long pLendTransactionId, final Long pCurrentPersonId, final int pFirstResult, final int pMaxResults) {
		final LendTransaction lendTransaction = lendTransactionService.findLendTransaction(pLendTransactionId);
		Person currentPerson = null;
		if (pCurrentPersonId != null) {
			currentPerson = personService.findPerson(pCurrentPersonId);
		}
		lendTransactionService.assertUserAuthorizedToView(currentPerson, lendTransaction);		
		return commentDao.findLendTransactionComments(lendTransaction, pFirstResult, pMaxResults);
	}
	
	public ListWithRowCount findLendTransactionComments(final Long pLendTransactionId, final int pFirstResult, final int pMaxResults) {
		final LendTransaction lendTransaction = lendTransactionService.findLendTransaction(pLendTransactionId);
		return commentDao.findLendTransactionComments(lendTransaction, pFirstResult, pMaxResults);
	}

	/**
	 * Returns comments but validates that the specified user is authorized to view those
	 * comments.
	 * 
	 * @param pGroupId
	 * @param pCurrentPersonId
	 * @param pFirstResult
	 * @param pMaxResults
	 * @return
	 */
	public ListWithRowCount findGroupCommentsWithAC(final Long pGroupId, final Long pCurrentPersonId, final int pFirstResult, final int pMaxResults) {
		final Group group = groupService.findGroup(pGroupId);
		Person currentPerson = null;
		if (pCurrentPersonId != null) {
			currentPerson = personService.findPerson(pCurrentPersonId);
		}
		groupService.assertUserAuthorizedToView(currentPerson, group);		
		return commentDao.findGroupComments(group, pFirstResult, pMaxResults);
	}
	
	public ListWithRowCount findGroupComments(final Long pGroupId, final int pFirstResult, final int pMaxResults) {
		final Group group = groupService.findGroup(pGroupId);
		return commentDao.findGroupComments(group, pFirstResult, pMaxResults);
	}
	
	public ListWithRowCount findOwnWallCommentsForPerson(final Long pPersonId, final int pFirstResult, final int pMaxResults) {
		if (!SecurityUtils.isLoggedIn()) {
			throw new SecurityException("Not logged in");
		}
		final Person person = personDao.findPerson(pPersonId);
		final Long[] connectionsIds = personService.getPersonConnectionIds(person, null);
		return commentDao.findOwnWallComments(pPersonId, connectionsIds, Boolean.TRUE, pFirstResult, pMaxResults);
	}
	
	public ListWithRowCount findOtherWallCommentsForPerson(final Long pVisitorId, final Long pWallOwnerId, final int pFirstResult, final int pMaxResults) {
		final Person wallOwner = personDao.findPerson(pWallOwnerId);
		final WallCommentsVisibility visibility = wallOwner.getWallCommentsVisibility();
		final String visibilityLabelCode = visibility.getLabelCode();
		Boolean includeWallOwnerPrivateComments = Boolean.FALSE;
		if (SecurityUtils.isLoggedIn() &&
			personService.isConnection(pVisitorId, pWallOwnerId)) {
			includeWallOwnerPrivateComments = Boolean.TRUE;
		}
		
		if (WallCommentsVisibility.PUBLIC.equals(visibilityLabelCode)) {
			return commentDao.findOtherWallComments(pWallOwnerId, pVisitorId, includeWallOwnerPrivateComments, Boolean.TRUE, pFirstResult, pMaxResults);
			
		}
		else if (WallCommentsVisibility.CONNECTIONS.equals(visibilityLabelCode)) {
			// Not logged in or not a connection
			if (pVisitorId == null ||
				!personService.isConnection(pVisitorId, pWallOwnerId)) {
				return commentDao.findOtherWallComments(pWallOwnerId, pVisitorId, includeWallOwnerPrivateComments, Boolean.FALSE, pFirstResult, pMaxResults); 
			}
			// Connection.
			else {
				return commentDao.findOtherWallComments(pWallOwnerId, pVisitorId, includeWallOwnerPrivateComments, Boolean.TRUE, pFirstResult, pMaxResults); 
			}
		}
		// Private.
		else {
			return commentDao.findOtherWallComments(pWallOwnerId, pVisitorId, includeWallOwnerPrivateComments, Boolean.FALSE, pFirstResult, pMaxResults);			
		}
	}
	
	/**
	 * Creates the comment and sends notifications to others who commented and container owner.
	 * Also, access control is verified.
	 * 
	 * @param pText
	 * @param pItemId
	 * @param pCommentOwnerId
	 * @return
	 * @throws CommentException
	 */
	public Long createCommentOnItemWithAC(final String pText, final Long pItemId, final Long pCommentOwnerId) throws CommentException {
		final Item item = itemService.findItem(pItemId);
		
		final Person commentOwner = personService.findPerson(pCommentOwnerId); 
		assertUserAuthorizedToAddCommentOnItem(commentOwner, item);
		
		final ItemComment itemComment = new ItemComment();
		itemComment.setCreationDate(new Date());
		itemComment.setItem(item);
		itemComment.setOwner(commentOwner);
		itemComment.setText(pText);
		itemComment.setAdminComment(Boolean.FALSE);
		itemComment.setPublicComment(Boolean.FALSE);
		
		Long commentID = commentDao.createComment(itemComment);
		
		item.addComment(itemComment);
		item.addCommentRecipient(itemComment.getOwner());
		itemService.updateItem(item);
		
		sendCommentAddedNotificationToAll(itemComment);
		
		return commentID;
	}
	
	/**
	 * Creates the comment and sends notifications to others who commented and container owner.
	 * Also, access control is verified.
	 * 
	 * @param pText
	 * @param pNeedId
	 * @param pCommentOwnerId
	 * @return
	 * @throws CommentException
	 */
	public Long createCommentOnNeedWithAC(final String pText, final Long pNeedId, final Long pCommentOwnerId) throws CommentException {
		final Need need = needService.findNeed(pNeedId);
		
		final Person commentOwner = personService.findPerson(pCommentOwnerId); 
		assertUserAuthorizedToAddCommentOnNeed(commentOwner, need);
		
		final NeedComment needComment = new NeedComment();
		needComment.setCreationDate(new Date());
		needComment.setNeed(need);
		needComment.setOwner(commentOwner);
		needComment.setText(pText);
		needComment.setAdminComment(Boolean.FALSE);
		needComment.setPublicComment(Boolean.FALSE);
		
		Long commentID = commentDao.createComment(needComment);
		
		need.addComment(needComment);
		need.addCommentRecipient(needComment.getOwner());
		needService.updateNeed(need);
		
		sendCommentAddedNotificationToAll(needComment);
		
		return commentID;
	}

	/**
	 * Creates the comment and sends notifications to others who commented and container owner.
	 * Also, access control is verified.
	 * 
	 * @param pText
	 * @param pLendTransactionId
	 * @param pCommentOwnerId
	 * @return
	 * @throws CommentException
	 */
	public Long createCommentOnLendTransactionWithAC(final String pText, final Long pLendTransactionId, final Long pCommentOwnerId) throws CommentException {
		final LendTransaction lendTransaction = lendTransactionService.findLendTransaction(pLendTransactionId);
		
		final Person commentOwner = personService.findPerson(pCommentOwnerId); 
		assertUserAuthorizedToAddCommentOnLendTransaction(commentOwner, lendTransaction);
		
		final LendTransactionComment lendTransactionComment = new LendTransactionComment();
		lendTransactionComment.setCreationDate(new Date());
		lendTransactionComment.setLendTransaction(lendTransaction);
		lendTransactionComment.setOwner(commentOwner);
		lendTransactionComment.setText(pText);
		lendTransactionComment.setAdminComment(Boolean.FALSE);
		lendTransactionComment.setPublicComment(Boolean.FALSE);
		
		Long commentID = commentDao.createComment(lendTransactionComment);
		
		lendTransaction.addComment(lendTransactionComment);
		// Not needed, the comment recipients are added when the lend transaction is created.
		//item.addCommentRecipient(lendTransactionComment.getOwner());
		lendTransactionService.updateLendTransaction(lendTransaction);
		
		sendCommentAddedNotificationToAll(lendTransactionComment);
		
		return commentID;
	}

	
	public Long createSystemCommentOnLendTransactionWithAC(final String pText, final Long pLendTransactionId, final Long pOwnerId, boolean pSendEmail) throws CommentException {
		final LendTransaction lendTransaction = lendTransactionService.findLendTransaction(pLendTransactionId);
		final Person owner = personService.findPerson(pOwnerId);
		
		final LendTransactionSystemComment lendTransactionSystemComment = new LendTransactionSystemComment();
		lendTransactionSystemComment.setCreationDate(new Date());
		lendTransactionSystemComment.setLendTransaction(lendTransaction);
		lendTransactionSystemComment.setText(pText);
		lendTransactionSystemComment.setOwner(owner);
		lendTransactionSystemComment.setAdminComment(Boolean.FALSE);
		lendTransactionSystemComment.setPublicComment(Boolean.FALSE);
		
		Long commentID = commentDao.createComment(lendTransactionSystemComment);
		
		lendTransaction.addComment(lendTransactionSystemComment);
		// Not needed, the comment recipients are added when the lend transaction is created.
		//item.addCommentRecipient(lendTransactionComment.getOwner());
		lendTransactionService.updateLendTransaction(lendTransaction);
		
		if (pSendEmail) {
			sendCommentAddedNotificationToAll(lendTransactionSystemComment);
		}
		
		return commentID;
	}

	/**
	 * Creates the comment and sends notifications to others who commented and container owner.
	 * Also, access control is verified.
	 * 
	 * @param pText
	 * @param pLendTransactionId
	 * @param pCommentOwnerId
	 * @return
	 * @throws CommentException
	 */
	public Long createCommentOnGroupWithAC(final String pText, final Long pGroupId, final Long pCommentOwnerId) throws CommentException {
		final Group group = groupService.findGroup(pGroupId);
		
		final Person commentOwner = personService.findPerson(pCommentOwnerId); 
		assertUserAuthorizedToAddCommentOnGroup(commentOwner, group);
		
		final GroupComment groupComment = new GroupComment();
		groupComment.setCreationDate(new Date());
		groupComment.setGroup(group);
		groupComment.setOwner(commentOwner);
		groupComment.setText(pText);
		groupComment.setAdminComment(Boolean.FALSE);
		groupComment.setPublicComment(Boolean.FALSE);
		
		Long commentID = commentDao.createComment(groupComment);
		
		group.addComment(groupComment);
		group.addCommentRecipient(groupComment.getOwner());
		groupService.updateGroup(group);
		
		sendCommentAddedOnGroupNotificationToAll(groupComment);
		
		return commentID;
	}
	
	public Long createCommentOnOwnWallWithAC(final String pText, final Boolean pPublicComment, final Long pCommentOwnerId) throws CommentException {		
		final Person commentOwner = personService.findPerson(pCommentOwnerId);
		
		final WallComment wallComment = new WallComment();
		wallComment.setCreationDate(new Date());
		wallComment.setOwner(commentOwner);
		wallComment.setText(pText);
		wallComment.setAdminComment(Boolean.FALSE);
		wallComment.setPublicComment(pPublicComment);
		wallComment.setPrivateComment(Boolean.FALSE);
		
		Long commentID = commentDao.createComment(wallComment);
		
		return commentID;
	}

	public Long createCommentOnOtherWallWithAC(final String pText, final Long pCommentOwnerId, final Long pWallOwnerId, final Boolean pPrivateComment) throws CommentException {		
		final Person commentOwner = personService.findPerson(pCommentOwnerId);
		final Person wallOwner = personService.findPerson(pWallOwnerId);
		
		assertUserAuthorizedToAddCommentOnWall(commentOwner, wallOwner);
		
		final WallComment wallComment = new WallComment();
		wallComment.setCreationDate(new Date());
		wallComment.setOwner(commentOwner);
		wallComment.setWallOwner(wallOwner);
		wallComment.setText(pText);
		wallComment.setAdminComment(Boolean.FALSE);
		wallComment.setPublicComment(Boolean.FALSE);
		wallComment.setPrivateComment(pPrivateComment);
		
		if (Boolean.TRUE.equals(wallOwner.getReceiveCommentsOnWallNotif())) {
			sendCommentAddedNotificationToOnePerson(wallComment, wallOwner);
		}
		
		Long commentID = commentDao.createComment(wallComment);
		
		return commentID;
	}

	public Long createChildCommentWithAC(final String pText, final Long pParentCommentId, final Long pCommentOwnerId) throws CommentException {
		final Person commentOwner = personService.findPerson(pCommentOwnerId);
		
		final Comment parentComment = commentDao.findComment(pParentCommentId);
		
		assertUserAuthorizedToAddChildComment(commentOwner, parentComment);
		
		final ChildComment childComment = new ChildComment();
		
		childComment.setCreationDate(new Date());
		childComment.setOwner(commentOwner);
		childComment.setText(pText);
		childComment.setParentComment(parentComment);
		childComment.setAdminComment(Boolean.FALSE);
		childComment.setPublicComment(Boolean.FALSE);
		
		Long commentID = commentDao.createComment(childComment);
		
		sendChildCommentAddedNotificationToAll(childComment);
		
		return commentID;
	}
	
	public void deleteComment(final Long pCommentId) {		
		final Comment comment = commentDao.findComment(pCommentId);
		removeCommentRecipientFromContainerIfNeeded(comment);
		commentDao.deleteComment(comment);
		
	}
	
	/**
	 * Deletes a comment but verifies that the specified user is allowed
	 * to execute that operation.
	 * 
	 * @param pCommentId
	 * @param pCurrentPersonId
	 */
	public void deleteCommentWithAC(final Long pCommentId, final Long pCurrentPersonId) {		
		final Comment comment = commentDao.findComment(pCommentId);
		final Person person = personService.findPerson(pCurrentPersonId);
		assertUserAuthorizedToDelete(person, comment);
		removeCommentRecipientFromContainerIfNeeded(comment);
		commentDao.deleteComment(comment);
	}
	
	public List<ChildComment> findChildCommentsByCreationDateAsc(final Comment pParentComment) {
		return commentDao.findChildCommentsList(pParentComment, 0, 0);
	}
	
	/**
	 * Remove the owner of the comment from the list of comment recipients in case the
	 * comment to be deleted is the only comment he made on that object.
	 *
	 * @param pComment
	 */
	private void removeCommentRecipientFromContainerIfNeeded(final Comment pComment) {
		if (Hibernate.getClass(pComment).isAssignableFrom(LendTransactionComment.class) ||
			Hibernate.getClass(pComment).isAssignableFrom(GroupComment.class) ||
			pComment == null || 
			pComment.getId() == null ||
			pComment.getOwner() == null ||
			pComment.getOwner().getId() == null ||
			pComment.getContainer() == null) {
			return;
		}
		final Long deletedCommentId = pComment.getId();
		final Long deletedCommentOwnerId = pComment.getOwner().getId();
		
		final Commentable commentable = pComment.getContainer();
		final Set comments = commentable.getComments();
		final Iterator ite = comments.iterator();
		
		while (ite.hasNext()) {
			final Comment comment = (Comment)ite.next();
			if (!isRecipientToBeRemoved(comment, deletedCommentId, deletedCommentOwnerId)) {
				return;
			}
			if (comment.getChildComments() != null) {
				for (ChildComment childComment: comment.getChildComments()) {
					if (!isRecipientToBeRemoved(childComment, deletedCommentId, deletedCommentOwnerId)) {
						return;
					}
				}
			}			
		}
		
		// User was only owner of that comment, so he should 
		// not be notified anymore in the future.
		commentable.removeCommentRecipient(pComment.getOwner());
		if (commentable instanceof Item) {
			itemService.updateItem((Item)commentable);
		}
		else if (commentable instanceof Need) {
			needService.updateNeed((Need)commentable);
		}
		else {
			throw new RuntimeException("Unhandled commentable type: " + commentable.getClass());
		}
	}
	
	private boolean isRecipientToBeRemoved(final Comment pComment, final Long pDeletedCommentId, final Long pDeletedCommentOwnerId) {
		if (pComment != null &&
			pComment.getId() != null &&
			pComment.getOwner() != null &&
			pComment.getOwner().getId() != null) {
			final Long oneCommentId = pComment.getId();
			final Long oneCommentOwnerId = pComment.getOwner().getId();
			// Not the comment to delete.
			if (!oneCommentId.equals(pDeletedCommentId)) {
				// Same owner, so he should still be in the people to notify list.
				if (oneCommentOwnerId.equals(pDeletedCommentOwnerId)) {
					return false;
				}
			}
		}
		return true;
	}
	
	public void updateComment(final Long pCommentId, final String pNewText) {		
		final Comment comment = commentDao.findComment(pCommentId);		
		
		comment.setModificationDate(new Date());
		comment.setText(pNewText);
		
		commentDao.updateComment(comment);
	}
	
	/**
	 * Updates the comment and verifies that the user is allowed to.
	 * 
	 * @param pCommentId
	 * @param pNewText
	 * @param pCurrentPersonId
	 * @throws CommentException 
	 */
	public void updateCommentWithAC(final Long pCommentId, final String pNewText, final Boolean pPublicComment, final Long pCurrentPersonId) throws CommentException {		
		final Comment comment = commentDao.findComment(pCommentId);
		if (comment instanceof SystemComment) {
			throw new CommentException("System comment cannot be edited");
		}
		
		final Person person = personService.findPerson(pCurrentPersonId);
		
		assertUserAuthorizedToEdit(person, comment);		
		
		comment.setModificationDate(new Date());
		comment.setText(pNewText);
		comment.setPublicComment(pPublicComment);
		
		commentDao.updateComment(comment);
	}
	
	/**
	 * Send the notification to all recipients + the owner of the container (if they subscribe
	 * to notifications of course).
	 * The owner of the comment does not get a notif for his own comment.
	 *  
	 * @param pComment
	 * @throws CommentException
	 */
	private void sendCommentAddedNotificationToAll(final Comment pComment) throws CommentException {
		final Commentable commentable = pComment.getContainer();
		boolean emailSentToContainerOwner = false;
		
		// Send to owner.
		Person containerOwner = null;		
		if (commentable instanceof CommentableWithOwner) {
			containerOwner = ((CommentableWithOwner)commentable).getOwner(); 
		}		
		
		// Send to container owner.
		if (containerOwner != null &&
			containerOwner.isEnabled() &&
			containerOwner.getReceiveCommentsOnOwnNotif() &&
			!containerOwner.getId().equals(pComment.getOwner().getId())) {
			sendCommentAddedNotificationToOnePerson(pComment, containerOwner);
			emailSentToContainerOwner = true;
		}
		
		// Send to recipients.
		final Iterator ite = commentable.getCommentsRecipients().iterator();
		while (ite.hasNext()) {
			Person recipient = (Person)ite.next();
			// NOT the owner of the comment
			//   AND
			// NOT (owner of the container AND email already sent to owner of the container)
			if (!recipient.getId().equals(pComment.getOwner().getId()) && 
				!(containerOwner != null && recipient.getId().equals(containerOwner.getId()) && emailSentToContainerOwner)){
				if (recipient.isEnabled() &&
					recipient.getReceiveCommentsOnCommentedNotif() &&
					!recipient.getId().equals(pComment.getOwner().getId())) {
					sendCommentAddedNotificationToOnePerson(pComment, recipient);
				}
			}
		}		
	}
	
	/**
	 * Send notifications to owner, administrators and members (if they want to receive notifications).
	 *
	 * @param pComment
	 * @throws CommentException
	 */
	private void sendCommentAddedOnGroupNotificationToAll(final GroupComment pComment) throws CommentException {
		final Group group = pComment.getGroup();
		final Person owner = group.getOwner();
		
		if (owner != null &&
			owner.isEnabled() &&
			Boolean.TRUE.equals(owner.getReceiveCommentsOnGroupsAdminNotif()) &&
			!owner.getId().equals(pComment.getOwner().getId())) {
			sendCommentAddedNotificationToOnePerson(pComment, owner);
		}
		
		final Set<Person> admins = group.getAdministrators();
		if (admins != null) {
			for (Person admin: admins) {
				if (admin.isEnabled() &&
						Boolean.TRUE.equals(admin.getReceiveCommentsOnGroupsAdminNotif()) &&
						!admin.getId().equals(pComment.getOwner().getId())) {
					sendCommentAddedNotificationToOnePerson(pComment, admin);
				}
			}
		}
		
		final Set<Person> members = group.getMembers();
		if (members != null) {
			for (Person member: members) {
				if (member.isEnabled() &&
						Boolean.TRUE.equals(member.getReceiveCommentsOnGroupsMemberNotif()) &&
						!member.getId().equals(pComment.getOwner().getId())) {
					sendCommentAddedNotificationToOnePerson(pComment, member);
				}
			}
		}		
	}

	private void sendChildCommentAddedNotificationToAll(final ChildComment pChildComment) throws CommentException {
		
		final Set<Person> recipients = new HashSet<Person>();
		
		final Person childCommentOwner = pChildComment.getOwner();
		
		final Comment parentComment = pChildComment.getParentComment();
		final Person parentCommentOwner = parentComment.getOwner();
		if (!childCommentOwner.equals(parentCommentOwner) &&
			Boolean.TRUE.equals(parentCommentOwner.getReceiveCommentsRepliesNotif())) {
			recipients.add(parentComment.getOwner());
		}
		
		if (parentComment.getChildComments() != null) {
			for (ChildComment childComment: parentComment.getChildComments()) {
				final Person childCommentOwner2 = childComment.getOwner();
				if (!childCommentOwner.equals(childCommentOwner2) &&
					Boolean.TRUE.equals(childCommentOwner2.getReceiveCommentsRepliesNotif())) {
					recipients.add(childCommentOwner2);
				}	
			}
		}
		
		// Force sending notification to wall owner.
		if (Hibernate.getClass(parentComment).isAssignableFrom(WallComment.class)) {
			final WallComment wallComment = findWallComment(parentComment.getId());
			if (wallComment.getWallOwner() != null) {
				final Person wallOwner = wallComment.getWallOwner();
				if (!recipients.contains(wallOwner) &&
					Boolean.TRUE.equals(wallOwner.getReceiveCommentsOnWallNotif()) &&
					!wallOwner.equals(childCommentOwner)) {
					recipients.add(wallOwner);
				}
			}
		}
		
		for (Person recipient: recipients) {
			sendChildCommentAddedNotificationToOnePerson(pChildComment, recipient);
		}		
	}

	/**
	 * Send the notification to one particular person.
	 * 
	 * @param pComment
	 * @param pPerson
	 * @throws CommentException
	 */
	private void sendCommentAddedNotificationToOnePerson(final Comment pComment, final Person pPerson) throws CommentException {
		CoreUtils.assertNotNull(pComment);
		CoreUtils.assertNotNull(pPerson);
		try {	
			// Send email (will actually create a JMS message, i.e. it is async).
			Map<String, String> objects = new HashMap<String, String>();
			objects.put("firstName", pPerson.getFirstName());
			objects.put("displayName", pPerson.getDisplayName());
			objects.put("commenterDisplayName", pComment.getOwner().getDisplayName());		
			
			final Commentable commentable = pComment.getContainer();
			String velocityTemplateLocation = null;
			if (commentable instanceof Item) {
				final Item item = (Item)commentable;
				objects.put("objectTitle", item.getTitle());
				objects.put("objectUrl", JsfUtils.getFullUrlWithPrefix(Configuration.getRootURL(),
						PagesURL.ITEM_OVERVIEW,
						PagesURL.ITEM_OVERVIEW_PARAM_ITEM_ID,
						item.getId().toString()));
				velocityTemplateLocation = "com/pferrot/lendity/emailtemplate/comment/added/item/fr";
			}
			else if (commentable instanceof Need) {
				final Need need = (Need)commentable;
				objects.put("objectTitle", need.getTitle());
				objects.put("objectUrl", JsfUtils.getFullUrlWithPrefix(Configuration.getRootURL(),
						PagesURL.NEED_OVERVIEW,
						PagesURL.NEED_OVERVIEW_PARAM_NEED_ID,
						need.getId().toString()));
				velocityTemplateLocation = "com/pferrot/lendity/emailtemplate/comment/added/need/fr";
			}
			else if (commentable instanceof LendTransaction) {
				final LendTransaction lendTransaction = (LendTransaction)commentable;
				objects.put("objectTitle", lendTransaction.getTitle());
				objects.put("objectUrl", JsfUtils.getFullUrlWithPrefix(Configuration.getRootURL(),
						PagesURL.LEND_TRANSACTION_OVERVIEW,
						PagesURL.LEND_TRANSACTION_OVERVIEW_PARAM_LEND_TRANSACTION_ID,
						lendTransaction.getId().toString()));
				velocityTemplateLocation = "com/pferrot/lendity/emailtemplate/comment/added/lendtransaction/fr";
			}
			else if (commentable instanceof Group) {
				final Group group = (Group)commentable;
				objects.put("objectTitle", group.getTitle());
				objects.put("objectUrl", JsfUtils.getFullUrlWithPrefix(Configuration.getRootURL(),
						PagesURL.GROUP_OVERVIEW,
						PagesURL.GROUP_OVERVIEW_PARAM_GROUP_ID,
						group.getId().toString()));
				velocityTemplateLocation = "com/pferrot/lendity/emailtemplate/comment/added/group/fr";
			}
			// WallComment.
			else if (commentable == null) {
				objects.put("homeUrl", JsfUtils.getFullUrlWithPrefix(Configuration.getRootURL(), PagesURL.HOME));
				velocityTemplateLocation = "com/pferrot/lendity/emailtemplate/comment/added/wall/fr";
			}
			else {
				throw new CommentException("Unhandled commentabe type " + commentable.getClass());
			}
			
			objects.put("comment", processAllNoHrefWithPerson(pComment.getText(), pPerson));
			// For the HTML template to correctly display new lines.
			
			objects.put("commentEscaped", processAllHrefWithPerson(HtmlUtils.getTextWithHrefLinks(HtmlUtils.escapeHtmlAndReplaceCr(pComment.getText())), pPerson));
			objects.put("signature", Configuration.getSiteName());
			objects.put("siteName", Configuration.getSiteName());
			objects.put("siteUrl", Configuration.getRootURL());
			objects.put("profileUrl", JsfUtils.getFullUrlWithPrefix(Configuration.getRootURL(), PagesURL.MY_PROFILE));
			
			Map<String, String> to = new HashMap<String, String>();
			to.put(pPerson.getEmail(), pPerson.getEmail());
			
			Map<String, String> inlineResources = new HashMap<String, String>();
			inlineResources.put("logo", "com/pferrot/lendity/emailtemplate/lendity_logo.png");
			
			getMailManager().send(Configuration.getNoReplySenderName(), 
					         Configuration.getNoReplyEmailAddress(),
					         to,
					         null, 
					         null,
					         Configuration.getSiteName() + ": commentaire ajouté",
					         objects, 
					         velocityTemplateLocation,
					         inlineResources);		
		} 
		catch (CommentException ce) {
			throw ce;
		}
		catch (Exception e) {
			throw new CommentException(e);
		}
		
	}

	private void sendChildCommentAddedNotificationToOnePerson(final ChildComment pChildComment, final Person pPerson) throws CommentException {
		CoreUtils.assertNotNull(pChildComment);
		CoreUtils.assertNotNull(pPerson);
		try {	
			final Comment parentComment = pChildComment.getParentComment();
			final String velocityTemplateLocation = "com/pferrot/lendity/emailtemplate/comment/reply/fr";
			
			// Send email (will actually create a JMS message, i.e. it is async).
			Map<String, String> objects = new HashMap<String, String>();
			objects.put("firstName", pPerson.getFirstName());
			objects.put("displayName", pPerson.getDisplayName());
			objects.put("commenterDisplayName", pChildComment.getOwner().getDisplayName());			
			objects.put("commentUrl", JsfUtils.getFullUrlWithPrefix(Configuration.getRootURL(),
					PagesURL.COMMENT_OVERVIEW,
					PagesURL.COMMENT_OVERVIEW_PARAM_COMMENT_ID,
					parentComment.getId().toString()));			
			objects.put("comment", processAllNoHrefWithPerson(pChildComment.getText(), pPerson));
			// For the HTML template to correctly display new lines.
			objects.put("commentEscaped", processAllHrefWithPerson(HtmlUtils.getTextWithHrefLinks(HtmlUtils.escapeHtmlAndReplaceCr(pChildComment.getText())), pPerson));
			objects.put("signature", Configuration.getSiteName());
			objects.put("siteName", Configuration.getSiteName());
			objects.put("siteUrl", Configuration.getRootURL());
			objects.put("profileUrl", JsfUtils.getFullUrlWithPrefix(Configuration.getRootURL(), PagesURL.MY_PROFILE));
			
			Map<String, String> to = new HashMap<String, String>();
			to.put(pPerson.getEmail(), pPerson.getEmail());
			
			Map<String, String> inlineResources = new HashMap<String, String>();
			inlineResources.put("logo", "com/pferrot/lendity/emailtemplate/lendity_logo.png");
			
			getMailManager().send(Configuration.getNoReplySenderName(), 
					         Configuration.getNoReplyEmailAddress(),
					         to,
					         null, 
					         null,
					         Configuration.getSiteName() + ": réponse à un commentaire",
					         objects, 
					         velocityTemplateLocation,
					         inlineResources);		
		} 
		catch (Exception e) {
			throw new CommentException(e);
		}
		
	}

	private Person getCurrentPerson() {
		return personDao.findPerson(PersonUtils.getCurrentPersonId());
	}

	public String getCommentContainerTitle(final Comment pComment) throws CommentException {
		CoreUtils.assertNotNull(pComment);
		if (Hibernate.getClass(pComment).isAssignableFrom(ItemComment.class)) {
			final ItemComment itemComment = commentDao.findItemComment(pComment.getId());
			final Item item = itemComment.getItem();
			return item.getTitle();
		}
		else if (Hibernate.getClass(pComment).isAssignableFrom(NeedComment.class)) {
			final NeedComment needComment = commentDao.findNeedComment(pComment.getId());
			final Need need = needComment.getNeed();
			return need.getTitle();
		}
		else if (Hibernate.getClass(pComment).isAssignableFrom(LendTransactionComment.class)) {
			final LendTransactionComment lendTransactionComment = commentDao.findLendTransactionComment(pComment.getId());
			final LendTransaction lendTransaction = lendTransactionComment.getLendTransaction();
			return lendTransaction.getTitle();
		}
		else if (Hibernate.getClass(pComment).isAssignableFrom(GroupComment.class)) {
			final GroupComment groupComment = commentDao.findGroupComment(pComment.getId());
			final Group group = groupComment.getGroup();
			return group.getTitle();
		}
		else if (Hibernate.getClass(pComment).isAssignableFrom(WallComment.class)) {
			final Person commentOwner = pComment.getOwner();
			final Locale locale = I18nUtils.getDefaultLocale();
			return I18nUtils.getMessageResourceString("menu_home", locale);
		}
		else if (Hibernate.getClass(pComment).isAssignableFrom(ChildComment.class)) {
			throw new CommentException("Not supported for child comments.");
		}
		else {
			throw new SecurityException("Unknown comment type: " + pComment.getClass().getName());
		}
	}
	
	public String getCommentContainerUrl(final Comment pComment) throws CommentException {
		CoreUtils.assertNotNull(pComment);
		if (Hibernate.getClass(pComment).isAssignableFrom(ItemComment.class)) {
			final ItemComment itemComment = commentDao.findItemComment(pComment.getId());
			final Item item = itemComment.getItem();
			return JsfUtils.getFullUrl(PagesURL.ITEM_OVERVIEW, PagesURL.ITEM_OVERVIEW_PARAM_ITEM_ID, item.getId().toString());
		}
		else if (Hibernate.getClass(pComment).isAssignableFrom(NeedComment.class)) {
			final NeedComment needComment = commentDao.findNeedComment(pComment.getId());
			final Need need = needComment.getNeed();
			return JsfUtils.getFullUrl(PagesURL.NEED_OVERVIEW, PagesURL.NEED_OVERVIEW_PARAM_NEED_ID, need.getId().toString());
		}
		else if (Hibernate.getClass(pComment).isAssignableFrom(LendTransactionComment.class)) {
			final LendTransactionComment lendTransactionComment = commentDao.findLendTransactionComment(pComment.getId());
			final LendTransaction lendTransaction = lendTransactionComment.getLendTransaction();
			return JsfUtils.getFullUrl(PagesURL.LEND_TRANSACTION_OVERVIEW, PagesURL.LEND_TRANSACTION_OVERVIEW_PARAM_LEND_TRANSACTION_ID, lendTransaction.getId().toString());
		}
		else if (Hibernate.getClass(pComment).isAssignableFrom(GroupComment.class)) {
			final GroupComment groupComment = commentDao.findGroupComment(pComment.getId());
			final Group group = groupComment.getGroup();
			return JsfUtils.getFullUrl(PagesURL.GROUP_OVERVIEW, PagesURL.GROUP_OVERVIEW_PARAM_GROUP_ID, group.getId().toString());
		}
		else if (Hibernate.getClass(pComment).isAssignableFrom(WallComment.class)) {
			return JsfUtils.getFullUrl(PagesURL.HOME);
		}
		else if (Hibernate.getClass(pComment).isAssignableFrom(ChildComment.class)) {
			throw new CommentException("Not supported for child comments.");
		}
		else {
			throw new SecurityException("Unknown comment type: " + pComment.getClass().getName());
		}
	}
	
    /////////////////////////////////////////////////////////
	// Access control - start
	
	public boolean isCurrentUserAuthorizedToView(final Comment pComment) {
		return isUserAuthorizedToView(personService.getCurrentPerson(), pComment);
	}
	
	public boolean isUserAuthorizedToView(final Person pPerson, final Comment pComment) {
		CoreUtils.assertNotNull(pComment);
		if (pPerson == null) {
			return false;
		}
		else if (isUserAuthorizedToEdit(pPerson, pComment)) {
			return true;
		}
		else if (Boolean.TRUE.equals(pComment.getPublicComment())) {
			return true;
		}
		if (Hibernate.getClass(pComment).isAssignableFrom(ItemComment.class)) {
			final ItemComment itemComment = commentDao.findItemComment(pComment.getId());
			final Item item = itemComment.getItem();
			return itemService.isUserAuthorizedToView(pPerson, item);
		}
		else if (Hibernate.getClass(pComment).isAssignableFrom(NeedComment.class)) {
			final NeedComment needComment = commentDao.findNeedComment(pComment.getId());
			final Need need = needComment.getNeed();
			return needService.isUserAuthorizedToView(pPerson, need);
		}
		else if (Hibernate.getClass(pComment).isAssignableFrom(LendTransactionComment.class)) {
			final LendTransactionComment lendTransactionComment = commentDao.findLendTransactionComment(pComment.getId());
			final LendTransaction lendTransaction = lendTransactionComment.getLendTransaction();
			return lendTransactionService.isUserAuthorizedToView(pPerson, lendTransaction);
		}
		else if (Hibernate.getClass(pComment).isAssignableFrom(GroupComment.class)) {
			final GroupComment groupComment = commentDao.findGroupComment(pComment.getId());
			final Group group = groupComment.getGroup();
			return groupService.isUserAuthorizedToViewComments(pPerson, group);
		}
		else if (Hibernate.getClass(pComment).isAssignableFrom(WallComment.class)) {
			final Person commentOwner = pComment.getOwner();
			if (pPerson.equals(commentOwner)) {
				return true;
			}
			final WallComment wallComment = commentDao.findWallComment(pComment.getId());
			final Person wallOwner = wallComment.getWallOwner();
			if (pPerson.equals(wallOwner)) {
				return true;
			}
			if (wallOwner != null) {
				if (Boolean.TRUE.equals(wallComment.getPrivateComment())) {
					return false;
				}
				else {
					final String wallCommentVisibility = wallOwner.getWallCommentsVisibility().getLabelCode();
					if (WallCommentsVisibility.PUBLIC.equals(wallCommentVisibility)) {
						return true;
					}
					else if (WallCommentsVisibility.CONNECTIONS.equals(wallCommentVisibility)) {
						return personService.isConnection(wallOwner, pPerson);
					}
					// Private visibility.
					else {
						return false;
					}
					
				}
			}
			else {
				if (Boolean.TRUE.equals(wallComment.getPrivateComment())) {
					return false;
				}
				else if (Boolean.TRUE.equals(wallComment.getPublicComment())) {
					return true;
				}
				else {
					return personService.isConnection(commentOwner, pPerson);
				}
				
			}
		}
		else if (Hibernate.getClass(pComment).isAssignableFrom(ChildComment.class)) {
			final ChildComment childComment = commentDao.findChildComment(pComment.getId());
			return isUserAuthorizedToView(pPerson, childComment.getParentComment());
		}
		else {
			throw new SecurityException("Unknown comment type: " + pComment.getClass().getName());
		}
	}
	
	public void assertCurrentUserAuthorizedToView(final Comment pComment) {
		if (!isCurrentUserAuthorizedToView(pComment)) {
			throw new SecurityException("Current user is not authorized to view comment");
		}
	}
	
	public void assertUserAuthorizedToView(final Person pPerson, final Comment pComment) {
		if (!isUserAuthorizedToView(pPerson, pComment)) {
			throw new SecurityException("User is not authorized to view comment");
		}
	}

	public boolean isCurrentUserAuthorizedToEdit(final Comment pComment) {
		return isUserAuthorizedToEdit(getCurrentPerson(), pComment);
	}

	public boolean isUserAuthorizedToEdit(final Person pPerson, final Comment pComment) {
		CoreUtils.assertNotNull(pComment);
		if (pPerson == null) {
			return false;
		}
		if (pPerson.getUser() != null &&
			pPerson.getUser().isAdmin()) {
			return true;
		}
		if (pComment instanceof SystemComment) {
			return false;
		}
		final Person commentOwner = pComment.getOwner();
		if (pPerson.equals(commentOwner)) {
			return true;
		}
		return false;
	}

	
	public void assertCurrentUserAuthorizedToEdit(final Comment pComment) {
		if (!isCurrentUserAuthorizedToEdit(pComment)) {
			throw new SecurityException("Current user is not authorized to edit comment");
		}
	}
	
	public void assertUserAuthorizedToEdit(final Person pPerson, final Comment pComment) {
		if (!isUserAuthorizedToEdit(pPerson, pComment)) {
			throw new SecurityException("User is not authorized to edit comment");
		}
	}

	public boolean isUserAuthorizedToAddCommentOnItem(final Person pPerson, final Item pItem) {
		return itemService.isUserAuthorizedToView(pPerson, pItem);
	}
	
	public boolean isCurrentUserAuthorizedToAddCommentOnNeed(final Need pNeed) {
		return needService.isCurrentUserAuthorizedToView(pNeed);
	}
	
	public boolean isUserAuthorizedToAddCommentOnNeed(final Person pPerson, final Need pNeed) {
		return needService.isUserAuthorizedToView(pPerson, pNeed);
	}
	
	public boolean isUserAuthorizedToAddCommentOnWall(final Person pPerson, final Person pWallOwner) {
		CoreUtils.assertNotNull(pWallOwner);
		if (pPerson == null) {
			return false;
		}
		else if (pPerson.equals(pWallOwner)) {
			return true;
		}
		final String wallCommentAddPermission = pWallOwner.getWallCommentsAddPermission().getLabelCode();
		if (WallCommentsAddPermission.EVERYONE.equals(wallCommentAddPermission)) {
			return true;
		}
		else if (WallCommentsAddPermission.CONNECTIONS.equals(wallCommentAddPermission)) {
			return personService.isConnection(pPerson, pWallOwner);
		}
		// None.
		else {
			return false;
		}
	}
	
	public boolean isUserAuthorizedToAddCommentOnLendTransaction(final Person pPerson, final LendTransaction pLendTransaction) {
		return lendTransactionService.isUserAuthorizedToView(pPerson, pLendTransaction);
	}
	
	public void assertUserAuthorizedToAddChildComment(final Person pPerson, final Comment pComment) {
		if (!isUserAuthorizedToAddChildComment(pPerson, pComment)) {
			throw new SecurityException("Person " + pPerson.getId() + " is not authorized to add child comment on comment " + pComment.getId());
		}
	}
	public boolean isCurrentUserAuthorizedToAddChildComment(final Comment pComment) {
		return isUserAuthorizedToAddChildComment(personService.getCurrentPerson(), pComment);
	}
	
	public boolean isUserAuthorizedToAddChildComment(final Person pPerson, final Comment pComment) {
		CoreUtils.assertNotNull(pPerson);
		CoreUtils.assertNotNull(pComment);
		
		if (Hibernate.getClass(pComment).isAssignableFrom(ChildComment.class)) {
			return false;
		}
		else if (Hibernate.getClass(pComment).isAssignableFrom(ItemComment.class)) {
			final ItemComment itemComment = commentDao.findItemComment(pComment.getId());
			return isUserAuthorizedToAddCommentOnItem(pPerson, itemComment.getItem());
		}
		else if (Hibernate.getClass(pComment).isAssignableFrom(NeedComment.class)) {
			final NeedComment needComment = commentDao.findNeedComment(pComment.getId());
			return isUserAuthorizedToAddCommentOnNeed(pPerson, needComment.getNeed());
		}
		else if (Hibernate.getClass(pComment).isAssignableFrom(GroupComment.class)) {
			final GroupComment groupComment = commentDao.findGroupComment(pComment.getId());
			return isUserAuthorizedToAddCommentOnGroup(pPerson, groupComment.getGroup());
		}
		else if (Hibernate.getClass(pComment).isAssignableFrom(LendTransactionComment.class)) {
			final LendTransactionComment lendTransactionComment = commentDao.findLendTransactionComment(pComment.getId());
			return isUserAuthorizedToAddCommentOnLendTransaction(pPerson, lendTransactionComment.getLendTransaction());
		}
		else if (Hibernate.getClass(pComment).isAssignableFrom(WallComment.class)) {
			final WallComment wallComment = commentDao.findWallComment(pComment.getId());
			Person wallOwner = wallComment.getWallOwner();
			if (wallOwner == null) {
				wallOwner = wallComment.getOwner();
			}
			final Person parentCommentOwner = pComment.getOwner();
			return isUserAuthorizedToView(pPerson, pComment) &&
			       isUserAuthorizedToAddCommentOnWall(pPerson, wallOwner);
		}
		else {
			throw new RuntimeException("Unknown comment type: " + pComment.getClass().getName());
		}
	}
	
	public boolean isUserAuthorizedToAddCommentOnGroup(final Person pPerson, final Group pGroup) {
		return groupService.isUserOwnerOrAdministratorOrMemberOfGroup(pPerson, pGroup);
	}
	
	public void assertUserAuthorizedToAddCommentOnItem(final Person pPerson, final Item pItem) {
		if (!isUserAuthorizedToAddCommentOnItem(pPerson, pItem)) {
			throw new SecurityException("Current user is not authorized to add comment");
		}
	}
	
	public void assertUserAuthorizedToAddCommentOnNeed(final Person pPerson, final Need pNeed) {
		if (!isUserAuthorizedToAddCommentOnNeed(pPerson, pNeed)) {
			throw new SecurityException("User is not authorized to add comment");
		}
	}
	
	public void assertUserAuthorizedToAddCommentOnWall(final Person pPerson, final Person pWallOwner) {
		if (!isUserAuthorizedToAddCommentOnWall(pPerson, pWallOwner)) {
			throw new SecurityException("User is not authorized to add comment");
		}
	}

	public void assertUserAuthorizedToAddCommentOnLendTransaction(final Person pPerson, final LendTransaction pLendTransaction) {
		if (!isUserAuthorizedToAddCommentOnLendTransaction(pPerson, pLendTransaction)) {
			throw new SecurityException("User is not authorized to add comment");
		}
	}

	public void assertUserAuthorizedToAddCommentOnGroup(final Person pPerson, final Group pGroup) {
		if (!isUserAuthorizedToAddCommentOnGroup(pPerson, pGroup)) {
			throw new SecurityException("User is not authorized to add comment");
		}
	}
	
	public void assertCurrentUserAuthorizedToAddCommentOnNeed(final Need pNeed) {
		if (!isCurrentUserAuthorizedToAddCommentOnNeed(pNeed)) {
			throw new SecurityException("Current user is not authorized to add comment");
		}
	}

	public boolean isCurrentUserAuthorizedToDelete(final Comment pComment) {
		return isCurrentUserAuthorizedToEdit(pComment);
	}
	
	public boolean isUserAuthorizedToDelete(final Person pPerson, final Comment pComment) {
		WallComment refWallComment = null;
		if (Hibernate.getClass(pComment).isAssignableFrom(WallComment.class)) {
			refWallComment = commentDao.findWallComment(pComment.getId());
			
		}
		else if (Hibernate.getClass(pComment).isAssignableFrom(ChildComment.class)) {
			final ChildComment childComment = findChildComment(pComment.getId());
			if (Hibernate.getClass(childComment.getParentComment()).isAssignableFrom(WallComment.class)) {
				refWallComment = findWallComment(childComment.getParentComment().getId());
			} 			
		}
		if (refWallComment != null) {
			// Users can delete others comments on their own wall.
			if (pPerson != null && pPerson.equals(refWallComment.getWallOwner())) {
				return true;
			}
		}
		return isUserAuthorizedToEdit(pPerson, pComment);
	}

	public void assertCurrentUserAuthorizedToDelete(final Comment pComment) {
		if (!isCurrentUserAuthorizedToDelete(pComment)) {
			throw new SecurityException("Current user is not authorized to delete comment");
		}
	}
	
	public void assertUserAuthorizedToDelete(final Person pPerson, final Comment pComment) {
		if (!isUserAuthorizedToDelete(pPerson, pComment)) {
			throw new SecurityException("User is not authorized to delete comment");
		}
	}

	// Access control - end.
	/////////////////////////////////////////////////////////
	
	
}
