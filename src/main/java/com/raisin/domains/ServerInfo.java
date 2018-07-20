package com.raisin.domains;

/**
 * Created by nayan.kakati on 11/16/17.
 */
public class ServerInfo {

	private int port;
	private String host;
	private String sinkA;
	private String sourceA;
	private String sourceB;

	public ServerInfo(int port, String host, String sinkA, String sourceA, String sourceB) {
		this.port = port;
		this.host = host;
		this.sinkA = sinkA;
		this.sourceA = sourceA;
		this.sourceB = sourceB;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getSinkA() {
		return sinkA;
	}

	public void setSinkA(String sinkA) {
		this.sinkA = sinkA;
	}

	public String getSourceA() {
		return sourceA;
	}

	public void setSourceA(String sourceA) {
		this.sourceA = sourceA;
	}

	public String getSourceB() {
		return sourceB;
	}

	public void setSourceB(String sourceB) {
		this.sourceB = sourceB;
	}
}
