var mAddCommentDefaultText = '';
var mAddChildCommentDefaultText = '';
var mSubmitButtonText = '';
var mContextPath = "";
var mAuthorizedToReply;
// The number of comments that is displayed.
var mNbCommentsLoaded = 0;
// The number of comments that has to be loaded at a time.
// It is also the default number of comments.
var mMaxResults = 10;

var mEditCommentLabel = "";
var mDeleteCommentLabel = "";
var mPublicIndicatorLabel = "";
// The ID of the parent comment when adding child comments.
var mTargetParentCommentId;
// Specify whether to show "destinee" of the wall comment when available.
var mShowOtherWallCommentOwner = false;

/**
 * Called when an item overview page is loaded in order to load the first comments.
 * 
 * @param pItemId
 * @return
 */
function initComments(pContainerId, pContainerType, pAuthorizedToReply, pContextPath, pAddCommentDefaultText, pAddChildCommentDefaultText, pSubmitButtonText, pEditCommentLabel, pDeleteCommentLabel, pPublicIndicatorLabel, pShowOtherWallCommentOwner) {
	mContextPath = pContextPath;
	mAddCommentDefaultText = pAddCommentDefaultText;
	mAuthorizedToReply = pAuthorizedToReply;
	mAddChildCommentDefaultText = pAddChildCommentDefaultText;
	mSubmitButtonText = pSubmitButtonText;
	mEditCommentLabel = pEditCommentLabel;
	mDeleteCommentLabel = pDeleteCommentLabel;
	mPublicIndicatorLabel = pPublicIndicatorLabel;
	mShowOtherWallCommentOwner = pShowOtherWallCommentOwner;
	if (pContainerType == "item") {
		loadCommentsForItem(pContainerId);
	}
	else if (pContainerType == "need") {
		loadCommentsForNeed(pContainerId);
	}	
	else if (pContainerType == "lendTransaction") {
		loadCommentsForLendTransaction(pContainerId);
	}
	else if (pContainerType == "group") {
		loadCommentsForGroup(pContainerId);
	}
	else if (pContainerType == "ownWall") {
		loadCommentsForOwnWall();
	}
	else if (pContainerType == "otherWall") {
		loadCommentsForOtherWall(pContainerId);
	}
	else if (pContainerType == "none") {
		loadOneComment(pContainerId);
	}
}

function loadMoreComments(pContainerId, pContainerType) {
	if (pContainerType == "item") {
		loadMoreCommentsInternalForItem(pContainerId);
	}
	else if (pContainerType == "need") {
		loadMoreCommentsInternalForNeed(pContainerId);
	}
	else if (pContainerType == "lendTransaction") {
		loadMoreCommentsInternalForLendTransaction(pContainerId);
	}
	else if (pContainerType == "group") {
		loadMoreCommentsInternalForGroup(pContainerId);
	}
	else if (pContainerType == "ownWall") {
		loadMoreCommentsInternalForOwnWall();
	}
	else if (pContainerType == "otherWall") {
		loadMoreCommentsInternalForOtherWall(pContainerId);
	}
}

/**
 * The button to add a new comment is pressed.
 * 
 * @return
 */
function postComment(pContainerId, pContainerType) {
	addCommentInProgress();
	addCommentInDb(pContainerId, pContainerType);
}

/**
 * The button to add a new child comment is pressed.
 * 
 * @return
 */
function postChildComment(pParentCommentId) {
	addChildCommentInProgress(pParentCommentId);
	addChildCommentInDb(pParentCommentId);
}

function addCommentInDb(pContainerId, pContainerType) {
	if (pContainerType == "item") {
		addCommentInDbInternalForItem(pContainerId);
	}
	else if (pContainerType == "need") {
		addCommentInDbInternalForNeed(pContainerId);
	}
	else if (pContainerType == "lendTransaction") {
		addCommentInDbInternalForLendTransaction(pContainerId);
	}
	else if (pContainerType == "group") {
		addCommentInDbInternalForGroup(pContainerId);
	}
	else if (pContainerType == "ownWall") {
		addCommentInDbInternalForOwnWall();
	}
	else if (pContainerType == "otherWall") {
		addCommentInDbInternalForOtherWall(pContainerId);
	}
}

function positionEditCommentInProgressBottom() {
	var editCommentInProgress = $j("#editCommentInProgress");
	var commentsContainer = $j("#commentsContainer");
	editCommentInProgress.insertAfter(commentsContainer);
}

function loadCommentsForItem(pItemId) {
	addCommentInProgress();
	$j.ajax({
		url: mContextPath + '/public/comment/comment.json',
		dataType: 'json',
		contentType: 'application/json',
		data: {action: 'read', itemID: pItemId, firstResult: mNbCommentsLoaded, maxResults: mMaxResults},
		success: loadCommentsResponse,
		cache: false
	});
}

function loadCommentsForNeed(pNeedId) {
	addCommentInProgress();
	$j.ajax({
		url: mContextPath + '/public/comment/comment.json',
		dataType: 'json',
		contentType: 'application/json',
		data: {action: 'read', needID: pNeedId, firstResult: mNbCommentsLoaded, maxResults: mMaxResults},
		success: loadCommentsResponse,
		cache: false
	});
}

function loadCommentsForLendTransaction(pLendTransactionId) {
	addCommentInProgress();
	$j.ajax({
		url: mContextPath + '/public/comment/comment.json',
		dataType: 'json',
		contentType: 'application/json',
		data: {action: 'read', lendTransactionID: pLendTransactionId, firstResult: mNbCommentsLoaded, maxResults: mMaxResults},
		success: loadCommentsResponse,
		cache: false
	});
}

