
import java.util.Random;

public class InternalAudit implements Runnable {

    private final BankAccount[] accounts;
    private final int auditId;
    private int MAXSLEEP;
    private final Random random = new Random();

    public InternalAudit(int auditId, BankAccount[] accounts) {
        this.auditId = auditId; //the auditors id
        this.accounts = accounts;
        this.MAXSLEEP = 1600; 
    }

    @Override
    public void run() {
        while (true) {
            try {
                System.out.println("Internal Audit " + auditId + " is running...");

                int totalBalance = 0;

                for (int i = 0; i < accounts.length; i++) {
                    accounts[i].internalAudit(auditId);
                    totalBalance += accounts[i].getBalance(); 
                }

                System.out.println("Audit " + auditId + " total balance across all accounts: " + totalBalance);

                Thread.sleep(random.nextInt(MAXSLEEP) + 1);
                Thread.yield();

            } catch (Exception e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
