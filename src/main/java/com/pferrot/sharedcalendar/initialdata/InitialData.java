package com.pferrot.sharedcalendar.initialdata;

import java.util.Date;

import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.security.providers.encoding.MessageDigestPasswordEncoder;

import com.pferrot.security.dao.RoleDao;
import com.pferrot.security.dao.UserDao;
import com.pferrot.security.model.Role;
import com.pferrot.security.model.User;
import com.pferrot.sharedcalendar.dao.ItemDao;
import com.pferrot.sharedcalendar.dao.ListValueDao;
import com.pferrot.sharedcalendar.dao.PersonDao;
import com.pferrot.sharedcalendar.model.Address;
import com.pferrot.sharedcalendar.model.ConnectionRequestResponse;
import com.pferrot.sharedcalendar.model.Country;
import com.pferrot.sharedcalendar.model.Gender;
import com.pferrot.sharedcalendar.model.Item;
import com.pferrot.sharedcalendar.model.ItemCategory;
import com.pferrot.sharedcalendar.model.Language;
import com.pferrot.sharedcalendar.model.Person;

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
		
		createPersonsAndUsers();
		createConnectionRequestResponse();
		createItemCategories();
		createLanguages();
		createItems();
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
//		user.setPassword(passwordEncoder.encodePassword("pferrot", null));
		user.setPassword("pf");
		user.setEnabled(Boolean.TRUE);
		user.setCreationDate(new Date());
		Role userRole = roleDao.findRole(Role.USER_ROLE_NAME);
		Role adminRole = roleDao.findRole(Role.ADMIN_ROLE_NAME);
		user.addRole(userRole);
		user.addRole(adminRole);		
		
		person.setUser(user);
		
		personDao.createPerson(person);
		
		
		
		// Create Patrice Ferrot, admin user.
		person = getNewPerson("Stupid", "Illusion", "Stupid.Illusion", "stupid.illusion@gmail.com",
				"Main Street", 12345, "Pik City",
				(Country)listValueDao.findListValue(Country.USA_LABEL_CODE),
				listValueDao.findGender(Gender.MALE_LABEL_CODE));
		
		user = new User();
		user.setUsername("stupid.illusion@gmail.com");
		user.setPassword("stupid");
		user.setEnabled(Boolean.TRUE);
		user.setCreationDate(new Date());
		user.addRole(userRole);
		
		person.setUser(user);		
		
		
		personDao.createPerson(person);
	}
	
	private static Person getNewPerson(final String firstName, final String lastName, final String displayName, 
			final String email, final String address1, final Integer zip, final String city, 
			final Country country, final Gender gender) {
		
		final Person person = new Person();
		
		person.setFirstName(firstName);
		person.setLastName(lastName);
		person.setDisplayName(displayName);
		person.setEmail(email);
		
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
	
	private void createLanguages() {
		Language language = new Language(Language.ENGLISH_LABEL_CODE);
		listValueDao.createListValue(language);
		
		language = new Language(Language.FRENCH_LABEL_CODE);
		listValueDao.createListValue(language);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	// ITEMS	
	private void createItemCategories() {
		ItemCategory category = new ItemCategory(ItemCategory.BOOK_LABEL_CODE);
		listValueDao.createListValue(category);
		
		category = new ItemCategory(ItemCategory.CD_LABEL_CODE);
		listValueDao.createListValue(category);
		
		category = new ItemCategory(ItemCategory.DVD_LABEL_CODE);
		listValueDao.createListValue(category);	
	}
	private void createItems() {
		// Create book.
		Item item = new Item();
		item.setTitle("Les fourmis");
		item.setDescription("Blabla blabla le dude blablabla tadadada...");
		item.addCategory((ItemCategory) listValueDao.findListValue(ItemCategory.BOOK_LABEL_CODE));
		item.setOwner(personDao.findPersonFromUsername("patrice.ferrot@gmail.com"));
		itemDao.createItem(item);

		// Create CD.
		item = new Item();
		item.setTitle("Massive Attack - Helligoland");
		item.setDescription("Track 1:..., Track 2: ...");
		item.addCategory((ItemCategory) listValueDao.findListValue(ItemCategory.CD_LABEL_CODE));
		item.setOwner(personDao.findPersonFromUsername("patrice.ferrot@gmail.com"));
		itemDao.createItem(item);

		// Create DVD.
		item = new Item();
		item.setTitle("Trainspotting");
		item.setDescription("Tadadadadadada dadadadada adadadadadadadadadad");
		item.addCategory((ItemCategory) listValueDao.findListValue(ItemCategory.DVD_LABEL_CODE));
		item.setOwner(personDao.findPersonFromUsername("patrice.ferrot@gmail.com"));
		itemDao.createItem(item);	
	}
}
