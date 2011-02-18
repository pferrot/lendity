function limitTextareaLength(pTextarea, pMaxLength) {
	pTextarea.value = pTextarea.value.slice(0, pMaxLength);
}

function makeQtipTitles() {
	$j('.qtipTitle').qtip({
	    content: {
	    	text: false // Use each elements title attribute
	    },
	    style: {
			background: '#094e62',
		    color: '#ffffff',
			border: {
				width: 1,
				radius: 5,
				color: '#073351'
			},
			padding: 0, 
			textAlign: 'center',
			tip: true // Give it a speech bubble tip with automatic corner detection
		},
		position: {
			corner: {
				target: 'topMiddle',
				tooltip: 'bottomMiddle'
			}
		}

	});
}

/*
 * Color the input fields that make an error.
 */
function colorErrorFields() {
  $j(document).ready(function() {
    $j("span.error").next().addClass('validationError');        
  });
}

function toggleVisibility(pElementId) {
	$j("#" + pElementId).toggle();
}

function focusFirtTextInputField() {
	$j(document).ready(function() {
		// This will not include the search filter, which is good.
        $j(".gt-form-text:first").focus();
    });
}

function editPictureOnChange(pContextPath) {
	document.getElementById('contentDiv').style.display = 'none';
    var inProgressDiv = document.getElementById('inProgressDiv');
    inProgressDiv.innerHTML = '<center><img src="' + pContextPath + '/public/images/icons/inprogress1.gif"/></center>';
    inProgressDiv.style.display = 'block';
    document.getElementById('processImageButton').click();	
}

/*
 * That function setup an input text field so that:
 * - the default content is the text parameter if empty (use to indicate what that
 *   search box if for)
 * - the default content is automatically deleted when focusing
 * - the default content is added when leaving the field and it is empty
 * - the form is submitted when pressing enter
 */
function setupSearchField(pFieldId, pClearSearchId, pText) {
  $j(document).ready(function() {
    if ($j("#" + pFieldId).val() == '' ||
        $j("#" + pFieldId).val() == pText) {
      $j("#" + pFieldId).val(pText);
      $j("#" + pFieldId).addClass('grayColor');
      //$j("#" + pClearSearchId).hide();
    }
    else {
      if (pClearSearchId) {
        $j("#" + pClearSearchId).show();
        $j("#" + pFieldId).addClass("filterActive");
      }
    }
    $j("#" + pFieldId).focus(function() {
	  if(this.value == pText ) {
	    this.value = '';
	    $j("#" + pFieldId).removeClass('grayColor');
	  }
    });
    $j("#" + pFieldId).blur(function() {
	  if(this.value == '') {
	    this.value = pText;
	    $j("#" + pFieldId).addClass('grayColor');
	  }
    });
    //$j("#" + pFieldId).bind('keypress', function(e) {
      //var code = (e.keyCode ? e.keyCode : e.which);
      // Enter keycode.  
      //if(code == 13) { 
        //this.submit();
      //}
    //});    
  });
}

function setupDropDownFilterField(pFieldId) {
  $j(document).ready(function() {
	if ($j("#" + pFieldId).val() && $j("#" + pFieldId).val() != '') {
	  $j("#" + pFieldId).addClass("filterActive");
    }
  });
}

/*
 * That method with add two fake text input fields to every form in order to prevent
 * the form from being submitted automatically with firefox when pressing enter.
 */
//function addTwoHiddenFields() {
//  $j(document).ready(function() {
//    $j("span.error").next().css('background-color', '#f8b6a1');        
//  });
//}


/*
 * Fully destroy a qTip.
 */
function destroyQtip(pQtipTarget) {
	var qtipTargetJquery = $j(pQtipTarget);
	if (qtipTargetJquery.data("qtip")) {
		qtipTargetJquery.qtip("destroy");
		qtipTargetJquery.removeData("qtip");
	}
}

/*
 * Callback method called when the qtip is hidden.
 */
