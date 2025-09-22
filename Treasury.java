
import java.util.Random;

public class Treasury implements Runnable {

    private final BankAccount[] accounts;
    private final int treasuryId;
    private int MAXSLEEP;
    private final Random random = new Random();

    public Treasury(int treasuryId, BankAccount[] accounts) {
        this.treasuryId = treasuryId;
        this.accounts = accounts;
        this.MAXSLEEP = 4000; 
    }

    @Override
    public void run() {
        while (true) {
           try {
                System.out.println("*****************************************************************************\n\n");
                System.out.println("UNITED STATES DEPARTMENT OF THE TREASURY - Bank Audit Beginning...\n");

                BankAccount.simOutputCSV(new StringBuilder("*****************************************************************************\n\n" +
                    "UNITED STATES DEPARTMENT OF THE TREASURY - Bank Audit Beginning...\n"));

                for (int i = 0; i < accounts.length; i++) {
                    accounts[i].treasury(treasuryId);
                }

                System.out.println("\nUNITED STATES DEPARTMENT OF THE TREASURY - Bank Audit Terminated...\n\n");
                System.out.println("*****************************************************************************\n\n");
                
                BankAccount.simOutputCSV(new StringBuilder("\nUNITED STATES DEPARTMENT OF THE TREASURY - Bank Audit Terminated...\n\n" +
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
