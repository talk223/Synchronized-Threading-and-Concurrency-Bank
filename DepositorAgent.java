import java.util.Random;

public class DepositorAgent implements Runnable {
    private final BankAccount[] accounts;
    private final Random rand = new Random();
    private final String name;

    public DepositorAgent(BankAccount[] accounts, String name) {
        this.accounts = accounts;
        this.name = name;
    }

    @Override
    public void run() {
        try {
            while (true) {
                // Pick random account
                BankAccount account = accounts[rand.nextInt(accounts.length)];

                // Deposit random $1–600
                int amount = rand.nextInt(600) + 1;
                account.deposit(amount, name);

                // Sleep random 0–1000 ms
                Thread.sleep(rand.nextInt(1000));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}