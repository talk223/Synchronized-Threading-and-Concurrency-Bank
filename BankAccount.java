
import java.util.concurrent.locks.*;

public class BankAccount {
    private final int accountId;
    private int balance;
    private final Lock lock = new ReentrantLock();


    public BankAccount(int accountId, int initialBalance) {
        this.accountId = accountId;
        this.balance = initialBalance;
    }

    public int getAccountId() {
        return accountId;
    }

    public int getBalance() {
        return balance;
    }

    public void deposit(int amount){
        lock.lock();
        try {
            balance += amount;
            System.out.println("Deposited " + amount + " to Account " + accountId + ". New balance: " + balance);
        } 
        finally {
            lock.unlock(); 
        } 
    }

    public void withdraw(int amount){
        lock.lock();
        try {
            if (balance >= amount) {
                balance -= amount;
                System.out.println("Withdrew " + amount + " from Account " + accountId + ". New balance: " + balance);
            } 
            else {
                System.out.println("Insufficient funds for withdrawal of " + amount + " from Account " + accountId + ".");
            }
        }
        finally {
            lock.unlock();
        }
   
    }

}
        