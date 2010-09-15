package com.pferrot.lendity.model;

import java.io.InputStream;
import java.io.Serializable;
import java.sql.Blob;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "DOCUMENTS")
public class Document implements Serializable {

	@Id @GeneratedValue
	@Column(name = "ID")
	// Allows accessing the ID even when proxy not loaded.
	@Access(value = AccessType.PROPERTY)
    private Long id;
	
	@Lob
	@Column(name = "CONTENT", nullable = false, columnDefinition="MEDIUMBLOB")
	private Blob content;
	
	// The input stream is only used by the DAO to store the content as a blob. 
	@Transient
	private InputStream inputStream;
	
	@Column(name = "NAME", nullable = false, length = 255)
	private String name;
	
	@Column(name = "SIZE", nullable = false)
	private Long size;

	@Column(name = "MIME_TYPE", nullable = false, length = 100)
	private String mimeType;

	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

//	public byte[] getBytes() {
//		return bytes;
//	}
//
//	public void setBytes(byte[] bytes) {
//		this.bytes = bytes;
//	}

	public Blob getContent() {
		return content;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public void setContent(Blob content) {
		this.content = content;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
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
		else if (!(obj instanceof Document)){
			return false;
		}
		else {
			final Document other = (Document)obj;
			return getId() != null && getId().equals(other.getId());
		}
	}	
}
