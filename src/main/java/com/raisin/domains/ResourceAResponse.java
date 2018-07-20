package com.raisin.domains;

/**
 * Created by nayan.kakati on 11/16/17.
 */
public class ResourceAResponse {

	private String id;
	private String status;
	private boolean isDefective;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isDefective() {
		return isDefective;
	}

	public void setDefective(boolean defective) {
		isDefective = defective;
	}
}