function qtipOnHide(pContent, pTarget, pJqueryDiv) {
	// TODO: if the qtip still contains the form, move it in its original div.
	// This occurs when opening another tooltip while one it opened. E.g. click on the
	// link to open the "lend back" tooltip when the "lend" tooltip is opened.
	if (pContent) {
		var contentHtml = pContent.html();
		if (contentHtml && contentHtml.length > 0) {
			pJqueryDiv.append(contentHtml);
		}
	}
	destroyQtip(pTarget);
}

/*
 * Replace the form in its original location: the div.
 */
function hideTooltip(pTooltipTarget, pJqueryForm, pJqueryDiv) {
    var formParent = pJqueryForm.parent();
  	var formParentContent = formParent.html();
  	pJqueryDiv.append(formParentContent);
  	
  	destroyQtip(pTooltipTarget);
}

function createFormTooltip(pJqueryTooltipTarget, pForm, pOnHideCallbackMethod) {
	var formParent = pForm.parent();
  	var formParentContent = formParent.html();
  	
  	var result = pJqueryTooltipTarget.qtip({
		content: {
			text: formParentContent
			//title: {
		    //  text: 'About Me'
		   //}
		},
		show: {
		      when: false, // Don't specify a show event
		      ready: true, // Show the tooltip when ready
		      solo: true,
		      delay: 0
		},
		hide: false,
		position: {
			corner: {
				target: 'topMiddle',
				tooltip: 'bottomMiddle'
			}
		},
		style: {
			background: '#094e62',
		    color: '#ffffff',
			border: {
				width: 1,
				radius: 5,
				color: '#073351'
			},
			padding: 0, 
			textAlign: 'center',
			tip: true // Give it a speech bubble tip with automatic corner detection
		}
	});
		
	var api = $j(result).qtip("api");
	// This is triggered right after another tooltip is opened due to the "solo" mode.
	api.onHide = pOnHideCallbackMethod;
	// Remove the form from its previous location - it must never be duplicated.
	formParent.empty();
	// Not sure why but the hover effect of the buttons in the tooltip does not work without this.	
	$j('.stylishButton').removeClass('ui-state-hover');
	$j('.stylishButton').hover(
		function(){ $j(this).addClass('ui-state-hover'); }, 
		function(){ $j(this).removeClass('ui-state-hover'); }
	);
	
	
}


/***************************************************************************************************
 * 
 * LEND ITEM
 * 
 ***************************************************************************************************/
/*
 * Replace the form in its original location: the div "lendDiv".
 */
function hideLendItemTooltip(pTooltipTarget) {
	hideTooltip(pTooltipTarget, $j('#lendForm'), $j('#lendDiv'));
}

function lendQtipOnHide(pEvent) {
	qtipOnHide(this.elements['content'], this.elements['target'], $j('#lendDiv'));
}

/*
 * That method will display / hide the tooltip that is used to lend an item.
 */
var mLendItemTooltip;
var mLendItemTooltipTarget;
function lendItemTooltip(pTooltipTarget, pItemID, pRedirectID) {
  // The tooltip is just closed.
  if ($j(pTooltipTarget).data("qtip")) {
	  hideLendItemTooltip(pTooltipTarget);
  }
  // The tooltip is opened.
  else {
	  	// Reset the form when it is displayed.
	    document.getElementById('lendForm').reset();
	    $j('#lendBorrowerId').removeClass("validationError");
	    $j('#lendBorrowerName').removeClass("validationError");
		// Set the correct itemID.
		$j('#lendItemId').val(pItemID);		
		// Set the correct redirectID.
		$j('#lendRedirectId').val(pRedirectID);		
			  	 
	  	mLendItemTooltipTarget = pTooltipTarget;
		mLendItemTooltip = createFormTooltip($j(pTooltipTarget), $j('#lendForm'), lendQtipOnHide);
		
		createDatePicker($j("#lendBorrowDate"));
	}    
}

