
import java.util.Random;

public class DepositorAgent implements Runnable {

    private final BankAccount[] accounts;
    private final int agentId;
    private int MAXSLEEP;
    private final Random random = new Random();

    public DepositorAgent(int agentId, BankAccount[] accounts) {
        this.agentId = agentId;
        this.accounts = accounts;
        this.MAXSLEEP = 2000;
    }

    @Override
    public void run() {
        while (true) {
            try {
                int accountIndex = random.nextInt(accounts.length);
                int amount = random.nextInt(600) + 1;

                accounts[accountIndex].deposit(amount, agentId);
                
                Thread.sleep(random.nextInt(MAXSLEEP) + 1);
                Thread.yield();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
