package net.conriot.sona.economy;

import lombok.Getter;

class Account {
	@Getter private String prefix;
	@Getter private String owner;
	@Getter private double balance;
	
	public Account(String prefix, String owner, double balance) {
		this.prefix = prefix;
		this.owner = owner;
		this.balance = balance;
	}
	
	public boolean hasFunds(double amount) {
		return (this.balance - amount) >= 0 ? true : false;
	}
	
	public void add(double amount) {
		this.balance += amount;
	}
	
	public void subtract(double amount) {
		this.balance -= amount;
	}
}
