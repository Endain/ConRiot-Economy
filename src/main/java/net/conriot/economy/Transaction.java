package net.conriot.economy;

import lombok.Getter;

public class Transaction {
	@Getter private String from;
	@Getter private String to;
	@Getter private String fromPrefix;
	@Getter private String toPrefix;
	@Getter private double amount;
	
	public Transaction(String fromPrefix, String from, String toPrefix, String to, double amount) {
		this.fromPrefix = fromPrefix;
		this.from = from;
		this.toPrefix = toPrefix;
		this.to = to;
		this.amount = amount;
	}
}
