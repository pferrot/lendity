package com.pferrot.sharedcalendar.dao;

import com.pferrot.sharedcalendar.model.Address;

public interface AddressDao {
	
	Long createAddress(Address address);
	
	Address findAddress(Long addressId);
	
	void updateAddress(Address address);
	
	void deleteAddress(Address address);
}