function createDatePicker(pJqueryInputField) {
	pJqueryInputField.datepicker( "destroy" );
	pJqueryInputField.datepicker({ dateFormat: 'dd.mm.yy', 
        dayNames: ['Dimanche', 'Lundi', 'Mardi', 'Mercredi', 'Jeudi', 'Vendredi', 'Samedi'],
        dayNamesMin: ['Di', 'Lu', 'Ma', 'Me', 'Je', 'Ve', 'Sa'],
        dayNamesShort: ['Dim', 'Lun', 'Mar', 'Mer', 'Jeu', 'Ven', 'Sam'],
        firstDay: 1,
        monthNames: ['Janvier','F&eacute;vrier','Mars','Avril','Mai','Juin','Juillet','Ao&ucirc;t','Septembre','Octobre','Novembre','D&eacute;cembre'],
        monthNamesShort: ['Jan','F&eacute;v','Mar','Avr','Mai','Jui','Jul','Ao&ucirc;','Sep','Oct','Nov','D&eacute;c']});

	pJqueryInputField.attr( 'readOnly' , 'true' );		
}

/*
 * Click the submit button in the tooltip when an item is lent.
 */
function submitLendItem() {	
	var borrowerDropDown = document.getElementById('lendBorrowerId');
	var jqueryBorrowerName = $j("#lendBorrowerName")
	if ($j("#lendBorrowerId").is(":visible") && 
	    borrowerDropDown.selectedIndex == 0) {
		$j(borrowerDropDown).addClass("validationError");
	}
	else if (jqueryBorrowerName.is(":visible") &&
			jqueryBorrowerName.val() == '') {
		jqueryBorrowerName.addClass("validationError");
	}
	else {
		document.getElementById("lendActionButton").click();
	}	
}

/*
 * Click the cancel button to close the lend item tooltip.
 */
function cancelLendItem() {
	hideLendItemTooltip(mLendItemTooltipTarget);
}

function enableLendBorrowerId() {
	$j("#lendBorrowerId").show();
	$j("#lendBorrowerToName").show();
	$j("#lendBorrowerName").hide();
	$j("#lendBorrowerToId").hide();
	
	resetLendBorrowerFields();	
}

function enableLendBorrowerName() {
	$j("#lendBorrowerName").show();
	$j("#lendBorrowerToId").show();
	$j("#lendBorrowerId").hide();
	$j("#lendBorrowerToName").hide();
	
	resetLendBorrowerFields();
}

function resetLendBorrowerFields() {
	// Maybe a bug in jQuery? Does not work in Safari 5 with the jQuery.val function.
	//$j("#lendBorrowerId").val(0);
	document.getElementById('lendBorrowerId').options[0].selected = true;
	$j("#lendBorrowerName").val("");
	$j('#lendBorrowerId').removeClass("validationError");
	$j('#lendBorrowerName').removeClass("validationError");	    
}

/***************************************************************************************************
 * 
 * LEND BACK ITEM
 * 
 ***************************************************************************************************/
function hideLendBackItemTooltip(pTooltipTarget) {
	hideTooltip(pTooltipTarget, $j('#lendBackForm'), $j('#lendBackDiv'));
}

function lendBackQtipOnHide(pEvent) {
	qtipOnHide(this.elements['content'], this.elements['target'], $j('#lendBackDiv'));
}

/*
 * That method will display / hide the tooltip that is used to lend an item.
 */
var mLendBackItemTooltip;
var mLendBackItemTooltipTarget;
function lendBackItemTooltip(pTooltipTarget, pItemID, pRedirectID) {
  // The tooltip is just closed.
  if ($j(pTooltipTarget).data("qtip")) {
	  hideLendBackItemTooltip(pTooltipTarget);
  }
  // The tooltip is opened.
  else {
	  	// Reset the form when it is displayed.
	    document.getElementById('lendBackForm').reset();
		// Set the correct itemID.
		$j('#lendBackItemId').val(pItemID);
		// Set the correct redirectID.
		$j('#lendBackRedirectId').val(pRedirectID);
		
	  	mLendBackItemTooltipTarget = pTooltipTarget;
	  	mLendBackItemTooltip = createFormTooltip($j(pTooltipTarget), $j('#lendBackForm'), lendBackQtipOnHide);
	}    
}

