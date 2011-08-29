package com.pferrot.lendity.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;

import com.pferrot.core.StringUtils;

public class HtmlUtils {

	
	/**
	 * Return an HTML escaped String and replaces all new line characters with <code><BR/></code> and
	 * all white spaces with <code>&nbsp;</code>.
	 * 
	 * @param pInputString
	 * @return
	 */
	public static String escapeHtmlAndReplaceCrAndWhiteSpaces(final String pInputString) {
		if (pInputString == null) {
			return null;
		}
		return StringEscapeUtils.escapeHtml(pInputString).
					replaceAll("\n", "<BR/>").
					replaceAll("\\s", "&nbsp;");
	}

	/**
	 * Return an HTML escaped String and replaces all new line characters with <code><BR/></code>.
	 * 
	 * @param pInputString
	 * @return
	 */
	public static String escapeHtmlAndReplaceCr(final String pInputString) {
		if (pInputString == null) {
			return null;
		}
		return StringEscapeUtils.escapeHtml(pInputString).
					replaceAll("\n", "<BR/>");
	}
	
	/**
	 * Makes HREF links that will open in a new window where URLs are detected (starting with http:// or https://)
	 * @param pInput
	 * @return
	 */
	public static String getTextWithHrefLinks(final String pInput) {
		if (StringUtils.isNullOrEmpty(pInput)) {
			return pInput;
		}
		
		// See http://www.codinghorror.com/blog/2008/10/the-problem-with-urls.html
		// \(?\bhttp://[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]
		final String regex = "[(]?https?://[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(pInput);

		final StringBuffer result = new StringBuffer();
		while (m.find()) {
			final String text = m.group();
			m.appendReplacement(result, getHrefReplacement(m));
			
		}
		m.appendTail(result);	
		return result.toString();
	}
	
	 private static String getHrefReplacement(final Matcher pMatcher) {
		 final String s = pMatcher.group(0);
		 String url = s;
		 boolean enclosedParens = false;
		 if (s.startsWith("(") && s.endsWith(")")) {
			 url = s.substring(1, s.length() - 1);
			 enclosedParens = true;
		 }
		 
		 String href = "<a href=\"" + url + "\" target=\"_blank\">" + url + "</a>";
		 if (enclosedParens) {
			 href = "(" + href + ")";
		 }
		 return href;
	 }
}
