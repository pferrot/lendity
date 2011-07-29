package com.pferrot.lendity.initialdata;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.providers.encoding.MessageDigestPasswordEncoder;

import com.pferrot.lendity.dao.GroupDao;
import com.pferrot.lendity.dao.ItemDao;
import com.pferrot.lendity.dao.ListValueDao;
import com.pferrot.lendity.dao.NeedDao;
import com.pferrot.lendity.dao.PersonDao;
import com.pferrot.lendity.dao.bean.GroupDaoQueryBean;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.dao.bean.PersonDaoQueryBean;
import com.pferrot.lendity.group.GroupService;
import com.pferrot.lendity.group.exception.GroupException;
import com.pferrot.lendity.item.jsf.ItemOverviewController;
import com.pferrot.lendity.model.ConnectionRequestResponse;
import com.pferrot.lendity.model.Country;
import com.pferrot.lendity.model.Gender;
import com.pferrot.lendity.model.Group;
import com.pferrot.lendity.model.Item;
import com.pferrot.lendity.model.ItemCategory;
import com.pferrot.lendity.model.ItemVisibility;
import com.pferrot.lendity.model.Language;
import com.pferrot.lendity.model.LendRequestResponse;
import com.pferrot.lendity.model.Need;
import com.pferrot.lendity.model.Objekt;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.model.PersonDetailsVisibility;
import com.pferrot.security.dao.RoleDao;
import com.pferrot.security.dao.UserDao;
import com.pferrot.security.model.Role;
import com.pferrot.security.model.User;
import com.pferrot.security.passwordgenerator.PasswordGenerator;

/**
 * Creates initial data necessary for the application (list values, user roles,...).
 * This bean is currently called from a servlet (see InitialDataController). 
 * It must move at some point outside of the web application.
 * 
 * @author Patrice
 *
 */
public class InitialData {
	
	private final static Log log = LogFactory.getLog(InitialData.class);
	
	private RoleDao roleDao;
	private UserDao userDao;
	private PersonDao personDao;
	private GroupDao groupDao;
	private GroupService groupService;
	private ListValueDao listValueDao;
	private ItemDao itemDao;
	private NeedDao needDao;
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

	public void setGroupDao(GroupDao groupDao) {
		this.groupDao = groupDao;
	}
	
	public void setGroupService(GroupService groupService) {
		this.groupService = groupService;
	}

	public void setItemDao(ItemDao itemDao) {
		this.itemDao = itemDao;
	}

	public void setNeedDao(NeedDao needDao) {
		this.needDao = needDao;
	}

	public void setListValueDao(ListValueDao listValueDao) {
		this.listValueDao = listValueDao;
	}
	