function loadCommentsForGroup(pGroupId) {
	addCommentInProgress();
	$j.ajax({
		url: mContextPath + '/public/comment/comment.json',
		dataType: 'json',
		contentType: 'application/json',
		data: {action: 'read', groupID: pGroupId, firstResult: mNbCommentsLoaded, maxResults: mMaxResults},
		success: loadCommentsResponse,
		cache: false
	});
}

function loadCommentsForOwnWall() {
	addCommentInProgress();
	$j.ajax({
		url: mContextPath + '/public/comment/comment.json',
		dataType: 'json',
		contentType: 'application/json',
		data: {action: 'read', wall: 'own', firstResult: mNbCommentsLoaded, maxResults: mMaxResults},
		success: loadCommentsResponse,
		cache: false
	});
}

function loadCommentsForOtherWall(pOtherPersonId) {
	addCommentInProgress();
	$j.ajax({
		url: mContextPath + '/public/comment/comment.json',
		dataType: 'json',
		contentType: 'application/json',
		data: {action: 'read', wall: pOtherPersonId, firstResult: mNbCommentsLoaded, maxResults: mMaxResults},
		success: loadCommentsResponse,
		cache: false
	});
}

function loadOneComment(pCommentId) {
	addCommentInProgress();
	$j.ajax({
		url: mContextPath + '/public/comment/comment.json',
		dataType: 'json',
		contentType: 'application/json',
		data: {action: 'read', commentID: pCommentId, firstResult: mNbCommentsLoaded, maxResults: mMaxResults},
		success: loadCommentsResponse,
		cache: false
	});
}


/**
 * Adds the comment in the DB. Returns an error message if error occurred.
 * 
 * @return
 */
function addCommentInDbInternalForItem(pItemId) {
	var commentTextarea = $j('#commentTextarea');
	var text = commentTextarea.val();
	text = $j.trim(text);
	if (text == mAddCommentDefaultText) {
		text = '';
	}
	// Add in DB.
	$j.ajax({
			url: mContextPath + '/public/comment/comment.json',
			dataType: 'json',
			contentType: 'application/json',
			data: {action: 'create', itemID: pItemId, text: text},
			success: addCommentInDbResponse,
			cache: false
	});
}

function addCommentInDbInternalForNeed(pNeedId) {
	var commentTextarea = $j('#commentTextarea');
	var text = commentTextarea.val();
	text = $j.trim(text);
	if (text == mAddCommentDefaultText) {
		text = '';
	}
	// Add in DB.
	$j.ajax({
			url: mContextPath + '/public/comment/comment.json',
			dataType: 'json',
			contentType: 'application/json',
			data: {action: 'create', needID: pNeedId, text: text},
			success: addCommentInDbResponse,
			cache: false
	});
}

function addCommentInDbInternalForLendTransaction(pLendTransactionId) {
	var commentTextarea = $j('#commentTextarea');
	var text = commentTextarea.val();
	text = $j.trim(text);
	if (text == mAddCommentDefaultText) {
		text = '';
	}
	// Add in DB.
	$j.ajax({
			url: mContextPath + '/public/comment/comment.json',
			dataType: 'json',
			contentType: 'application/json',
			data: {action: 'create', lendTransactionID: pLendTransactionId, text: text},
			success: addCommentInDbResponse,
			cache: false
	});
}

function addCommentInDbInternalForGroup(pGroupId) {
	var commentTextarea = $j('#commentTextarea');
	var text = commentTextarea.val();
	text = $j.trim(text);
	if (text == mAddCommentDefaultText) {
		text = '';
	}
	// Add in DB.
	$j.ajax({
			url: mContextPath + '/public/comment/comment.json',
			dataType: 'json',
			contentType: 'application/json',
			data: {action: 'create', groupID: pGroupId, text: text},
			success: addCommentInDbResponse,
			cache: false
	});
}

function addCommentInDbInternalForOwnWall() {
	var commentTextarea = $j('#commentTextarea');
	var text = commentTextarea.val();
	text = $j.trim(text);
	if (text == mAddCommentDefaultText) {
		text = '';
	}
	var isPublicComment = $j("#addCommentPublicCommentCheckbox").attr("checked");
	// Add in DB.
	$j.ajax({
			url: mContextPath + '/public/comment/comment.json',
			dataType: 'json',
			contentType: 'application/json',
			data: {action: 'create', wall: 'own', text: text, publicComment: isPublicComment},
			success: addCommentInDbResponse,
			cache: false
	});
}

function addCommentInDbInternalForOtherWall(pOtherPersonId) {
	var commentTextarea = $j('#commentTextarea');
	var text = commentTextarea.val();
	text = $j.trim(text);
	if (text == mAddCommentDefaultText) {
		text = '';
	}
	var isPublicComment = $j("#addCommentPublicCommentCheckbox").attr("checked");
	// Add in DB.
	$j.ajax({
			url: mContextPath + '/public/comment/comment.json',
			dataType: 'json',
			contentType: 'application/json',
			data: {action: 'create', wall: pOtherPersonId, text: text, publicComment: isPublicComment},
			success: addCommentInDbResponse,
			cache: false
	});
}

function addChildCommentInDb(pParentCommentId) {
	var childCommentTextarea = $j('#childCommentTextarea' + pParentCommentId);
	var text = childCommentTextarea.val();
	text = $j.trim(text);
	if (text == mAddChildCommentDefaultText) {
		text = '';
	}
	// Add in DB.
	$j.ajax({
			url: mContextPath + '/public/comment/comment.json',
			dataType: 'json',
			contentType: 'application/json',
			data: {action: 'create', parentCommentID: pParentCommentId, text: text},
			success: addChildCommentInDbResponse,
			cache: false
	});
}

