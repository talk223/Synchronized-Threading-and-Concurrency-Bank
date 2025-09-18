import java.util.Random;

public class TransferAgent implements Runnable {

    private final BankAccount[] accounts;
    private final int agentId;
    private int MAXSLEEP;
    private final Random random = new Random();

    public TransferAgent(int agentId, BankAccount[] accounts) {
        this.agentId = agentId;
        this.accounts = accounts;
        this.MAXSLEEP = 2500;
    }

    @Override
    public void run() {
        while (true) { 
            try {
                int from = random.nextInt(accounts.length);
                int to = random.nextInt(accounts.length);
                while (to == from) {
                    to = random.nextInt(accounts.length);
                }
                
                int amount = random.nextInt(200) + 1; // Transfer between $1 and $200

                accounts[from].transfer(accounts[to], amount, agentId);

                Thread.sleep(random.nextInt(MAXSLEEP) + 1);
                Thread.yield();

            } 
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    
    }

    

}


