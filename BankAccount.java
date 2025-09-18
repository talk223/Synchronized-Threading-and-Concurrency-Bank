
import java.util.concurrent.locks.*;

public class BankAccount {

    private final int accountId;
    private int balance;
    private final Lock lock = new ReentrantLock();

    private final Condition sufficientFunds = lock.newCondition();

    private static int transactionNumber = 0;
    private static int internalTransNumber = 0;
    private static int treasuryTransNumber = 0;

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
            transactionNumber++;
            balance += amount;
            System.out.println("Agent DT" + agentId + " deposits " + amount + " into: JA-" 
                + accountId + "\t (+) JA-" + accountId + " balance is $" + balance
                + "\t\t\t\t\t\t\t\t\t" + transactionNumber + "\n");
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
                    System.out.println("\t\t\t\tAgent WT" + agentId + " attempts to withdraw " + amount + " from Account JA-" 
                        + accountId + " (******) WITHDRAWL BLOCKED - INSUFFICIENT FUNDS!!!! Balance only $" + balance + "\n");
                    sufficientFunds.await();

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            transactionNumber++;
            balance -= amount;
            System.out.println("\t\t\t\tAgent WT" + agentId + " withdraws $" + amount + " from JA-" 
                + accountId + "\t (-) JA-" + accountId + " balance is $" 
                + balance + "\t\t\t\t\t" + transactionNumber + "\n");
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
        int temp = transactionNumber;

        if (lock.tryLock()) {
            
            try {
                if (temp - internalTransNumber != 0) {
                    System.out.println("The total number of transactions since the last Internal audit is: " + (temp - internalTransNumber) + "\n");
                
                }
                System.out.println("INTERNAL BANK AUDITOR FINDS CURRENT ACCOUNT BALANCE FOR JA-" + accountId + " TO BE: $" + balance);

                internalTransNumber = temp;

            } finally {
                
                lock.unlock();
            }

            
        } else {
            //do nothing 
            System.out.println("Audit " + auditId + " could not access Account " + accountId + "\n");
        }
    }

    public void treasury(int treasuryId) {
        int temp = transactionNumber;

        if (lock.tryLock()) {
            try {
                if(temp - treasuryTransNumber != 0) {
                    System.out.println("The total number of transactions since the last Treasury Department audit is: " + (temp - treasuryTransNumber));
                }   
                System.out.println("TREASURY DEPARTMENT AUDITOR FINDS CURRENT ACCOUNT BALANCE FOR JA-" + accountId + " TO BE: $" + balance);

            } finally {
                lock.unlock();
            }
            

        } else {
            //do nothing 
            System.out.println("Audit " + treasuryId + " could not access Account " + accountId);
        }
    }

}
