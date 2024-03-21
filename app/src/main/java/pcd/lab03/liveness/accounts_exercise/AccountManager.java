package pcd.lab03.liveness.accounts_exercise;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AccountManager {
	
	private final Account[] accounts;

	public AccountManager(int nAccounts, int amount){
		accounts = new Account[nAccounts];
		for (int i = 0; i < accounts.length; i++){
			accounts[i] = new Account(amount, new ReentrantLock());
		}
	}
	
	public void transferMoney(int from,	int to, int amount) throws InsufficientBalanceException {
		Account fromAccount = accounts[from];
		Account toAccount = accounts[to];
		Lock fromLock = fromAccount.getLock();
		Lock toLock = toAccount.getLock();
		try {
			if (from > to) {
				toLock.lock();
				fromLock.lock();
			} else {
				fromLock.lock();
				toLock.lock();
			}
			if (fromAccount.getBalance() < amount) {
				throw new InsufficientBalanceException();
			}
			fromAccount.debit(amount);
			toAccount.credit(amount);
		} finally {
			fromLock.unlock();
			toLock.unlock();
		}
	}
	
	public int getNumAccounts() {
		return accounts.length;
	}
}

