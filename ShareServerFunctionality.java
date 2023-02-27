import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.MINUTES;

public class ShareServerFunctionality implements ShareServer {

    private String user = new String("eveh");
    private String pwrd = new String("pwrd1234");
    private static long securityToken;
    private float accountBalance = 0.0f;
    private static final ScheduledExecutorService createAccessToken = Executors.newScheduledThreadPool(1);

    HashMap<String, Share> allAvailableShares = new HashMap<String, Share>();
    HashMap<String, ShareHolding> ownedShares = new HashMap<String, ShareHolding>();

    public ShareServerFunctionality() throws RemoteException {
        super();
        readInStockPricesFromFile();
    }

    public static void main(String args[]) {

        try {
            String serverName = "assignmentOneServer";
            ShareServer shareServer = new ShareServerFunctionality();
            ShareServer stub = (ShareServer) UnicastRemoteObject.exportObject(shareServer, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(serverName, stub);
            createAccessToken();
            System.out.println("SERVER STARTED!!!!");
        } catch (Exception exception) {
            System.out.println("Server initialisation failed");
            exception.printStackTrace();
        }
    }

    private static void createAccessToken() {
        final Runnable tokenSession = new Runnable() {
            @Override
            public void run() {
                securityToken = new Random().nextLong();
            }
        };
        createAccessToken.scheduleAtFixedRate(tokenSession, 0, 5, MINUTES);

    }

    @Override
    public long login(String user, String pwrd) throws AuthenticationFailed, RemoteException {
        if (this.user.equals(user) && this.pwrd.equals(pwrd)) {
            return securityToken;
        } else {
            throw new AuthenticationFailed("Your username or password is incorrect.");
        }
    }

    @Override
    public HashMap<String, Share> downloadAvailableShares(long securityToken)
            throws AuthenticationFailed, RemoteException {
        if (!(securityToken == ShareServerFunctionality.securityToken)) {
            throw new AuthenticationFailed("Session has expired, please reauthenticate");
        }
        return allAvailableShares;
    }

    @Override
    public void depositToAccount(long securityToken, int amount) throws RemoteException, AuthenticationFailed {
        if (!(securityToken == ShareServerFunctionality.securityToken)) {
            throw new AuthenticationFailed("Session has expired, please reauthenticate");
        }
        accountBalance += amount;
    }

    @Override
    public void withdrawFromAccount(long securityToken, int amount)
            throws AuthenticationFailed, RemoteException, InsufficientFundsException {
        if (!(securityToken == ShareServerFunctionality.securityToken)) {
            throw new AuthenticationFailed("Session has expired, please reauthenticate");
        }
        if (amount > accountBalance) {
            throw new InsufficientFundsException(
                    "Payment failed. Account balance is too low.");
        }
        accountBalance -= amount;
    }

    @Override
    public void buyShares(long securityToken, String shareName, int amount)
            throws AuthenticationFailed, RemoteException, InsufficientFundsException {
        if (!(securityToken == ShareServerFunctionality.securityToken)) {
            throw new AuthenticationFailed("Session has expired, please reauthenticate");
        }
        if (allAvailableShares.get(shareName).getCurrentPrice() > accountBalance * amount) {
            throw new InsufficientFundsException("Your account balance is too low to buy this amount of shares!");
        }

        Share newShare = allAvailableShares.get(shareName);
        if (ownedShares.get(shareName) == null) {
            ownedShares.put(shareName,
                    new ShareHoldingFunctionality(newShare.getNameOfShare(), amount, newShare.getCurrentPrice()));
        } else {
            ownedShares.replace(shareName, new ShareHoldingFunctionality(newShare.getNameOfShare(),
                    newShare.getAvailableShares() + amount, newShare.getCurrentPrice()));
        }

        if ((newShare.getAvailableShares() - amount) != 0) {
            allAvailableShares.replace(shareName,
                    new ShareFunctionality(newShare.getNameOfShare(), newShare.getCurrentPrice(),
                            newShare.getAvailableShares() - amount));
        } else {
            allAvailableShares.remove(shareName);
        }
        accountBalance -= newShare.getCurrentPrice() * amount;
    }

    public void sellShares(long securityToken, String shareName, int amount)
            throws AuthenticationFailed, RemoteException {
        if (!(securityToken == ShareServerFunctionality.securityToken)) {
            throw new AuthenticationFailed("Session has expired, please reauthenticate");
        }

        ShareHolding newShare = ownedShares.get(shareName);
        if (allAvailableShares.get(shareName) == null) {
            allAvailableShares.put(shareName,
                    new ShareFunctionality(shareName, newShare.getCurrentSharePrice(), amount));
        } else {
            Share anotherNewShare = allAvailableShares.get(shareName);
            allAvailableShares.replace(shareName,
                    new ShareFunctionality(shareName, anotherNewShare.getCurrentPrice(),
                            anotherNewShare.getAvailableShares() + amount));
        }

        if ((newShare.getNumberOfSharesOwned() - amount) != 0) {
            ownedShares.replace(shareName, new ShareHoldingFunctionality(shareName,
                    newShare.getNumberOfSharesOwned() - amount, newShare.getCurrentSharePrice()));
        } else {
            ownedShares.remove(shareName);
        }
        accountBalance += newShare.getCurrentSharePrice() * amount;

    }

    @Override
    public HashMap<String, ShareHolding> getShareHoldingObjects(long securityToken)
            throws AuthenticationFailed, RemoteException {
        if (!(securityToken == ShareServerFunctionality.securityToken)) {
            throw new AuthenticationFailed("Session has expired, please reauthenticate");
        }
        return ownedShares;
    }

    private void readInStockPricesFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader("stonks.csv"))) {
            String line;
            String delimiter = ",";
            String[] data;
            while ((line = br.readLine()) != null) {
                data = line.split(delimiter);
                allAvailableShares.put(data[0],
                        new ShareFunctionality(data[0], Float.parseFloat(data[2]), Integer.parseInt(data[1])));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