/*
 * Click the submit button in the tooltip when an item is back.
 */
function submitLendBackItem() {	
	document.getElementById("lendBackActionButton").click();
}

/*
 * Click the cancel button to close the lend back item tooltip.
 */
function cancelLendBackItem() {
	hideLendBackItemTooltip(mLendBackItemTooltipTarget);
}

/***************************************************************************************************
 * 
 * DELETE INTERNAL ITEM
 * 
 ***************************************************************************************************/
function hideDeleteInternalItemTooltip(pTooltipTarget) {
	hideTooltip(pTooltipTarget, $j('#deleteInternalItemForm'), $j('#deleteInternalItemDiv'));
}

function deleteInternalItemQtipOnHide(pEvent) {
	qtipOnHide(this.elements['content'], this.elements['target'], $j('#deleteInternalItemDiv'));
}

/*
 * That method will display / hide the tooltip that is used to lend an item.
 */
var mDeleteInternalItemTooltip;
var mDeleteInternalItemTarget;
function deleteInternalItemTooltip(pTooltipTarget, pItemID, pRedirectID) {
  // The tooltip is just closed.
  if ($j(pTooltipTarget).data("qtip")) {
	  hideDeleteInternalItemTooltip(pTooltipTarget);
  }
  // The tooltip is opened.
  else {
	  	// Reset the form when it is displayed.
	    document.getElementById('deleteInternalItemForm').reset();
		// Set the correct itemID.
		$j('#deleteInternalItemId').val(pItemID);
		// Set the correct redirectID.
		$j('#deleteInternalItemRedirectId').val(pRedirectID);
		
	  	mDeleteInternalItemTooltipTarget = pTooltipTarget;
	  	mDeleteInternalItemTooltip = createFormTooltip($j(pTooltipTarget), $j('#deleteInternalItemForm'), deleteInternalItemQtipOnHide);
	}    
}

/*
 * Click the submit button in the tooltip when an item is back.
 */
function submitDeleteInternalItem() {	
	document.getElementById("deleteInternalItemActionButton").click();
}

/*
 * Click the cancel button to close the lend back item tooltip.
 */
function cancelDeleteInternalItem() {
	hideDeleteInternalItemTooltip(mDeleteInternalItemTooltipTarget);
}

/***************************************************************************************************
 * 
 * DELETE NEED
 * 
 ***************************************************************************************************/
function hideDeleteNeedTooltip(pTooltipTarget) {
	hideTooltip(pTooltipTarget, $j('#deleteNeedForm'), $j('#deleteNeedDiv'));
}

function deleteNeedQtipOnHide(pEvent) {
	qtipOnHide(this.elements['content'], this.elements['target'], $j('#deleteNeedDiv'));
}

/*
 * That method will display / hide the tooltip that is used to delete a need.
 */
var mDeleteNeedTooltip;
var mDeleteNeedTarget;
function deleteNeedTooltip(pTooltipTarget, pNeedID, pRedirectID) {
  // The tooltip is just closed.
  if ($j(pTooltipTarget).data("qtip")) {
	  hideDeleteNeedTooltip(pTooltipTarget);
  }
  // The tooltip is opened.
  else {
	  	// Reset the form when it is displayed.
	    document.getElementById('deleteNeedForm').reset();
		// Set the correct needID.
		$j('#deleteNeedId').val(pNeedID);
		// Set the correct redirectID.
		$j('#deleteNeedRedirectId').val(pRedirectID);
		
	  	mDeleteNeedTooltipTarget = pTooltipTarget;
	  	mDeleteNeedTooltip = createFormTooltip($j(pTooltipTarget), $j('#deleteNeedForm'), deleteNeedQtipOnHide);
	}    
}

/*
 * Click the submit button.
 */
function submitDeleteNeed() {	
	document.getElementById("deleteNeedActionButton").click();
}

/*
 * Click the cancel button.
 */
function cancelDeleteNeed() {
	hideDeleteNeedTooltip(mDeleteNeedTooltipTarget);
}

