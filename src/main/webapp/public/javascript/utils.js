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
function setupSearchField(fieldId, clearSearchId, text) {
  $j(document).ready(function() {
    if ($j("#" + fieldId).val() == '' ||
        $j("#" + fieldId).val() == text) {
      $j("#" + fieldId).val(text);
      $j("#" + fieldId).addClass('grayColor');
      //$j("#" + clearSearchId).hide();
    }
    else {
      $j("#" + clearSearchId).show();
      $j("#" + fieldId).addClass("filterActive");
    }
    $j("#" + fieldId).focus(function() {
	  if(this.value == text ) {
	    this.value = '';
	    $j("#" + fieldId).removeClass('grayColor');
	  }
    });
    $j("#" + fieldId).blur(function() {
	  if(this.value == '') {
	    this.value = text;
	    $j("#" + fieldId).addClass('grayColor');
	  }
    });
    //$j("#" + fieldId).bind('keypress', function(e) {
      //var code = (e.keyCode ? e.keyCode : e.which);
      // Enter keycode.  
      //if(code == 13) { 
        //this.submit();
      //}
    //});    
  });
}

function setupDropDownFilterField(fieldId) {
  $j(document).ready(function() {
	if ($j("#" + fieldId).val() != null && $j("#" + fieldId).val() != '') {
	  $j("#" + fieldId).addClass("filterActive");
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
 * Setup the span fields with an attribute 'usage' containing the value 'lendItem' to display
 * a qtip tooltip when clicked. That tooltip allows lending the item.
 */
function setupLendItem() {	
  $j(document).ready(function() {
    // Use each method to gain access to all youtube links
	$j('span[usage*="lendItem"]').each(function() {
      var itemID = $j(this).attr('itemID');
      $j(this).qtip({
    		   content: {
    	 	       url: 'http://localhost:8080/shared_calendar/auth/item/internalItemLendTooltip.faces?itemID=' + itemID,
    	 	       method: 'get'
    	       },
    	       show: {
    	           when: 'click', // Show it on click...
    	           solo: true, // ...and hide all others when its shown
    	           delay: 0
     	       },
    	       hide: { 
     	    	   when: 'click'
     	       }, // Hide it when inactive...

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
                   tip: true, // Give it a speech bubble tip with automatic corner detection
     		   }
      });	 
    });
  });	
}

// TODO - currently same as setupLendItem
function setupLendBackItem() {	
  $j(document).ready(function() {
    // Use each method to gain access to all youtube links
	$j('span[usage*="lendBackItem"]').each(function() {
      var itemID = $j(this).attr('itemID');
      $j(this).qtip({
    		   //content: '<form type="post"><input type="hidden" name="lendItemId" value="' + itemId + '"/><input class="datepicker" type="text" name="lendItemDate"/><br/><button type="submit" value="Submit"/></form>',
    		   content: {
    	 	       url: 'http://localhost:8080/shared_calendar/auth/item/internalItemLendTooltip.faces?itemID=' + itemID,
    	 	       method: 'get'
    	       },
    	       show: {
    	           when: 'click', // Show it on click...
    	           solo: true, // ...and hide all others when its shown
    	           delay: 0
     	       },
    	       hide: { when: { event: 'click' } }, // Hide it when inactive...

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
                  tip: true, // Give it a speech bubble tip with automatic corner detection
    		   }
      });	 
    });
  });	
}

function submitLendItem(theButton, borrowerDropDownId) {
	var borrowerDropDown = document.getElementById(borrowerDropDownId);
	if (borrowerDropDown.selectedIndex == 0) {
		$j(borrowerDropDown).addClass("validationError");
	}
	else {
		theButton.form.submit();
	}
}