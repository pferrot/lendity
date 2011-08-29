//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// YOUTUBE

function getYouTubeVideoIds(pText) {
    /* Commented regex (in PHP syntax)
    $text = preg_replace('%
        # Match any youtube URL in the wild.
        (?:https?://)?   # Optional scheme. Either http or https
        (?:www\.)?       # Optional www subdomain
        (?:              # Group host alternatives
          youtu\.be/     # Either youtu.be,
        | youtube\.com   # or youtube.com
          (?:            # Group path alternatives
            /embed/      # Either /embed/
          | /v/          # or /v/
          | /watch\?v=   # or /watch\?v=
          )              # End path alternatives.
        )                # End host alternatives.
        ([\w\-]{10,12})  # $1: Allow 10-12 for 11 char youtube id.
        \b               # Anchor end to word boundary.
        [?=&\w]*         # Consume any URL (query) remainder.
        (?!              # But don\'t match URLs already linked.
          [\'"][^<>]*>   # Not inside a start tag,
        | </a>           # or <a> element text contents.
        )                # End negative lookahead assertion.
        %ix', 
        '<a href="http://www.youtube.com/watch?v=$1">YouTube link: $1</a>',
        $text);
    */
    // Here is the same regex in JavaScript literal regexp syntax:
	var result = new Array();
	var urls = pText.match(/(?:https?:\/\/)?(?:www\.)?(?:youtu\.be\/|youtube\.com(?:\/embed\/|\/v\/|\/watch\?v=))([\w\-]{10,12})/ig);
	if (urls) {
		for (var i = 0; i < urls.length; i++) {
			var url = urls[i];
			if (url.length > 11) {
				var videoId = url.substring(url.length - 11, url.length);
				if (result.indexOf(videoId) < 0) {
					result.push(videoId);
				}
			}
		}
	}
	
	return result;
}

function getYouTubeHtml(pVideoId, pContainerDivClass, pFirstColumnWidth, pVideoWidth, pVideoHeight) {
	var result = 
		// Need to use class and not ID because there can be several videos but the DIVs cannot have the same ID.
		'<div style="width: 100%;" class="' + pContainerDivClass + '">' + 
		'<div style="width: ' + pFirstColumnWidth + 'px; float: left;">&nbsp;</div>' +
		'<div style="float: left;">' +
		'<br/>' +
		'<iframe type="text/html" width="' + pVideoWidth + '" height="' + pVideoHeight + '" src="http://www.youtube.com/embed/' + pVideoId + '" frameborder="0"></iframe>' +
		'<br/>' +
	    '</div>' +
	    '<div style="clear: both;"></div>' +
	    '</div>';
	
	return result;
}

function appendYouTubeVideosFromText(pText, pContainerDivClass, pFirstColumnWidth, pVideoWidth, pVideoHeight, pDivToAppendTo) {
	var youTubeVideoIds = getYouTubeVideoIds(pText);
	var html = '';
	for (var i = 0; i < youTubeVideoIds.length; i++) {
		var videoId = youTubeVideoIds[i];
		html += getYouTubeHtml(videoId, pContainerDivClass, pFirstColumnWidth, pVideoWidth, pVideoHeight);

	}	
	if (html.length > 0) {
		$j("#" + pDivToAppendTo).append(html);
	}
}

