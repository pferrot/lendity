var mAddCommentDefaultText = '';
var mContextPath = "";
// The number of comments that is displayed.
var mNbCommentsLoaded = 0;
// The number of comments that has to be loaded at a time.
// It is also the default number of comments.
var mMaxResults = 10;

var mEditCommentLabel = "";
var mDeleteCommentLabel = "";

/**
 * Called when an item overview page is loaded in order to load the first comments.
 * 
 * @param pItemId
 * @return
 */
function initComments(pContainerId, pContainerType, pContextPath, pAddCommentDefaultText, pEditCommentLabel, pDeleteCommentLabel) {
	mContextPath = pContextPath;
	mAddCommentDefaultText = pAddCommentDefaultText;
	mEditCommentLabel = pEditCommentLabel;
	mDeleteCommentLabel = pDeleteCommentLabel;
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
}

/**
 * The button to add a new button is pressed.
 * 
 * @return
 */
function postComment(pContainerId, pContainerType) {
	addCommentInProgress();
	addCommentInDb(pContainerId, pContainerType);
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


/**
 * Adds the comment in the DB. Returns an error message if error occurred.
 * 
 * @return
 */
function addCommentInDbInternalForItem(pItemId) {
	var commentTextarea = $j('#commentTextarea');
	var text = commentTextarea.val();
	text = $j.trim(text);
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
		addCommentInternal(comment.commentID, comment.text, comment.ownerName, comment.ownerUrl,
				comment.dateAdded, comment.profilePictureUrl, comment.canEdit, comment.systemComment, false);
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
		commentBox.remove();
		mNbCommentsLoaded = mNbCommentsLoaded - 1;
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
		var ownerName = pData.ownerName;
		var ownerUrl = pData.ownerUrl;
		var dateAdded = pData.dateAdded;
		var profilePictureUrl = pData.profilePictureUrl;
		var canEdit = pData.canEdit;
		var systemComment = pData.systemComment;
		addCommentInternal(commentId, text, ownerName, ownerUrl, dateAdded, profilePictureUrl, canEdit, systemComment, true);
		resetCommentTextArea();
		mNbCommentsLoaded = mNbCommentsLoaded + 1;
		hideNoCommentDiv();
	}
	addCommentStopProgress();
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
	var commentSpan = $j("#commentSpan" + pCommentId);
	return br2nl(commentSpan.html()); 
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
	
	editCommentBox.hide();
	editCommentInProgress.show();
	
	var newText = $j('#editCommentTextarea').val();
	
	
	$j.ajax({
		url: mContextPath + '/public/comment/comment.json',
		dataType: 'json',
		contentType: 'application/json',
		data: {action: 'update', commentID: commentId, text: newText},
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
		
		var editCommentInProgress = $j("#editCommentInProgress");
		editCommentInProgress.hide();
		var commentContainerBox = $j("#comment" + commentID);
		commentContainerBox.show();
	}	
}

/**
 * Add the new comment in the DOM.
 * 
 * @param pCommentId
 * @param pText
 * @param pOwnerName
 * @param pOwnerUrl
 * @param pCommentDate
 * @param pProfilePictureUrl
 * @param pEditEnabled
 * @param pSystemComment
 * @param pAddFirst
 * @return
 */
function addCommentInternal(pCommentId, pText, pOwnerName, pOwnerUrl, pCommentDate, 
		pProfilePictureUrl, pEditEnabled, pSystemComment, pAddFirst) {
	var containerDiv = $j('#commentsContainer');
	
	var toPrepend = '<div class="gt-form-row gt-width-100 commentBox" commentId="' + pCommentId + '" id="comment' + pCommentId + '">' +
	'<table class="thumbnailOutterTable" width="100%" style="vertical-align: top;"><tr><td class="thumbnailOutterTd" valign="top">' +
	'	<table class="thumbnailInnerTable"><tr><td valign="top">';
	if (pOwnerUrl) {
		toPrepend +=
		'		<a href="' + pOwnerUrl + '">' +
		'			<div class="thumbnailDiv" valign="top">' +
		'				<img src="' + pProfilePictureUrl + '"/>' +
		'			</div>' +
		'        </a>';
	}
	else {
		toPrepend +=
		'			<div class="thumbnailDiv" valign="top">' +
		'				<img src="' + mContextPath + '/public/images/icons/dummy_user_small.png"/>' +
		'			</div>';	
	}
	toPrepend += 
	'	</td></tr></table>' +
	'</td><td valign="top">' +
	'	<div class="highlightedBgLight">' +
	'		<table width="100%">' +
	'			<tr>' +
	'				<td>';
	if (pOwnerUrl) {
		// Normal comment.
		if (!pSystemComment) {
			toPrepend += '					<label class="small"><a href="' + pOwnerUrl + '">' + pOwnerName + '</a>, ' + pCommentDate + '</label>';
		}
		// System comment in the name of a normal user.
		else {
			toPrepend += '					<label class="small"><a href="' + pOwnerUrl + '">' + pOwnerName + '</a> (system generated), ' + pCommentDate + '</label>';
		}
	}
	// Pure system comment.
	else {
		toPrepend += '					<label class="small">System comment, ' + pCommentDate + '</label>';
	}
	toPrepend += '				</td>';
	if (pEditEnabled) {
		toPrepend += '				<td style="text-align: right;">' +
			'					<label class="small"><span class="linkStyleActionSmall" onClick="removeComment(this);">' + mDeleteCommentLabel + '</span> | <span class="linkStyleActionSmall" onClick="editComment(this);">' + mEditCommentLabel + '</span></label>' +
			'				</td>';
	}
	toPrepend += '			</tr>' +
	'		</table>' +
	'	</div>' +
	'	<span class="fontSizeSmall" id="commentSpan' + pCommentId + '">' + pText + '</span>' +
	'</td></tr></table>' + 
    '</div>';

	if (pAddFirst) {
		containerDiv.prepend(toPrepend);
	}
	else {
		containerDiv.append(toPrepend);
	}
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