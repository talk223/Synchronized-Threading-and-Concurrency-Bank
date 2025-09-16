
import java.util.concurrent.locks.*;

public class BankAccount {

    private final int accountId;
    private int balance;
    private final Lock lock = new ReentrantLock();

    private final Condition sufficientFunds = lock.newCondition();

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

    public Lock getLock() {
        return lock;
    }

    public void deposit(int amount, int agentId) {
        lock.lock();
        try {
            balance += amount;
            System.out.println("Depositor " + agentId + " deposited " + amount + " to Account " + accountId + ". New balance: " + balance);
            sufficientFunds.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void withdraw(int amount, int agentId) {
        lock.lock();
        try {
            while (balance < amount) {
                try {
                    System.out.println("Insufficient funds for withdrawal of " + amount + " from Account " + accountId + ". Waiting for deposit...");
                    sufficientFunds.await();

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            balance -= amount;
            System.out.println("Withdrawer " + agentId + " withdrew " + amount + " from Account " + accountId + ". New balance: " + balance);
        } finally {
            lock.unlock();
        }
    }

    public void transfer(BankAccount toAccount, int amount, int agentId) {
        BankAccount first, second;

        if(this.accountId < toAccount.getAccountId()){
            first = this;
        }
        else{
            first = toAccount;
        }

        if(this.accountId < toAccount.getAccountId()){
            second = this;
        }
        else{
            second = toAccount;
        }


        if (first.getLock().tryLock()) {
            try {
                if (second.getLock().tryLock()) {
                    try {
                        // check if enough money, otherwise just abort
                        if (this.balance >= amount) {
                            this.balance -= amount;
                            toAccount.balance += amount; // direct change instead of calling deposit()
                            System.out.println("Transfer Agent " + agentId + " moved $" + amount
                                    + " from Account " + this.accountId + " to Account " + toAccount.getAccountId()
                                    + ". New balance: " + this.balance);
                        } else {
                            System.out.println("Transfer Agent " + agentId + " aborted transfer of $" + amount
                                    + " from Account " + this.accountId + " (insufficient funds).");
                        }
                    } finally {
                        second.getLock().unlock();
                    }
                } 
                else {
                    //welp do nothing
                }
            } finally {
                first.getLock().unlock();
            }
        } 
        else {
           //welp do nothing
        }
    }

    public void internalAudit(int auditId) {
    if (lock.tryLock()) {
        try {
            System.out.println("Audit " + auditId + " sees Account " + accountId +
                               " Balance: " + balance);
        } finally {
            lock.unlock();
        }
    } else {
        // couldn’t get the lock, skip this account
        System.out.println("Audit " + auditId + " could not access Account " + accountId);
    }
}

}
