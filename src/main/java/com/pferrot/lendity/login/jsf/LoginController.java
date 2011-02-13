package com.pferrot.lendity.login.jsf;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.servlet.ServletException;

import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;
import org.springframework.security.ui.webapp.AuthenticationProcessingFilter;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.login.filter.CustomAuthenticationProcessingFilter;
import com.pferrot.lendity.utils.JsfUtils;

/**
 * See http://ocpsoft.com/java/acegi-spring-security-jsf-login-page/
 */
@ViewController(viewIds={"/login.jspx"})
public class LoginController {
	
	private String username;
	private String password;
	private Boolean rememberMe;
	private String redirectTo;
	
	@InitView
	public void initView() {
		try {
			final boolean loginFailed = CustomAuthenticationProcessingFilter.LOGIN_FAILED_PARAMETER_VALUE.equals(
					JsfUtils.getRequestParameter(CustomAuthenticationProcessingFilter.LOGIN_FAILED_PARAMETER_NAME));
			if (loginFailed) {
				final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
				final String message = I18nUtils.getMessageResourceString("validation_loginFailed", locale);
				JsfUtils.addErrorMessage(AuthenticationProcessingFilter.SPRING_SECURITY_FORM_USERNAME_KEY, message);
				JsfUtils.addErrorMessage(AuthenticationProcessingFilter.SPRING_SECURITY_FORM_PASSWORD_KEY, "");
			}
			final String redirectTo = JsfUtils.getRequestParameter(CustomAuthenticationProcessingFilter.REDIRECT_TO_PARAMETER_NAME);
			if (!StringUtils.isNullOrEmpty(redirectTo)) {
				final String redirectToDecoded = URLDecoder.decode(redirectTo, JsfUtils.URL_ENCODING);
				setRedirectTo(redirectToDecoded);
			}
		}
		catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
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

	public String getRedirectTo() {
		return redirectTo;
	}

	public void setRedirectTo(String redirectTo) {
		this.redirectTo = redirectTo;
	}
}
