package Banking;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BankAccount {
    private final int id;
    private int balance;
    private final Lock lock = new ReentrantLock();

    public BankAccount(int id, int initialBalance) {
        this.id = id;
        this.balance = initialBalance;
    }

    public int getId(){
        return  id;
    }
    public int getBalance() {
        // TODO: Consider locking (if needed)
        lock.lock();
        try {
            return balance;
        }
        finally {
            lock.unlock();
        }
    }

    public Lock getLock() {
        return lock;
    }

    public void deposit(int amount) {
        // TODO: Safely add to balance.
        lock.lock();
        try {
            balance = amount + balance;
        }
        finally {
            lock.unlock();
        }
    }

    public void withdraw(int amount) {
        // TODO: Safely withdraw from balance.

        lock.lock();
        try {
            balance = balance - amount;
        }
        finally {
            lock.unlock();
        }
    }

    public void transfer(BankAccount target, int amount) {
        // TODO: Safely make the changes
        // HINT: Both accounts need to be locked, while the changes are being made
        // HINT: Be cautious of potential deadlocks.
        BankAccount firstlock = this;
        BankAccount secondlock = target;

        if(this.balance > target.balance){
            firstlock = target;
            secondlock = this;
        }
        firstlock.getLock().lock();
        secondlock.getLock().lock();
        try{
            this.withdraw(amount);
            target.deposit(amount);
        }
        finally {
            secondlock.getLock().unlock();
            firstlock.getLock().unlock();
        }
    }
}
