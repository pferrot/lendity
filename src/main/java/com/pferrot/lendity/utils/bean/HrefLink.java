package com.pferrot.lendity.utils.bean;

import java.io.Serializable;

public class HrefLink implements Serializable {
	
	private String url;
	private String label;
	
	public HrefLink(String url, String label) {
		super();
		this.url = url;
		this.label = label;
	}

	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	

}
