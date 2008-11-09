package com.pferrot.sharedcalendar.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
		name = "LIST_VALUE_TYPE",
		discriminatorType = DiscriminatorType.STRING
)
@Table(name = "LIST_VALUES")
public abstract class ListValue {
	
	@Id @GeneratedValue
	@Column(name = "ID")	
	private Long id;
	
	@Column(name = "LABEL_CODE", nullable = false, length = 255)	
	private String labelCode;
	
	public ListValue() {
		super();
	}

	public ListValue(String labelCode) {
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
	
	public void setLabelCode(String labelCode) {
		this.labelCode = labelCode;
	}
}
