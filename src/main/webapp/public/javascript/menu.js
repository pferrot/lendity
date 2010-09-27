var mCurrentServletPath;
var mCurrentSubMenuId;
var mCurrentMenuLinkId;
var subMenusToMenuLinksMap = new Object();
subMenusToMenuLinksMap.divSubMenuLogin = 'menuHomeLink';
subMenusToMenuLinksMap.divSubMenuHome = 'menuHomeLink2';
subMenusToMenuLinksMap.divSubMenuItems = 'menuMyItemsLink';
subMenusToMenuLinksMap.divSubMenuBorrowedItems = 'menuMyBorrowedItemsLink';
subMenusToMenuLinksMap.divSubMenuNeeds = 'menuMyNeedsLink';
subMenusToMenuLinksMap.divSubMenuConnections = 'menuMyConnectionsLink';
subMenusToMenuLinksMap.divSubMenuProfile = 'menuProfileLink';


var menuLinksArray = ['menuHomeLink', 'menuHomeLink2', 'menuMyItemsLink', 'menuMyBorrowedItemsLink', 'menuMyNeedsLink', 'menuMyConnectionsLink', 'menuProfileLink'];
var subMenusArray = ['divSubMenuLogin', 'divSubMenuHome', 'divSubMenuItems', 'divSubMenuBorrowedItems', 'divSubMenuNeeds', 'divSubMenuConnections', 'divSubMenuProfile'];

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
	else if (pServletPath.indexOf('auth/item/external') > 0 ||
			 pServletPath.indexOf('auth/lendrequest/myPendingLendRequestsOutList') > 0 ||
			 pServletPath.indexOf('auth/item/myConnectionsItemsList') > 0 ||
			 pServletPath.indexOf('auth/item/myBorrowedItemsList') > 0) {
		mCurrentMenuLinkId = 'menuMyBorrowedItemsLink';
		mCurrentSubMenuId = 'divSubMenuBorrowedItems';
		menuHighlightBorrowedItems();
		menuBorrowedItemsOver();
		
		menuLinkConfig('menuMyBorrowedItemsLink');
		subMenuConfig('divSubMenuBorrowedItems');
	}
	else if (pServletPath.indexOf('auth/item') > 0 ||
			pServletPath.indexOf('auth/lendrequest/myPendingLendRequestsList') > 0) {
		mCurrentMenuLinkId = 'menuMyItemsLink';
		mCurrentSubMenuId = 'divSubMenuItems';
		menuHighlightItems();
		menuItemsOver();
		
		menuLinkConfig('menuMyItemsLink');
		subMenuConfig('divSubMenuItems');
	}
	else if (pServletPath.indexOf('auth/need') > 0) {
		mCurrentMenuLinkId = 'menuMyNeedsLink';
		mCurrentSubMenuId = 'divSubMenuNeeds';
		menuHighlightNeeds();
		menuNeedsOver();
		
		menuLinkConfig('menuMyNeedsLink');
		subMenuConfig('divSubMenuNeeds');
	}
	else if (pServletPath.indexOf('auth/person/myProfile') > 0 ||
			pServletPath.indexOf('auth/changepassword') > 0 ||
			 pServletPath.indexOf('auth/person/personEdit') > 0) {
		mCurrentMenuLinkId = 'menuProfileLink';
		mCurrentSubMenuId = 'divSubMenuProfile';
		menuHighlightProfile();
		menuProfileOver();
		
		menuLinkConfig('menuProfileLink');
		subMenuConfig('divSubMenuProfile');
	}
	else if (pServletPath.indexOf('auth/person') > 0 ||
			 pServletPath.indexOf('auth/connectionrequest') > 0) {
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

function menuHighlightConnections() {
	menuHighlight('menuMyConnectionsLink');
}

function menuHighlightProfile() {
	menuHighlight('menuProfileLink');
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

function menuConnectionsOver() {
	menuOver('divSubMenuConnections');
}

function menuProfileOver() {
	menuOver('divSubMenuProfile');
}

function menuOver(pSubMenuId) {
	var index;
	for (var i = 0; i < subMenusArray.length; i++) {
		var subMenu = subMenusArray[i];
		if (pSubMenuId == subMenu) {
			$j('#' + subMenu).show();
		}
		else {
			$j('#' + subMenu).hide();
		}
	}
}

function menuOut() {
	for (var i = 0; i < subMenusArray.length; i++) {
		var subMenu = subMenusArray[i];
		if (subMenu == mCurrentSubMenuId) {
			$j('#' + subMenu).show();
		}
		else {
			$j('#' + subMenu).hide();
		}
	}
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
				var id = $j(this).attr('id');
				var linkId = subMenusToMenuLinksMap[id];
				$j('#' + linkId).removeClass('highlightedMenu3');
				$j(this).hide();
				menuOver(mCurrentSubMenuId);
			});
		}
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

