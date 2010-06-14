package com.pferrot.lendity.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import com.pferrot.lendity.i18n.LabelCodeAware;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
		name = "LIST_VALUE_TYPE",
		discriminatorType = DiscriminatorType.STRING
)
@Table(name = "LIST_VALUES")
public abstract class ListValue implements Serializable, LabelCodeAware {
	
	@Id @GeneratedValue
	@Column(name = "ID")	
	private Long id;
	
	@Column(name = "LABEL_CODE", unique = true, nullable = false, length = 255)	
	private String labelCode;
	
	public ListValue() {
		super();
	}

	public ListValue(final String labelCode) {
		super();
		this.labelCode = labelCode;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getLabelCode() {
		return labelCode;
	}
	
	public void setLabelCode(final String labelCode) {
		this.labelCode = labelCode;
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
		if (this == obj) {
			return true;
		}
		// This tests if null at the same time.
		else if (!(obj instanceof ListValue)){
			return false;
		}
		else {
			final ListValue other = (ListValue)obj;
			return id != null && id.equals(other.getId());
		}
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer();
		sb.append("ID: ");
		sb.append(getId());
		sb.append(", label code: ");
		sb.append(getLabelCode());
		return sb.toString();
	}
}
