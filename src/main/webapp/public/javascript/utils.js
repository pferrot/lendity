function toggleQuestion(pAnswerId) {
	$j('#' + pAnswerId).toggle();	
}

function limitTextareaLength(pTextarea, pMaxLength) {
	pTextarea.value = pTextarea.value.slice(0, pMaxLength);
}

function makeQtipTitles() {
	$j('.qtipTitle').qtip({
	    content: {
	    	text: false // Use each elements title attribute
	    },
	    style: {
			background: '#4c4847',
		    color: '#ffffff',
			border: {
				width: 1,
				radius: 5,
				color: '#4c4847'
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
function setupSearchField(pFieldId, pText, pColorBackground) {
  $j(document).ready(function() {
    if ($j("#" + pFieldId).val() == '' ||
        $j("#" + pFieldId).val() == pText) {
      $j("#" + pFieldId).val(pText);
      $j("#" + pFieldId).addClass('grayColor');
      $j("#" + pFieldId).attr('wasEmpty', 'true');
    }
    else {
      if (pColorBackground) {
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
			background: '#4c4847',
		    color: '#ffffff',
			border: {
				width: 1,
				radius: 5,
				color: '#4c4847'
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
	    //$j('#lendBorrowerId').removeClass("validationError");
	    //$j('#lendBorrowerName').removeClass("validationError");
	    //$j('#lendBorrowDate').removeClass("validationError");
	    //$j('#lendEndDate').removeClass("validationError");
	    $j('#lendForm').find('input').removeClass('validationError');
		// Set the correct itemID.
		$j('#lendItemId').val(pItemID);		
		// Set the correct redirectID.
		$j('#lendRedirectId').val(pRedirectID);		
			  	 
	  	mLendItemTooltipTarget = pTooltipTarget;
		mLendItemTooltip = createFormTooltip($j(pTooltipTarget), $j('#lendForm'), lendQtipOnHide);
		
		createDatePicker($j("#lendBorrowDate"));
		createDatePicker($j("#lendEndDate"));
	}    
}

function createDatePicker(pJqueryInputField) {
	createDatePickerInternal(pJqueryInputField, false, false);
}

function createDatePickerWithChangeYear(pJqueryInputField) {
	createDatePickerInternal(pJqueryInputField, true, true);
}

function createDatePickerInternal(pJqueryInputField, pChangeYear, pChangeMonth) {
	// Seems to never be called - in tooltips at least (even when
	// opening up a tooltip for the second time).
	if (pJqueryInputField.data("datepicker")) {
		pJqueryInputField.datepicker("destroy");
	}
	// This is necessary since the "destroy" method does not
	// seem to work very well.
	// If not doing that, the datepicker does not work the
	// second time the tooltip is opened.
	pJqueryInputField.removeClass("hasDatepicker");
	
	pJqueryInputField.datepicker({ dateFormat: 'dd.mm.yy', 
	    dayNames: ['Dimanche', 'Lundi', 'Mardi', 'Mercredi', 'Jeudi', 'Vendredi', 'Samedi'],
	    dayNamesMin: ['Di', 'Lu', 'Ma', 'Me', 'Je', 'Ve', 'Sa'],
	    dayNamesShort: ['Dim', 'Lun', 'Mar', 'Mer', 'Jeu', 'Ven', 'Sam'],
	    firstDay: 1,
	    monthNames: ['Janvier','F&eacute;vrier','Mars','Avril','Mai','Juin','Juillet','Ao&ucirc;t','Septembre','Octobre','Novembre','D&eacute;cembre'],
	    monthNamesShort: ['Jan','F&eacute;v','Mar','Avr','Mai','Jui','Jul','Ao&ucirc;','Sep','Oct','Nov','D&eacute;c'],
	    changeMonth: pChangeMonth,
	    changeYear: pChangeYear,
	    yearRange: '-130:+0'});
	
	pJqueryInputField.attr( 'readOnly' , 'true' );
}


/*
 * Click the submit button in the tooltip when an item is lent.
 */
function submitLendItem() {
	var noError = true;
	
	var borrowerDropDown = document.getElementById('lendBorrowerId');
	var jqueryBorrowerName = $j("#lendBorrowerName")
	if ($j("#lendBorrowerId").is(":visible")) {
		if (borrowerDropDown.selectedIndex == 0) {
			$j(borrowerDropDown).addClass("validationError");
			noError = false;
		}
		else {
			$j(borrowerDropDown).removeClass("validationError");
		}
	}
	
	if (jqueryBorrowerName.is(":visible")) {
		if (jqueryBorrowerName.val() == '') {
			jqueryBorrowerName.addClass("validationError");
			noError = false;
		}
		else {
			jqueryBorrowerName.removeClass("validationError");	
		}
	}

	var jqueryBorrowDate = $j("#lendBorrowDate");
	if (jqueryBorrowDate.val() == '') {
		jqueryBorrowDate.addClass("validationError");
		noError = false;
	}
	else {
		jqueryBorrowDate.removeClass("validationError");
	}

	var jqueryEndDate = $j("#lendEndDate");
	if (jqueryEndDate.val() == '') {
		jqueryEndDate.addClass("validationError");
		noError = false;
	}
	else {
		jqueryEndDate.removeClass("validationError");
	}
	
	if (noError) {
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
 * DELETE ITEM
 * 
 ***************************************************************************************************/
function hideDeleteItemTooltip(pTooltipTarget) {
	hideTooltip(pTooltipTarget, $j('#deleteItemForm'), $j('#deleteItemDiv'));
}

function deleteItemQtipOnHide(pEvent) {
	qtipOnHide(this.elements['content'], this.elements['target'], $j('#deleteItemDiv'));
}

/*
 * That method will display / hide the tooltip that is used to lend an item.
 */
var mDeleteItemTooltip;
var mDeleteItemTarget;
function deleteItemTooltip(pTooltipTarget, pItemID, pRedirectID) {
  // The tooltip is just closed.
  if ($j(pTooltipTarget).data("qtip")) {
	  hideDeleteItemTooltip(pTooltipTarget);
  }
  // The tooltip is opened.
  else {
	  	// Reset the form when it is displayed.
	    document.getElementById('deleteItemForm').reset();
	    // Hide error messages if any.
	    $j('#deleteItemForm').find('input').removeClass('validationError');
		// Set the correct itemID.
		$j('#deleteItemId').val(pItemID);
		// Set the correct redirectID.
		$j('#deleteItemRedirectId').val(pRedirectID);
		
	  	mDeleteItemTooltipTarget = pTooltipTarget;
	  	mDeleteItemTooltip = createFormTooltip($j(pTooltipTarget), $j('#deleteItemForm'), deleteItemQtipOnHide);
	}    
}

/*
 * Click the submit button in the tooltip when an item is back.
 */
function submitDeleteItem() {	
	document.getElementById("deleteItemActionButton").click();
}

/*
 * Click the cancel button to close the lend back item tooltip.
 */
function cancelDeleteItem() {
	hideDeleteItemTooltip(mDeleteItemTooltipTarget);
}

/***************************************************************************************************
 * 
 * DELETE ALL POTENTIAL CONNECTIONS
 * 
 ***************************************************************************************************/
function hideDeleteAllPotentialConnectionsTooltip(pTooltipTarget) {
	hideTooltip(pTooltipTarget, $j('#deleteAllPotentialConnectionsForm'), $j('#deleteAllPotentialConnectionsDiv'));
}

function deleteAllPotentialConnectionsQtipOnHide(pEvent) {
	qtipOnHide(this.elements['content'], this.elements['target'], $j('#deleteAllPotentialConnectionsDiv'));
}

/*
 * That method will display / hide the tooltip that is used to delete all potential conections.
 */
var mDeleteAllPotentialConnectionsTooltip;
var mDeleteAllPotentialConnectionsTarget;
function deleteAllPotentialConnectionsTooltip(pTooltipTarget, pRedirectID) {
  // The tooltip is just closed.
  if ($j(pTooltipTarget).data("qtip")) {
	  hideDeleteAllPotentialConnectionsTooltip(pTooltipTarget);
  }
  // The tooltip is opened.
  else {
	  	// Reset the form when it is displayed.
	    document.getElementById('deleteAllPotentialConnectionsForm').reset();
	    // Hide error messages if any.
	    $j('#deleteAllPotentialConnectionsForm').find('input').removeClass('validationError');
		// Set the correct redirectID.
		$j('#deleteAllPotentialConnectionsRedirectId').val(pRedirectID);
		
	  	mDeleteAllPotentialConnectionsTooltipTarget = pTooltipTarget;
	  	mDeleteAllPotentialConnectionsTooltip = createFormTooltip($j(pTooltipTarget), $j('#deleteAllPotentialConnectionsForm'), deleteAllPotentialConnectionsQtipOnHide);
	}    
}

/*
 * Click the submit button in the tooltip to delete all potential connections.
 */
function submitDeleteAllPotentialConnections() {	
	document.getElementById("deleteAllPotentialConnectionsActionButton").click();
}

/*
 * Click the cancel button to close the tooltip.
 */
function cancelDeleteAllPotentialConnections() {
	hideDeleteAllPotentialConnectionsTooltip(mDeleteAllPotentialConnectionsTooltipTarget);
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
	    // Hide error messages if any.
	    $j('#deleteNeedForm').find('input').removeClass('validationError');
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
 * DELETE GROUP
 * 
 ***************************************************************************************************/
function hideDeleteGroupTooltip(pTooltipTarget) {
	hideTooltip(pTooltipTarget, $j('#deleteGroupForm'), $j('#deleteGroupDiv'));
}

function deleteGroupQtipOnHide(pEvent) {
	qtipOnHide(this.elements['content'], this.elements['target'], $j('#deleteGroupDiv'));
}

/*
 * That method will display / hide the tooltip that is used to lend an group.
 */
var mDeleteGroupTooltip;
var mDeleteGroupTarget;
function deleteGroupTooltip(pTooltipTarget, pGroupID, pRedirectID) {
  // The tooltip is just closed.
  if ($j(pTooltipTarget).data("qtip")) {
	  hideDeleteGroupTooltip(pTooltipTarget);
  }
  // The tooltip is opened.
  else {
	  	// Reset the form when it is displayed.
	    document.getElementById('deleteGroupForm').reset();
	    // Hide error messages if any.
	    $j('#deleteGroupForm').find('input').removeClass('validationError');
		// Set the correct groupID.
		$j('#deleteGroupId').val(pGroupID);
		// Set the correct redirectID.
		$j('#deleteGroupRedirectId').val(pRedirectID);
		
	  	mDeleteGroupTooltipTarget = pTooltipTarget;
	  	mDeleteGroupTooltip = createFormTooltip($j(pTooltipTarget), $j('#deleteGroupForm'), deleteGroupQtipOnHide);
	}    
}

/*
 * Click the submit button in the tooltip when an group is back.
 */
function submitDeleteGroup() {	
	document.getElementById("deleteGroupActionButton").click();
}

/*
 * Click the cancel button to close the lend back group tooltip.
 */
function cancelDeleteGroup() {
	hideDeleteGroupTooltip(mDeleteGroupTooltipTarget);
}

/***************************************************************************************************
 * 
 * REMOVE GROUP MEMBER
 * 
 ***************************************************************************************************/
function hideRemoveGroupMemberTooltip(pTooltipTarget) {
	hideTooltip(pTooltipTarget, $j('#removeGroupMemberForm'), $j('#removeGroupMemberDiv'));
}

function removeGroupMemberQtipOnHide(pEvent) {
	qtipOnHide(this.elements['content'], this.elements['target'], $j('#removeGroupMemberDiv'));
}

/*
 * That method will display / hide the tooltip that is used to lend an group.
 */
var mRemoveGroupMemberTooltip;
var mRemoveGroupMemberTarget;
function removeGroupMemberTooltip(pTooltipTarget, pGroupID, pPersonID, pRedirectID) {
  // The tooltip is just closed.
  if ($j(pTooltipTarget).data("qtip")) {
	  hideRemoveGroupMemberTooltip(pTooltipTarget);
  }
  // The tooltip is opened.
  else {
	  	// Reset the form when it is displayed.
	    document.getElementById('removeGroupMemberForm').reset();
	    // Hide error messages if any.
	    $j('#removeGroupMemberForm').find('input').removeClass('validationError');
	    // Set the correct groupID.
		$j('#removeGroupMemberGroupId').val(pGroupID);
		// Set the correct groupID.
		$j('#removeGroupMemberPersonId').val(pPersonID);
		// Set the correct redirectID.
		$j('#removeGroupMemberRedirectId').val(pRedirectID);
		
	  	mRemoveGroupMemberTooltipTarget = pTooltipTarget;
	  	mRemoveGroupMemberTooltip = createFormTooltip($j(pTooltipTarget), $j('#removeGroupMemberForm'), removeGroupMemberQtipOnHide);
	}    
}

/*
 * Click the submit button in the tooltip when an group is back.
 */
function submitRemoveGroupMember() {	
	document.getElementById("removeGroupMemberActionButton").click();
}

/*
 * Click the cancel button to close the lend back group tooltip.
 */
function cancelRemoveGroupMember() {
	hideRemoveGroupMemberTooltip(mRemoveGroupMemberTooltipTarget);
}

/***************************************************************************************************
 * 
 * ADD GROUP ADMIN MEMBER
 * 
 ***************************************************************************************************/
function hideAddGroupAdminTooltip(pTooltipTarget) {
	hideTooltip(pTooltipTarget, $j('#addGroupAdminForm'), $j('#addGroupAdminDiv'));
}

function addGroupAdminQtipOnHide(pEvent) {
	qtipOnHide(this.elements['content'], this.elements['target'], $j('#addGroupAdminDiv'));
}

/*
 * That method will display / hide the tooltip that is used to lend an group.
 */
var mAddGroupAdminTooltip;
var mAddGroupAdminTarget;
function addGroupAdminTooltip(pTooltipTarget, pGroupID, pPersonID, pRedirectID) {
  // The tooltip is just closed.
  if ($j(pTooltipTarget).data("qtip")) {
	  hideAddGroupAdminTooltip(pTooltipTarget);
  }
  // The tooltip is opened.
  else {
	  	// Reset the form when it is displayed.
	    document.getElementById('addGroupAdminForm').reset();
	    // Hide error messages if any.
	    $j('#addGroupAdminForm').find('input').removeClass('validationError');
	    // Set the correct groupID.
		$j('#addGroupAdminGroupId').val(pGroupID);
		// Set the correct groupID.
		$j('#addGroupAdminPersonId').val(pPersonID);
		// Set the correct redirectID.
		$j('#addGroupAdminRedirectId').val(pRedirectID);
		
	  	mAddGroupAdminTooltipTarget = pTooltipTarget;
	  	mAddGroupAdminTooltip = createFormTooltip($j(pTooltipTarget), $j('#addGroupAdminForm'), addGroupAdminQtipOnHide);
	}    
}

/*
 * Click the submit button in the tooltip when an group is back.
 */
function submitAddGroupAdmin() {	
	document.getElementById("addGroupAdminActionButton").click();
}

/*
 * Click the cancel button to close the lend back group tooltip.
 */
function cancelAddGroupAdmin() {
	hideAddGroupAdminTooltip(mAddGroupAdminTooltipTarget);
}

/***************************************************************************************************
 * 
 * REMOVE GROUP ADMIN MEMBER
 * 
 ***************************************************************************************************/
function hideRemoveGroupAdminTooltip(pTooltipTarget) {
	hideTooltip(pTooltipTarget, $j('#removeGroupAdminForm'), $j('#removeGroupAdminDiv'));
}

function removeGroupAdminQtipOnHide(pEvent) {
	qtipOnHide(this.elements['content'], this.elements['target'], $j('#removeGroupAdminDiv'));
}

/*
 * That method will display / hide the tooltip that is used to lend an group.
 */
var mRemoveGroupAdminTooltip;
var mRemoveGroupAdminTarget;
function removeGroupAdminTooltip(pTooltipTarget, pGroupID, pPersonID, pRedirectID) {
  // The tooltip is just closed.
  if ($j(pTooltipTarget).data("qtip")) {
	  hideRemoveGroupAdminTooltip(pTooltipTarget);
  }
  // The tooltip is opened.
  else {
	  	// Reset the form when it is displayed.
	    document.getElementById('removeGroupAdminForm').reset();
	    // Hide error messages if any.
	    $j('#removeGroupAdminForm').find('input').removeClass('validationError');
	    // Set the correct groupID.
		$j('#removeGroupAdminGroupId').val(pGroupID);
		// Set the correct groupID.
		$j('#removeGroupAdminPersonId').val(pPersonID);
		// Set the correct redirectID.
		$j('#removeGroupAdminRedirectId').val(pRedirectID);
		
	  	mRemoveGroupAdminTooltipTarget = pTooltipTarget;
	  	mRemoveGroupAdminTooltip = createFormTooltip($j(pTooltipTarget), $j('#removeGroupAdminForm'), removeGroupAdminQtipOnHide);
	}    
}

/*
 * Click the submit button in the tooltip when an group is back.
 */
function submitRemoveGroupAdmin() {	
	document.getElementById("removeGroupAdminActionButton").click();
}

/*
 * Click the cancel button to close the lend back group tooltip.
 */
function cancelRemoveGroupAdmin() {
	hideRemoveGroupAdminTooltip(mRemoveGroupAdminTooltipTarget);
}

/***************************************************************************************************
 * 
 * REMOVE AND BAN GROUP MEMBER
 * 
 ***************************************************************************************************/
function hideRemoveAndBanGroupMemberTooltip(pTooltipTarget) {
	hideTooltip(pTooltipTarget, $j('#removeAndBanGroupMemberForm'), $j('#removeAndBanGroupMemberDiv'));
}

function removeAndBanGroupMemberQtipOnHide(pEvent) {
	qtipOnHide(this.elements['content'], this.elements['target'], $j('#removeAndBanGroupMemberDiv'));
}

/*
 * That method will display / hide the tooltip that is used to lend an group.
 */
var mRemoveAndBanGroupMemberTooltip;
var mRemoveAndBanGroupMemberTarget;
function removeAndBanGroupMemberTooltip(pTooltipTarget, pGroupID, pPersonID, pRedirectID) {
  // The tooltip is just closed.
  if ($j(pTooltipTarget).data("qtip")) {
	  hideRemoveAndBanGroupMemberTooltip(pTooltipTarget);
  }
  // The tooltip is opened.
  else {
	  	// Reset the form when it is displayed.
	    document.getElementById('removeAndBanGroupMemberForm').reset();
	    // Hide error messages if any.
	    $j('#removeAndBanGroupMemberForm').find('input').removeClass('validationError');
	    // Set the correct groupID.
		$j('#removeAndBanGroupMemberGroupId').val(pGroupID);
		// Set the correct groupID.
		$j('#removeAndBanGroupMemberPersonId').val(pPersonID);
		// Set the correct redirectID.
		$j('#removeAndBanGroupMemberRedirectId').val(pRedirectID);
		
	  	mRemoveAndBanGroupMemberTooltipTarget = pTooltipTarget;
	  	mRemoveAndBanGroupMemberTooltip = createFormTooltip($j(pTooltipTarget), $j('#removeAndBanGroupMemberForm'), removeAndBanGroupMemberQtipOnHide);
	}    
}

/*
 * Click the submit button in the tooltip when an group is back.
 */
function submitRemoveAndBanGroupMember() {	
	document.getElementById("removeAndBanGroupMemberActionButton").click();
}

/*
 * Click the cancel button to close the lend back group tooltip.
 */
function cancelRemoveAndBanGroupMember() {
	hideRemoveAndBanGroupMemberTooltip(mRemoveAndBanGroupMemberTooltipTarget);
}

/***************************************************************************************************
 * 
 * UNBAN FROM GROUP
 * 
 ***************************************************************************************************/
function hideUnbanFromGroupTooltip(pTooltipTarget) {
	hideTooltip(pTooltipTarget, $j('#unbanFromGroupForm'), $j('#unbanFromGroupDiv'));
}

function unbanFromGroupQtipOnHide(pEvent) {
	qtipOnHide(this.elements['content'], this.elements['target'], $j('#unbanFromGroupDiv'));
}

/*
 * That method will display / hide the tooltip that is used to lend an group.
 */
var mUnbanFromGroupTooltip;
var mUnbanFromGroupTarget;
function unbanFromGroupTooltip(pTooltipTarget, pGroupID, pPersonID, pRedirectID) {
  // The tooltip is just closed.
  if ($j(pTooltipTarget).data("qtip")) {
	  hideUnbanFromGroupTooltip(pTooltipTarget);
  }
  // The tooltip is opened.
  else {
	  	// Reset the form when it is displayed.
	    document.getElementById('unbanFromGroupForm').reset();
	    // Hide error messages if any.
	    $j('#unbanFromGroupForm').find('input').removeClass('validationError');
	    // Set the correct groupID.
		$j('#unbanFromGroupGroupId').val(pGroupID);
		// Set the correct groupID.
		$j('#unbanFromGroupPersonId').val(pPersonID);
		// Set the correct redirectID.
		$j('#unbanFromGroupRedirectId').val(pRedirectID);
		
	  	mUnbanFromGroupTooltipTarget = pTooltipTarget;
	  	mUnbanFromGroupTooltip = createFormTooltip($j(pTooltipTarget), $j('#unbanFromGroupForm'), unbanFromGroupQtipOnHide);
	}    
}

/*
 * Click the submit button in the tooltip when an group is back.
 */
function submitUnbanFromGroup() {	
	document.getElementById("unbanFromGroupActionButton").click();
}

/*
 * Click the cancel button to close the lend back group tooltip.
 */
function cancelUnbanFromGroup() {
	hideUnbanFromGroupTooltip(mUnbanFromGroupTooltipTarget);
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
	    // Hide error messages if any.
	    $j('#requestConnectionForm').find('input').removeClass('validationError');
	    $j('#requestConnectionForm').find('textarea').removeClass('validationError');	    
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
	var noError = true;

	var jqueryText = $j("#requestConnectionText");
	if (jqueryText.val() == '') {
		jqueryText.addClass("validationError");
		noError = false;
	}
	else {
		jqueryText.removeClass("validationError");
	}
	
	if (noError) {
		document.getElementById("requestConnectionActionButton").click();
	}		
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
	    // Hide error messages if any.
	    $j('#acceptConnectionForm').find('input').removeClass('validationError');
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
	    // Hide error messages if any.
	    $j('#refuseConnectionForm').find('input').removeClass('validationError');
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
	    // Hide error messages if any.
	    $j('#banConnectionForm').find('input').removeClass('validationError');
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
 * ACCEPT GROUP JOIN
 * 
 ***************************************************************************************************/
function hideAcceptGroupJoinTooltip(pTooltipTarget) {
	hideTooltip(pTooltipTarget, $j('#acceptGroupJoinForm'), $j('#acceptGroupJoinDiv'));
}

function acceptGroupJoinQtipOnHide(pEvent) {
	qtipOnHide(this.elements['content'], this.elements['target'], $j('#acceptGroupJoinDiv'));
}

/*
 * That method will display / hide the tooltip.
 */
var mAcceptGroupJoinTooltip;
var mAcceptGroupJoinTooltipTarget;
function acceptGroupJoinTooltip(pTooltipTarget, pGroupJoinRequestID, pRedirectID) {
  // The tooltip is just closed.
  if ($j(pTooltipTarget).data("qtip")) {
	  hideAcceptGroupJoinTooltip(pTooltipTarget);
  }
  // The tooltip is opened.
  else {
	  	// Reset the form when it is displayed.
	    document.getElementById('acceptGroupJoinForm').reset();
	    // Hide error messages if any.
	    $j('#acceptGroupJoinForm').find('input').removeClass('validationError');
		// Set the correct ID.
		$j('#acceptGroupJoinGroupJoinRequestId').val(pGroupJoinRequestID);
		// Set the correct redirectID.
		$j('#acceptGroupJoinRedirectId').val(pRedirectID);
		
	  	mAcceptGroupJoinTooltipTarget = pTooltipTarget;
	  	mAcceptGroupJoinTooltip = createFormTooltip($j(pTooltipTarget), $j('#acceptGroupJoinForm'), acceptGroupJoinQtipOnHide);
	}    
}

/*
 * Click the submit button in the tooltip.
 */
function submitAcceptGroupJoin() {	
	document.getElementById("acceptGroupJoinActionButton").click();
}

/*
 * Click the cancel button to close the tooltip.
 */
function cancelAcceptGroupJoin() {
	hideAcceptGroupJoinTooltip(mAcceptGroupJoinTooltipTarget);
}

/***************************************************************************************************
 * 
 * REFUSE GROUP JOIN
 * 
 ***************************************************************************************************/
function hideRefuseGroupJoinTooltip(pTooltipTarget) {
	hideTooltip(pTooltipTarget, $j('#refuseGroupJoinForm'), $j('#refuseGroupJoinDiv'));
}

function refuseGroupJoinQtipOnHide(pEvent) {
	qtipOnHide(this.elements['content'], this.elements['target'], $j('#refuseGroupJoinDiv'));
}

/*
 * That method will display / hide the tooltip.
 */
var mRefuseGroupJoinTooltip;
var mRefuseGroupJoinTooltipTarget;
function refuseGroupJoinTooltip(pTooltipTarget, pGroupJoinRequestID, pRedirectID) {
  // The tooltip is just closed.
  if ($j(pTooltipTarget).data("qtip")) {
	  hideRefuseGroupJoinTooltip(pTooltipTarget);
  }
  // The tooltip is opened.
  else {
	  	// Reset the form when it is displayed.
	    document.getElementById('refuseGroupJoinForm').reset();
	    // Hide error messages if any.
	    $j('#refuseGroupJoinForm').find('input').removeClass('validationError');
		// Set the correct ID.
		$j('#refuseGroupJoinGroupJoinRequestId').val(pGroupJoinRequestID);
		// Set the correct redirectID.
		$j('#refuseGroupJoinRedirectId').val(pRedirectID);
		
	  	mRefuseGroupJoinTooltipTarget = pTooltipTarget;
	  	mRefuseGroupJoinTooltip = createFormTooltip($j(pTooltipTarget), $j('#refuseGroupJoinForm'), refuseGroupJoinQtipOnHide);
	}    
}

/*
 * Click the submit button in the tooltip.
 */
function submitRefuseGroupJoin() {	
	document.getElementById("refuseGroupJoinActionButton").click();
}

/*
 * Click the cancel button to close the tooltip.
 */
function cancelRefuseGroupJoin() {
	hideRefuseGroupJoinTooltip(mRefuseGroupJoinTooltipTarget);
}

/***************************************************************************************************
 * 
 * BAN GROUP JOIN
 * 
 ***************************************************************************************************/
function hideBanGroupJoinTooltip(pTooltipTarget) {
	hideTooltip(pTooltipTarget, $j('#banGroupJoinForm'), $j('#banGroupJoinDiv'));
}

function banGroupJoinQtipOnHide(pEvent) {
	qtipOnHide(this.elements['content'], this.elements['target'], $j('#banGroupJoinDiv'));
}

/*
 * That method will display / hide the tooltip.
 */
var mBanGroupJoinTooltip;
var mBanGroupJoinTooltipTarget;
function banGroupJoinTooltip(pTooltipTarget, pGroupJoinRequestID, pRedirectID) {
  // The tooltip is just closed.
  if ($j(pTooltipTarget).data("qtip")) {
	  hideBanGroupJoinTooltip(pTooltipTarget);
  }
  // The tooltip is opened.
  else {
	  	// Reset the form when it is displayed.
	    document.getElementById('banGroupJoinForm').reset();
	    // Hide error messages if any.
	    $j('#banGroupJoinForm').find('input').removeClass('validationError');
		// Set the correct ID.
		$j('#banGroupJoinGroupJoinRequestId').val(pGroupJoinRequestID);
		// Set the correct redirectID.
		$j('#banGroupJoinRedirectId').val(pRedirectID);
		
	  	mBanGroupJoinTooltipTarget = pTooltipTarget;
	  	mBanGroupJoinTooltip = createFormTooltip($j(pTooltipTarget), $j('#banGroupJoinForm'), banGroupJoinQtipOnHide);
	}    
}

/*
 * Click the submit button in the tooltip.
 */
function submitBanGroupJoin() {	
	document.getElementById("banGroupJoinActionButton").click();
}

/*
 * Click the cancel button to close the tooltip.
 */
function cancelBanGroupJoin() {
	hideBanGroupJoinTooltip(mBanGroupJoinTooltipTarget);
}

/***************************************************************************************************
 * 
 * REQUEST GROUP JOIN
 * 
 ***************************************************************************************************/
function hideRequestGroupJoinTooltip(pTooltipTarget) {
	hideTooltip(pTooltipTarget, $j('#requestGroupJoinForm'), $j('#requestGroupJoinDiv'));
}

function requestGroupJoinQtipOnHide(pEvent) {
	qtipOnHide(this.elements['content'], this.elements['target'], $j('#requestGroupJoinDiv'));
}

/*
 * That method will display / hide the tooltip.
 */
var mRequestGroupJoinTooltip;
var mRequestGroupJoinTooltipTarget;
function requestGroupJoinTooltip(pTooltipTarget, pGroupID, pRedirectID) {
  // The tooltip is just closed.
  if ($j(pTooltipTarget).data("qtip")) {
	  hideRequestGroupJoinTooltip(pTooltipTarget);
  }
  // The tooltip is opened.
  else {
	  	// Reset the form when it is displayed.
	    document.getElementById('requestGroupJoinForm').reset();
	    // Hide error messages if any.
	    $j('#requestGroupJoinForm').find('input').removeClass('validationError');
		// Set the correct ID.
		$j('#requestGroupJoinGroupId').val(pGroupID);
		// Set the correct redirectID.
		$j('#requestGroupJoinRedirectId').val(pRedirectID);
		
	  	mRequestGroupJoinTooltipTarget = pTooltipTarget;
	  	mRequestGroupJoinTooltip = createFormTooltip($j(pTooltipTarget), $j('#requestGroupJoinForm'), requestGroupJoinQtipOnHide);
	}    
}

/*
 * Click the submit button in the tooltip.
 */
function submitRequestGroupJoin() {
	
	var noError = submitGroupJoinInternal();
	
	if (noError) {
		document.getElementById("requestGroupJoinActionButton").click();
	}
	
}

function submitGroupJoinInternal() {
	var noError = true;
	
	var groupPasswordHidden = $j('#groupPasswordKey');
	if (groupPasswordHidden) {
		var groupPasswordKey = groupPasswordHidden.val();
		if (groupPasswordKey) {
			var groupPasswordInput = $j('#groupPasswordEntered');
			var groupPasswordEntered = groupPasswordInput.val();
			var groupPasswordEnteredKey = $j.md5(groupPasswordEntered);
			if (groupPasswordEnteredKey != groupPasswordKey) {
				groupPasswordInput.addClass("validationError");
				noError = false;
			}
			else {
				groupPasswordInput.removeClass("validationError");
			}
		}
	}
	
	return noError;
}

/*
 * Click the cancel button to close the tooltip.
 */
function cancelRequestGroupJoin() {
	hideRequestGroupJoinTooltip(mRequestGroupJoinTooltipTarget);
}

/***************************************************************************************************
 * 
 * JOIN GROUP
 * 
 ***************************************************************************************************/
function hideJoinGroupTooltip(pTooltipTarget) {
	hideTooltip(pTooltipTarget, $j('#joinGroupForm'), $j('#joinGroupDiv'));
}

function joinGroupQtipOnHide(pEvent) {
	qtipOnHide(this.elements['content'], this.elements['target'], $j('#joinGroupDiv'));
}

/*
 * That method will display / hide the tooltip.
 */
var mJoinGroupTooltip;
var mJoinGroupTooltipTarget;
function joinGroupTooltip(pTooltipTarget, pGroupID, pRedirectID) {
  // The tooltip is just closed.
  if ($j(pTooltipTarget).data("qtip")) {
	  hideJoinGroupTooltip(pTooltipTarget);
  }
  // The tooltip is opened.
  else {
	  	// Reset the form when it is displayed.
	    document.getElementById('joinGroupForm').reset();
	    // Hide error messages if any.
	    $j('#joinGroupForm').find('input').removeClass('validationError');
		// Set the correct ID.
		$j('#joinGroupGroupId').val(pGroupID);
		// Set the correct redirectID.
		$j('#joinGroupRedirectId').val(pRedirectID);
		
	  	mJoinGroupTooltipTarget = pTooltipTarget;
	  	mJoinGroupTooltip = createFormTooltip($j(pTooltipTarget), $j('#joinGroupForm'), joinGroupQtipOnHide);
	}    
}

/*
 * Click the submit button in the tooltip.
 */
function submitJoinGroup() {
	
	var noError = submitGroupJoinInternal();
	
	if (noError) {
		document.getElementById("joinGroupActionButton").click();
	}
}

/*
 * Click the cancel button to close the tooltip.
 */
function cancelJoinGroup() {
	hideJoinGroupTooltip(mJoinGroupTooltipTarget);
}

/***************************************************************************************************
 * 
 * LEAVE GROUP
 * 
 ***************************************************************************************************/
function hideLeaveGroupTooltip(pTooltipTarget) {
	hideTooltip(pTooltipTarget, $j('#leaveGroupForm'), $j('#leaveGroupDiv'));
}

function leaveGroupQtipOnHide(pEvent) {
	qtipOnHide(this.elements['content'], this.elements['target'], $j('#leaveGroupDiv'));
}

/*
 * That method will display / hide the tooltip.
 */
var mLeaveGroupTooltip;
var mLeaveGroupTooltipTarget;
function leaveGroupTooltip(pTooltipTarget, pGroupID, pRedirectID) {
  // The tooltip is just closed.
  if ($j(pTooltipTarget).data("qtip")) {
	  hideLeaveGroupTooltip(pTooltipTarget);
  }
  // The tooltip is opened.
  else {
	  	// Reset the form when it is displayed.
	    document.getElementById('leaveGroupForm').reset();
	    // Hide error messages if any.
	    $j('#leaveGroupForm').find('input').removeClass('validationError');
		// Set the correct ID.
		$j('#leaveGroupGroupId').val(pGroupID);
		// Set the correct redirectID.
		$j('#leaveGroupRedirectId').val(pRedirectID);
		
	  	mLeaveGroupTooltipTarget = pTooltipTarget;
	  	mLeaveGroupTooltip = createFormTooltip($j(pTooltipTarget), $j('#leaveGroupForm'), leaveGroupQtipOnHide);
	}    
}

/*
 * Click the submit button in the tooltip.
 */
function submitLeaveGroup() {
	document.getElementById("leaveGroupActionButton").click();
}

/*
 * Click the cancel button to close the tooltip.
 */
function cancelLeaveGroup() {
	hideLeaveGroupTooltip(mLeaveGroupTooltipTarget);
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
	    // Hide error messages if any.
	    $j('#requestLendForm').find('input').removeClass('validationError');
	    $j('#requestLendForm').find('textarea').removeClass('validationError');
		// Set the correct ID.
		$j('#requestLendItemId').val(pItemID);
		// Set the correct redirectID.
		$j('#requestLendRedirectId').val(pRedirectID);
		
	  	mRequestLendTooltipTarget = pTooltipTarget;
	  	mRequestLendTooltip = createFormTooltip($j(pTooltipTarget), $j('#requestLendForm'), requestLendQtipOnHide);
	  	
	  	createDatePicker($j("#requestLendEndDate"));
	  	createDatePicker($j("#requestLendStartDate"));
	}    
}

/*
 * Click the submit button in the tooltip.
 */
function submitRequestLend() {
	var noError = true;
	var jqueryStartDate = $j("#requestLendStartDate");
	if (jqueryStartDate.val() == '') {
		jqueryStartDate.addClass("validationError");
		noError = false;
	}
	else {
		jqueryStartDate.removeClass("validationError");
	}
	
	var jqueryEndDate = $j("#requestLendEndDate");
	if (jqueryEndDate.val() == '') {
		jqueryEndDate.addClass("validationError");
		noError = false;
	}
	else {
		jqueryEndDate.removeClass("validationError");
	}

	var jqueryText = $j("#requestLendText");
	if (jqueryText.val() == '') {
		jqueryText.addClass("validationError");
		noError = false;
	}
	else {
		jqueryText.removeClass("validationError");
	}
	
	if (noError) {
		document.getElementById("requestLendActionButton").click();
	}	
}

/*
 * Click the cancel button to close the tooltip.
 */
function cancelRequestLend() {
	hideRequestLendTooltip(mRequestLendTooltipTarget);
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
	    // Hide error messages if any.
	    $j('#removeConnectionForm').find('input').removeClass('validationError');
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
	    // Hide error messages if any.
	    $j('#removeBannedPersonForm').find('input').removeClass('validationError');
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

/***************************************************************************************************
 * 
 * TRANSFER ITEM
 * 
 ***************************************************************************************************/
function hideTransferItemTooltip(pTooltipTarget) {
	hideTooltip(pTooltipTarget, $j('#transferItemForm'), $j('#transferItemDiv'));
}

function transferItemQtipOnHide(pEvent) {
	qtipOnHide(this.elements['content'], this.elements['target'], $j('#transferItemDiv'));
}

/*
 * That method will display / hide the tooltip that is used to transfer an item.
 */
var mTransferItemTooltip;
var mTransferItemTarget;
function transferItemTooltip(pTooltipTarget, pLendTransactionID) {
  // The tooltip is just closed.
  if ($j(pTooltipTarget).data("qtip")) {
	  hideTransferItemTooltip(pTooltipTarget);
  }
  // The tooltip is opened.
  else {
	  	// Reset the form when it is displayed.
	    document.getElementById('transferItemForm').reset();
	    // Hide error messages if any.
	    $j('#transferItemForm').find('input').removeClass('validationError');
		// Set the correct lendTransactionId.
		$j('#transferLendTransactionId').val(pLendTransactionID);
		
	  	mTransferItemTooltipTarget = pTooltipTarget;
	  	mTransferItemTooltip = createFormTooltip($j(pTooltipTarget), $j('#transferItemForm'), transferItemQtipOnHide);
	}    
}

function submitTransferItem() {	
	document.getElementById("transferItemActionButton").click();
}

function cancelTransferItem() {
	hideTransferItemTooltip(mTransferItemTooltipTarget);
}

