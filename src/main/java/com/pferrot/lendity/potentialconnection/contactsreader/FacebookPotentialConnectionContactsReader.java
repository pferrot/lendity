package com.pferrot.lendity.potentialconnection.contactsreader;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.potentialconnection.bean.PotentialConnectionContactBean;
import com.pferrot.lendity.potentialconnection.exception.PotentialConnectionException;

import flexjson.JSONDeserializer;

/**
 * For reading potential contacts from Facebook.
 *
 * @author pferrot
 *
 */
public class FacebookPotentialConnectionContactsReader implements FirstNameLastNamePotentialConnectionContactsReader {

	private final static Log log = LogFactory.getLog(FacebookPotentialConnectionContactsReader.class);
	
	public Set<PotentialConnectionContactBean> getContacts(final Object pObject) throws PotentialConnectionException {		
		final Set<PotentialConnectionContactBean> result = new HashSet<PotentialConnectionContactBean>();
		final String jsonData = (String)pObject;
		
		final Map map = new JSONDeserializer<Map>().deserialize(jsonData);
		if (map != null) {			
			final Collection<Map> data = (Collection<Map>)map.get("data");
			if (data != null) {
				for (Map friend: data) {
					String name = (String)friend.get("name");
					if (!StringUtils.isNullOrEmpty(name)) {
						name = name.trim();
						if (log.isDebugEnabled()) {	
							log.debug("Friend name: " + name);
						}
						PotentialConnectionContactBean pc = new PotentialConnectionContactBean(null, name);
						result.add(pc);
					}
				}
				
			}
		
		}
		
		return result;
	}
}
