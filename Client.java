import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Client {

    private long securityToken;

    private ShareServer shareServer;
    private Scanner scanner = new Scanner(System.in);
    private HashMap<String, Share> allAvailableShares;
    private HashMap<String, ShareHolding> ownedShares;

    public Client(ShareServer shareServer) {
        this.shareServer = shareServer;
        userLogin();
    }

    public static void main(String args[]) {

        try {
            String serverName = "assignmentOneServer";
            Registry registry = LocateRegistry.getRegistry();
            ShareServer shareServer = (ShareServer) registry.lookup(serverName);
            System.out.println("SERVER HAS STARTED !!! (Client)");
            new Client(shareServer);
        } catch (Exception exception) {
            System.out.println("Server initialisation failed (Client)");
            exception.printStackTrace();
        }
    }

    public void userLogin() {
        String user, pwrd;
        while (true) {
            System.out.println("Please enter username");
            user = scanner.next();
            System.out.println("Please enter password");
            pwrd = scanner.next();

            try {
                securityToken = shareServer.login(user, pwrd);
                break;
            } catch (AuthenticationFailed e) {
                System.out.println(e.getMessage());
            } catch (RemoteException e) {
                e.printStackTrace();
                return;
            }
        }
        runTradingSystem();
    }

    public void runTradingSystem() {
        int input;
        System.out.println(
                "Welcome To The Stock Market\nWhat would you like to do today?\n1 - View All Available Stocks on The Market Today\n2 - Purchase New Shares\n3 - View All Owned Stocks\n4 - Deposit Funds Into User Account\n5 - Withdraw Funds From User Account\n6 - Sell Shares \n8 - Exit");
        try {
            while (true) {
                input = scanner.nextInt();
                switch (input) {
                    case 1:
                        printShares();
                        break;
                    case 2:
                        buyShares();
                        break;
                    case 3:
                        printOwnedShares();
                        break;
                    case 4:
                        depositToAccount();
                        break;
                    case 5:
                        withdrawFromAccount();
                        break;
                    case 6:
                        sellShares();
                        break;
                    case 7:
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Error. Try Again.");
                }
            }
        } catch (InputMismatchException e) {
            System.err.println("The input you have selected is not an option. Try again!");
            return;
        }
    }

    public void buyShares() {
        System.out.println("What company are you buying shares of? ");
        String shareName = scanner.next();
        System.out.println("How many shares are you buying?");
        int amount = scanner.nextInt();
        try {
            shareServer.buyShares(securityToken, shareName, amount);
            System.out.println("You now own these shares");
        } catch (AuthenticationFailed e) {
            System.out.println(e.getMessage());
            userLogin();
        } catch (InsufficientFundsException e) {
            System.out.println(e.getMessage());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return;
    }

    public void sellShares() {
        System.out.println("Input the name of the company you wish to sell the shares of");
        String shareName = scanner.next();
        System.out.println("How many shares are you selling?");
        int amount = scanner.nextInt();
        try {
            shareServer.sellShares(securityToken, shareName, amount);
            System.out.println("Shares have been sold");
        } catch (AuthenticationFailed e) {
            System.out.println(e.getMessage());
            userLogin();
        } catch (InsufficientFundsException e) {
            System.out.println(e.getMessage());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return;
    }

    public void downloadOwnedShares() {
        System.out.println("Downloading the list of shares you own\n");
        try {
            ownedShares = shareServer.getShareHoldingObjects(securityToken);
        } catch (AuthenticationFailed e) {
            System.out.println(e.getMessage());
            userLogin();
        } catch (RemoteException e) {
            e.printStackTrace();
            return;
        }
    }

    public void downloadAvailableShares() {
        System.out.println("Downloading list of shares available on the market \n");
        try {
            allAvailableShares = shareServer.downloadAvailableShares(securityToken);
        } catch (AuthenticationFailed e) {
            System.out.println(e.getMessage());
            userLogin();
        } catch (RemoteException e) {
            e.printStackTrace();
            return;
        }
    }

    private void printShares() {
        downloadAvailableShares();
        if (allAvailableShares != null) {
            for (String key : allAvailableShares.keySet()) {
                Share x = allAvailableShares.get(key);
                System.out.println("Company name: " + x.getNameOfShare());
                System.out.println("Price: " + String.format("%.2f", x.getCurrentPrice()));
                System.out.println("Available shares to buy: " + x.getAvailableShares());
                System.out.println(
                        "This price will update in  " + x.getTimeUntilPriceUpdate() + " seconds");
                System.out.println("\n");
            }
        }
    }

    private void printOwnedShares() {
        downloadOwnedShares();
        if (ownedShares != null) {
            for (String key : ownedShares.keySet()) {
                ShareHolding temp = ownedShares.get(key);
                System.out.println("Company name: " + temp.getNameOfShare());
                System.out.println("Price: " + String.format("%.2f", temp.getCurrentSharePrice()));
                System.out.println("Number of shares you own: " + temp.getNumberOfSharesOwned());
                System.out.println("\n");
            }
        }
    }

    public void depositToAccount() {
        System.out.println("Enter how much money you wish to deposit into your account : \n");
        int amount = scanner.nextInt();
        System.out.println("Money has been deposited into your account : \n");
        try {
            shareServer.depositToAccount(securityToken, amount);
        } catch (AuthenticationFailed e) {
            System.out.println(e.getMessage());
            userLogin();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return;
    }

    public void withdrawFromAccount() {
        System.out.println("Enter how much money you wish to withdraw from your account : \n");
        int amount = scanner.nextInt();
        System.out.println("Money has been withdrawn from your account : \n");
        try {
            shareServer.withdrawFromAccount(securityToken, amount);
        } catch (AuthenticationFailed e) {
            System.out.println(e.getMessage());
            userLogin();
        } catch (InsufficientFundsException e) {
            System.out.println(e.getMessage());
        } catch (RemoteException e) {
            e.printStackTrace();
            return;
        }
    }
}
