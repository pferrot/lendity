package com.pferrot.lendity.listener;

import java.io.UnsupportedEncodingException;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.ServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * From http://www.developpez.net/forums/d518620/java/developpement-web-java/frameworks/jsf/encoding-utf-8-jsf-facelets/
 * 
 * @author pferrot
 *
 */
public class UTF8Listener implements PhaseListener {
	
	private final static Log log = LogFactory.getLog(UTF8Listener.class);
 
    public void beforePhase(PhaseEvent e) {
        if (e.getPhaseId() == PhaseId.APPLY_REQUEST_VALUES) {
            ServletRequest request = (ServletRequest) e.getFacesContext().getExternalContext().getRequest();
            try {
                request.setCharacterEncoding("UTF-8");
            } catch (UnsupportedEncodingException uee) {
            	if (log.isErrorEnabled()) {
            		log.error(uee);
            	}
            }
        }
    }
 
    public void afterPhase(PhaseEvent e) {
    }
 
    public PhaseId getPhaseId() {
        return javax.faces.event.PhaseId.ANY_PHASE;
    }
}
 