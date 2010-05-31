/*
 * Color the input fields that make an error.
 */
function colorErrorFields() {
  $j(document).ready(function() {
    $j("span.error").next().addClass('validationError');        
  });
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
      $j("#" + pClearSearchId).show();
      $j("#" + pFieldId).addClass("filterActive");
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
			border: {
				width: 3,
				radius: 7,
				color: '#009b4b'
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
		
		$j("#lendBorrowDate").datepicker();
	}    
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
	$j("#lendBorrowerId").val(0);
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