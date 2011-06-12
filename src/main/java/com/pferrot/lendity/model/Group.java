package com.pferrot.lendity.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.pferrot.core.CoreUtils;

@Entity
@Table(name = "GROUPS")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Audited
public class Group implements CommentableWithOwner<GroupComment>, Ownable {
	
	@Id @GeneratedValue
	@Column(name = "ID")
    private Long id;
	
	@Column(name = "TITLE", nullable = false, length = 255)
	private String title;
	
	@Column(name = "DESCRIPTION", nullable = false, length = 3999)
	private String description;

	@OneToOne(targetEntity = Document.class, cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	@JoinColumn(name = "IMAGE_1_ID", nullable = true)
	@NotAudited
	private Document image1;
	
	@OneToOne(targetEntity = Document.class, cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	@JoinColumn(name = "THUMBNAIL_1_ID", nullable = true)
	@NotAudited
	private Document thumbnail1;
	
	// True if owner / administrators must validate new members,
	// false otherwise.
	@Column(name = "VALIDATE_MEMBERSHIP", nullable = false)
	private Boolean validateMembership;
	
	@ManyToOne(targetEntity = Person.class)
	@JoinColumn(name = "OWNER_ID")
	private Person owner;
	
	@ManyToMany(targetEntity = com.pferrot.lendity.model.Person.class)
	@JoinTable(
			name = "GROUPS_ADMINISTRATORS",
			joinColumns = {@JoinColumn(name = "GROUP_ID")},
			inverseJoinColumns = {@JoinColumn(name = "ADMINISTRATOR_ID")}
	)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Set<Person> administrators = new HashSet<Person>();
	
	@ManyToMany(targetEntity = com.pferrot.lendity.model.Person.class)
	@JoinTable(
			name = "GROUPS_MEMBERS",
			joinColumns = {@JoinColumn(name = "GROUP_ID")},
			inverseJoinColumns = {@JoinColumn(name = "MEMBER_ID")}
	)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Set<Person> members = new HashSet<Person>();
	
	@ManyToMany(targetEntity = com.pferrot.lendity.model.Person.class)
	@JoinTable(
			name = "GROUPS_BANNED_PERSONS",
			joinColumns = {@JoinColumn(name = "GROUP_ID")},
			inverseJoinColumns = {@JoinColumn(name = "BANNED_PERSON_ID")}
	)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Set<Person> bannedPersons = new HashSet<Person>();
	
	@OneToMany(targetEntity = com.pferrot.lendity.model.GroupComment.class,
			mappedBy = "group",
			cascade = CascadeType.REMOVE,
			fetch = FetchType.LAZY)
	private Set<GroupComment> comments = new HashSet<GroupComment>();
	
	/**
	 * Persons who get an email when a comment is added.
	 */
	@ManyToMany(targetEntity = com.pferrot.lendity.model.Person.class)
	@JoinTable(
			name = "GROUPS_COMMENTS_RECIPIENTS",
			joinColumns = {@JoinColumn(name = "GROUP_ID")},
			inverseJoinColumns = {@JoinColumn(name = "COMMENT_RECIPIENT_ID")}
	)
	private Set<Person> commentsRecipients = new HashSet<Person>();
	
	@Column(name = "CREATION_DATE", nullable = false)
	private Date creationDate;

	@Version
	@Column(name = "OBJ_VERSION")
	private int version;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Document getImage1() {
		return image1;
	}

	public void setImage1(Document image1) {
		this.image1 = image1;
	}

	public Document getThumbnail1() {
		return thumbnail1;
	}

	public void setThumbnail1(Document thumbnail1) {
		this.thumbnail1 = thumbnail1;
	}

	public Boolean getValidateMembership() {
		return validateMembership;
	}

	public void setValidateMembership(Boolean validateMembership) {
		this.validateMembership = validateMembership;
	}

	public Person getOwner() {
		return owner;
	}

	public void setOwner(Person owner) {
		this.owner = owner;
	}

	public Set<Person> getAdministrators() {
		return administrators;
	}

	public void setAdministrators(final Set<Person> pPersons) {
		this.administrators = pPersons;
	}

	public void addAdministrator(final Person pPerson) {
		CoreUtils.assertNotNull(pPerson);
		administrators.add(pPerson);
	}
		
	public void removeAdministrator(final Person pPerson) {
		CoreUtils.assertNotNull(pPerson);
		administrators.remove(pPerson);
	}

	public Set<Person> getMembers() {
		return members;
	}

	public void setMembers(final Set<Person> pPersons) {
		this.members = pPersons;
	}

	public void addMember(final Person pPerson) {
		CoreUtils.assertNotNull(pPerson);
		members.add(pPerson);
	}
		
	public void removeMember(final Person pPerson) {
		CoreUtils.assertNotNull(pPerson);
		members.remove(pPerson);
	}	
	
	public Set<Person> getBannedPersons() {
		return bannedPersons;
	}

	public void setBannedPersons(final Set<Person> pPersons) {
		this.bannedPersons = pPersons;
	}

	public void addBannedPerson(final Person pPerson) {
		CoreUtils.assertNotNull(pPerson);
		bannedPersons.add(pPerson);
	}
		
	public void removeBannedPerson(final Person pPerson) {
		CoreUtils.assertNotNull(pPerson);
		bannedPersons.remove(pPerson);
	}

	public Set<GroupComment> getComments() {
		return comments;
	}

	public void setComments(final Set<GroupComment> pComments) {
		this.comments = pComments;
	}

	public void addComment(final GroupComment pComment) {
		CoreUtils.assertNotNull(pComment);
		comments.add(pComment);
	}
		
	public void removeComment(final GroupComment pComment) {
		CoreUtils.assertNotNull(pComment);
		comments.remove(pComment);
	}

	public Set<Person> getCommentsRecipients() {
		return commentsRecipients;
	}

	public void setCommentsRecipients(final Set<Person> pCommentsRecipients) {
		this.commentsRecipients = pCommentsRecipients;
	}

	public void addCommentRecipient(final Person pCommentRecipient) {
		CoreUtils.assertNotNull(pCommentRecipient);
		commentsRecipients.add(pCommentRecipient);
	}
		
	public void removeCommentRecipient(final Person pCommentRecipient) {
		CoreUtils.assertNotNull(pCommentRecipient);
		commentsRecipients.remove(pCommentRecipient);
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Group other = (Group) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Group [id=" + id + ", title=" + title + "]";
	}	
}