function loadMoreCommentsInternalForItem(pItemId) {
	hideLoadExtraCommentsDiv();	
	
	positionEditCommentInProgressBottom();
	editCommentInProgress();
	
	$j.ajax({
		url: mContextPath + '/public/comment/comment.json',
		dataType: 'json',
		contentType: 'application/json',
		data: {action: 'read', itemID: pItemId, firstResult: mNbCommentsLoaded, maxResults: mMaxResults},
		success: loadCommentsResponse,
		cache: false
	});	
}

function loadMoreCommentsInternalForNeed(pNeedId) {
	hideLoadExtraCommentsDiv();	
	
	positionEditCommentInProgressBottom();
	editCommentInProgress();
	
	$j.ajax({
		url: mContextPath + '/public/comment/comment.json',
		dataType: 'json',
		contentType: 'application/json',
		data: {action: 'read', needID: pNeedId, firstResult: mNbCommentsLoaded, maxResults: mMaxResults},
		success: loadCommentsResponse,
		cache: false
	});	
}

function loadMoreCommentsInternalForLendTransaction(pLendTransactionId) {
	hideLoadExtraCommentsDiv();	
	
	positionEditCommentInProgressBottom();
	editCommentInProgress();
	
	$j.ajax({
		url: mContextPath + '/public/comment/comment.json',
		dataType: 'json',
		contentType: 'application/json',
		data: {action: 'read', lendTransactionID: pLendTransactionId, firstResult: mNbCommentsLoaded, maxResults: mMaxResults},
		success: loadCommentsResponse,
		cache: false
	});	
}

function loadMoreCommentsInternalForGroup(pGroupId) {
	hideLoadExtraCommentsDiv();	
	
	positionEditCommentInProgressBottom();
	editCommentInProgress();
	
	$j.ajax({
		url: mContextPath + '/public/comment/comment.json',
		dataType: 'json',
		contentType: 'application/json',
		data: {action: 'read', groupID: pGroupId, firstResult: mNbCommentsLoaded, maxResults: mMaxResults},
		success: loadCommentsResponse,
		cache: false
	});	
}

function loadMoreCommentsInternalForOwnWall() {
	hideLoadExtraCommentsDiv();	
	
	positionEditCommentInProgressBottom();
	editCommentInProgress();
	
	$j.ajax({
		url: mContextPath + '/public/comment/comment.json',
		dataType: 'json',
		contentType: 'application/json',
		data: {action: 'read', wall: 'own', firstResult: mNbCommentsLoaded, maxResults: mMaxResults},
		success: loadCommentsResponse,
		cache: false
	});	
}

function loadMoreCommentsInternalForOtherWall(pOtherPersonId) {
	hideLoadExtraCommentsDiv();	
	
	positionEditCommentInProgressBottom();
	editCommentInProgress();
	
	$j.ajax({
		url: mContextPath + '/public/comment/comment.json',
		dataType: 'json',
		contentType: 'application/json',
		data: {action: 'read', wall: pOtherPersonId, firstResult: mNbCommentsLoaded, maxResults: mMaxResults},
		success: loadCommentsResponse,
		cache: false
	});	
}


/**
 * Handles the response from the server that contains the initial comments.
 * 
 * @param pData
 * @param pTextStatus
 * @param pXmlHttpRequest
 * @return
 */
function loadCommentsResponse(pData, pTextStatus, pXmlHttpRequest) {
	addCommentsFromJsonData(pData);
	addCommentStopProgress();
	editCommentStopProgress();
}

/**
 * 
 * @param pJsonData
 * @return
 */
function addCommentsFromJsonData(pJsonData) {
	var comments = pJsonData.comments;
	var nb = pJsonData.nb;
	for (var i = 0; i < nb; i++) {
		var comment = comments[i];
		addCommentInternal(comment.commentID, comment.text, comment.textWithoutHref, comment.ownerName, comment.ownerUrl, comment.wallOwnerName, comment.wallOwnerUrl,
				comment.dateAdded, comment.profilePictureUrl, comment.canEdit, comment.canDelete, comment.systemComment, comment.adminComment, comment.publicComment, comment.otherWallComment, false);
	}
	var childComments = pJsonData.childComments;
	if (childComments != null) {
		for (var i = 0; i < childComments.length; i++) {
			var comment = childComments[i];
			addChildCommentInternal(comment.commentID, comment.parentCommentID, comment.text, comment.textWithoutHref, comment.ownerName, comment.ownerUrl,
					comment.dateAdded, comment.profilePictureUrl, comment.canEdit, comment.canDelete, comment.systemComment, comment.adminComment, comment.publicComment, comment.otherWallComment, false);
		}
	}
	mNbCommentsLoaded = mNbCommentsLoaded + nb;
	var nbExtra = pJsonData.nbExtra;
	if (nbExtra > 0) {
		showLoadExtraCommentsDiv(nbExtra);
	}
	else {
		hideLoadExtraCommentsDiv();
	}
	if (mNbCommentsLoaded == 0) {
		showNoCommentDiv();
	}
	else {
		hideNoCommentDiv();
	}
	
}

function showLoadExtraCommentsDiv(pNbExtra) {
	$j("#nbExtraComments").html(pNbExtra);
	$j("#loadMoreCommentsContainer").show();
}

