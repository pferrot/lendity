package com.pferrot.sharedcalendar.dao.hibernate;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.pferrot.sharedcalendar.dao.AddressDao;
import com.pferrot.sharedcalendar.model.Address;

public class AddressDaoHibernateImpl extends HibernateDaoSupport implements AddressDao {

	public Long createAddress(final Address address) {
		return (Long)getHibernateTemplate().save(address);
	}

	public void deleteAddress(final Address address) {
		getHibernateTemplate().delete(address);
		
	}

	public Address findAddress(final Long addressId) {
		return (Address)getHibernateTemplate().load(Address.class, addressId);
	}

	public void updateAddress(final Address address) {
		getHibernateTemplate().update(address);		
	}
}
