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
 * Called when an internal item overview page is loaded in order to load the first comments.
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
		loadComments(pContainerId, undefined);
	}
	else if (pContainerType == "need") {
		loadComments(undefined, pContainerId);
	}	
}

function loadMoreComments(pContainerId, pContainerType) {
	if (pContainerType == "item") {
		loadMoreCommentsInternal(pContainerId, undefined);
	}
	else if (pContainerType == "need") {
		loadMoreCommentsInternal(undefined, pContainerId);
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
		addCommentInDbInternal(pContainerId, undefined);
	}
	else if (pContainerType == "need") {
		addCommentInDbInternal(undefined, pContainerId);
	}
}

function positionEditCommentInProgressBottom() {
	var editCommentInProgress = $j("#editCommentInProgress");
	var commentsContainer = $j("#commentsContainer");
	editCommentInProgress.insertAfter(commentsContainer);
}

function loadComments(pItemId, pNeedId) {
	addCommentInProgress();
	$j.ajax({
		url: mContextPath + '/auth/comment/comment.json',
		dataType: 'json',
		contentType: 'application/json',
		data: {action: 'read', itemID: pItemId, needID: pNeedId, firstResult: mNbCommentsLoaded, maxResults: mMaxResults},
		success: loadCommentsResponse
	});
}


/**
 * Adds the comment in the DB. Returns an error message if error occurred.
 * 
 * @return
 */
function addCommentInDbInternal(pItemId, pNeedId) {
	var commentTextarea = $j('#commentTextarea');
	var text = commentTextarea.val();
	text = $j.trim(text);
	// Add in DB.
	$j.ajax({
			url: mContextPath + '/auth/comment/comment.json',
			dataType: 'json',
			contentType: 'application/json',
			data: {action: 'create', itemID: pItemId, needID: pNeedId, text: text},
			success: addCommentInDbResponse
	});
}

function loadMoreCommentsInternal(pItemId, pNeedId) {
	hideLoadExtraCommentsDiv();	
	
	positionEditCommentInProgressBottom();
	editCommentInProgress();
	
	$j.ajax({
		url: mContextPath + '/auth/comment/comment.json',
		dataType: 'json',
		contentType: 'application/json',
		data: {action: 'read', itemID: pItemId, needID: pNeedId, firstResult: mNbCommentsLoaded, maxResults: mMaxResults},
		success: loadCommentsResponse
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
				comment.dateAdded, comment.profilePictureUrl, comment.canEdit, false);
	}
	mNbCommentsLoaded = mNbCommentsLoaded + nb;
	var nbExtra = pJsonData.nbExtra;
	if (nbExtra > 0) {
		showLoadExtraCommentsDiv(nbExtra);
	}
	else {
		hideLoadExtraCommentsDiv();
	}
	
}

function showLoadExtraCommentsDiv(pNbExtra) {
	$j("#nbExtraComments").html(pNbExtra);
	$j("#loadMoreCommentsContainer").show();
}

function hideLoadExtraCommentsDiv() {
	$j("#loadMoreCommentsContainer").hide();
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
		url: mContextPath + '/auth/comment/comment.json',
		dataType: 'json',
		contentType: 'application/json',
		data: {action: 'delete', commentID: commentId},
		success: removeCommentFromDbResponse
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
		addCommentInternal(commentId, text, ownerName, ownerUrl, dateAdded, profilePictureUrl, canEdit, true);
		resetCommentTextArea();
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
		url: mContextPath + '/auth/comment/comment.json',
		dataType: 'json',
		contentType: 'application/json',
		data: {action: 'update', commentID: commentId, text: newText},
		success: editCommentInDbResponse
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
 * @return
 */
function addCommentInternal(pCommentId, pText, pOwnerName, pOwnerUrl, pCommentDate, 
		pProfilePictureUrl, pEditEnabled, pAddFirst) {
	var containerDiv = $j('#commentsContainer');
	
	var toPrepend = '<div class="gt-form-row gt-width-100 commentBox" commentId="' + pCommentId + '" id="comment' + pCommentId + '">' +
	'<table class="thumbnailOutterTable" width="100%" style="vertical-align: top;"><tr><td class="thumbnailOutterTd" valign="top">' +
	'	<table class="thumbnailInnerTable"><tr><td valign="top">' +
	'		<a href="' + pOwnerUrl + '">' +
	'			<div class="thumbnailDiv" valign="top">' +
	'				<img src="' + pProfilePictureUrl + '"/>' +
	'			</div>' +
	'        </a>' +
	'	</td></tr></table>' +
	'</td><td valign="top">' +
	'	<div class="highlightedBgLight">' +
	'		<table width="100%">' +
	'			<tr>' +
	'				<td>' +
	'					<label class="small"><a href="' + pOwnerUrl + '">' + pOwnerName + '</a>, ' + pCommentDate + '</label>' +
	'				</td>';
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