function hideLoadExtraCommentsDiv() {
	$j("#loadMoreCommentsContainer").hide();
}

function showNoCommentDiv() {
	$j("#noCommentContainer").show();
}

function hideNoCommentDiv() {
	$j("#noCommentContainer").hide();
}

/**
 * Adds an error message to the addComment textarea and colors the textarea in red.
 * 
 * @param pErrorMessage
 * @return
 */
function addCommentError(pErrorMessage) {
	commentError(pErrorMessage, "commentTextarea");
}

function addChildCommentError(pErrorMessage, pParentCommentId) {
	commentError(pErrorMessage, "childCommentTextarea" + pParentCommentId);
}

/**
 * Adds an error message to the editComment textarea and colors the textarea in red.
 * 
 * @param pErrorMessage
 * @return
 */
function editCommentError(pErrorMessage) {
	commentError(pErrorMessage, "editCommentTextarea");
}

/**
 * Adds an error message to the specidied textarea and colors the textarea in red.
 * 
 * @param pErrorMessage
 * @param pTextareaId
 * @return
 */
function commentError(pErrorMessage, pTextareaId) {
	var textareaParent = $j("#" + pTextareaId).parent();
	var firstChild = textareaParent.children().first();
	// The errorMessage span does not exist yet (i.e. first error).
	if (!firstChild.hasClass("error")) {
		textareaParent.prepend("<span class='error'>" + pErrorMessage + "</span>")
	}
	// Not the first error, update the error message.
	else {
		firstChild.html(pErrorMessage);
	}	
	$j("#" + pTextareaId).addClass("validationError");
}

/**
 * Clear the error message of the addComment box.
 * 
 * @return
 */
function addCommentErrorRemove() {
	$j("#commentTextarea").prev().remove();
	$j("#commentTextarea").removeClass("validationError");
}

function addChildCommentErrorRemove(pParentCommentId) {
	$j("#childCommentTextarea" + pParentCommentId).prev().remove();
	$j("#childCommentTextarea" + pParentCommentId).removeClass("validationError");
}

/**
 * Clear the error message of the editComment box.
 * 
 * @return
 */
function editCommentErrorRemove() {
	$j("#editCommentTextarea").prev().remove();
	$j("#editCommentTextarea").removeClass("validationError");
}

function resetCommentTextArea() {
	addCommentErrorRemove();
	$j('#commentTextarea').val(mAddCommentDefaultText);
	$j('#commentTextarea').addClass('grayColor');
	$j('#addCommentPublicCommentCheckbox').attr('checked', false);
	
}

function resetChildCommentTextArea(pParentCommentId) {
	addChildCommentErrorRemove(pParentCommentId);
	$j('#childCommentTextareaContainer' + pParentCommentId).hide();
	//$j('#childCommentTextarea' + pParentCommentId).attr('rows', '1');
	$j('#childCommentButtonContainer' + pParentCommentId).hide();
	$j('#childCommentTextarea' + pParentCommentId).val(mAddChildCommentDefaultText);
	$j('#childCommentTextarea' + pParentCommentId).addClass('grayColor');
	$j('#childCommentLinkContainer' + pParentCommentId).show();
}

/**
 * Displays the in progress dialog for the addComment box.
 * 
 * @return
 */
function addCommentInProgress() {
	$j('#addCommentBox').hide();
	$j('#addCommentInProgress').show();	
}

function addChildCommentInProgress(pParentCommentId) {
	$j('#addChildCommentBox' + pParentCommentId).hide();
	$j('#addChildCommentInProgress' + pParentCommentId).show();	
}

function editCommentInProgress() {
	$j('#editCommentInProgress').show();	
}

function editCommentStopProgress() {
	$j('#editCommentInProgress').hide();	
}

/**
 * Hides the in progress dialog for the addComment box.
 * 
 * @return
 */
function addCommentStopProgress() {
	$j('#addCommentInProgress').hide();
	$j('#addCommentBox').show();	
}

function addChildCommentStopProgress(pParentCommentId) {
	$j('#addChildCommentInProgress' + pParentCommentId).hide();
	$j('#addChildCommentBox' + pParentCommentId).show();	
}

/**
 * When the button to remove a comment is clicked.
 * The comment is removed from both the DB and the UI.
 * 
 * @param pSpanLink
 * @return
 */
function removeComment(pSpanLink) {
	var jSpan = $j(pSpanLink);
	var commentBox = getContainerCommentBox(jSpan);
	var commentId = getCommentIdFromCommentBox(commentBox);
	
	var editCommentInProgress = $j("#editCommentInProgress");
	editCommentInProgress.insertAfter(commentBox);
	commentBox.hide();
	editCommentInProgress.show();
	
	$j.ajax({
		url: mContextPath + '/public/comment/comment.json',
		dataType: 'json',
		contentType: 'application/json',
		data: {action: 'delete', commentID: commentId},
		success: removeCommentFromDbResponse,
		cache: false
	});	
}


function removeCommentFromDbResponse(pData, pTextStatus, pXmlHttpRequest) {
	var commentID = pData.commentID;
	var commentBox = $j('#comment' + commentID);
	var editCommentInProgress = $j("#editCommentInProgress");
	editCommentInProgress.hide();
	
	var errorMessage = pData.errorMessage;
	
	// Could not delete.
	if (errorMessage != undefined &&
		errorMessage.length > 0) {
		// Redisplay message.		
		commentBox.show();
	}
	// Remove the deleted comment from the UI.
	else {
		var commentBoxParent = commentBox.parent();
		// If it is a parent comment, we must also remove all children comments.
		if (commentBoxParent.attr('id') == ('commentsFamily' + commentID)) {
			// Do not delete the editCommentInProgress.
			editCommentInProgress.insertAfter(commentBoxParent);
			commentBoxParent.remove();
			$j('#addChildCommentBox' + commentID).remove();
			$j('#addChildCommentInProgress' + commentID).remove();
			mNbCommentsLoaded = mNbCommentsLoaded - 1;
		}
		else {
			commentBox.remove();
		}		
		
		if (mNbCommentsLoaded <= 0) {
			showNoCommentDiv();
		}
	}
}


