package com.pferrot.lendity.dao.hibernate;

import java.sql.Blob;
import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.pferrot.lendity.dao.DocumentDao;
import com.pferrot.lendity.model.Document;
import com.pferrot.lendity.model.OrderedListValue;

public class DocumentDaoHibernateImpl extends HibernateDaoSupport implements DocumentDao {

	public Long createDocument(final Document pDocument) {
		final Blob blob = Hibernate.createBlob(pDocument.getInputStream(), pDocument.getSize(), getSession());
		pDocument.setContent(blob);
		return (Long)getHibernateTemplate().save(pDocument);
	}

	public void deleteDocument(final Document pDocument) {
		getHibernateTemplate().delete(pDocument);
	}

	public void updateDocument(final Document pDocument) {
		final Blob blob = Hibernate.createBlob(pDocument.getInputStream(), pDocument.getSize(), getSession());
		pDocument.setContent(blob);
		getHibernateTemplate().update(pDocument);
	}

	public Document findDocument(final Long pDocumentId) {
		final Document document = (Document)getHibernateTemplate().load(Document.class, pDocumentId);
//		final Blob blob = document.getContent();
//		document.setBytes(toByteArray(blob));
		return document;
	}

	public boolean isDocumentPublic(final Long pDocumentId) {		
		final List<Boolean> list = getHibernateTemplate().find("select publik from Document d where d.id = ?", pDocumentId);
		if (list == null || list.isEmpty()) {
			return false;
		}
		return Boolean.TRUE.equals(list.get(0));
	}

//	private byte[] toByteArray(Blob fromImageBlob) {
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		try {
//			return toByteArrayImpl(fromImageBlob, baos);
//		} 
//		catch (Exception e) {
//		}
//		return null;
//	}
//
//
//	private byte[] toByteArrayImpl(final Blob blob, ByteArrayOutputStream baos) throws IOException, SQLException {
//		byte buf[] = new byte[4000];
//		int dataSize;
//		InputStream is = blob.getBinaryStream();
//		try {
//			while((dataSize = is.read(buf)) != -1) {
//				baos.write(buf, 0, dataSize);
//			}    
//		} 
//		finally {
//			if(is != null) {
//				is.close();
//			}
//		}
//		return baos.toByteArray();
//	}

}
