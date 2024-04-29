package pcd.lab03.liveness.accounts_exercise;

import java.util.concurrent.locks.*;
class Account {

	private int balance;
	private Lock lock;

	public Account(int amount, Lock lock){
		balance = amount;
		this.lock = lock;
	}
	public Account(int amount){
		balance = amount;
	}

	public int getBalance(){
		return balance;
	}

	public void debit(int amount) throws InsufficientBalanceException{
		balance-=amount;
	}

	public void credit(int amount){
		balance+=amount;
	}

	public Lock getLock() {
		return lock;
	}
}
