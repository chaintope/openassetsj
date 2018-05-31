package com.chaintope.openassetsj.model;

public class Rpc {

	private String username;
	private String password;
	private int port;
	private String host;

	public Rpc(String username, String password, int port, String host) {

		this.username = username;
		this.password = password;
		this.port = port;
		this.host = host;
	}

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
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
}
