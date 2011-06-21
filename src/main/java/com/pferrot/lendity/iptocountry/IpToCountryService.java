package com.pferrot.lendity.iptocountry;

import java.math.BigInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.CoreUtils;
import com.pferrot.lendity.dao.IpToCountryDao;
import com.pferrot.lendity.invitation.InvitationService;
import com.pferrot.lendity.model.IpToCountry;

/**
 * See:
 * http://www.javaranch.com/journal/2008/08/Journal200808.jsp#a2
 * http://software77.net/geo-ip/
 * 
 * @author pferrot
 *
 */
public class IpToCountryService {
	
	private final static Log log = LogFactory.getLog(InvitationService.class);
	
	private IpToCountryDao ipToCountryDao;

	public IpToCountryDao getIpToCountryDao() {
		return ipToCountryDao;
	}

	public void setIpToCountryDao(IpToCountryDao ipToCountryDao) {
		this.ipToCountryDao = ipToCountryDao;
	}
	
	public IpToCountry findIpToCountry(final BigInteger pTransformedIp) {
		return getIpToCountryDao().findCountry(Long.valueOf(pTransformedIp.longValue()));
	}
	
	
	public boolean isIpInSwitzerland(final String pIpAddress) {
		try {
			if ("127.0.0.1".equals(pIpAddress)) {
				return true;
			}
			final BigInteger transformedIp = getTransformedIp(pIpAddress);
			final IpToCountry itc = findIpToCountry(transformedIp);
			return IpToCountry.SWITZERLAND_CTRY.equals(itc.getCtry());
		}
		catch (Exception e) {
			// Do not take the risk to loose subscribers...if there is a problem,
			// consider he is in CH.
			return true;
		}
	}
	
	private BigInteger getTransformedIp(final String pIpAddress) {
		CoreUtils.assertNotNull(pIpAddress);
	    BigInteger base = new BigInteger("256");
        String[] numberTokens = pIpAddress.split("\\.");
        int power = 3;
        BigInteger bigResult = new BigInteger("0");
        for (String number : numberTokens) {
            BigInteger bigNumber = new BigInteger(number);
            BigInteger raise = raiseToPow(base, power--);
            bigNumber = bigNumber.multiply(raise);
            bigResult = bigResult.add(bigNumber);
        }
        if (log.isDebugEnabled()) {
        	log.debug("Big result for IP '" + pIpAddress + "': " + bigResult);
        }
        return bigResult;
	}
	
	private BigInteger raiseToPow(final BigInteger pNumber, final int pPower) {
		return BigInteger.valueOf(Double.valueOf(Math.pow(pNumber.doubleValue(), pPower)).longValue());
	}
}
