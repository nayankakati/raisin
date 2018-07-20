package com.raisin.domains;

/**
 * Created by nayan.kakati on 11/16/17.
 */
public class ResourceBResponse {

	private String status;
	private String id;
	private boolean isDefective;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isDefective() {
		return isDefective;
	}

	public void setDefective(boolean defective) {
		isDefective = defective;
	}
}
