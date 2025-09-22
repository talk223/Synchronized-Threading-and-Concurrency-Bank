/* Name: Taha Alkattan
    Course: CNT 4714 Fall 2025
    Assignment title: Project 2 – Synchronized/Cooperating Threads – A Banking Simulation
    Due Date: September 28, 2025
*/
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {

        System.out.println(" * * * SIMULATION BEGINS...\n");
        System.out.println("Deposit Agents \t\t\t Withdrawal Agents \t\t\t Balances \t\t\t\t\t\t Transaction Number");
        System.out.println("============== \t\t\t ================= \t\t\t ======== \t\t\t\t\t\t ==================");

        
        BankAccount[] accounts = { new BankAccount(1, 0), new BankAccount(2, 0) };
        
        ExecutorService executor = Executors.newCachedThreadPool();

        DepositorAgent depositor1 = new DepositorAgent(1, accounts);
        DepositorAgent depositor2 = new DepositorAgent(2, accounts);
        DepositorAgent depositor3 = new DepositorAgent(3, accounts);
        DepositorAgent depositor4 = new DepositorAgent(4, accounts);
        DepositorAgent depositor5 = new DepositorAgent(5, accounts);

        WithdrawalAgent withdrawal1 = new WithdrawalAgent(1, accounts);
        WithdrawalAgent withdrawal2 = new WithdrawalAgent(2, accounts);
        WithdrawalAgent withdrawal3 = new WithdrawalAgent(3, accounts);
        WithdrawalAgent withdrawal4 = new WithdrawalAgent(4, accounts);
        WithdrawalAgent withdrawal5 = new WithdrawalAgent(5, accounts);
        WithdrawalAgent withdrawal6 = new WithdrawalAgent(6, accounts);
        WithdrawalAgent withdrawal7 = new WithdrawalAgent(7, accounts);
        WithdrawalAgent withdrawal8 = new WithdrawalAgent(8, accounts);
        WithdrawalAgent withdrawal9 = new WithdrawalAgent(9, accounts);
        WithdrawalAgent withdrawal10 = new WithdrawalAgent(10, accounts);        

        TransferAgent transfer1 = new TransferAgent(1, accounts);
        TransferAgent transfer2 = new TransferAgent(2, accounts);

        InternalAudit audit1 = new InternalAudit(1, accounts);

        Treasury treasury1 = new Treasury(1, accounts);   

        try {
            executor.execute(depositor1);
            executor.execute(depositor2);
            executor.execute(depositor3);
            executor.execute(depositor4);
            executor.execute(depositor5);

            executor.execute(withdrawal1);
            executor.execute(withdrawal2);
            executor.execute(withdrawal3);
            executor.execute(withdrawal4);  
            executor.execute(withdrawal5);
            executor.execute(withdrawal6);
            executor.execute(withdrawal7);
            executor.execute(withdrawal8);
            executor.execute(withdrawal9);
            executor.execute(withdrawal10);

            executor.execute(transfer1);
            executor.execute(transfer2);

            executor.execute(audit1);

            executor.execute(treasury1);    


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    
}
