package com.pferrot.lendity.initialdata;

import java.util.Date;

import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.security.providers.encoding.MessageDigestPasswordEncoder;

import com.pferrot.security.dao.RoleDao;
import com.pferrot.security.dao.UserDao;
import com.pferrot.security.model.Role;
import com.pferrot.security.model.User;
import com.pferrot.security.passwordgenerator.PasswordGenerator;
import com.pferrot.lendity.dao.ItemDao;
import com.pferrot.lendity.dao.ListValueDao;
import com.pferrot.lendity.dao.PersonDao;
import com.pferrot.lendity.model.Address;
import com.pferrot.lendity.model.ConnectionRequestResponse;
import com.pferrot.lendity.model.Country;
import com.pferrot.lendity.model.Gender;
import com.pferrot.lendity.model.InternalItem;
import com.pferrot.lendity.model.Item;
import com.pferrot.lendity.model.ItemCategory;
import com.pferrot.lendity.model.Language;
import com.pferrot.lendity.model.LendRequestResponse;
import com.pferrot.lendity.model.Person;

/**
 * Creates initial data necessary for the application (list values, user roles,...).
 * This bean is currently called from a servlet (see InitialDataController). 
 * It must move at some point outside of the web application.
 * 
 * @author Patrice
 *
 */
public class InitialData {
	
	private RoleDao roleDao;
	private UserDao userDao;
	private PersonDao personDao;
	private ListValueDao listValueDao;
	private ItemDao itemDao;
	private MessageDigestPasswordEncoder passwordEncoder;	
	
