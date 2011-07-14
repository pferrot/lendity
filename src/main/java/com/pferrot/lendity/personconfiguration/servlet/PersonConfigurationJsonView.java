package com.pferrot.lendity.personconfiguration.servlet;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.View;

import flexjson.JSONSerializer;

/**
 * See http://perfectionlabstips.wordpress.com/2009/01/07/serving-json-data-with-spring-mvc-2/
 * 
 * @author pferrot
 *
 */
public class PersonConfigurationJsonView implements View  {

	public String getContentType() {
		return "application/json";
	}

	public void render(final Map pMap, final HttpServletRequest pRequest,
			final HttpServletResponse pResponse) throws Exception {
		try {
			String data = new JSONSerializer().exclude("*.class").deepSerialize(pMap);
		    pResponse.getWriter().write(data);
		}
		catch (java.io.IOException e) {
			// leave empty
		}		
	}

}