function appendYouTubeVideosFromDiv(pDivId, pContainerDivClass, pFirstColumnWidth, pVideoWidth, pVideoHeight, pDivToAppendTo) {
	appendYouTubeVideosFromText($j("#" + pDivId).html(), pContainerDivClass, pFirstColumnWidth, pVideoWidth, pVideoHeight, pDivToAppendTo);
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// SOUNDCLOUD

function getSoundCloudUrls(pText) {
    // Here is the same regex in JavaScript literal regexp syntax:
	var result = new Array();
	var urls = pText.match(/(?:https?:\/\/)?(?:www\.)?(soundcloud\.com\/)([\w\-]+)\/([\w\-]+)/ig);
	if (urls) {
		for (var i = 0; i < urls.length; i++) {
			var url = urls[i];
			if (result.indexOf(url) < 0) {
				result.push(url);
			}
		}
	}
	return result;
}

function getSoundCloudHtml(pSoundCloudUrl, pContainerDivClass, pFirstColumnWidth) {
	var encodedUrl = encodeURIComponent(pSoundCloudUrl);
	
	var result = 
		// Need to use class and not ID because there can be several soundclouds but the DIVs cannot have the same ID.
		'<div style="width: 100%;" class="' + pContainerDivClass + '">' + 
		'<table style="width: 100%;"><tr>' +
		'<td style="width: ' + pFirstColumnWidth + 'px;">&nbsp;</td><td>' + 
		'<br/>' +
		// See http://developers.soundcloud.com/docs/widget#embed-code
		'<object height="81" width="100%" id="myPlayer" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000">' +
		  '<param name="movie" value="http://player.soundcloud.com/player.swf?url=' + encodedUrl + '&enable_api=true&object_id=myPlayer"></param>' +
		  '<param name="allowscriptaccess" value="always"></param>' +
		  '<embed allowscriptaccess="always" height="81" src="http://player.soundcloud.com/player.swf?url=' + encodedUrl + '&enable_api=true&object_id=myPlayer" type="application/x-shockwave-flash" width="100%" name="myPlayer"></embed>' +
		'</object>' +
		'<br/>' +
	    '</td></tr></table>' +
	    '</div>';
	
	return result;
}

function appendSoundCloudFromText(pText, pContainerDivClass, pFirstColumnWidth, pDivToAppendTo) {
	var urls = getSoundCloudUrls(pText);
	
	var html = '';
	for (var i = 0; i < urls.length; i++) {
		var url = urls[i];
		
		var encodedUrl = encodeURIComponent(url);		 
		html += getSoundCloudHtml(url, pContainerDivClass, pFirstColumnWidth);
	}
	
	if (html.length > 0) {
		$j("#" + pDivToAppendTo).append(html);
	}
}

function appendSoundCloudFromDiv(pDivId, pContainerDivClass, pFirstColumnWidth, pDivToAppendTo) {
	appendSoundCloudFromText($j("#" + pDivId).html(), pContainerDivClass, pFirstColumnWidth, pDivToAppendTo);	
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// DAILYMOTION

function getDailymotionVideoIds(pText) {
    // Here is the same regex in JavaScript literal regexp syntax:
	var result = new Array();
	var urls = pText.match(/(?:https?:\/\/)?(?:www\.)?(dailymotion\.com\/video\/)([a-zA-Z0-9]{5,7})/ig);
	if (urls) {
		for (var i = 0; i < urls.length; i++) {
			var url = urls[i];
			if (url.length > 6) {
				var videoId = url.substring(url.length - 6, url.length);
				if (result.indexOf(videoId) < 0) {
					result.push(videoId);
				}
			}
		}
	}
	return result;
}

function getDailymotionHtml(pVideoId, pContainerDivClass, pFirstColumnWidth, pVideoWidth, pVideoHeight) {
	var result = 
		// Need to use class and not ID because there can be several videos but the DIVs cannot have the same ID.
		'<div style="width: 100%;" class="' + pContainerDivClass + '">' + 
		'<div style="width: ' + pFirstColumnWidth + 'px; float: left;">&nbsp;</div>' +
		'<div style="float: left;">' +
		'<br/>' +
		'<iframe type="text/html" width="' + pVideoWidth + '" height="' + pVideoHeight + '" src="http://www.dailymotion.com/embed/video/' + pVideoId + '" frameborder="0"></iframe>' +
		'<br/>' +
	    '</div>' +
	    '<div style="clear: both;"></div>' +
	    '</div>';
	
	return result;
}

function appendDailymotionVideosFromText(pText, pContainerDivClass, pFirstColumnWidth, pVideoWidth, pVideoHeight, pDivToAppendTo) {
	var videoIds = getDailymotionVideoIds(pText);
	
	var html = '';
	for (var i = 0; i < videoIds.length; i++) {
		var videoId = videoIds[i];
		html += getDailymotionHtml(videoId, pContainerDivClass, pFirstColumnWidth, pVideoWidth, pVideoHeight);
			
	}
	
	if (html.length > 0) {
		$j("#" + pDivToAppendTo).append(html);
	}
}

function appendDailymotionVideosFromDiv(pDivId, pContainerDivClass, pFirstColumnWidth, pVideoWidth, pVideoHeight, pDivToAppendTo) {
	appendDailymotionVideosFromText($j("#" + pDivId).html(), pContainerDivClass, pFirstColumnWidth, pVideoWidth, pVideoHeight, pDivToAppendTo);
}