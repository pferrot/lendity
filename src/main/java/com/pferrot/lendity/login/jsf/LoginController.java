package com.pferrot.lendity.login.jsf;

import java.io.IOException;
import java.util.Locale;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.utils.JsfUtils;

/**
 * See http://ocpsoft.com/java/acegi-spring-security-jsf-login-page/
 */
@ViewController(viewIds={"/login.jspx"})
public class LoginController {
	
	private static final String LOGIN_FAILED_PARAMETER_NAME = "loginFailed";
	
	private String username;
	private String password;
	private Boolean rememberMe;
	
	@InitView
	public void initView() {
		if ("true".equals(JsfUtils.getRequestParameter(LOGIN_FAILED_PARAMETER_NAME))) {
			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			final String message = I18nUtils.getMessageResourceString("validation_loginFailed", locale);
			JsfUtils.addErrorMessage("j_username", message);
			JsfUtils.addErrorMessage("j_password", "");
		}
	}
    //managed properties for the login page, username/password/etc...
	 
    // This is the action method called when the user clicks the "login" button
    public String doLogin() throws IOException, ServletException
    { 
        JsfUtils.doForward("/j_spring_security_check");
        
//        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
//        
//        RequestDispatcher dispatcher = ((ServletRequest) context.getRequest())
//                 .getRequestDispatcher("/j_spring_security_check");
// 
//        dispatcher.forward((ServletRequest) context.getRequest(),
//                (ServletResponse) context.getResponse());
// 
//        FacesContext.getCurrentInstance().responseComplete();
        // It's OK to return null here because Faces is just going to exit.
        return null;
    }

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getRememberMe() {
		return rememberMe;
	}

	public void setRememberMe(Boolean rememberMe) {
		this.rememberMe = rememberMe;
	}
}
