package com.pferrot.sharedcalendar.login.listener;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.springframework.security.BadCredentialsException;
import org.springframework.security.ui.AbstractProcessingFilter;

public class LoginErrorPhaseListener implements PhaseListener {
 
    public void beforePhase(final PhaseEvent arg0)
    {
        Exception e = (Exception) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(
                AbstractProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY);
 
        if (e instanceof BadCredentialsException)
        {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(
                    AbstractProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY, null);
            System.out.println("Username or password not valid.");
//            FacesUtils.addErrorMessage("Username or password not valid.");
        }
    }
 
    public void afterPhase(final PhaseEvent arg0)
    {}
 
    public PhaseId getPhaseId()
    {
        return PhaseId.RENDER_RESPONSE;
    }
 
}