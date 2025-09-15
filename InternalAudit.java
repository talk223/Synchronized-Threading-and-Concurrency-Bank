import java.util.List;
import java.util.Random;

public class InternalAudit implements Runnable {
    private final BankAccount[] accounts;
    private final int auditId;
    private int MAXSLEEP;
    private final Random random = new Random();

    public InternalAudit(int auditId, BankAccount[] accounts) {
        this.auditId = auditId;
        this.accounts = accounts;
        this.MAXSLEEP = 1600; // Audit every 1.6 seconds
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(random.nextInt(MAXSLEEP) + 1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            List<java.util.concurrent.locks.Lock> acquiredLocks = new java.util.ArrayList<>(); // arraylist of locks. this is to keep track of locks acquired so far
            boolean allLocked = true;
            
            for (int i = 0; i < accounts.length; i++) {
                if (accounts[i].getLock().tryLock()) {
                    acquiredLocks.add(accounts[i].getLock());
                } else {
                    allLocked = false;
                    break;
                }
            }

            if (allLocked) {
                try {
                    System.out.println("Audit " + auditId + " - Account Balances:");
                    
                    for (int i = 0; i < accounts.length; i++) {
                        System.out.printf("Account %d: $%d%n", accounts[i].getAccountId(), accounts[i].getBalance());
                    }
                    System.out.println("-----------------------------");
                } 
                finally {
                    // Release all locks
                    for (int i = 0; i < acquiredLocks.size(); i++) {
                        acquiredLocks.get(i).unlock();
                    }
                }
            } else {

                for (int i = 0; i < acquiredLocks.size(); i++) {
                    acquiredLocks.get(i).unlock();
                }
                // Try again later
                continue;
            }
        }
    }
    
}
