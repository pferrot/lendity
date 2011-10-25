var mCurrentServletPath;
var mCurrentSubMenuId;
var mActiveSubMenuId;
var mCurrentMenuLinkId;
var ownItem = false;
var isTransaction = false;
var isTransactionOut = false;
var subMenusToMenuLinksMap = new Object();
subMenusToMenuLinksMap.divSubMenuLogin = 'menuHomeLink';
subMenusToMenuLinksMap.divSubMenuHome = 'menuHomeLink2';
subMenusToMenuLinksMap.divSubMenuItems = 'menuMyItemsLink';
subMenusToMenuLinksMap.divSubMenuBorrowedItems = 'menuMyBorrowedItemsLink';
subMenusToMenuLinksMap.divSubMenuNeeds = 'menuMyNeedsLink';
subMenusToMenuLinksMap.divSubMenuConnections = 'menuMyConnectionsLink';
subMenusToMenuLinksMap.divSubMenuGroups = 'menuMyGroupsLink';

var mCurrentSubMenuTimeout;


var menuLinksArray = ['menuHomeLink', 'menuHomeLink2', 'menuMyItemsLink', 'menuMyBorrowedItemsLink', 'menuMyNeedsLink', 'menuMyConnectionsLink', 'menuMyGroupsLink'];
var subMenusArray = ['divSubMenuLogin', 'divSubMenuHome', 'divSubMenuItems', 'divSubMenuBorrowedItems', 'divSubMenuNeeds', 'divSubMenuConnections', 'divSubMenuGroups'];

function initMenu(pServletPath) {
	mCurrentServletPath = pServletPath;
	$j(".sideMenuLink").addClass(function() {
		if (($j(this).attr("href")).indexOf(mCurrentServletPath) >= 0) {
			return "highlightedSubMenu";
		}
		else {
			return "";
		}
	});
	if (pServletPath.indexOf('login.faces') > 0) {
		mCurrentMenuLinkId = 'menuHomeLink';
		mCurrentSubMenuId = 'divSubMenuLogin';
		menuHighlightLogin();
		menuLoginOver();
		
		menuLinkConfig('menuHomeLink');
		subMenuConfig('divSubMenuLogin');
	}
	else if (pServletPath.indexOf('auth/home') > 0) {
		mCurrentMenuLinkId = 'menuHomeLink2';
		mCurrentSubMenuId = 'divSubMenuHome';
		menuHighlightHome();
		menuHomeOver();
		
		menuLinkConfig('menuHomeLink2');
		subMenuConfig('divSubMenuHome');
	}
	else if (!ownItem &&
			 (pServletPath.indexOf('public/item') > 0 ||
			  pServletPath.indexOf('auth/lendtransaction/myLendTransactionsOutList') > 0 ||
			  pServletPath.indexOf('auth/lendtransaction/myLendTransactionsOutWaitingForInputList') > 0) ||
			  isTransactionOut) {
		mCurrentMenuLinkId = 'menuMyBorrowedItemsLink';
		mCurrentSubMenuId = 'divSubMenuBorrowedItems';
		menuHighlightBorrowedItems();
		menuBorrowedItemsOver();
		
		menuLinkConfig('menuMyBorrowedItemsLink');
		subMenuConfig('divSubMenuBorrowedItems');
	}
	else if (ownItem ||
			 pServletPath.indexOf('auth/item') > 0 ||
			 pServletPath.indexOf('auth/lendtransaction/myLendTransactionsList') > 0 ||
			 isTransaction ||
			 pServletPath.indexOf('auth/lendtransaction/myLendTransactionsWaitingForInputList') > 0) {
		mCurrentMenuLinkId = 'menuMyItemsLink';
		mCurrentSubMenuId = 'divSubMenuItems';
		menuHighlightItems();
		menuItemsOver();
		
		menuLinkConfig('menuMyItemsLink');
		subMenuConfig('divSubMenuItems');
	}
	else if (pServletPath.indexOf('auth/need') > 0 ||
			 pServletPath.indexOf('public/need') > 0) {
		mCurrentMenuLinkId = 'menuMyNeedsLink';
		mCurrentSubMenuId = 'divSubMenuNeeds';
		menuHighlightNeeds();
		menuNeedsOver();
		
		menuLinkConfig('menuMyNeedsLink');
		subMenuConfig('divSubMenuNeeds');
	}
	else if (pServletPath.indexOf('auth/group') > 0 ||
			pServletPath.indexOf('public/group') > 0 ||
			pServletPath.indexOf('auth/groupjoinrequest') > 0) {
		mCurrentMenuLinkId = 'menuMyGroupsLink';
		mCurrentSubMenuId = 'divSubMenuGroups';
		menuHighlightGroups();
		menuGroupsOver();
		
		menuLinkConfig('menuMyGroupsLink');
		subMenuConfig('divSubMenuGroups');
	}
	else if ((pServletPath.indexOf('auth/person') > 0 ||
			 pServletPath.indexOf('public/person') > 0 ||
			 pServletPath.indexOf('auth/connectionrequest') > 0 ||
			 pServletPath.indexOf('auth/invitation') > 0 ||
			 pServletPath.indexOf('auth/potentialconnection') > 0) &&
			 !(pServletPath.indexOf('auth/person/myProfile') > 0 ||
			   pServletPath.indexOf('auth/person/personEdit') > 0 ||
			   pServletPath.indexOf('auth/person/personEditPicture') > 0 ||
			   pServletPath.indexOf('auth/changepassword/changepassword') > 0)) {
		mCurrentMenuLinkId = 'menuMyConnectionsLink';
		mCurrentSubMenuId = 'divSubMenuConnections';
		menuHighlightConnections();
		menuConnectionsOver();
		
		menuLinkConfig('menuMyConnectionsLink');
		subMenuConfig('divSubMenuConnections');
	}
	
}

