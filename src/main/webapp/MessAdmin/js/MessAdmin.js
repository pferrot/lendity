// Adapted from http://webreflection.blogspot.com/2007/10/cow-web-ram-javascript-function-to.html
// Stores Strings, so should be (carefully!) eval()'ed if stored item is a script

// Please remember that RAM goal is to believe in itself, it's quite obvious that if some malicius
// site/page/code replace informations or uses malicius version of common libraries RAM function
// should become totally insecure.
//
// The best practice I could suggest is to use a "private mutable key": location.hostname or session id

// Looks like the guys making NoScript were a bit concerned about the security of window.name ;)
// When you change the domain, reload the page or press enter in the urlbar they "clean up"
// window.name and remove chars like ")'?=}], so basically anything you would need for working javascript code.
// NoScript also converts \x01 and so on to " ", so each time you visit the page and
// window.name is cleaned up, RAM will append more and more of the cached stuff to the
// string and you get a ton of garbage infront of it.
// My solution, just add a small key with " as data and check if it's there, if not set
// window.name to "" and recache, if it's there just do nothing.

// Another nice option is http://www.thomasfrank.se/sessionvars.html
/*
eval(
 // from second time You visit this page
 RAM("my personal key") ||

 // only first time You visit this page
 RAM("my personal key", "alert('Hello World')")
);
*/
function RAM(k,v) {
	var f = function(k,v) {
		return v ? k.replace(/\x01\x01/g,c) : k.replace(/\x01/g,c+c)
	},
	c = String.fromCharCode(1),
	k2 = "."+c+"."+f(k)+"."+c+".",
	r = window.name.split(k2),
	testCache = function () { // reset cache in case of NoScript tampering
		var cacheOK = /NoScript\.\x01\.=\.\x01/.test(window.name);
		if (! cacheOK) {
			window.name = '';
			r = [];
			cache("."+c+"."+f('NoScript')+"."+c+".", f('='));
		}
		return cacheOK;
	},
	cache = function(k,v) {
		window.name = [r[0],f(v),r[2]||""].join(k);
		return v
	};
	if (window.sessionStorage) {
		// try html 5 sessionStorage mechanism
		if (arguments.length<2) {
			return window.sessionStorage.getItem(k);
		} else {
			if (v != null) {//FIXME null or undefined
				window.sessionStorage.setItem(k, v);
			} else {
				window.sessionStorage.removeItem(k);
			}
			return v || "";
		}
	} else {
		// fall back to window.name
		return (arguments.length<2 && testCache()) ? f(r[1]||"",1) : cache(k2,v)&&v
	}
}

/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/

//<label><input type="checkbox" onclick="javascript:checkUncheckAllCB(this, 'applicationIds');"/>select all</label>
function checkUncheckAllCB(theElementCB, name) {
	var elements = theElementCB.form.elements;
	for(i = 0; i < elements.length; ++i) {
		theField = elements[i];
		if(theField.type == 'checkbox' && theField.name == name) {
			theField.checked = theElementCB.checked;
		}
	}
}

function hasCheckedCB(theElementCB, name) {
	var elements = theElementCB.form.elements;
	for(i = 0; i < elements.length; ++i) {
		theField = elements[i];
		if(theField.type == 'checkbox' && theField.name == name && theField.checked) {
			return true;
		}
	}
	return false;
}

/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/

function addWindowOnLoadHandler(handler) {
	var oldHandler = window.onload;
	if (typeof oldHandler != 'function') {
		window.onload = handler;
	} else {
		window.onload = function() {
			oldHandler();
			handler();
		};
	}
}

/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/

function addClass(obj, className) {
	if(! new RegExp('\\b'+className+'\\b').test(obj.className)){
		obj.className += obj.className ? ' '+className : className;
	}
}
function removeClass(obj, className) {
	var rep = obj.className.match(' '+className) ? ' '+className : className;
	obj.className = obj.className.replace(rep,'');
}

/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/

function showElementWithId(id) {
	var node = document.getElementById(id);
	if (node) {
		node.style.display = 'inline';
	}
}
function hideElementWithId(id) {
	var node = document.getElementById(id);
	if (node) {
		node.style.display = 'none';
	}
}

/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/

/* http://www.ccs.neu.edu/home/dherman/javascript/behavior/ */
myrules = {
	'.infoballoonable': {
		onmouseover: function() {
			showElementWithId(this.id + '-infoballoon');
		},
		onmouseout: function() {
			hideElementWithId(this.id + '-infoballoon');
		}
	},
	'.collapsible': {
		/* Note: innerHTML is non-standard behaviour. But it is much more efficient than strict DOM:
		 * var len = document.getElementById("textDiv").firstChild.length;
		 * document.getElementById("textDiv").firstChild.replaceData(0, len, "Replacement text");
		 * 
		 */
		onclick: function() {
			var expanded = '[-] ';
			var collapsed = '[+] ';
			var target = document.getElementById(this.id + '-target');
			if (target.style.display == 'none') {
				showElementWithId(target.id);
				this.innerHTML = expanded + this.innerHTML.substr(collapsed.length);
				RAM(this.id + location.hostname, "expanded");
			} else {
				hideElementWithId(target.id);
				this.innerHTML = collapsed + this.innerHTML.substr(expanded.length);
				RAM(this.id + location.hostname, "collapsed");
			}
		}
	}
}
Behavior.register(myrules);

