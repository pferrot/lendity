package com.pferrot.lendity.comment;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import com.pferrot.lendity.item.ItemService;
import com.pferrot.lendity.lendtransaction.LendTransactionService;
import com.pferrot.lendity.model.Comment;
import com.pferrot.lendity.model.Commentable;
import com.pferrot.lendity.model.CommentableWithOwner;
import com.pferrot.lendity.model.Item;
import com.pferrot.lendity.model.Item;
import com.pferrot.lendity.model.ItemComment;
import com.pferrot.lendity.model.LendTransaction;
import com.pferrot.lendity.model.LendTransactionComment;
import com.pferrot.lendity.model.LendTransactionSystemComment;
import com.pferrot.lendity.model.Need;
import com.pferrot.lendity.model.NeedComment;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.model.SystemComment;
import com.pferrot.lendity.need.NeedService;
import com.pferrot.lendity.person.PersonService;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.HtmlUtils;
import com.pferrot.lendity.utils.JsfUtils;

public class CommentService {

	private final static Log log = LogFactory.getLog(CommentService.class);
	
	private CommentDao commentDao;
	private ItemService itemService;
	private NeedService needService;
	private LendTransactionService lendTransactionService;
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

	public void setCommentDao(final CommentDao commentDao) {
		this.commentDao = commentDao;
	}
	
	public void setPersonDao(final PersonDao pPersonDao) {
		this.personDao = pPersonDao;
	}
	
	public Comment findComment(final Long pCommentId) {
		return commentDao.findComment(pCommentId);
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
	
	/**
	 * Remove the owner of the comment from the list of comment recipients in case the
	 * comment to be deleted is the only comment he made on that object.
	 *
	 * @param pComment
	 */
	private void removeCommentRecipientFromContainerIfNeeded(final Comment pComment) {
		if (Hibernate.getClass(pComment).isAssignableFrom(LendTransactionComment.class) ||
			pComment == null || 
			pComment.getId() == null ||
			pComment.getOwner() == null ||
			pComment.getOwner().getId() == null) {
			return;
		}
		final Long deletedCommentId = pComment.getId();
		final Long deletedCommentOwnerId = pComment.getOwner().getId();
		
		final Commentable commentable = pComment.getContainer();
		final Set comments = commentable.getComments();
		final Iterator ite = comments.iterator();
		
		while (ite.hasNext()) {
			final Comment comment = (Comment)ite.next();
			if (comment != null &&
				comment.getId() != null &&
				comment.getOwner() != null &&
				comment.getOwner().getId() != null) {
				final Long oneCommentId = comment.getId();
				final Long oneCommentOwnerId = comment.getOwner().getId();
				// Not the comment to delete.
				if (!oneCommentId.equals(deletedCommentId)) {
					// Same owner, so he should still be in the people to notify list.
					if (oneCommentOwnerId.equals(deletedCommentOwnerId)) {
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
	public void updateCommentWithAC(final Long pCommentId, final String pNewText, final Long pCurrentPersonId) throws CommentException {		
		final Comment comment = commentDao.findComment(pCommentId);
		if (comment instanceof SystemComment) {
			throw new CommentException("System comment cannot be edited");
		}
		
		final Person person = personService.findPerson(pCurrentPersonId);
		
		assertUserAuthorizedToEdit(person, comment);		
		
		comment.setModificationDate(new Date());
		comment.setText(pNewText);
		
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
		if (pComment instanceof CommentableWithOwner) {
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
			else {
				throw new CommentException("Unhandled commentabe type " + commentable.getClass());
			}
			
			objects.put("comment", pComment.getText());
			// For the HTML template to correctly display new lines.
			objects.put("commentEscaped", HtmlUtils.escapeHtmlAndReplaceCr(pComment.getText()));
			objects.put("signature", Configuration.getSiteName());
			objects.put("siteName", Configuration.getSiteName());
			objects.put("siteUrl", Configuration.getRootURL());
			objects.put("profileUrl", JsfUtils.getFullUrlWithPrefix(Configuration.getRootURL(), PagesURL.MY_PROFILE));
			
			Map<String, String> to = new HashMap<String, String>();
			to.put(pPerson.getEmail(), pPerson.getEmail());
			
			Map<String, String> inlineResources = new HashMap<String, String>();
			inlineResources.put("logo", "com/pferrot/lendity/emailtemplate/lendity_logo.gif");
			
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

	private Person getCurrentPerson() {
		return personDao.findPerson(PersonUtils.getCurrentPersonId());
	}
	
    /////////////////////////////////////////////////////////
	// Access control - start
	
	public boolean isCurrentUserAuthorizedToView(final Comment pComment) {
		CoreUtils.assertNotNull(pComment);
		if (isCurrentUserAuthorizedToEdit(pComment)) {
			return true;
		}
		if (pComment instanceof ItemComment) {
			ItemComment itemComment = (ItemComment)pComment;
			Item item = itemComment.getItem();
			return itemService.isCurrentUserAuthorizedToView(item);
		}
		else if (pComment instanceof NeedComment) {
			NeedComment needComment = (NeedComment)pComment;
			Need need = needComment.getNeed();
			return needService.isCurrentUserAuthorizedToView(need);
		}
		else if (pComment instanceof LendTransactionComment) {
			LendTransactionComment lendTransactionComment = (LendTransactionComment)pComment;
			LendTransaction lendTransaction = lendTransactionComment.getLendTransaction();
			return lendTransactionService.isCurrentUserAuthorizedToView(lendTransaction);
		}
		
		return false;
	}
	
	public boolean isUserAuthorizedToView(final Person pPerson, final Comment pComment) {
		CoreUtils.assertNotNull(pComment);
		if (isUserAuthorizedToEdit(pPerson, pComment)) {
			return true;
		}
		if (pComment instanceof ItemComment) {
			ItemComment itemComment = (ItemComment)pComment;
			Item item = itemComment.getItem();
			return itemService.isUserAuthorizedToView(pPerson, item);
		}
		else if (pComment instanceof NeedComment) {
			NeedComment needComment = (NeedComment)pComment;
			Need need = needComment.getNeed();
			return needService.isUserAuthorizedToView(pPerson, need);
		}
		else if (pComment instanceof LendTransactionComment) {
			LendTransactionComment lendTransactionComment = (LendTransactionComment)pComment;
			LendTransaction lendTransaction = lendTransactionComment.getLendTransaction();
			return lendTransactionService.isUserAuthorizedToView(pPerson, lendTransaction);
		}
		
		return false;
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
	
	public boolean isUserAuthorizedToAddCommentOnLendTransaction(final Person pPerson, final LendTransaction pLendTransaction) {
		return lendTransactionService.isUserAuthorizedToView(pPerson, pLendTransaction);
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

	public void assertUserAuthorizedToAddCommentOnLendTransaction(final Person pPerson, final LendTransaction pLendTransaction) {
		if (!isUserAuthorizedToAddCommentOnLendTransaction(pPerson, pLendTransaction)) {
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
