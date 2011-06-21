package com.pferrot.lendity.dao.hibernate;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.pferrot.core.CoreUtils;
import com.pferrot.lendity.dao.IpToCountryDao;
import com.pferrot.lendity.model.IpToCountry;

/**
 * See:
 * http://www.javaranch.com/journal/2008/08/Journal200808.jsp#a2
 * http://software77.net/geo-ip/
 * 
 * @author pferrot
 *
 */
public class IpToCountryDaoHibernateImpl extends HibernateDaoSupport implements IpToCountryDao {

	public IpToCountry findCountry(final Long pTransformedIp) {
		CoreUtils.assertNotNull(pTransformedIp);
		List<IpToCountry> list = getHibernateTemplate().find("from IpToCountry itc where itc.ipFrom < ? and itc.ipTo > ?", new Object[]{pTransformedIp, pTransformedIp});		
		
		if (list == null ||
			list.isEmpty()) {
			return null;
		}
		else if (list.size() == 1) {
			final IpToCountry itc = list.get(0); 
			return itc;
		}
		else {
			throw new DataIntegrityViolationException("More that one ipToCountry with value '" + pTransformedIp + "'");
		}
	}

}