/**
 * Returns the closest comment box given a jQuery element.
 * 
 * @param pJqueryElement
 * @return
 */
function getContainerCommentBox(pJqueryElement) {
	var commentBoxDiv = pJqueryElement;
	do {
		commentBoxDiv = commentBoxDiv.parent();
	} while (!commentBoxDiv.hasClass('commentBox'));
	return commentBoxDiv;
}

/**
 * Given a comment box, retrieves the corresponding comment ID.
 * @param pCommentBox
 * @return
 */
function getCommentIdFromCommentBox(pCommentBox) {
	return pCommentBox.attr('commentId');	
}

/**
 * Callback method once the response for adding a comment is received.
 * 
 * @param pJson
 * @return
 */
function addCommentInDbResponse(pData, pTextStatus, pXmlHttpRequest) {
	var errorMessage = pData.errorMessage;
	if (errorMessage != undefined &&
		errorMessage.length > 0) {
		addCommentError(errorMessage);
	}
	else {
		var commentId = pData.commentID;
		var text = pData.text;
		var textWithoutHref = pData.textWithoutHref;
		var ownerName = pData.ownerName;
		var ownerUrl = pData.ownerUrl;
		var wallOwnerName = pData.wallOwnerName;
		var wallOwnerUrl = pData.wallOwnerUrl;
		var dateAdded = pData.dateAdded;
		var profilePictureUrl = pData.profilePictureUrl;
		var canEdit = pData.canEdit;
		var canDelete = pData.canDelete;
		var systemComment = pData.systemComment;
		var adminComment = pData.adminComment;
		var publicComment = pData.publicComment;
		var otherWallComment = pData.otherWallComment;
		addCommentInternal(commentId, text, textWithoutHref, ownerName, ownerUrl, wallOwnerName, wallOwnerUrl, dateAdded, profilePictureUrl, canEdit, canDelete, systemComment, adminComment, publicComment, otherWallComment, true);
		resetCommentTextArea();
		mNbCommentsLoaded = mNbCommentsLoaded + 1;
		hideNoCommentDiv();
	}
	addCommentStopProgress();
}

/**
 * Callback method once the response for adding a comment is received.
 * 
 * @param pJson
 * @return
 */
function addChildCommentInDbResponse(pData, pTextStatus, pXmlHttpRequest) {
	var errorMessage = pData.errorMessage;
	var parentCommentId = pData.parentCommentID;
	if (errorMessage != undefined &&
		errorMessage.length > 0) {
		addChildCommentError(errorMessage, parentCommentId);
	}
	else {
		var commentId = pData.commentID;
		var text = pData.text;
		var textWithoutHref = pData.textWithoutHref;
		var ownerName = pData.ownerName;
		var ownerUrl = pData.ownerUrl;
		var dateAdded = pData.dateAdded;
		var profilePictureUrl = pData.profilePictureUrl;
		var canEdit = pData.canEdit;
		var canDelete = pData.canDelete;
		var systemComment = pData.systemComment;
		var adminComment = pData.adminComment;
		var publicComment = pData.publicComment;
		var otherWallComment = pData.otherWallComment;
		addChildCommentInternal(commentId, parentCommentId, text, textWithoutHref, ownerName, ownerUrl, dateAdded, profilePictureUrl, canEdit, canDelete, systemComment, adminComment, publicComment, otherWallComment, false);
		resetChildCommentTextArea(parentCommentId);
	}
	addChildCommentStopProgress(parentCommentId);
}

/**
 * The button to edit a button is clicked. This will hide the message and display
 * a textarea instead.
 *
 * @param pSpanLink
 * @return
 */
function editComment(pSpanLink) {
	var jSpan = $j(pSpanLink);
	var commentBox = getContainerCommentBox(jSpan);
	var commentId = getCommentIdFromCommentBox(commentBox);
	var editCommentBox = $j("#editCommentBox");
	
	// Hide the comment that was being modified.
	if (editCommentBox.is(':visible')) {
		editCommentCancel();
	}
	
	
	
	editCommentBox.insertAfter(commentBox);
	var editCommentInProgress = $j("#editCommentInProgress");
	editCommentInProgress.insertAfter(commentBox);
	commentBox.hide();
	editCommentInProgress.show();
	
	var editCommentPublicCommentTd = $j("#editCommentPublicCommentTd");
	if (editCommentPublicCommentTd) {
		if (commentBox.parent().hasClass("childComment")) {
			editCommentPublicCommentTd.hide();
		}
		else {
			editCommentPublicCommentTd.show();
			// Set the checkbox according to the value in the DB.
			var isPublicComment = "true" == $j('#comment' + commentId).attr("publicComment");
			$j("#editCommentPublicCommentCheckbox").attr("checked", isPublicComment);
			
		}
	}
	
	
	
	editCommentBox.attr('commentId', commentId);
	$j('#editCommentTextarea').val(getCommentTextFromHtml(commentId));
	editCommentInProgress.hide();
	editCommentBox.show();
}

