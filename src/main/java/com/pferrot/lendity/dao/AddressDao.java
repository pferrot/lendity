package com.pferrot.lendity.dao;

import com.pferrot.lendity.model.Address;

public interface AddressDao {
	
	Long createAddress(Address address);
	
	Address findAddress(Long addressId);
	
	void updateAddress(Address address);
	
	void deleteAddress(Address address);
}
