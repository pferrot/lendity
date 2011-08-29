package com.pferrot.lendity.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.envers.Audited;


@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
		name = "COMMENT_TYPE",
		discriminatorType = DiscriminatorType.STRING
)
@Table(name = "COMMENTS")
@Audited
public abstract class Comment implements Serializable {

	@Id @GeneratedValue
	@Column(name = "ID")
    private Long id;
	
	@Column(name = "TEXT", nullable = false, length = 500)
    private String text;
	
	@ManyToOne(targetEntity = Person.class)
	@JoinColumn(name = "OWNER_ID")
	private Person owner;
	
	@Column(name = "CREATION_DATE", nullable = false)
	private Date creationDate;
	
	@Column(name = "MODIFICATION_DATE", nullable = true)
	private Date modificationDate;
	
	@OneToMany(targetEntity = com.pferrot.lendity.model.ChildComment.class, mappedBy = "parentComment", cascade = CascadeType.REMOVE)
	private Set<ChildComment> childComments = new HashSet<ChildComment>();	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Person getOwner() {
		return owner;
	}

	public void setOwner(Person owner) {
		this.owner = owner;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}
	
	public Set<ChildComment> getChildComments() {
		return childComments;
	}

	public void setChildComments(Set<ChildComment> childComments) {
		this.childComments = childComments;
	}

	public abstract Commentable getContainer();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		// This tests if null at the same time.
		else if (!(obj instanceof Comment)){
			return false;
		}
		else {
			final Comment other = (Comment)obj;
			return id != null && id.equals(other.getId());
		}
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer();
		sb.append("ID: ");
		sb.append(getId());
		return sb.toString();
	}
}
