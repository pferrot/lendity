package com.pferrot.lendity.utils;

import org.apache.commons.lang.StringEscapeUtils;

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
}
