package com.pferrot.sharedcalendar.login.jsf;

import java.io.IOException;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class LoginController {
	
    //managed properties for the login page, username/password/etc...
	 
    // This is the action method called when the user clicks the "login" button
    public String doLogin() throws IOException, ServletException
    {
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
 
        HttpServletRequest request = (HttpServletRequest) context.getRequest();
        RequestDispatcher dispatcher = (request.getRequestDispatcher(request.getContextPath() + "/j_spring_security_check"));
 
        dispatcher.forward((ServletRequest) context.getRequest(),
                (ServletResponse) context.getResponse());
 
        FacesContext.getCurrentInstance().responseComplete();
        // It's OK to return null here because Faces is just going to exit.
        return null;
    }
}