	public void setRoleDao(RoleDao roleDao) {
		this.roleDao = roleDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public void setPersonDao(PersonDao personDao) {
		this.personDao = personDao;
	}

	public void setItemDao(ItemDao itemDao) {
		this.itemDao = itemDao;
	}

	public void setListValueDao(ListValueDao listValueDao) {
		this.listValueDao = listValueDao;
	}
	
	public void setPasswordEncoder(MessageDigestPasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public void createAll() {
		if (!isEmptyDB()) {
			throw new PermissionDeniedDataAccessException("DB is not empty...cannot initialize data.", null);
		}
		createCountries();
		createRoles();
		createGenders();
		createItemCategories();
		createLanguages();
		createConnectionRequestResponse();
		createLendRequestResponse();
		
		createPersonsAndUsers();
		//createItems();
		//createRandomPersonsAndUsers(20, personDao.findPersonFromUsername("patrice.ferrot@gmail.com"));	
	}

	/**
	 * Basic implementation. If the "User" user role is present, then we
	 * consider that the DB is not empty, otherwise we consider it is.
	 * 
	 * @return
	 */
	private boolean isEmptyDB() {
		Role userRole = roleDao.findRole(Role.USER_ROLE_NAME);
		return userRole == null;
	}
	
	private void createGenders() {
		Gender male = new Gender(Gender.MALE_LABEL_CODE, new Integer(1));
		listValueDao.createListValue(male);
		
		Gender female = new Gender(Gender.FEMALE_LABEL_CODE, new Integer(2));		
		listValueDao.createListValue(female);		
	}
	
	private void createRoles() {
		Role adminRole = new Role();
		adminRole.setName(Role.ADMIN_ROLE_NAME);
		roleDao.createRole(adminRole);
		
		Role userRole = new Role();
		userRole.setName(Role.USER_ROLE_NAME);
		roleDao.createRole(userRole);
	}
	
	private void createPersonsAndUsers() {
		
		// Create Patrice Ferrot, admin user.
		Person person = getNewPerson("Patrice", "Ferrot", "Patrice", "patrice.ferrot@gmail.com",
				"Châtelard 1", 1400, "Yverdon-les-Bains",
				(Country)listValueDao.findListValue(Country.SWITZERLAND_LABEL_CODE),
				listValueDao.findGender(Gender.MALE_LABEL_CODE));
		
		User user = new User();
		user.setUsername("patrice.ferrot@gmail.com");
		user.setPassword("klop");
		user.setEnabled(Boolean.TRUE);
		user.setCreationDate(new Date());
		user.setActivationCode("MANUALLY ACTIVATED");
		user.setActivationDate(new Date());
		Role userRole = roleDao.findRole(Role.USER_ROLE_NAME);
		//Role adminRole = roleDao.findRole(Role.ADMIN_ROLE_NAME);
		user.addRole(userRole);
		//user.addRole(adminRole);		
		
		person.setUser(user);
		
		personDao.createPerson(person);		
		
		
		// Create Stupid Illusion, normal user.
		person = getNewPerson("Stupid", "Illusion", "Stupid.Illusion", "stupid.illusion@gmail.com",
				"Main Street", 12345, "Pik City",
				(Country)listValueDao.findListValue(Country.USA_LABEL_CODE),
				listValueDao.findGender(Gender.MALE_LABEL_CODE));
		
		user = new User();
		user.setUsername("stupid.illusion@gmail.com");
		user.setPassword("stupid");
		user.setEnabled(Boolean.TRUE);
		user.setCreationDate(new Date());
		user.setActivationCode("MANUALLY ACTIVATED");
		user.setActivationDate(new Date());
		user.addRole(userRole);
		
		person.setUser(user);		
		
		
		personDao.createPerson(person);
	}
	
	private void createRandomPersonsAndUsers(int pNumberToCreate, final Person pPatricePerson) {
		Person person = null;
		User user = null;
		String firstName = null;
		String lastName = null;
		String displayName = null;
		String email = null;
		String password = null;
		Role userRole = roleDao.findRole(Role.USER_ROLE_NAME);
		
		for (int i = 0; i < pNumberToCreate; i++) {
			firstName = PasswordGenerator.getNewPassword(PasswordGenerator.getRandom(4, 12));
			lastName = PasswordGenerator.getNewPassword(PasswordGenerator.getRandom(4, 12));
			displayName = firstName + " " + lastName;
			email = PasswordGenerator.getNewPassword(PasswordGenerator.getRandom(4, 12)) + "@test1234.com";
		    password = PasswordGenerator.getNewPassword();
			person = getNewPerson(firstName, lastName, displayName, email,
					null, null, null,
					null, null);
			
			user = new User();
			user.setUsername(email);
			user.setPassword(password);
			user.setEnabled(Boolean.TRUE);
			user.setCreationDate(new Date());
			user.setActivationCode("MANUALLY ACTIVATED");
			user.setActivationDate(new Date());
			user.addRole(userRole);
			
			person.setUser(user);
			
			personDao.createPerson(person);
			
			createItems(person, PasswordGenerator.getRandom(0, 10));
			
			// Connect to Patrice?
			if (PasswordGenerator.getRandom(0, 3) == 0) {
				person.addConnection(pPatricePerson);
				personDao.updatePerson(pPatricePerson);
				personDao.updatePerson(person);				
			}
		}
		
	}
	
	private static Person getNewPerson(final String firstName, final String lastName, final String displayName, 
			final String email, final String address1, final Integer zip, final String city, 
			final Country country, final Gender gender) {
		
		final Person person = new Person();
		
		person.setFirstName(firstName);
		person.setLastName(lastName);
		person.setEmail(email);
		person.setEnabled(Boolean.TRUE);
		
		Address address = new Address();
		address.setAddress1(address1);
		address.setZip(zip);
		address.setCity(city);
		address.setCountry(country);
		person.setAddress(address);
		
		person.setGender(gender);
		
		return person;
	}

	// TODO
	private void createCountries() {
		Country country = new Country(Country.SWITZERLAND_LABEL_CODE);
		listValueDao.createListValue(country);
		
		country = new Country(Country.USA_LABEL_CODE);
		listValueDao.createListValue(country);		
	}
	
	private void createConnectionRequestResponse() {
		ConnectionRequestResponse crr = new ConnectionRequestResponse(ConnectionRequestResponse.ACCEPT_LABEL_CODE, new Integer(1));
		listValueDao.createListValue(crr);
		
		crr = new ConnectionRequestResponse(ConnectionRequestResponse.REFUSE_LABEL_CODE, new Integer(2));
		listValueDao.createListValue(crr);
		
		crr = new ConnectionRequestResponse(ConnectionRequestResponse.BAN_LABEL_CODE, new Integer(3));
		listValueDao.createListValue(crr);
	}

	private void createLendRequestResponse() {
		LendRequestResponse lrr = new LendRequestResponse(LendRequestResponse.ACCEPT_LABEL_CODE, new Integer(1));
		listValueDao.createListValue(lrr);
		
		lrr = new LendRequestResponse(LendRequestResponse.REFUSE_LABEL_CODE, new Integer(2));
		listValueDao.createListValue(lrr);
		
		lrr = new LendRequestResponse(LendRequestResponse.IGNORE_LABEL_CODE, new Integer(3));
		listValueDao.createListValue(lrr);
	}
	
	private void createLanguages() {
		Language language = new Language(Language.ENGLISH_LABEL_CODE);
		listValueDao.createListValue(language);
		
		language = new Language(Language.FRENCH_LABEL_CODE);
		listValueDao.createListValue(language);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	// ITEMS	
	private void createItemCategories() {	
		for (int i = 0; i < ItemCategory.LABEL_CODES.length; i++) {
			listValueDao.createListValue(new ItemCategory(ItemCategory.LABEL_CODES[i]));
		}	
	}
	
	private void createItems() {
		Person person = null;
		
		// Create random items.
		createItems(personDao.findPersonFromUsername("patrice.ferrot@gmail.com"), 50);
		
		// Create random items.
		createItems(personDao.findPersonFromUsername("stupid.illusion@gmail.com"), 30);
	}

	private void createItems(final Person pPerson, final int pNbItems) {
		for (int i = 0; i < pNbItems; i++) {
			InternalItem item = new InternalItem();
			item.setTitle(PasswordGenerator.getNewPassword(PasswordGenerator.getRandom(10, 40)));
			item.setDescription(PasswordGenerator.getNewPassword(PasswordGenerator.getRandom(0, 255)));
			item.setCategory((ItemCategory) listValueDao.findListValue(ItemCategory.LABEL_CODES[PasswordGenerator.getRandom(0, ItemCategory.LABEL_CODES.length - 1)]));
			item.setOwner(pPerson);
			item.setVisible(PasswordGenerator.getRandom(0, 1) == 0? Boolean.FALSE:Boolean.TRUE);
			itemDao.createItem(item);
		}		
	}
}
