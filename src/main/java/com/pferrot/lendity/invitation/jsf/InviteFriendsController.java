package com.pferrot.lendity.invitation.jsf;

import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.invitation.InvitationException;
import com.pferrot.lendity.invitation.InvitationService;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.registration.RegistrationService;

@ViewController(viewIds={"/auth/invitation/inviteFriends.jspx"})
public class InviteFriendsController {
		
		private final static Log log = LogFactory.getLog(InviteFriendsController.class);

		private InvitationService invitationService;
		private RegistrationService registrationService;
		
		private String email;
		
		@InitView
		public void initView() {
		}
		
		public InvitationService getInvitationService() {
			return invitationService;
		}

		public void setInvitationService(InvitationService invitationService) {
			this.invitationService = invitationService;
		}

		public RegistrationService getRegistrationService() {
			return registrationService;
		}

		public void setRegistrationService(RegistrationService registrationService) {
			this.registrationService = registrationService;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public void validateEmail(FacesContext context, UIComponent toValidate, Object value) {
			String message = "";
			String email = (String) value;
			if (!email.contains("@")) {
				((UIInput)toValidate).setValid(false);
				final Locale locale = I18nUtils.getDefaultLocale();
				message = I18nUtils.getMessageResourceString("validation_emailNotValid", locale);
				context.addMessage(toValidate.getClientId(context), new FacesMessage(message));
			}
			else if (!registrationService.isUsernameAvailable(email)) {
				((UIInput)toValidate).setValid(false);
				final Locale locale = I18nUtils.getDefaultLocale();
				message = I18nUtils.getMessageResourceString("validation_userAlreadyExists", locale);
				context.addMessage(toValidate.getClientId(context), new FacesMessage(message));
			}
		}

		public String submit() {
			try {
				getInvitationService().sendInvitation(PersonUtils.getCurrentPersonId(), getEmail());
				return "success";
			}
			catch (InvitationException e) {
				throw new RuntimeException(e);
			}
		}

}
