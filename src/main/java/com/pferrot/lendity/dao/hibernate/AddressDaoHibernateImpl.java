package com.pferrot.lendity.dao.hibernate;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.pferrot.lendity.dao.AddressDao;
import com.pferrot.lendity.model.Address;

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