/***************************************************************************************************
 * 
 * REQUEST CONNECTION
 * 
 ***************************************************************************************************/
function hideRequestConnectionTooltip(pTooltipTarget) {
	hideTooltip(pTooltipTarget, $j('#requestConnectionForm'), $j('#requestConnectionDiv'));
}

function requestConnectionQtipOnHide(pEvent) {
	qtipOnHide(this.elements['content'], this.elements['target'], $j('#requestConnectionDiv'));
}

/*
 * That method will display / hide the tooltip.
 */
var mRequestConnectionTooltip;
var mRequestConnectionTooltipTarget;
function requestConnectionTooltip(pTooltipTarget, pPersonID, pRedirectID) {
  // The tooltip is just closed.
  if ($j(pTooltipTarget).data("qtip")) {
	  hideRequestConnectionTooltip(pTooltipTarget);
  }
  // The tooltip is opened.
  else {
	  	// Reset the form when it is displayed.
	    document.getElementById('requestConnectionForm').reset();
		// Set the correct ID.
		$j('#requestConnectionPersonId').val(pPersonID);
		// Set the correct redirectID.
		$j('#requestConnectionRedirectId').val(pRedirectID);
		
	  	mRequestConnectionTooltipTarget = pTooltipTarget;
	  	mRequestConnectionTooltip = createFormTooltip($j(pTooltipTarget), $j('#requestConnectionForm'), requestConnectionQtipOnHide);
	}    
}

/*
 * Click the submit button in the tooltip.
 */
function submitRequestConnection() {
	document.getElementById("requestConnectionActionButton").click();
}

/*
 * Click the cancel button to close the tooltip.
 */
function cancelRequestConnection() {
	hideRequestConnectionTooltip(mRequestConnectionTooltipTarget);
}

/***************************************************************************************************
 * 
 * ACCEPT CONNECTION
 * 
 ***************************************************************************************************/
function hideAcceptConnectionTooltip(pTooltipTarget) {
	hideTooltip(pTooltipTarget, $j('#acceptConnectionForm'), $j('#acceptConnectionDiv'));
}

function acceptConnectionQtipOnHide(pEvent) {
	qtipOnHide(this.elements['content'], this.elements['target'], $j('#acceptConnectionDiv'));
}

/*
 * That method will display / hide the tooltip.
 */
var mAcceptConnectionTooltip;
var mAcceptConnectionTooltipTarget;
function acceptConnectionTooltip(pTooltipTarget, pConnectionRequestID, pRedirectID) {
  // The tooltip is just closed.
  if ($j(pTooltipTarget).data("qtip")) {
	  hideAcceptConnectionTooltip(pTooltipTarget);
  }
  // The tooltip is opened.
  else {
	  	// Reset the form when it is displayed.
	    document.getElementById('acceptConnectionForm').reset();
		// Set the correct ID.
		$j('#acceptConnectionConnectionRequestId').val(pConnectionRequestID);
		// Set the correct redirectID.
		$j('#acceptConnectionRedirectId').val(pRedirectID);
		
	  	mAcceptConnectionTooltipTarget = pTooltipTarget;
	  	mAcceptConnectionTooltip = createFormTooltip($j(pTooltipTarget), $j('#acceptConnectionForm'), acceptConnectionQtipOnHide);
	}    
}

/*
 * Click the submit button in the tooltip.
 */
function submitAcceptConnection() {	
	document.getElementById("acceptConnectionActionButton").click();
}

/*
 * Click the cancel button to close the tooltip.
 */
function cancelAcceptConnection() {
	hideAcceptConnectionTooltip(mAcceptConnectionTooltipTarget);
}

/***************************************************************************************************
 * 
 * REFUSE CONNECTION
 * 
 ***************************************************************************************************/
function hideRefuseConnectionTooltip(pTooltipTarget) {
	hideTooltip(pTooltipTarget, $j('#refuseConnectionForm'), $j('#refuseConnectionDiv'));
}

