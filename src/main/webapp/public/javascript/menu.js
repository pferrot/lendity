var MENU_HEADER = 0;
var MENU_CONTENT = 1;
var MENU_OUT = 0;
var MENU_IN = 1;
var MENU_EFFECT_SHOW = "blind";
var MENU_EFFECT_HIDE = MENU_EFFECT_SHOW;
// First element: 0 if out of header, 1 if in.
// Second element: 0 if out of content, 1 if in.
var menu1Status = [MENU_OUT, MENU_OUT];
var menu2Status = [MENU_OUT, MENU_OUT];
var menusStatus = [menu1Status, menu2Status];



$j(document).ready(function() {
  
  $j("#menu1Header").bind("mouseenter", function() {
    showMenuContent(0, "#menu1Header", "#menu1Content", 4);     
  });
  
  $j("#menu1Content").bind("mouseenter", function() {
     menusStatus[0][MENU_CONTENT] = MENU_IN;  
  });
  
  
  $j("#menu1Header").bind("mouseleave", function() {
    // Time out to make sure we do not hide the content if going to the content.
    setTimeout("hideMenuContent(0, MENU_HEADER, '#menu1Content');", 1);
  });
  
  $j("#menu1Content").bind("mouseleave", function() {
    // Time out to make sure we do not hide the content if going to the header.
    setTimeout("hideMenuContent(0, MENU_CONTENT, '#menu1Content');", 1);
  });
  
  
  
  
  
  $j("#menu2Header").bind("mouseenter", function() {
    showMenuContent(1, "#menu2Header", "#menu2Content", 2);     
  });
  
  $j("#menu2Content").bind("mouseenter", function() {
     menusStatus[1][MENU_CONTENT] = MENU_IN;  
  });
  
  
  $j("#menu2Header").bind("mouseleave", function() {
    // Time out to make sure we do not hide the content if going to the content.
    setTimeout("hideMenuContent(1, MENU_HEADER, '#menu2Content');", 10);
  });
  
  $j("#menu2Content").bind("mouseleave", function() {
    // Time out to make sure we do not hide the content if going to the header.
    setTimeout("hideMenuContent(1, MENU_CONTENT, '#menu2Content');", 10);
  });     
  
  
         
  
});  
  
// Hide the menu content.
function hideMenuContent(menuIndex, headerOrContent, contentId) {
  menusStatus[menuIndex][headerOrContent] = MENU_OUT;
  var outOfHeader = menusStatus[menuIndex][MENU_HEADER] == MENU_OUT;
  var outOfContent = menusStatus[menuIndex][MENU_CONTENT] == MENU_OUT;
  if (outOfHeader && outOfContent) {
    var options = { number: 9 };
    $j(contentId).hide(MENU_EFFECT_HIDE, options, 500, null);
  }
}

function showMenuContent(menuIndex, headerId, contentId, nbEntries) {
  var outOfHeader = menusStatus[menuIndex][MENU_HEADER] == MENU_OUT;
  var outOfContent = menusStatus[menuIndex][MENU_CONTENT] == MENU_OUT;
  //alert(outOfHeader + " " + outOfContent);
  // Only display if it was not displayed already.
  if (outOfContent && outOfHeader) {
    //alert("Oui");
    var headerPos = $j(headerId).position();
    var headerWidth = $j(headerId).width();
    var headerHeight = $j(headerId).outerHeight();
    
    $j(contentId).css("top", headerPos.top + headerHeight);  
    $j(contentId).css("left", headerPos.left);
    
    // TODO: Calculate number of entries un menu.
    //var nbEntries = 4; 
    //$j("#menu1Content > .menuEntry").length;
    if (nbEntries > 0) {
     
      var height = 50;
      
      $j("#" + contentId + " > .menuEntry").css("height", height);            
      
      $j(contentId).css("height", height * nbEntries );
      
      var options = {};
      $j(contentId).show(MENU_EFFECT_SHOW, options, 300, null);
    }
  }
  // Always displayed from header.
  menusStatus[menuIndex][MENU_HEADER] = MENU_IN;
}
    