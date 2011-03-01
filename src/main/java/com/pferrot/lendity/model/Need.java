package com.pferrot.lendity.model;
// Generated 10 oct. 2008 00:01:18 by Hibernate Tools 3.2.0.b9

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

import com.pferrot.core.CoreUtils;

/**
 * @author Patrice
 *
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "NEEDS")
@Audited
public class Need implements Objekt, CommentableWithOwner<NeedComment> {
	
	@Id @GeneratedValue
	@Column(name = "ID")
    private Long id;

	@OneToOne(targetEntity = Person.class)
	@JoinColumn(name = "OWNER_ID")
	private Person owner;
	
	@Column(name = "TITLE", nullable = false, length = 255)
	private String title;

	@Column(name = "DESCRIPTION", nullable = true, length = 3999)
	private String description;
	
	@ManyToOne(targetEntity = ItemCategory.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "CATEGORY_ID", nullable = false)
	private ItemCategory category;
	
	@ManyToOne(targetEntity = ItemVisibility.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "VISIBILITY_ID", nullable = false)
	private ItemVisibility visibility;
	
	@Column(name = "CREATION_DATE", nullable = false)
	private Date creationDate;

	@OneToMany(targetEntity = com.pferrot.lendity.model.NeedComment.class,
			mappedBy = "need",
			cascade = CascadeType.REMOVE,
			fetch = FetchType.LAZY)
	private Set<NeedComment> comments = new HashSet<NeedComment>();
	
	/**
	 * Persons who get an email when a comment is added.
	 */
	@ManyToMany(targetEntity = com.pferrot.lendity.model.Person.class)
	@JoinTable(
			name = "NEEDS_COMMENTS_RECIPIENTS",
			joinColumns = {@JoinColumn(name = "NEED_ID")},
			inverseJoinColumns = {@JoinColumn(name = "COMMENT_RECIPIENT_ID")}
	)
	private Set<Person> commentsRecipients = new HashSet<Person>();
	
	@ManyToMany(targetEntity = com.pferrot.lendity.model.Item.class)
	@JoinTable(
			name = "ITEMS_NEEDS",
			joinColumns = {@JoinColumn(name = "NEED_ID")},
			inverseJoinColumns = {@JoinColumn(name = "ITEM_ID")}
	)
	private Set<Item> relatedItems = new HashSet<Item>();
	
	@Version
	@Column(name = "OBJ_VERSION")
	private int version;	
	
    public Need() {
    	super();
    }
   	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Person getOwner() {
		return owner;
	}

	public void setOwner(Person owner) {
		this.owner = owner;
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

	public ItemCategory getCategory() {
		return category;
	}

	public void setCategory(ItemCategory category) {
		this.category = category;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Set<NeedComment> getComments() {
		return comments;
	}

	public void setComments(final Set<NeedComment> pComments) {
		this.comments = pComments;
	}

	public void addComment(final NeedComment pComment) {
		CoreUtils.assertNotNull(pComment);
		comments.add(pComment);
	}
		
	public void removeComment(final NeedComment pComment) {
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

	public Set<Item> getRelatedItems() {
		return relatedItems;
	}

	public void setRelatedItems(final Set<Item> pRelatedItems) {
		this.relatedItems = pRelatedItems;
	}

	public void addRelatedItem(final Item pRelatedItem) {
		CoreUtils.assertNotNull(pRelatedItem);
		relatedItems.add(pRelatedItem);
	}
		
	public void removeRelatedItem(final Item pRelatedItem) {
		CoreUtils.assertNotNull(pRelatedItem);
		relatedItems.remove(pRelatedItem);
	}

	public ItemVisibility getVisibility() {
		return visibility;
	}
	
	public void setVisibility(ItemVisibility visibility) {
		this.visibility = visibility;
	}

	public boolean isPublicVisibility() {
		return getVisibility().getLabelCode().equals(ItemVisibility.PUBLIC);
	}

	public boolean isPrivateVisibility() {
		return getVisibility().getLabelCode().equals(ItemVisibility.PRIVATE);
	}
	
	public boolean isConnectionsVisibility() {
		return getVisibility().getLabelCode().equals(ItemVisibility.CONNECTIONS);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		// This tests if null at the same time.
		else if (!(obj instanceof Need)){
			return false;
		}
		else {
			final Need other = (Need)obj;
			return getId() != null && getId().equals(other.getId());
		}
	}


	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer();
		
		if (this instanceof Need) {
			sb.append("Need, ");
			sb.append("ID: ");
			sb.append(getId());
		}
		sb.append(", title: ");
		sb.append(getTitle());
		return sb.toString();
	}
}