function refuseConnectionQtipOnHide(pEvent) {
	qtipOnHide(this.elements['content'], this.elements['target'], $j('#refuseConnectionDiv'));
}

/*
 * That method will display / hide the tooltip.
 */
var mRefuseConnectionTooltip;
var mRefuseConnectionTooltipTarget;
function refuseConnectionTooltip(pTooltipTarget, pConnectionRequestID, pRedirectID) {
  // The tooltip is just closed.
  if ($j(pTooltipTarget).data("qtip")) {
	  hideRefuseConnectionTooltip(pTooltipTarget);
  }
  // The tooltip is opened.
  else {
	  	// Reset the form when it is displayed.
	    document.getElementById('refuseConnectionForm').reset();
		// Set the correct ID.
		$j('#refuseConnectionConnectionRequestId').val(pConnectionRequestID);
		// Set the correct redirectID.
		$j('#refuseConnectionRedirectId').val(pRedirectID);
		
	  	mRefuseConnectionTooltipTarget = pTooltipTarget;
	  	mRefuseConnectionTooltip = createFormTooltip($j(pTooltipTarget), $j('#refuseConnectionForm'), refuseConnectionQtipOnHide);
	}    
}

/*
 * Click the submit button in the tooltip.
 */
function submitRefuseConnection() {	
	document.getElementById("refuseConnectionActionButton").click();
}

/*
 * Click the cancel button to close the tooltip.
 */
function cancelRefuseConnection() {
	hideRefuseConnectionTooltip(mRefuseConnectionTooltipTarget);
}

/***************************************************************************************************
 * 
 * BAN CONNECTION
 * 
 ***************************************************************************************************/
function hideBanConnectionTooltip(pTooltipTarget) {
	hideTooltip(pTooltipTarget, $j('#banConnectionForm'), $j('#banConnectionDiv'));
}

function banConnectionQtipOnHide(pEvent) {
	qtipOnHide(this.elements['content'], this.elements['target'], $j('#banConnectionDiv'));
}

/*
 * That method will display / hide the tooltip.
 */
var mBanConnectionTooltip;
var mBanConnectionTooltipTarget;
function banConnectionTooltip(pTooltipTarget, pConnectionRequestID, pRedirectID) {
  // The tooltip is just closed.
  if ($j(pTooltipTarget).data("qtip")) {
	  hideBanConnectionTooltip(pTooltipTarget);
  }
  // The tooltip is opened.
  else {
	  	// Reset the form when it is displayed.
	    document.getElementById('banConnectionForm').reset();
		// Set the correct ID.
		$j('#banConnectionConnectionRequestId').val(pConnectionRequestID);
		// Set the correct redirectID.
		$j('#banConnectionRedirectId').val(pRedirectID);
		
	  	mBanConnectionTooltipTarget = pTooltipTarget;
	  	mBanConnectionTooltip = createFormTooltip($j(pTooltipTarget), $j('#banConnectionForm'), banConnectionQtipOnHide);
	}    
}

/*
 * Click the submit button in the tooltip.
 */
function submitBanConnection() {	
	document.getElementById("banConnectionActionButton").click();
}

/*
 * Click the cancel button to close the tooltip.
 */
function cancelBanConnection() {
	hideBanConnectionTooltip(mBanConnectionTooltipTarget);
}

/***************************************************************************************************
 * 
 * REQUEST LEND
 * 
 ***************************************************************************************************/
function hideRequestLendTooltip(pTooltipTarget) {
	hideTooltip(pTooltipTarget, $j('#requestLendForm'), $j('#requestLendDiv'));
}

function requestLendQtipOnHide(pEvent) {
	qtipOnHide(this.elements['content'], this.elements['target'], $j('#requestLendDiv'));
}

/*
 * That method will display / hide the tooltip.
 */
