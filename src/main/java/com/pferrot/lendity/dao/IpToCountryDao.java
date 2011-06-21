package com.pferrot.lendity.dao;

import com.pferrot.lendity.model.IpToCountry;


/**
 * See:
 * http://www.javaranch.com/journal/2008/08/Journal200808.jsp#a2
 * http://software77.net/geo-ip/
 * 
 * @author pferrot
 *
 */
public interface IpToCountryDao {
	
	IpToCountry findCountry(Long pTransformedIp);
	
}
