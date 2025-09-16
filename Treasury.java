
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
           

        }
    }

}
