
import java.util.Random;

public class InternalAudit implements Runnable {

    private final BankAccount[] accounts;
    private final int auditId;
    private int MAXSLEEP;
    private final Random random = new Random();

    public InternalAudit(int auditId, BankAccount[] accounts) {
        this.auditId = auditId; 
        this.accounts = accounts;
        this.MAXSLEEP = 3000; 
    }

    @Override
    public void run() {
        //im doing this bc this kept running before other threads started
        try {
            Thread.sleep(2000); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
        
        while (true) {
            try {
                System.out.println("*****************************************************************************\n\n");
                System.out.println("Internal Bank Audit beginning...\n");

                BankAccount.simOutputCSV(new StringBuilder("*****************************************************************************\n\n" +
                    "Internal Bank Audit beginning...\n"));

                for (int i = 0; i < accounts.length; i++) {
                    accounts[i].internalAudit(auditId);
                 
                }

                System.out.println("\nInternal Bank Audit complete.\n\n");
                System.out.println("*****************************************************************************\n\n");
                
                BankAccount.simOutputCSV(new StringBuilder("\nInternal Bank Audit complete.\n\n" +
                    "*****************************************************************************\n\n"));

                Thread.sleep(random.nextInt(MAXSLEEP) + 1);
                Thread.yield();


            } catch (Exception e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
