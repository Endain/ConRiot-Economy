package net.conriot.economy;

import net.conriot.sona.mysql.IOCallback;
import net.conriot.sona.mysql.MySQL;
import net.conriot.sona.mysql.Query;
import net.conriot.sona.mysql.Result;

class Validator implements IOCallback {
	private Transaction transaction;
	private TransactionCallback callback;
	private double newFromBal = 0;
	private double newToBal = 0;
	
	public Validator(Transaction transaction, TransactionCallback callback) {
		this.transaction = transaction;
		this.callback = callback;
		
		// Query the database to get some account informations
		queryAccounts();
	}
	
	private void queryAccounts() {
		// Create a query to get the accounts being used
		Query q = MySQL.makeQuery();
		q.setQuery("SELECT * FROM economy WHERE owner=? OR owner=?");
		q.add(this.transaction.getFromPrefix() + this.transaction.getFrom());
		q.add(this.transaction.getToPrefix() + this.transaction.getTo());
		// Execute query to asynchronously load economy data
		MySQL.execute(this, null, q);
	}
	
	private boolean transactionValid(Result result) {
		// If no data was returned, then some or all accounts did not exist
		if(result == null) {
			if(this.transaction.getToPrefix().equals("**"))
				this.callback.complete(Status.UNKNOWN_FROM, this.transaction);
			else
				this.callback.complete(Status.UNKNOWN_TO, this.transaction);
			// Return validation failure
			return false;
		}
		
		// If to or from prefix is '**' it is to/from the server, validate accordingly
		if(this.transaction.getFromPrefix().equals("**")) {
			result.next();
			if((this.transaction.getToPrefix() + this.transaction.getTo()).equals((String)result.get(0))) {
				// Record what the new balance of the 'to' account will be
				this.newToBal = ((double)result.get(1) + this.transaction.getAmount());
				// Payments from the server to an account will always succeed
				return true;
			} else {
				// The 'to' account did not exist
				this.callback.complete(Status.UNKNOWN_TO, this.transaction);
			}
		} else if(this.transaction.getToPrefix().equals("**")) {
			result.next();
			if((this.transaction.getFromPrefix() + this.transaction.getFrom()).equals((String)result.get(0))) {
				// Verify the account has enough to pay the server
				if(((double)result.get(1) - this.transaction.getAmount()) >= 0) {
					// Record the new balanced for the 'from' account
					this.newFromBal = ((double)result.get(1) - this.transaction.getAmount());
					return true;
				} else
					this.callback.complete(Status.INSUFFICIENT_FUNDS, this.transaction);
			} else {
				// The 'from' account did not exist
				this.callback.complete(Status.UNKNOWN_TO, this.transaction);
			}
		} else {
			boolean validTo = false;
			boolean validFrom = false;
			boolean sufficientFunds = false;
			
			while(result.next()) {
				// Check if this entry is the 'from' account
				if((this.transaction.getFromPrefix() + this.transaction.getFrom()).equals(result.get(0))) {
					validFrom = true;
					// Check if the 'from' account has enough funds
					if(((double)result.get(1) - this.transaction.getAmount()) >= 0) {
						// Record the new balanced for the 'from' account
						this.newFromBal = ((double)result.get(1) - this.transaction.getAmount());
						sufficientFunds = true;
					}
				}
				// Check if this entry is the 'to' account
				else if((this.transaction.getToPrefix() + this.transaction.getTo()).equals(result.get(0))) {
					// Record what the new balance of the 'to' account will be
					this.newToBal = ((double)result.get(1) + this.transaction.getAmount());
					validTo = true;
				}
			}
			
			//Check if all conditions met, if so return true
			if(validFrom && validTo && sufficientFunds)
				return true;
			
			// Otherwise, send error depending on what failed
			if(!validFrom)
				this.callback.complete(Status.UNKNOWN_FROM, this.transaction);
			else if(!validTo)
				this.callback.complete(Status.UNKNOWN_TO, this.transaction);
			else
				this.callback.complete(Status.INSUFFICIENT_FUNDS, this.transaction);
		}
		
		// Default to false
		return false;
	}

	@Override
	public void complete(boolean success, Object tag, Result result) {
		// If success, validate returned data otherwise return system error
		if(success) {
			// Continue to execution if the results can be validated
			if(transactionValid(result))
				new Executor(this.transaction, this.callback, this.newFromBal, this.newToBal);
		} else {
			callback.complete(Status.SYSTEM_ERROR, this.transaction);
		}
	}
}