var mRequestLendTooltip;
var mRequestLendTooltipTarget;
function requestLendTooltip(pTooltipTarget, pItemID, pRedirectID) {
  // The tooltip is just closed.
  if ($j(pTooltipTarget).data("qtip")) {
	  hideRequestLendTooltip(pTooltipTarget);
  }
  // The tooltip is opened.
  else {
	  	// Reset the form when it is displayed.
	    document.getElementById('requestLendForm').reset();
		// Set the correct ID.
		$j('#requestLendItemId').val(pItemID);
		// Set the correct redirectID.
		$j('#requestLendRedirectId').val(pRedirectID);
		
	  	mRequestLendTooltipTarget = pTooltipTarget;
	  	mRequestLendTooltip = createFormTooltip($j(pTooltipTarget), $j('#requestLendForm'), requestLendQtipOnHide);
	}    
}

/*
 * Click the submit button in the tooltip.
 */
function submitRequestLend() {	
	document.getElementById("requestLendActionButton").click();
}

/*
 * Click the cancel button to close the tooltip.
 */
function cancelRequestLend() {
	hideRequestLendTooltip(mRequestLendTooltipTarget);
}

/***************************************************************************************************
 * 
 * ACCEPT LEND
 * 
 ***************************************************************************************************/
function hideAcceptLendTooltip(pTooltipTarget) {
	hideTooltip(pTooltipTarget, $j('#acceptLendForm'), $j('#acceptLendDiv'));
}

function acceptLendQtipOnHide(pEvent) {
	qtipOnHide(this.elements['content'], this.elements['target'], $j('#acceptLendDiv'));
}

/*
 * That method will display / hide the tooltip.
 */
var mAcceptLendTooltip;
var mAcceptLendTooltipTarget;
function acceptLendTooltip(pTooltipTarget, pLendRequestID, pRedirectID) {
  // The tooltip is just closed.
  if ($j(pTooltipTarget).data("qtip")) {
	  hideAcceptLendTooltip(pTooltipTarget);
  }
  // The tooltip is opened.
  else {
	  	// Reset the form when it is displayed.
	    document.getElementById('acceptLendForm').reset();
		// Set the correct ID.
		$j('#acceptLendLendRequestId').val(pLendRequestID);
		// Set the correct redirectID.
		$j('#acceptLendRedirectId').val(pRedirectID);
		
	  	mAcceptLendTooltipTarget = pTooltipTarget;
	  	mAcceptLendTooltip = createFormTooltip($j(pTooltipTarget), $j('#acceptLendForm'), acceptLendQtipOnHide);
	}    
}

/*
 * Click the submit button in the tooltip.
 */
function submitAcceptLend() {	
	document.getElementById("acceptLendActionButton").click();
}

/*
 * Click the cancel button to close the tooltip.
 */
function cancelAcceptLend() {
	hideAcceptLendTooltip(mAcceptLendTooltipTarget);
}

/***************************************************************************************************
 * 
 * REFUSE LEND
 * 
 ***************************************************************************************************/
function hideRefuseLendTooltip(pTooltipTarget) {
	hideTooltip(pTooltipTarget, $j('#refuseLendForm'), $j('#refuseLendDiv'));
}

function refuseLendQtipOnHide(pEvent) {
	qtipOnHide(this.elements['content'], this.elements['target'], $j('#refuseLendDiv'));
}

/*
 * That method will display / hide the tooltip.
 */
var mRefuseLendTooltip;
var mRefuseLendTooltipTarget;
function refuseLendTooltip(pTooltipTarget, pLendRequestID, pRedirectID) {
  // The tooltip is just closed.
  if ($j(pTooltipTarget).data("qtip")) {
	  hideRefuseLendTooltip(pTooltipTarget);
  }
  // The tooltip is opened.
  else {
	  	// Reset the form when it is displayed.
	    document.getElementById('refuseLendForm').reset();
		// Set the correct ID.
		$j('#refuseLendLendRequestId').val(pLendRequestID);
		// Set the correct redirectID.
		$j('#refuseLendRedirectId').val(pRedirectID);
		
	  	mRefuseLendTooltipTarget = pTooltipTarget;
	  	mRefuseLendTooltip = createFormTooltip($j(pTooltipTarget), $j('#refuseLendForm'), refuseLendQtipOnHide);
	}    
}