addWindowOnLoadHandler(function () {
	// insert anchor as first body child
	var topAnchor = document.createElement("a");
	topAnchor.setAttribute("name", "top");
	document.body.parentNode.insertBefore(topAnchor, document.body);

	var expanded = '[-] ';
	var collapsed = '[+] ';
	var subMenuHMTL = '[';
	var collapsible = document.getElementsByClassName ? document.getElementsByClassName('collapsible') : document.getElementsBySelector('.collapsible');
	for (var i = 0; i < collapsible.length; ++i) {
		var myElement = collapsible[i];
		// insert a link to the top of the page
		var topLinkElement = document.createElement("span");
		topLinkElement.innerHTML = '<a href="#top" style="font-size: smaller">&uArr;</a>&nbsp;';
		var parentNode = myElement.parentNode;
		var insertBefore = myElement;
		if (parentNode.nodeName == "FIELDSET") {
			insertBefore = parentNode;
			parentNode = parentNode.parentNode;
		}
		parentNode.insertBefore(topLinkElement, insertBefore);

		// collect the sub-menu
		if (myElement.innerHTML) {
			subMenuHMTL += ' <a href="#' + myElement.id + '">' + myElement.innerHTML + '</a> |'
		}

		// add collapsed or expanded state visuals
		myElement.innerHTML = expanded + myElement.innerHTML;
		var myElementId = myElement.id + location.hostname;
		try {
			var state = RAM(myElementId) || RAM(myElementId, "expanded");
			if (state == "collapsed") {
				var target = document.getElementById(myElement.id + '-target');
				hideElementWithId(target.id);
				myElement.innerHTML = collapsed + myElement.innerHTML.substr(expanded.length);
			}
		} catch (e) {/*alert(e)*/}; // NoScript causes RAM("id") to fail
	}
	// build the sub-menu
	var menu = document.getElementById("menu");
	if (collapsible.length > 0 && subMenuHMTL.length > 2) {
		subMenuHMTL = subMenuHMTL.substring(0, subMenuHMTL.length-1) + ']'
		var submenu = document.createElement("div");
		submenu.setAttribute("id", "submenu");
		submenu.innerHTML = subMenuHMTL;
		menu.appendChild(submenu);
	}
	// add some vertical space at top of page...
	var emptySpace = document.createElement("div");
	emptySpace.setAttribute("id", "menu_empty_space_on_top");
	menu.parentNode.insertBefore(emptySpace, menu.nextSibling);
	var onResizeHandler = function() {
		emptySpace.style.height = menu.clientHeight + "px";//offsetHeight scrollHeight
	};
	onResizeHandler();
	var oldHandler = window.onresize;
	if (typeof oldHandler != 'function') {
		window.onresize = onResizeHandler;
	} else {
		window.onresize = function() {
			oldHandler();
			onResizeHandler();
		};
	}
})

/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/

// page refresh:
// define reloadPage()
// <body onload="setAutorefresh(document.getElementById('autorefresh'))">
// <input type="text" name="autorefresh" id="autorefresh" value="<core:out value='<%=request.getAttribute("autorefresh")%>'/>" size="3" maxlength="3" onchange="setAutorefresh(this); return false;"/>
var shouldRefreshPage = false;
function setAutorefresh(textfield) {
	shouldRefreshPage = false;
	if (textfield == null) {
		return false;
	}
	var delay = -1;
	if (isNaN(parseInt(textfield.value))) {
		textfield.value = '';
	} else {
		delay = parseInt(textfield.value);
		if (delay > 0 && delay < 5) {
			delay = 5;
		}
		textfield.value = delay;
	}
	if (delay > 0) {
		shouldRefreshPage = true;
		window.setTimeout('autoReloadPage()', 1000*delay);
	}
	return true;
}
function autoReloadPage() {
	if (shouldRefreshPage) {
		reloadPage();
	}
}
//function reloadPage() {
//}

/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/

/*
addWindowOnLoadHandler(function () {
	var inputSubmits = document.getElementsByTagName('input');
	for (var i = 0; i < inputSubmits.length; ++i) {
		var myElement = inputSubmits[i];
		if (myElement.type == 'submit') {
			addClass(myElement, 'submit');
		}
	}
})
*/

/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/

// AJAX functions...