	public void setPasswordEncoder(MessageDigestPasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public void createAll() {
		try {
//		final Person patricePerson = personDao.findPersonFromUsername("patrice.ferrot@gmail.com");
//		if (!isEmptyDB()) {
//			throw new PermissionDeniedDataAccessException("DB is not empty...cannot initialize data.", null);
//		}
//		createCountries();
//		createRoles();
//		createGenders();
//		createItemCategories();
//		createLanguages();
//		createConnectionRequestResponse();
//		createLendRequestResponse();
		
//		createPersonsAndUsers();
//			createRandomPersonsAndUsers(10000);	
//			createRandomGroups(1000);
//		createRandomPersonsAndUsers(1);
			connectPersons(200000);
//			populateGroups(5000);
//			createRandomItems(30000);
//			createRandomNeeds(2000);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}		
	}
	
	private void connectPersons(int pNbConnectionsToCreate) {
		for (int i = 0; i < pNbConnectionsToCreate; i++) {
			// Need to pass different firstResult to get different persons.
			// Probably the cache returning the same results for the same query...
			final Person p1 = findRandomPerson(0);
			final Person p2 = findRandomPerson(1);
			if (!p1.getId().equals(p2.getId())) {
				p1.addConnection(p2);
				personDao.updatePerson(p1);
				personDao.updatePerson(p2);
			}
			if ((i+1 % 100) == 0) {
				log.warn("Connections: " + (i+1));
			}
		}
	}
	
	private void populateGroups(int pNbMembershipToCreate) throws GroupException {
		for (int i = 0; i < pNbMembershipToCreate; i++) {
			final Group g = findRandomGroup();
			final Person p = findRandomPerson();
			if (!p.getId().equals(g.getOwner().getId())) {
				groupService.updateGroupAddMemberPrivileged(g, p);
			}
			if ((i+1 % 100) == 0) {
				log.warn("Groups populations: " + (i+1));
			}
		}
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
		Person person = getNewPerson("Patrice", "Ferrot", "Patrice", "patrice.ferrot@gmail.com");
		
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
		person = getNewPerson("Stupid", "Illusion", "Stupid.Illusion", "stupid.illusion@gmail.com");
		
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
	
	private void createRandomGroups(int pNumberToCreate) {
		Group group = null;
		String title = null;
		String description = null;
		Boolean validateMembership = null;
		
		for (int i = 0; i < pNumberToCreate; i++) {
			title = PasswordGenerator.getNewPassword(PasswordGenerator.getRandom(4, 20));
			description = PasswordGenerator.getNewPassword(PasswordGenerator.getRandom(20, 1000));
			validateMembership = getRandomBoolean();			
			
			group = new Group();
			group.setTitle(title);
			group.setDescription(description);
			group.setCreationDate(new Date());
			group.setOwner(findRandomPerson());
			group.setValidateMembership(validateMembership);
			
			groupDao.createGroup(group);
			
			if ((i+1 % 100) == 0) {
				log.warn("Groups created: " + (i+1));
			}
		}		
	}
	
	private void createRandomPersonsAndUsers(int pNumberToCreate) {
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
		    //password = PasswordGenerator.getNewPassword();
		    // klop encrypted
			password = "21a543c24d2197587030b5297fc242b6";
			person = getNewPerson(firstName, lastName, displayName, email);
			
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
			
			if ((i+1 % 100) == 0) {
				log.warn("Persons created: " + (i+1));
			}
			
//			createItems(person, PasswordGenerator.getRandom(0, 20));
//			createNeeds(person, PasswordGenerator.getRandom(0, 5));
			
			// Connect to Patrice?
//			if (PasswordGenerator.getRandom(0, 3) == 0) {
//				person.addConnection(pPatricePerson);
//				personDao.updatePerson(pPatricePerson);
//				personDao.updatePerson(person);				
//			}
		}
		
	}
	
	private Person getNewPerson(final String firstName, final String lastName, final String displayName, 
			final String email) {
		
		final Person person = new Person();
		
		person.setFirstName(firstName);
		person.setLastName(lastName);
		person.setBirthdate(new Date());
		person.setDisplayName(displayName);
		person.setEmail(email);
		person.setEmailSubscriber(Boolean.FALSE);
		person.setReceiveNeedsNotifications(Boolean.FALSE);
		person.setReceiveCommentsOnCommentedNotif(Boolean.FALSE);
		person.setReceivePotentialConnectionNotif(Boolean.FALSE);
		person.setReceiveCommentsOnOwnNotif(Boolean.FALSE);
		person.setReceiveNewsletter(Boolean.FALSE);
		person.setDetailsVisibility((PersonDetailsVisibility)listValueDao.findListValue(PersonDetailsVisibility.PRIVATE));
		person.setEnabled(Boolean.TRUE);
		
		// That is really fake...
		person.setAddressHome(PasswordGenerator.getNewPassword(PasswordGenerator.getRandom(4, 12)));
		person.setAddressHomeLatitude(getRandomDouble(0, 180));
		person.setAddressHomeLongitude(getRandomDouble(0, 180));
		
		person.setNbEvalScore1(Integer.valueOf(0));
		person.setNbEvalScore2(Integer.valueOf(0));
		
		return person;
	}

	// TODO
	private void createCountries() {
		Country country = new Country(Country.SWITZERLAND_LABEL_CODE);
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
	
	private Person findRandomPerson() {
		return findRandomPerson(0);
	}
	
	private Person findRandomPerson(final int pFirstResult) {
		final PersonDaoQueryBean queryBean = new PersonDaoQueryBean();
		queryBean.setEnabled(Boolean.TRUE);
		queryBean.setOrderBy("random");
		queryBean.setFirstResult(pFirstResult);
		queryBean.setMaxResults(1);
		final List<Person> persons = personDao.findPersonsList(queryBean);
		return persons.get(0);
	}
	
	private Group findRandomGroup() {
		final GroupDaoQueryBean queryBean = new GroupDaoQueryBean();
		queryBean.setOrderBy("random");
		queryBean.setFirstResult(0);
		queryBean.setMaxResults(1);
		final List<Group> groups = groupDao.findGroupsList(queryBean);
		return groups.get(0);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	// ITEMS	
	private void createItemCategories() {	
		for (int i = 0; i < ItemCategory.LABEL_CODES.length; i++) {
			listValueDao.createListValue(new ItemCategory(ItemCategory.LABEL_CODES[i]));
		}	
	}
	
	private void createRandomItems(final int pNbToCreate) {
		for (int i = 0; i < pNbToCreate; i++) {
			final Person p = findRandomPerson();
			createItems(p, 1);
			if ((i+1 % 100) == 0) {
				log.warn("Items created: " + (i+1));
			}			
		}
	}

	private void createItems(final Person pPerson, final int pNbItems) {
		for (int i = 0; i < pNbItems; i++) {
			Item item = new Item();
			
			populateObjekt(item, pPerson);
			
			int tmp = PasswordGenerator.getRandom(0, 10);
			if (tmp > 8) {
				item.setToGiveForFree(Boolean.TRUE);
			}
			else {
				item.setToGiveForFree(Boolean.FALSE);
			}
			if (Boolean.FALSE.equals(item.getToGiveForFree())) {
				tmp = PasswordGenerator.getRandom(0, 10);
				if (tmp > 8) {
					item.setSalePrice(getRandomDouble(30, 1000));
				}	
			}
			tmp = PasswordGenerator.getRandom(0, 10);
			if (tmp > 8) {
				item.setRentalFee(getRandomDouble(10, 300));
			}

			itemDao.createItem(item);
		}		
	}

	private void createRandomNeeds(final int pNbNeeds) {
		for (int i = 0; i < pNbNeeds; i++) {
			createNeeds(findRandomPerson(), 1);
			if ((i+1 % 100) == 0) {
				log.warn("Needs created: " + (i+1));
			}
		}
	}
	
	private void populateObjekt(final Objekt pObjekt, final Person pOwner) {
		pObjekt.setTitle(getRandomText(1, 4, 3, 15));
		pObjekt.setDescription(getRandomText(0, 50, 3, 20));
		pObjekt.setOwner(pOwner);
		pObjekt.setCreationDate(new Date());
		
		int tmp = PasswordGenerator.getRandom(0, 2);
		if (tmp == 0) {
			pObjekt.setVisibility((ItemVisibility)listValueDao.findListValue(ItemVisibility.PRIVATE));
		}
		else if (tmp == 1) {
			pObjekt.setVisibility((ItemVisibility)listValueDao.findListValue(ItemVisibility.CONNECTIONS));
		}
		else {
			pObjekt.setVisibility((ItemVisibility)listValueDao.findListValue(ItemVisibility.PUBLIC));
		}
		
		pObjekt.setCategory(getRandomItemCategory());
		
		final ListWithRowCount groups = groupService.findPersonGroupsWhereOwnerOrAdministratorOrMember(pOwner.getId(), null, 0, 0);
		if (groups != null && groups.getRowCount() > 0) {
			List<Group> groupsList = groups.getList();
			long count = groups.getRowCount();
			for (int j = 0; j < count; j++) {
				tmp = PasswordGenerator.getRandom(0, 10);
				if (tmp > 6) {
					Group group = groupsList.get(j);
					pObjekt.addGroupAuthorized(group);
				}
			}				
		}		
	}
	
	private ItemCategory getRandomItemCategory() {
		String[] labelCodes = ItemCategory.LABEL_CODES;
		int i = PasswordGenerator.getRandom(0, labelCodes.length - 1);
		return (ItemCategory)listValueDao.findListValue(labelCodes[i]);
	}
	
	private void createNeeds(final Person pPerson, final int pNbNeeds) {
		for (int i = 0; i < pNbNeeds; i++) {
			Need need = new Need();
			
			populateObjekt(need, pPerson);
			
			needDao.createNeed(need);
		}		
	}
	
	private String getRandomText(final int pMinNbWords, final int pMaxNbWords, final int pMinWordSize, final int pMaxWordSize) {
		final int nbWords = PasswordGenerator.getRandom(pMinNbWords, pMaxNbWords);
		final StringBuffer result = new StringBuffer();
		for (int i = 0; i < nbWords; i++) {
			result.append(PasswordGenerator.getNewPassword(PasswordGenerator.getRandom(pMinWordSize, pMaxWordSize)));
			if (i < nbWords - 1) {
				result.append(" ");
			}
		}
		return result.toString();
		
	}
	
	private Boolean getRandomBoolean() {
		final int i = PasswordGenerator.getRandom(0, 1);
		if (i == 0) {
			return Boolean.FALSE;
		}
		else {
			return Boolean.TRUE;
		}
	}
	
	private Double getRandomDouble(final int pMinValue, final int pMaxValue) {
		final int i = PasswordGenerator.getRandom(pMinValue, pMaxValue);
		return new Double(i);
	}
}