/**
 * Returns the unescaped text for a particular comment.
 * 
 * @param pCommentId
 * @return
 */
function getCommentTextFromHtml(pCommentId) {
	//var commentSpan = $j("#commentSpan" + pCommentId);
	//return br2nl(commentSpan.html());
	//var commentDiv = $j("#comment" + pCommentId);	
	//return br2nl(commentDiv.attr("textWithoutHref"));
	var commentWithoutHrefDiv = $j("#commentWithoutHref" + pCommentId);
	return br2nl(commentWithoutHrefDiv.html());
}

/**
 * The button to cancel the edition of a comment is clicked.
 * 
 * @return
 */
function editCommentCancel() {
	var editCommentBox = $j("#editCommentBox");
	var commentId = editCommentBox.attr('commentId');
	editCommentBox.hide();
	editCommentErrorRemove();
	$j('#comment' + commentId).show();
	
}

/**
 * The button to submit the new message is clicked.
 *
 * @return
 */
function editCommentSubmit() {
	var editCommentBox = $j("#editCommentBox");
	var editCommentInProgress = $j("#editCommentInProgress");
	
	var commentId = editCommentBox.attr('commentId');
	var isPublicComment = $j('#comment' + commentId).attr('publicComment');

	if (!editCommentBox.parent().hasClass("childComment")) {
		var editCommentPublicCommentCheckbox = $j("#editCommentPublicCommentCheckbox");
		if (editCommentPublicCommentCheckbox.is(":visible")) {
			isPublicComment = editCommentPublicCommentCheckbox.attr('checked');
		}
	}
	
	editCommentBox.hide();
	editCommentInProgress.show();
	
	var newText = $j('#editCommentTextarea').val();	
	
	$j.ajax({
		url: mContextPath + '/public/comment/comment.json',
		dataType: 'json',
		contentType: 'application/json',
		data: {action: 'update', commentID: commentId, text: newText, publicComment: isPublicComment},
		success: editCommentInDbResponse,
		cache: false
	});
}

/**
 * Tries to save the new comment in the DB. Return an error message if an error
 * occurred.
 *
 * @param pCommentId
 * @param pNewText
 * @return
 */
function editCommentInDbResponse(pJsonData, pTextStatus, pXmlHttpRequest) {
	var errorMessage = pJsonData.errorMessage;	
	
	if (errorMessage != undefined &&
		errorMessage.length > 0) {
		editCommentError(errorMessage);
		var editCommentInProgress = $j("#editCommentInProgress");
		editCommentInProgress.hide();
		
		var editCommentBox = $j("#editCommentBox");
		editCommentBox.show();
	}
	else {
		// Update the comment and display it.
		var commentID = pJsonData.commentID;
		var newText = pJsonData.text;
		var commentSpan = $j("#commentSpan" + commentID);
		commentSpan.html(newText);
		// Update the value of the publicComment attribute.
		$j('#comment' + commentID).attr("publicComment", pJsonData.publicComment);
		
		var newTextWithoutHref = pJsonData.textWithoutHref;
		
		//var commentDiv = $j("#comment" + commentID);
		//commentDiv.attr("textWithoutHref", newTextWithoutHref);
		
		var commentWithoutHrefDiv = $j("#commentWithoutHref" + commentID);
		commentWithoutHrefDiv.html(newTextWithoutHref);
		if (pJsonData.publicComment) {
			$j("#publicIndicatior" + commentID).show();
		}
		else {
			$j("#publicIndicatior" + commentID).hide();
		}
		
		var editCommentInProgress = $j("#editCommentInProgress");
		editCommentInProgress.hide();
		var commentContainerBox = $j("#comment" + commentID);
		commentContainerBox.show();
		addAllEmbeddedStuff(commentID, newText);
	}	
}

/**
 * Add the new comment in the DOM.
 * 
 * @param pCommentId
 * @param pText
 * @param pOwnerName
 * @param pOwnerUrl
 * @param pWallOwnerName
 * @param pWallOwnerUrl
 * @param pCommentDate
 * @param pProfilePictureUrl
 * @param pEditEnabled
 * @param pDeleteEnabled
 * @param pSystemComment
 * @param pAdminComment
 * @param pPublicComment 
 * @param pAddFirst
 * @return
 */
