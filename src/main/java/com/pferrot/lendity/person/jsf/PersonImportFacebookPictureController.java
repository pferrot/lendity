package com.pferrot.lendity.person.jsf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.configuration.Configuration;
import com.pferrot.lendity.model.Document;
import com.pferrot.lendity.person.PersonService;
import com.pferrot.lendity.social.facebook.FacebookUtils;
import com.pferrot.lendity.utils.JsfUtils;

@ViewController(viewIds={"/auth/person/personImportFacebookPicture.jspx"})
public class PersonImportFacebookPictureController  {
	
	private final static Log log = LogFactory.getLog(PersonImportFacebookPictureController.class);
	
	public final static String FACEBOOK_CODE_PARAMETER_NAME = "code";
	
	private PersonService personService;	


	@InitView
	public void initView() {
		try {
			final String facebookCode = JsfUtils.getRequestParameter(FACEBOOK_CODE_PARAMETER_NAME);
			if (!StringUtils.isNullOrEmpty(facebookCode)) {				
				final String next = JsfUtils.getFullUrlWithPrefix(Configuration.getRootURL(), PagesURL.PERSON_IMPORT_FACEBOOK_PICTURE);
		        final String accessToken = FacebookUtils.getFacebookAccessToken(facebookCode, next);
		        final File squarePic = FacebookUtils.getFacebookProfilePictureSquare(accessToken);
		        final File largePic = FacebookUtils.getFacebookProfilePictureLarge(accessToken);
		        
		        if (squarePic != null && largePic != null) {
					getPersonService().updatePersonPicture(getPersonService().getCurrentPerson(), 
							getDocFromTempFile(largePic, "image/jpeg", "fbLarge.jpg"), 
							getDocFromTempFile(squarePic, "image/jpeg", "fbSquare.jpg"));
				}	        
			}
		}
		catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error(e);
			}
		}
		finally {
			// Always redirect to the profile page.
			JsfUtils.redirect(PagesURL.MY_PROFILE);
		}
	}



	public PersonService getPersonService() {
		return personService;
	}
	
	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}
	
	/**
	 * Returns a document from a file on the file system.
	 * 
	 * @param pFile
	 * @param pMimeType
	 * @param pOriginalFileName
	 * @return
	 * @throws FileNotFoundException 
	 */
	private Document getDocFromTempFile(final File pFile, final String pMimeType, final String pOriginalFileName) throws FileNotFoundException {
		final Document document = new Document();
		document.setMimeType(pMimeType);
		document.setSize(pFile.length());
		document.setName(pOriginalFileName);
		final FileInputStream fis = new FileInputStream(pFile);
		document.setInputStream(fis);
		return document;
	}
}