/*
 * Click the submit button in the tooltip.
 */
function submitRefuseLend() {	
	document.getElementById("refuseLendActionButton").click();
}

/*
 * Click the cancel button to close the tooltip.
 */
function cancelRefuseLend() {
	hideRefuseLendTooltip(mRefuseLendTooltipTarget);
}

/***************************************************************************************************
 * 
 * REMOVE CONNECTION
 * 
 ***************************************************************************************************/
function hideRemoveConnectionTooltip(pTooltipTarget) {
	hideTooltip(pTooltipTarget, $j('#removeConnectionForm'), $j('#removeConnectionDiv'));
}

function removeConnectionQtipOnHide(pEvent) {
	qtipOnHide(this.elements['content'], this.elements['target'], $j('#removeConnectionDiv'));
}

/*
 * That method will display / hide the tooltip.
 */
var mRemoveConnectionTooltip;
var mRemoveConnectionTooltipTarget;
function removeConnectionTooltip(pTooltipTarget, pPersonID, pRedirectID) {
  // The tooltip is just closed.
  if ($j(pTooltipTarget).data("qtip")) {
	  hideRemoveConnectionTooltip(pTooltipTarget);
  }
  // The tooltip is opened.
  else {
	  	// Reset the form when it is displayed.
	    document.getElementById('removeConnectionForm').reset();
		// Set the correct ID.
		$j('#removeConnectionPersonId').val(pPersonID);
		// Set the correct redirectID.
		$j('#removeConnectionRedirectId').val(pRedirectID);
		
	  	mRemoveConnectionTooltipTarget = pTooltipTarget;
	  	mRemoveConnectionTooltip = createFormTooltip($j(pTooltipTarget), $j('#removeConnectionForm'), removeConnectionQtipOnHide);
	}    
}

/*
 * Click the submit button in the tooltip.
 */
function submitRemoveConnection() {	
	document.getElementById("removeConnectionActionButton").click();
}

/*
 * Click the cancel button to close the tooltip.
 */
function cancelRemoveConnection() {
	hideRemoveConnectionTooltip(mRemoveConnectionTooltipTarget);
}

/***************************************************************************************************
 * 
 * REMOVE BANNED PERSON
 * 
 ***************************************************************************************************/
function hideRemoveBannedPersonTooltip(pTooltipTarget) {
	hideTooltip(pTooltipTarget, $j('#removeBannedPersonForm'), $j('#removeBannedPersonDiv'));
}

function removeBannedPersonQtipOnHide(pEvent) {
	qtipOnHide(this.elements['content'], this.elements['target'], $j('#removeBannedPersonDiv'));
}

/*
 * That method will display / hide the tooltip.
 */
var mRemoveBannedPersonTooltip;
var mRemoveBannedPersonTooltipTarget;
function removeBannedPersonTooltip(pTooltipTarget, pPersonID, pRedirectID) {
  // The tooltip is just closed.
  if ($j(pTooltipTarget).data("qtip")) {
	  hideRemoveBannedPersonTooltip(pTooltipTarget);
  }
  // The tooltip is opened.
  else {
	  	// Reset the form when it is displayed.
	    document.getElementById('removeBannedPersonForm').reset();
		// Set the correct ID.
		$j('#removeBannedPersonPersonId').val(pPersonID);
		// Set the correct redirectID.
		$j('#removeBannedPersonRedirectId').val(pRedirectID);
		
	  	mRemoveBannedPersonTooltipTarget = pTooltipTarget;
	  	mRemoveBannedPersonTooltip = createFormTooltip($j(pTooltipTarget), $j('#removeBannedPersonForm'), removeBannedPersonQtipOnHide);
	}    
}

/*
 * Click the submit button in the tooltip.
 */
function submitRemoveBannedPerson() {	
	document.getElementById("removeBannedPersonActionButton").click();
}

/*
 * Click the cancel button to close the tooltip.
 */
function cancelRemoveBannedPerson() {
	hideRemoveBannedPersonTooltip(mRemoveBannedPersonTooltipTarget);
}

