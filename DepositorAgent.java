import java.util.Random;

public class DepositorAgent implements Runnable {
    private final BankAccount[] accounts;
    private final Random random = new Random();
    private final int agentId;

    public DepositorAgent(int agentId, BankAccount[] accounts) {
        this.agentId = agentId;
        this.accounts = accounts;
    }

    @Override
    public void run() {
        for (int i = 0; i < 5; i++) { // Each agent makes 5 deposits
            int accountIndex = random.nextInt(accounts.length);
            int amount = random.nextInt(99) + 1; // Deposit between $1 and $99
            accounts[accountIndex].deposit(amount);
            System.out.printf("Depositor %d deposited $%d into Account %d. New balance: $%d%n",
                    agentId, amount, accounts[accountIndex].getAccountId(), accounts[accountIndex].getBalance());
            try {
                Thread.sleep(random.nextInt(500)); // Sleep for up to 500ms
            } 
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}