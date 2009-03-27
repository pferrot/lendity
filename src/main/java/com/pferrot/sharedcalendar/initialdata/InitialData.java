package com.pferrot.sharedcalendar.initialdata;

import java.util.Date;

import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.security.providers.encoding.MessageDigestPasswordEncoder;

import com.pferrot.security.dao.RoleDao;
import com.pferrot.security.dao.UserDao;
import com.pferrot.security.model.Role;
import com.pferrot.security.model.User;
import com.pferrot.sharedcalendar.dao.ListValueDao;
import com.pferrot.sharedcalendar.dao.PersonDao;
import com.pferrot.sharedcalendar.model.Address;
import com.pferrot.sharedcalendar.model.Country;
import com.pferrot.sharedcalendar.model.Gender;
import com.pferrot.sharedcalendar.model.Person;
import com.pferrot.sharedcalendar.model.PersonSpeciality;
import com.pferrot.sharedcalendar.model.movie.MovieCategory;
import com.pferrot.sharedcalendar.model.movie.MovieFormat;
import com.pferrot.sharedcalendar.model.movie.MovieLanguage;
import com.pferrot.sharedcalendar.model.movie.MovieSubtitle;

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
		createTimeZones();
		createCountries();
		createRoles();
		createGenders();
		
		createPersonSpecialities();
		createPersonsAndUsers();
		
		createMovieCategories();
		createMovieFormats();
		createMovieLanguages();
		createMovieSubtitles();
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
		Person person = getNewPerson("Patrice", "Ferrot", "patrice.ferrot@gmail.com",
				"Châtelard 1", 1400, "Yverdon-les-Bains",
				(Country)listValueDao.findListValue(Country.SWITZERLAND_LABEL_CODE),
				listValueDao.findGender(Gender.MALE_LABEL_CODE));
		
		User user = new User();
		user.setUsername("pferrot");
		user.setPassword(passwordEncoder.encodePassword("patrice", null));
		user.setEnabled(Boolean.TRUE);
		user.setCreationDate(new Date());
		Role userRole = roleDao.findRole(Role.USER_ROLE_NAME);
		Role adminRole = roleDao.findRole(Role.ADMIN_ROLE_NAME);
		user.addRole(userRole);
		user.addRole(adminRole);		
		
		person.setUser(user);
		
		personDao.createPerson(person);
		
		// Create Angelina Jolie...
		person = getNewPerson("Angelina", "Jolie", null,
				null, null, null,
				(Country)listValueDao.findListValue(Country.USA_LABEL_CODE),
				listValueDao.findGender(Gender.FEMALE_LABEL_CODE));
		
		person.addSpeciality((PersonSpeciality)listValueDao.findListValue(PersonSpeciality.ACTOR_LABEL_CODE));
		personDao.createPerson(person);

		// Create Francis Ford Coppola...
		person = getNewPerson("Francis Ford", "Coppola", null,
				null, null, null,
				(Country)listValueDao.findListValue(Country.USA_LABEL_CODE),
				listValueDao.findGender(Gender.MALE_LABEL_CODE));
		
		person.addSpeciality((PersonSpeciality)listValueDao.findListValue(PersonSpeciality.DIRECTOR_LABEL_CODE));
		personDao.createPerson(person);
	}
	
	private static Person getNewPerson(final String firstName, final String lastName, final String email, final String address1,
			final Integer zip, final String city, final Country country, final Gender gender) {
		
		final Person person = new Person();
		
		person.setFirstName(firstName);
		person.setLastName(lastName);
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
	private void createTimeZones() {

	}

	// TODO
	private void createCountries() {
		Country country = new Country(Country.SWITZERLAND_LABEL_CODE);
		listValueDao.createListValue(country);
		
		country = new Country(Country.USA_LABEL_CODE);
		listValueDao.createListValue(country);		
	}
	
	private void createPersonSpecialities() {
		PersonSpeciality speciality = new PersonSpeciality(PersonSpeciality.ACTOR_LABEL_CODE);
		listValueDao.createListValue(speciality);
		
		speciality = new PersonSpeciality(PersonSpeciality.DIRECTOR_LABEL_CODE);
		listValueDao.createListValue(speciality);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	// MOVIES	
	private void createMovieCategories() {
		MovieCategory category = new MovieCategory(MovieCategory.ACTION_LABEL_CODE);
		listValueDao.createListValue(category);
		
		category = new MovieCategory(MovieCategory.ADVENTURE_LABEL_CODE);
		listValueDao.createListValue(category);
		
		category = new MovieCategory(MovieCategory.FICTION_LABEL_CODE);
		listValueDao.createListValue(category);	
	}
	
	private void createMovieFormats() {
		MovieFormat format = new MovieFormat(MovieFormat.DVD_ZONE_2_LABEL_CODE);
		listValueDao.createListValue(format);
	}
	
	private void createMovieLanguages() {
		MovieLanguage language = new MovieLanguage(MovieLanguage.ENGLISH_LABEL_CODE);
		listValueDao.createListValue(language);
	}

	private void createMovieSubtitles() {
		MovieSubtitle subtitle = new MovieSubtitle(MovieSubtitle.ENGLISH_LABEL_CODE);
		listValueDao.createListValue(subtitle);
	}
}
