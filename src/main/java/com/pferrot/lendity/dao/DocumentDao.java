package com.pferrot.lendity.dao;

import com.pferrot.lendity.model.Document;

public interface DocumentDao {
	
	Long createDocument(Document document);
	
	Document findDocument(Long documentId);
	
	void updateDocument(Document document);
	
	void deleteDocument(Document document);
	
	boolean isDocumentPublic(Long documentId);


}
