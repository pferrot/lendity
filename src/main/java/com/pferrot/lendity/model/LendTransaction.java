package com.pferrot.lendity.model;

import java.io.Serializable;
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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

import com.pferrot.core.CoreUtils;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "LEND_TRANSACTIONS")
public class LendTransaction implements Commentable<LendTransactionComment>, Serializable {
	
	@Id @GeneratedValue
	@Column(name = "ID")
    private Long id;
	
	@OneToMany(targetEntity = com.pferrot.lendity.model.LendTransactionComment.class,
			mappedBy = "lendTransaction",
			cascade = CascadeType.REMOVE,
			fetch = FetchType.LAZY)
	private Set<LendTransactionComment> comments = new HashSet<LendTransactionComment>();
	
	/**
	 * Persons who get an email when a comment is added.
	 */
	@ManyToMany(targetEntity = com.pferrot.lendity.model.Person.class)
	@JoinTable(
			name = "LEND_TRANSACTIONS_COMMENTS_RECIPIENTS",
			joinColumns = {@JoinColumn(name = "LEND_TRANSACTION_ID")},
			inverseJoinColumns = {@JoinColumn(name = "COMMENT_RECIPIENT_ID")}
	)
	private Set<Person> commentsRecipients = new HashSet<Person>();

	@ManyToOne(targetEntity = LendTransactionStatus.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "STATUS_ID", nullable = false)
	@Audited
	private LendTransactionStatus status;
	
	// Date when the transaction starts, i.e. when the item should be given to the borrower.
	// Note that that date may be modified as long as the status is "opened".
	@Column(name = "START_DATE", nullable = true)
	@Audited
	private Date startDate;
	
	// Date when the transaction is completed.
	@Column(name = "END_DATE", nullable = true)
	@Audited
	private Date endDate;
	
	@OneToOne(targetEntity = Person.class)
	@JoinColumn(name = "LENDER_ID", nullable = false)
	private Person lender;
	
	@OneToOne(targetEntity = Person.class)
	@JoinColumn(name = "BORROWER_ID", nullable = false)
	private Person borrower;
	
	@OneToOne(targetEntity = LendRequest.class)
	@JoinColumn(name = "LEND_REQUEST_ID", nullable = true)
	private LendRequest lendRequest;
	
	@OneToOne(targetEntity = com.pferrot.lendity.model.InternalItem.class)
	@JoinColumn(name = "ITEM_ID", nullable = false)
	private InternalItem item;
	
	@Column(name = "CREATION_DATE", nullable = false)
	private Date creationDate;

	public LendTransaction() {
		super();
	}	

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public Set<LendTransactionComment> getComments() {
		return comments;
	}

	public void setComments(final Set<LendTransactionComment> pComments) {
		this.comments = pComments;
	}

	public void addComment(final LendTransactionComment pComment) {
		CoreUtils.assertNotNull(pComment);
		comments.add(pComment);
	}
		
	public void removeComment(final LendTransactionComment pComment) {
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

	public LendTransactionStatus getStatus() {
		return status;
	}

	public void setStatus(LendTransactionStatus status) {
		this.status = status;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Person getLender() {
		return lender;
	}

	public void setLender(Person lender) {
		this.lender = lender;
	}

	public Person getBorrower() {
		return borrower;
	}

	public void setBorrower(Person borrower) {
		this.borrower = borrower;
	}

	public LendRequest getLendRequest() {
		return lendRequest;
	}

	public void setLendRequest(LendRequest lendRequest) {
		this.lendRequest = lendRequest;
	}

	public InternalItem getItem() {
		return item;
	}

	public void setItem(InternalItem item) {
		this.item = item;
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
		LendTransaction other = (LendTransaction) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "LendTransaction [id=" + id + "]";
	}	
}