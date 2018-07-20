package com.raisin.domains;

/**
 * Created by nayan.kakati on 11/16/17.
 */
public class SinkRequest {

	private String id;
	private String kind;

	public SinkRequest(String id, String kind) {
		this.id = id;
		this.kind = kind;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}
}
