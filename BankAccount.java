import java.util.concurrent.locks.*;
import java.time.format.DateTimeFormatter;
import java.io.FileWriter;

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
        StringBuilder flaggedTransacion = new StringBuilder();

        try {
            transactionNumber++;
            balance += amount;
            System.out.println("Agent DT" + agentId + " deposits " + amount + " into: JA-" 
                + accountId + "\t (+) JA-" + accountId + " balance is $" + balance
                + "\t\t\t\t\t\t\t\t\t" + transactionNumber + "\n");
            sufficientFunds.signalAll();

            if(amount >= 450){
                flaggedTransacion.append("Agent DT").append(agentId)
                    .append(" issued a deposit of $").append(amount + ".00 at: ").append(java.time.LocalDateTime.now().getDayOfMonth() + "/" 
                    + java.time.LocalDateTime.now().getMonthValue() + "/" + java.time.LocalDateTime.now().getYear() + " ")
                    .append(java.time.LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))+ " EST   Transaction Number: ").append(transactionNumber + "\n");

                System.out.println("* * * FLAGGED TRANSACTION * * * --> Agent DT" + agentId 
                    + " Made A Deposit In Excess Of $450.00 USD - See Flagged Transactions Log.\n");

                
                flagFileCSV(flaggedTransacion);
            }
        } finally {
            lock.unlock();
        }
    }

    public void withdraw(int amount, int agentId) {
        lock.lock();
        StringBuilder flaggedTransaction = new StringBuilder();
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

            if(amount >= 90){

                flaggedTransaction.append("Agent WT").append(agentId)
                    .append(" issued a Withdrawal of $").append(amount + ".00 at: ").append(java.time.LocalDateTime.now().getDayOfMonth() + "/" 
                    + java.time.LocalDateTime.now().getMonthValue() + "/" + java.time.LocalDateTime.now().getYear() + " ")
                    .append(java.time.LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))+ " EST   Transaction Number: ").append(transactionNumber + "\n");

                System.out.println("* * * FLAGGED TRANSACTION * * * --> Agent WT" + agentId 
                    + " Made A Withdrawal In Excess Of $90.00 USD - See Flagged Transactions Log.\n");

                flagFileCSV(flaggedTransaction);
            }

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
                            transactionNumber++;
                            this.balance -= amount;
                            toAccount.balance += amount; // direct change instead of calling deposit()
                            System.out.println("Transfer --> Agent TR" + agentId + " transferring $" + amount 
                                + " from JA-" + this.accountId + " to JA-" + toAccount.getAccountId()
                                + "-- JA-" + this.accountId + " balance is now $" + this.balance + "\t\t\t" + transactionNumber);

                            System.out.println("TRANSFER COMPLETE --> Account JA-" + toAccount.getAccountId() 
                                + " balance now $" + toAccount.getBalance() + "\n");
                        } else {
                            System.out.println("TRANSFER --> Agent TR" + agentId + " attempts to transfer $" + amount
                                    + " from JA-" + this.accountId + " to JA-" + toAccount.getAccountId() + 
                                    ". Balnce only $" + this.balance + " (******) TRANSFER ABORTED - INSUFFICIENT FUNDS!!!\n");
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

    public void flagFileCSV(StringBuilder line){
        try(FileWriter writer = new java.io.FileWriter("transactions.csv", true)){
            writer.write(line.toString());
        } 
        catch (Exception ex) {
                System.out.println("Error writing invoice to CSV");
        }
    }


}
