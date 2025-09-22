
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

                BankAccount.treasury(treasuryId, accounts);

                Thread.sleep(random.nextInt(MAXSLEEP) + 1);
                Thread.yield();

            } catch (Exception e) {
                Thread.currentThread().interrupt();
                break;
            }

        }
    }

}
