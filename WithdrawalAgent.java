
import java.util.Random;

public class WithdrawalAgent implements Runnable {

    private final BankAccount[] accounts;
    private final int agentId;
    private int MAXSLEEP;
    private final Random random = new Random();

    public WithdrawalAgent(int agentId, BankAccount[] accounts) {
        this.agentId = agentId;
        this.accounts = accounts;
        this.MAXSLEEP = 1000;
    }

    @Override
    public void run() { 
        while (true) { 
            try {
                int accountIndex = random.nextInt(accounts.length);
                int amount = random.nextInt(99) + 1;

                accounts[accountIndex].withdraw(amount, agentId);

                Thread.sleep(random.nextInt(MAXSLEEP) + 1); // Sleep for up to 1000ms
                Thread.yield();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    
    }

}