function menuHighlightLogin() {
	menuHighlight('menuHomeLink');
}

function menuHighlightHome() {
	menuHighlight('menuHomeLink2');
}

function menuHighlightItems() {
	menuHighlight('menuMyItemsLink');
}

function menuHighlightBorrowedItems() {
	menuHighlight('menuMyBorrowedItemsLink');
}

function menuHighlightNeeds() {
	menuHighlight('menuMyNeedsLink');
}

function menuHighlightGroups() {
	menuHighlight('menuMyGroupsLink');
}

function menuHighlightConnections() {
	menuHighlight('menuMyConnectionsLink');
}

function menuHighlight(pMenuId) {
	$j('#' + pMenuId).addClass('highlightedMenu');	
}

function menuLoginOver() {
	menuOver('divSubMenuLogin');
}

function menuHomeOver() {
	menuOver('divSubMenuHome');
}

function menuItemsOver() {
	menuOver('divSubMenuItems');
}

function menuBorrowedItemsOver() {
	menuOver('divSubMenuBorrowedItems');
}

function menuNeedsOver() {
	menuOver('divSubMenuNeeds');
}

function menuGroupsOver() {
	menuOver('divSubMenuGroups');
}

function menuConnectionsOver() {
	menuOver('divSubMenuConnections');
}

function menuOver(pSubMenuId) {
	var index;
	mActiveSubMenuId = pSubMenuId;
	for (var i = 0; i < subMenusArray.length; i++) {
		var subMenu = subMenusArray[i];		
		if (pSubMenuId == subMenu) {
			$j('#' + subMenu).show();
			if (subMenu != mCurrentSubMenuId) {
				var linkId = subMenusToMenuLinksMap[subMenu];
				$j('#' + linkId).addClass('highlightedMenu3');
			}
		}
		else {
			$j('#' + subMenu).hide();
			var linkId = subMenusToMenuLinksMap[subMenu];
			$j('#' + linkId).removeClass('highlightedMenu3');
		}
	}
}

/*
function menuOut() {
	for (var i = 0; i < subMenusArray.length; i++) {
		var subMenu = subMenusArray[i];
		if (subMenu == mCurrentSubMenuId) {
			$j('#' + subMenu).show();
		}
		else {
			$j('#' + subMenu).hide();
			var linkId = subMenusToMenuLinksMap[subMenu];
			$j('#' + linkId).removeClass('highlightedMenu3');
		}
	}
}
*/

function menuOut() {
	var id = mActiveSubMenuId;
	mActiveSubMenuId = undefined;
	// Reset the timeout in case there was one already.
	if (mCurrentSubMenuTimeout) {
		clearTimeout(mCurrentSubMenuTimeout);
	}
	var hideWithTimeout = setTimeout("hideSubmenuWithTimeout('" + id + "')", 2000);
	mCurrentSubMenuTimeout = hideWithTimeout;
}

function subMenuConfig(pSubMenuId) {
	for (var i = 0; i < subMenusArray.length; i++) {
		var subMenu = subMenusArray[i];
		if (pSubMenuId != subMenu) {
			$j('#' + subMenu).addClass('highlightedSubMenu2');
			$j('#' + subMenu).mouseover(function() {
				var id = $j(this).attr('id');
				var linkId = subMenusToMenuLinksMap[id];
				$j('#' + linkId).addClass('highlightedMenu3');
				menuOver(id);
			});
			$j('#' + subMenu).mouseout(function() {
				mActiveSubMenuId = undefined;
				var id = $j(this).attr('id');
				// Reset the timeout in case there was one already.
				if (mCurrentSubMenuTimeout) {
					clearTimeout(mCurrentSubMenuTimeout);
					//subMenusToTimeoutMap[id] = undefined;
				}
				//alert(id);
				var hideWithTimeout = setTimeout("hideSubmenuWithTimeout('" + id + "')", 2000);
				mCurrentSubMenuTimeout = hideWithTimeout;
			});
		}
	}
}

function hideSubmenuWithTimeout(pId) {
	var linkId = subMenusToMenuLinksMap[pId];
	// Do not hide if the mouse was over and then back over the div.
	if (mActiveSubMenuId === undefined) {
		$j('#' + linkId).removeClass('highlightedMenu3');
		$j('#' + pId).hide();
		menuOver(mCurrentSubMenuId);
	}
	if (mActiveSubMenuId != pId) {
		$j('#' + linkId).removeClass('highlightedMenu3');	
	}
}

function menuLinkConfig(pMenuLinkId) {
	for (var i = 0; i < menuLinksArray.length; i++) {
		var menuLinkId = menuLinksArray[i];
		if (pMenuLinkId != menuLinkId) {
			$j('#' + menuLinkId).addClass('highlightedMenu2');
		}
	}
}

