
import java.util.Random;

public class WithdrawalAgent implements Runnable {

    private final BankAccount[] accounts;
    private final Random random = new Random();
    private final int agentId;
    private int MAXSLEEP;

    public WithdrawalAgent(int agentId, BankAccount[] accounts) {
        this.agentId = agentId;
        this.accounts = accounts;
        this.MAXSLEEP = 1000;
    }

    


    @Override
    public void run() { 

        int accountIndex = random.nextInt(accounts.length);
        int amount = random.nextInt(99) + 1; // Withdraw between $1 and $99
        accounts[accountIndex].withdraw(amount);
        System.out.printf("Withdrawal Agent %d withdrew $%d from Account %d. New balance: $%d%n",
                agentId, amount, accounts[accountIndex].getAccountId(), accounts[accountIndex].getBalance());
        try {
            Thread.sleep(random.nextInt(500)); // Sleep for up to 500ms
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
       
    }

}
