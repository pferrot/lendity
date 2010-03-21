package com.pferrot.sharedcalendar.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * This filter sets the character encoding in every request.
 *
 * @author pferrot
 *
 */
public class UTF8Filter implements Filter {
    
	public void init(FilterConfig filterConfig) throws ServletException {}
    
    public void destroy() {}
    
    public void doFilter(final ServletRequest request,
    		final ServletResponse response, final FilterChain chain) throws IOException, ServletException
    {
        request.setCharacterEncoding("UTF-8");
        chain.doFilter(request, response);
    }
}
