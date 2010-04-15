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
    if ($j("#" + fieldId).val() == '') {
      $j("#" + fieldId).val(text);
      //$j("#" + clearSearchId).hide();
    }
    else {
      $j("#" + clearSearchId).show();
    }
    $j("#" + fieldId).focus(function() {
	  if(this.value == text ) {
	    this.value = '';
	  }
    });
    $j("#" + fieldId).blur(function() {
	  if(this.value == '') {
	    this.value = text;
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

/*
 * That method with add two fake text input fields to every form in order to prevent
 * the form from being submitted automatically with firefox when pressing enter.
 */
//function addTwoHiddenFields() {
//  $j(document).ready(function() {
//    $j("span.error").next().css('background-color', '#f8b6a1');        
//  });
//}