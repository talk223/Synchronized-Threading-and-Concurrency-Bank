import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.*;

public class BankAccount {

    private final int accountId;
    private int balance;
    private final Lock lock = new ReentrantLock();

    private final Condition sufficientFunds = lock.newCondition();
    private LocalDateTime now = LocalDateTime.now();

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

            simOutputCSV(new StringBuilder("Agent DT" + agentId + " deposits " + amount + " into: JA-" 
                + accountId + "\t (+) JA-" + accountId + " balance is $" + balance
                + "\t\t\t\t\t\t\t\t\t" + transactionNumber + "\n"));

            sufficientFunds.signalAll();

            if(amount >= 450){
                flaggedTransacion.append("Agent DT").append(agentId)
                    .append(" issued a deposit of $").append(amount + ".00 at: ").append(now.getDayOfMonth() + "/" 
                    + now.getMonthValue() + "/" + now.getYear() + " ")
                    .append(now.format(DateTimeFormatter.ofPattern("HH:mm:ss"))+ " EST   Transaction Number: ").append(transactionNumber + "\n");

                System.out.println("* * * FLAGGED TRANSACTION * * * --> Agent DT" + agentId 
                    + " Made A Deposit In Excess Of $450.00 USD - See Flagged Transactions Log.\n");

                simOutputCSV(new StringBuilder("* * * FLAGGED TRANSACTION * * * --> Agent DT" + agentId 
                    + " Made A Deposit In Excess Of $450.00 USD - See Flagged Transactions Log.\n"));

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

                    simOutputCSV(new StringBuilder("\t\t\t\tAgent WT" + agentId + " attempts to withdraw " + amount + " from Account JA-" 
                        + accountId + " (******) WITHDRAWL BLOCKED - INSUFFICIENT FUNDS!!!! Balance only $" + balance + "\n"));

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
            
            simOutputCSV(new StringBuilder("\t\t\t\tAgent WT" + agentId + " withdraws $" + amount + " from JA-" 
                + accountId + "\t (-) JA-" + accountId + " balance is $"
                + balance + "\t\t\t\t\t" + transactionNumber + "\n"));
            
            if(amount >= 90){

                flaggedTransaction.append("Agent WT").append(agentId)
                    .append(" issued a Withdrawal of $").append(amount + ".00 at: ").append(now.getDayOfMonth() + "/" 
                    + now.getMonthValue() + "/" + now.getYear() + " ")
                    .append(now.format(DateTimeFormatter.ofPattern("HH:mm:ss"))+ " EST   Transaction Number: ").append(transactionNumber + "\n");

                System.out.println("* * * FLAGGED TRANSACTION * * * --> Agent WT" + agentId 
                    + " Made A Withdrawal In Excess Of $90.00 USD - See Flagged Transactions Log.\n");

                simOutputCSV(new StringBuilder("* * * FLAGGED TRANSACTION * * * --> Agent WT" + agentId 
                    + " Made A Withdrawal In Excess Of $90.00 USD - See Flagged Transactions Log.\n"));

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

                            simOutputCSV(new StringBuilder("TRANSFER --> Agent TR" + agentId + " transferring $" + amount
                                + " from JA-" + this.accountId + " to JA-" + toAccount.getAccountId() + "\n"));


                            System.out.println("TRANSFER COMPLETE --> Account JA-" + toAccount.getAccountId() 
                                + " balance now $" + toAccount.getBalance() + "\n");

                            simOutputCSV(new StringBuilder("TRANSFER COMPLETE --> Account JA-" + toAccount.getAccountId() 
                                + " balance now $" + toAccount.getBalance() + "\n"));

                        } else {
                            System.out.println("TRANSFER --> Agent TR" + agentId + " attempts to transfer $" + amount
                                    + " from JA-" + this.accountId + " to JA-" + toAccount.getAccountId() + 
                                    ". Balance only $" + this.balance + " (******) TRANSFER ABORTED - INSUFFICIENT FUNDS!!!\n");
                            
                            simOutputCSV(new StringBuilder("TRANSFER --> Agent TR" + agentId + " attempts to transfer $" + amount
                                    + " from JA-" + this.accountId + " to JA-" + toAccount.getAccountId() + 
                                    ". Balance only $" + this.balance + " (******) TRANSFER ABORTED - INSUFFICIENT FUNDS!!!\n"));

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

    /*temp - internalTransNumber != 0*/
    static int printOnce = 0;

    public static void internalAudit(int auditId, BankAccount[] accounts) {
        List<Lock> acquiredLocks = new ArrayList<>();


        try {
            
            for (int i = 0; i < accounts.length; i++) {
                if (accounts[i].getLock().tryLock()) {
                    acquiredLocks.add(accounts[i].getLock());
                } else {
                    for (int j = 0; j < acquiredLocks.size(); j++) {
                        acquiredLocks.get(j).unlock();
                    }
                    return; 
                }
            }

            int temp = transactionNumber;

            System.out.println("*****************************************************************************\n\n");
            System.out.println("Internal Bank Audit beginning...\n");

            simOutputCSV(new StringBuilder("*****************************************************************************\n\n" +
                "Internal Bank Audit beginning...\n"));

            System.out.println("The total number of transactions since the last Internal audit is: "
                               + (temp - internalTransNumber) + "\n");

            simOutputCSV(new StringBuilder("The total number of transactions since the last Internal audit is: "
                               + (temp - internalTransNumber) + "\n"));

            for (int i = 0; i < accounts.length; i++) {
                System.out.println("INTERNAL BANK AUDITOR FINDS CURRENT ACCOUNT BALANCE FOR JA-"
                                   + accounts[i].getAccountId() + " TO BE: $" + accounts[i].getBalance());
                simOutputCSV(new StringBuilder("INTERNAL BANK AUDITOR FINDS CURRENT ACCOUNT BALANCE FOR JA-"
                                   + accounts[i].getAccountId() + " TO BE: $" + accounts[i].getBalance() + "\n"));
            }

            internalTransNumber = temp;

            System.out.println("\nInternal Bank Audit complete.\n\n");
            System.out.println("*****************************************************************************\n\n");
            simOutputCSV(new StringBuilder("\nInternal Bank Audit complete.\n\n"));
            simOutputCSV(new StringBuilder("*****************************************************************************\n\n"));

        } finally {
            for (int i = 0; i < acquiredLocks.size(); i++) {
                acquiredLocks.get(i).unlock();
            }
        }

        // if (lock.tryLock()) {
            
        //     try {
        //         if (printOnce == 0) {

        //             System.out.println("The total number of transactions since the last Internal audit is: " + (temp - internalTransNumber) + "\n"); 

        //             simOutputCSV(new StringBuilder("The total number of transactions since the last Internal audit is: " + (temp - internalTransNumber) + "\n"));
        //             printOnce++;

        //         }
        //         System.out.println("INTERNAL BANK AUDITOR FINDS CURRENT ACCOUNT BALANCE FOR JA-" + accountId + " TO BE: $" + balance);

        //         simOutputCSV(new StringBuilder("INTERNAL BANK AUDITOR FINDS CURRENT ACCOUNT BALANCE FOR JA-" + accountId + " TO BE: $" + balance + "\n"));

        //         internalTransNumber = temp;

        //     } finally {
                
        //         lock.unlock();
        //     }

            
        // } else {
        //     //do nothing 
        //     //System.out.println("Audit " + auditId + " could not access Account " + accountId + "\n");
        // }
    }

    public static void treasury(int treasuryId, BankAccount[] accounts) {
        
        List<Lock> acquiredLocks = new ArrayList<>();


        try {
            
            for (int i = 0; i < accounts.length; i++) {
                if (accounts[i].getLock().tryLock()) {
                    acquiredLocks.add(accounts[i].getLock());
                } else {
                    for (int j = 0; j < acquiredLocks.size(); j++) {
                        acquiredLocks.get(j).unlock();
                    }
                    return; 
                }
            }

            int temp = transactionNumber;

            System.out.println("*****************************************************************************\n\n");
            System.out.println("UNITED STATES DEPARTMENT OF THE TREASURY - Bank Audit Beginning...\n");

            BankAccount.simOutputCSV(new StringBuilder("*****************************************************************************\n\n" +
                "UNITED STATES DEPARTMENT OF THE TREASURY - Bank Audit Beginning...\n"));

            System.out.println("The total number of transactions since the last Treasury Department audit is: "
                               + (temp - treasuryTransNumber) + "\n");

            simOutputCSV(new StringBuilder("The total number of transactions since the last Treasury Department audit is: "
                               + (temp - treasuryTransNumber) + "\n"));

            for (int i = 0; i < accounts.length; i++) {
                System.out.println("TREASURY DEPARTMENT AUDITOR FINDS CURRENT ACCOUNT BALANCE FOR JA-" + accounts[i].getAccountId() + 
                                    " TO BE: $" + accounts[i].getBalance());

                simOutputCSV(new StringBuilder("TREASURY DEPARTMENT AUDITOR FINDS CURRENT ACCOUNT BALANCE FOR JA-" + accounts[i].getAccountId() + 
                            " TO BE: $" + accounts[i].getBalance() + "\n"));
            }

            internalTransNumber = temp;

            System.out.println("\nUNITED STATES DEPARTMENT OF THE TREASURY - Bank Audit Terminated...\n\n");
            System.out.println("*****************************************************************************\n\n");
                
            BankAccount.simOutputCSV(new StringBuilder("\nUNITED STATES DEPARTMENT OF THE TREASURY - Bank Audit Terminated...\n\n" +
                "*****************************************************************************\n\n"));

        } finally {
            for (int i = 0; i < acquiredLocks.size(); i++) {
                acquiredLocks.get(i).unlock();
            }
        }
        
        // int temp = transactionNumber;

        // if (lock.tryLock()) {
        //     try {
        //         if(temp - treasuryTransNumber != 0) {
        //             System.out.println("The total number of transactions since the last Treasury Department audit is: " + (temp - treasuryTransNumber));
        //             simOutputCSV(new StringBuilder("The total number of transactions since the last Treasury Department audit is: " + (temp - treasuryTransNumber) + "\n"));
        //         }   
        //         System.out.println("TREASURY DEPARTMENT AUDITOR FINDS CURRENT ACCOUNT BALANCE FOR JA-" + accountId + " TO BE: $" + balance);
        //         simOutputCSV(new StringBuilder("TREASURY DEPARTMENT AUDITOR FINDS CURRENT ACCOUNT BALANCE FOR JA-" + accountId + " TO BE: $" + balance + "\n"));
        //     } finally {
        //         lock.unlock();
        //     }
            

        // } else {
        //     //do nothing 
        //     //System.out.println("Audit " + treasuryId + " could not access Account " + accountId);
        // }
    }

    public void flagFileCSV(StringBuilder line){
        try(FileWriter writer = new java.io.FileWriter("transactions.csv", true)){
            writer.write(line.toString());
        } 
        catch (Exception ex) {
                System.out.println("Error writing invoice to CSV");
        }
    }

    public static void simOutputCSV(StringBuilder line){
        try(FileWriter writer = new java.io.FileWriter("simulationOutput.csv", true)){
            writer.write(line.toString());
        } 
        catch (Exception ex) {
                System.out.println("Error writing invoice to CSV");
        }
    }

}