function addCommentInternal(pCommentId, pText, pTextWithoutHref, pOwnerName, pOwnerUrl, pWallOwnerName, pWallOwnerUrl, pCommentDate, 
		pProfilePictureUrl, pEditEnabled, pDeleteEnabled, pSystemComment,
		pAdminComment, pPublicComment, pOtherWallComment, pAddFirst) {
	var containerDiv = $j('#commentsContainer');
	
	var commentBackgroundClass = 'commentBackground';
	if (pAdminComment) {
		commentBackgroundClass = 'adminCommentBackground';
	}
	else if (pOtherWallComment) {
		commentBackgroundClass = 'otherWallCommentBackground';
	}
		
	var headerClass = 'commentHighlightedBgDark';
	if (pAdminComment) {
		headerClass = 'adminCommentHighlightedBgDark';
	}
	else if (pOtherWallComment) {
		headerClass = 'otherWallCommentHighlightedBgDark';
	}
	
	var html = getCommentHtml(pCommentId, pText, pTextWithoutHref, pOwnerName, pOwnerUrl, pWallOwnerName, pWallOwnerUrl, pCommentDate,
			pProfilePictureUrl, "thumbnailOutterTd", pEditEnabled, pDeleteEnabled, pSystemComment, pPublicComment,
			commentBackgroundClass, headerClass);
	
	html =
		'<div id="commentsFamily' + pCommentId + '">' +
		html +
		'</div>';
	
	//html += 
	//	'<div class="childComment" id="replyLink' + pCommentId + '" onClick="showReplyTextarea(' + pCommentId + ');">' +
	//	'<span class="linkStyleActionSmall">REPLY</span>' + 
	//	'</div>';

	if (mAuthorizedToReply && !pAdminComment) {
		html += 
			'<div id="addChildCommentBox' + pCommentId + '" class="childComment">' +
			    '<div id="childCommentLinkContainer' + pCommentId + '" class="gt-form-row gt-width-100 grayColor small pointer" onClick="this.style.display = \'none\'; document.getElementById(\'childCommentTextareaContainer' + pCommentId + '\').style.display = \'block\'; document.getElementById(\'childCommentButtonContainer' + pCommentId + '\').style.display = \'block\'; document.getElementById(\'childCommentTextarea' + pCommentId + '\').focus();">' +
			    	mAddChildCommentDefaultText +
			    '</div>' +
				'<div id="childCommentTextareaContainer' + pCommentId + '" class="gt-form-row gt-width-100" style="display: none;">' +
					'<textarea id="childCommentTextarea' + pCommentId + '" rows="3" style="width: 100%;" class="fontSizeSmall"/>' +
				'</div>' +
				'<div id="childCommentButtonContainer' + pCommentId + '" class="gt-form-row gt-width-100" style="display: none;">' +
					'<table class="buttonsTable">' +
						'<tr>' +
							'<td>' +
								'<span id="childCommentSubmit' + pCommentId +'" class="buttonLight2" onClick="postChildComment('+ pCommentId +');">' + mSubmitButtonText + '</span>' +
							'</td>' +
						'</tr>' +
					'</table>' +
				'</div>' +
			'</div>' +
			// See http://juixe.com/techknow/index.php/2007/01/14/div-align-with-css/
			'<div class="childCommentClear"></div>';
		
		html += 
			'<div id="addChildCommentInProgress' + pCommentId + '" style="display: none;" class="childComment">' +
				'<div class="gt-form-row gt-width-100">' +
					'<center>' +
						'<img src="' + mContextPath + '/public/images/icons/inprogress1.gif"/>' +
					'</center>' +
				'</div>' +
			'</div>' +
			// See http://juixe.com/techknow/index.php/2007/01/14/div-align-with-css/
			'<div class="childCommentClear"></div>';
	}

	if (pAddFirst) {
		containerDiv.prepend(html);
	}
	else {
		containerDiv.append(html);
	}
	
	addAllEmbeddedStuff(pCommentId, pText);
	
	setupSearchField('childCommentTextarea' + pCommentId, mAddChildCommentDefaultText, true);
	//$j("#childCommentSubmit" + pCommentId).button();
}

function showReplyTextarea(pParentCommentId) {
	$j("#replyLink" + pParentCommentId).hide();
	$j("#addChildCommentBox" + pParentCommentId).show();
}

/**
 * Add the new child comment in the DOM.
 * 
 * @param pCommentId
 * @param pChildCommentId
 * @param pText
 * @param pOwnerName
 * @param pOwnerUrl
 * @param pCommentDate
 * @param pProfilePictureUrl
 * @param pEditEnabled
 * @param pDeleteEnabled
 * @param pSystemComment
 * @param pAdminComment
 * @param pPublicComment
 * @param pAddFirst
 * @return
 */
function addChildCommentInternal(pCommentId, pParentCommentId, pText, pTextWithoutHref, pOwnerName, pOwnerUrl, pCommentDate, 
		pProfilePictureUrl, pEditEnabled, pDeleteEnabled, pSystemComment, pAdminComment, pPublicComment, pOtherWallComment, pAddFirst) {
	
	var parentCommentDiv = $j('#commentsFamily' + pParentCommentId);
	
	var commentBackgroundClass = 'childCommentBackground';
	if (pAdminComment) {
		commentBackgroundClass = 'adminChildCommentBackground';
	}
	else if (pOtherWallComment) {
		commentBackgroundClass = 'otherWallChildCommentBackground';
	}
		
	var headerClass = 'commentHighlightedBg';
	if (pAdminComment) {
		headerClass = 'adminChildCommentHighlightedBg';
	}
	else if (pOtherWallComment) {
		headerClass = 'otherWallChildCommentHighlightedBg';
	}
	
	var html = getCommentHtml(pCommentId, pText, pTextWithoutHref, pOwnerName, pOwnerUrl, null, null, pCommentDate, 
		pProfilePictureUrl, "thumbnailOutterTdSmall",  pEditEnabled, pDeleteEnabled, pSystemComment, pPublicComment, commentBackgroundClass, headerClass);
	
	html = 
		'<div class="childComment">' +
		html; +
		'</div>' +
		// See http://juixe.com/techknow/index.php/2007/01/14/div-align-with-css/
		'<div class="childCommentClear"></div>';

	if (pAddFirst) {
		parentCommentDiv.prepend(html);
	}
	else {
		parentCommentDiv.append(html);
	}
	
	addAllEmbeddedStuff(pCommentId, pText);
}

