import java.util.Random;

public class DepositorAgent implements Runnable {
    private final BankAccount[] accounts;
    private final Random random = new Random();
    private final int agentId;
    private int MAXSLEEP;

    public DepositorAgent(int agentId, BankAccount[] accounts) {
        this.agentId = agentId;
        this.accounts = accounts;
        this.MAXSLEEP = 1500;
    }

    @Override
    public void run() { 
        int accountIndex = random.nextInt(accounts.length);
        int amount = random.nextInt(600) + 1; 
        accounts[accountIndex].deposit(amount);
        System.out.printf("Depositor %d deposited $%d into Account %d. New balance: $%d%n",
                agentId, amount, accounts[accountIndex].getAccountId(), accounts[accountIndex].getBalance());
        try {
            Thread.sleep(random.nextInt(500)); 
            } 
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}