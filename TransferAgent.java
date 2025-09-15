
import java.util.Random;

public class TransferAgent implements Runnable {

    private final BankAccount[] accounts;
    private final int agentId;
    private int MAXSLEEP;
    private final Random random = new Random();

    public TransferAgent(int agentId, BankAccount[] accounts) {
        this.agentId = agentId;
        this.accounts = accounts;
        this.MAXSLEEP = 1400;
    }

    @Override
    public void run() {
        boolean transferred = false;
        while (!transferred) {
            int fromIndex = random.nextInt(accounts.length);
            int toIndex;
            do {
                toIndex = random.nextInt(accounts.length);
            } while (toIndex == fromIndex); 

            int amount = random.nextInt(200) + 1; // Transfer between $1 and $200

            BankAccount fromAccount = accounts[fromIndex];
            BankAccount toAccount = accounts[toIndex];

            boolean fromLocked = fromAccount.getLock().tryLock();
            boolean toLocked = false;
            if (fromLocked) {
                try {
                    toLocked = toAccount.getLock().tryLock();
                    if (toLocked) {
                        try {
                            if (fromAccount.getBalance() >= amount) {
                                performAtomicTransfer(fromAccount, toAccount, amount);
                                System.out.printf("Transfer Agent %d transferred $%d from Account %d to Account %d.%n",
                                        agentId, amount, fromAccount.getAccountId(), toAccount.getAccountId());
                            } 
                            else {
                                System.out.printf("Transfer Agent %d failed to transfer $%d from Account %d to Account %d due to insufficient funds.%n",
                                        agentId, amount, fromAccount.getAccountId(), toAccount.getAccountId());
                            }

                            transferred = true;
                        } finally {
                            toAccount.getLock().unlock();
                        }
                    }
                } finally {
                    fromAccount.getLock().unlock();
                }
            }
            if (!transferred) {
                // Could not acquire both locks, wait and try again
                try {
                    Thread.sleep(random.nextInt(MAXSLEEP) + 1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    // Helper for atomic transfer using reflection
    static void performAtomicTransfer(BankAccount from, BankAccount to, int amount) {
        try {
            java.lang.reflect.Field balanceField = BankAccount.class.getDeclaredField("balance");
            balanceField.setAccessible(true);
            int fromBalance = (int) balanceField.get(from);
            if (fromBalance >= amount) {
                balanceField.set(from, fromBalance - amount);
                int toBalance = (int) balanceField.get(to);
                balanceField.set(to, toBalance + amount);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}