function getCommentHtml(pCommentId, pText, pTextWithoutHref, pOwnerName, pOwnerUrl, pWallOwnerName, pWallOwnerUrl, pCommentDate, 
		pProfilePictureUrl, pThumbnailOutterTdClass, pEditEnabled, pDeleteEnabled, pSystemComment, pPublicComment, pCommentBackgroundClass, pHeaderClass) {
	
	var result = 
	'<div style="display: none;" id="commentWithoutHref' + pCommentId + '">' + pTextWithoutHref + '</div>' +
	'<div class="gt-form-row gt-width-100 commentBox" commentId="' + pCommentId + '" id="comment' + pCommentId + '" publicComment="' + pPublicComment + '">' +
	'<table class="thumbnailOutterTable" width="100%" style="vertical-align: top;"><tr><td class="' + pThumbnailOutterTdClass + '" valign="top">' +
	'	<table class="thumbnailInnerTable"><tr><td valign="top">';
	if (pOwnerUrl) {
		result +=
		'		<a href="' + pOwnerUrl + '">' +
		'			<div class="thumbnailDiv" valign="top">' +
		'				<img src="' + pProfilePictureUrl + '"/>' +
		'			</div>' +
		'        </a>';
	}
	else {
		result +=
		'			<div class="thumbnailDiv" valign="top">' +
		'				<img src="' + mContextPath + '/public/images/icons/dummy_user_small.png"/>' +
		'			</div>';	
	}
	result += 
	'	</td></tr></table>' +
	'</td><td valign="top" class="' + pCommentBackgroundClass + '">' +
	'	<div class="' + pHeaderClass + '">' +
	'		<table width="100%">' +
	'			<tr>' +
	'				<td>';
	if (pOwnerUrl) {
		// Normal comment.
		if (!pSystemComment) {
			result += '					<label class="fontSizeNormalSmall"><a href="' + pOwnerUrl + '">' + pOwnerName + '</a>';
			
			if (mShowOtherWallCommentOwner && pWallOwnerUrl) {
				result += ' >> <a href="' + pWallOwnerUrl + '">' + pWallOwnerName + '</a>'
			}
			
			result += ', ' + pCommentDate + '</label>';
		}
		// System comment in the name of a normal user.
		else {
			result += '					<label class="fontSizeNormalSmall"><a href="' + pOwnerUrl + '">' + pOwnerName + '</a> (system generated), ' + pCommentDate + '</label>';
		}
	}
	// Pure system comment.
	else {
		result += '					<label class="small">System comment, ' + pCommentDate + '</label>';
	}
	result += '				</td>';
	
	
	result += '				<td style="text-align: right;"><label class="small">';
	
	var publicCommentIndicatorStyle = "";
	if (!pPublicComment) {
		publicCommentIndicatorStyle =  'style="display: none;"';
	}
	result += '<span id="publicIndicatior' + pCommentId + '" ' + publicCommentIndicatorStyle + '>';
	result += '<span class="small90"><b>' + mPublicIndicatorLabel + '</b></span>';
	if (pDeleteEnabled || pEditEnabled) {
		result += ' | ';
	}	
	result += '</span>';
	
	
	if (pDeleteEnabled) {
		result += '<span class="linkStyleActionSmall" onClick="removeComment(this);">' + mDeleteCommentLabel + '</span>';
	}
	if (pEditEnabled && pDeleteEnabled) {
		result += ' | ';
	}
	if (pEditEnabled) {
		result += '<span class="linkStyleActionSmall" onClick="editComment(this);">' + mEditCommentLabel + '</span>';
	}
		'				</td>';
	
	result += '</label></td>';
	result += '			</tr>' +
	'		</table>' +
	'	</div>' +
	'	<div class="commentContent"><span class="fontSizeComment" id="commentSpan' + pCommentId + '">' + pText + '</span></div>' +
	'</td></tr></table>' + 
    '</div>';	
		
	return result;
}

function removeYouTubeEmbeddedVideosForComment(pCommentId) {
	$j(".youTubeVideoFor" + pCommentId).remove();	
}

function addAllEmbeddedStuff(pCommentId, pText) {
	addYouTubeEmbeddedVideosForComment(pCommentId, pText);
	addDailymotionEmbeddedVideosForComment(pCommentId, pText);
	addSoundCloudEmbeddedMusicForComment(pCommentId, pText);
}

function addYouTubeEmbeddedVideosForComment(pCommentId, pText) {
	removeYouTubeEmbeddedVideosForComment(pCommentId);
	appendYouTubeVideosFromText(pText, "youTubeVideoFor" + pCommentId, 72, 320, 193, "comment" + pCommentId);
}

function removeDailymotionEmbeddedVideosForComment(pCommentId) {
	$j(".dailymotionVideoFor" + pCommentId).remove();	
}

function addDailymotionEmbeddedVideosForComment(pCommentId, pText) {
	removeDailymotionEmbeddedVideosForComment(pCommentId);
	appendDailymotionVideosFromText(pText, "dailymotionVideoFor" + pCommentId, 72, 320, 240, "comment" + pCommentId);
	
}

function removeSoundCloudEmbeddedMusicForComment(pCommentId) {
	$j(".soundCloudFor" + pCommentId).remove();	
}

function addSoundCloudEmbeddedMusicForComment(pCommentId, pText) {
	removeSoundCloudEmbeddedMusicForComment(pCommentId);
	appendSoundCloudFromText(pText, "soundCloudFor" + pCommentId, 72, "comment" + pCommentId);
}

/**
 * Returns text where new line characters are replaced with <br/>
 * @param pText
 * @return
 */
function nl2br(pText) {
	return pText.replace(/\n/g, '<br/>');
}

function br2nl(pText) {
	var result = pText.replace(/<br>/g, "\n");
	result = result.replace(/<BR>/g, "\n");
	result = result.replace(/<br\/>/g, "\n");
	result = result.replace(/<BR\/>/g, "\n");
	
	return result;
}