// Create a "loading" div
// should be done in the header/footer to allow for i18n...
addWindowOnLoadHandler(function () {
	var d = document.getElementById("ajaxLoadingDiv");
	if (!d) {
		d = document.createElement("div");
		d.id = "ajaxLoadingDiv";
		d.innerHTML = 'Loading...';// no i18n here...
		document.body.insertBefore(d, document.body.firstChild);
	}
	d = document.getElementById("ajaxDownloadTime");
	if (!d) {
		d = document.createElement("ajaxDownloadTime");
		d.id = "ajaxDownloadTime";
		d.style.display = 'none';
		document.body.insertBefore(d, document.body.firstChild);
	}
	d = document.getElementById("ajaxDownloadSize");
	if (!d) {
		d = document.createElement("ajaxDownloadSize");
		d.id = "ajaxDownloadSize";
		d.style.display = 'none';
		document.body.insertBefore(d, document.body.firstChild);
	}
})

/* RPC call (take 2)
 * Adapted from: http://microformats.org/wiki/rest/ahah
 * Replace a given HTML element with dynamic content (background loading)
 * See also: http://developer.apple.com/internet/webcontent/xmlhttpreq.html
 * See also: http://developer.apple.com/internet/webcontent/iframe.html
 * Note :
It is essential that the data returned from the server be sent with a Content-Type set to text/xml.
Content that is sent as text/plain or text/html is accepted by the instance of the request object
however it will only be available for use via the responseText property.
 * Usage :
With all the AJAX excitement, it's worth pointing out a simple technique
I call JAH - which can be Just Async HTML, or Javascript Async HTML to taste.

This works on the microformat principle that XHTML is XML, but with the
added advantage that Javascript already knows how to handle XHTML DOM's
so no xml parsing is required.

You just include <script type="text/javascript" language="javascript1.3" src="jah.js"></script>
in the <head> and then link to dynamic pages with
<a href="javascript:jah('kevin.html','target');">kevin</a>
where target is the id of the HTML element you want to replace.
 */

//AKA loadXMLDoc(url, target)
function jah(url, target, method, params) {
	//AKA processReqChange()
	function jahDone() {
		// only if jah_req is "loaded"
		/*	Object status integer:
			0 = uninitialized
			1 = opened
			2 = header received
			3 = loading
			4 = complete
		 */
		if (jah_req.readyState == 4) {
			hideElementWithId('ajaxLoadingDiv');
			document.getElementById('ajaxDownloadTime').innerText = ( (new Date()).valueOf() - jah_req.startTime ) + ' ms';
			document.getElementById('ajaxDownloadSize').innerText = jah_req.responseText.length + ' bytes';
			// Note: file:/// and ftp:// do not return HTTP status (== 0),
			// which is why they return zero for status and an empty string for statusText.
			// only if "OK"
			if (jah_req.status == 200) {
				// ...processing statements go here...
				// jah_req.getResponseHeader('...');
				results = jah_req.responseText; // jah_req.responseXML
				document.getElementById(jah_req.targetId).innerHTML = results;
			} else {
				//alert("There was a "+jah_req.status+" error retrieving the XML data:\n" +
				//	jah_req.statusText);
				document.getElementById(jah_req.targetId).innerHTML="jah error "+jah_req.status+":\n" +
					jah_req.statusText;
			}
		}
	}

	function prepareXMLHttpRequest() {
		jah_req.targetId = target;
		if (! method) {
			method = "GET";
		}
		// onreadystatechange: Event handler for an event that fires at every state change
		jah_req.onreadystatechange = jahDone;
		//syntax: open("method", "URL"[, asyncFlag[, "userName"[, "password"]]])
		jah_req.open(method, url, true);
		// setRequestHeader MUST come AFTER open()
		jah_req.setRequestHeader("X-Powered-By", "MessAdmin JAH");
		if (method == "POST" || method == "post") {
			jah_req.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
			jah_req.setRequestHeader("Content-length", params ? params.length : 0);
			jah_req.setRequestHeader("Connection", "close");
		}
		jah_req.startTime = (new Date()).valueOf();
	}

//	if (url.toLowerCase().substr(0, 4) != "http") {
//		var prefix = window.location.protocol + "//" + window.location.host;
//		if (url.charAt(0) != '/') {
//			prefix = prefix + '/';
//		}
//		url = prefix + url;
//	}
	// global request and XML document objects
	var jah_req;
	showElementWithId('ajaxLoadingDiv');
	// native XMLHttpRequest object
	if (window.XMLHttpRequest) {
		jah_req = new XMLHttpRequest();
		prepareXMLHttpRequest();
		// Transmits the request, optionally with postable string or DOM object data
		if (! params) {
			params = null;
		}
		jah_req.send(params);
	// IE/Windows ActiveX version
	} else if (window.ActiveXObject) {
		try {
			jah_req = new ActiveXObject("Microsoft.XMLHTTP");
		} catch (error) {
			jah_req = new ActiveXObject("Msxml2.XMLHTTP");
		}
		if (jah_req) {
			prepareXMLHttpRequest();
			if (params) {
				jah_req.send(params);
			} else {
				jah_req.send();
			}
		} else {
			alert('Error: no XMLHttpRequest implementation found!');
			window.location.href = url;
		}
	} else {
		alert('Error: no XMLHttpRequest implementation found!');
		window.location.href = url;
	}
}
