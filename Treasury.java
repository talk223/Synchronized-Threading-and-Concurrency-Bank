import java.util.List;
import java.util.Random;

public class Treasury implements Runnable {
    private final BankAccount[] accounts;
    private final int treasuryId;
    private int MAXSLEEP;
    private final Random random = new Random(); 

    public Treasury(int treasuryId, BankAccount[] accounts) {
        this.treasuryId = treasuryId;
        this.accounts = accounts;
        this.MAXSLEEP = 2000; // Treasury action every 2 seconds
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

            List<java.util.concurrent.locks.Lock> acquiredLocks = new java.util.ArrayList<>(); //similar to what i did in InternalAudit.java
            // Try to acquire all locks
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
                    int totalBalance = 0;
                    for (int i = 0; i < accounts.length; i++) {
                        totalBalance += accounts[i].getBalance();
                    }
                    System.out.printf("Treasury %d - Total Bank Balance: $%d%n", treasuryId, totalBalance);
                } 
                finally {
                    // Release all locks
                    for (int i=0; i < acquiredLocks.size(); i++) {
                        acquiredLocks.get(i).unlock();
                    }
                }
            } 
            else {
                // Release any locks acquired so far
                for (int i=0; i < acquiredLocks.size(); i++) {
                    acquiredLocks.get(i).unlock();
                }
            
            }
        }
    }
    
}
