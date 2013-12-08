package net.conriot.sona.economy;

public enum Status {
	SUCCESS(1),
	INSUFFICIENT_FUNDS(2),
	UNKNOWN_TO(3),
	UNKNOWN_FROM(4),
	SYSTEM_ERROR(5);
	
	private int value;
	
	private Status(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
}
