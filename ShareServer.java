import java.rmi.*;
import java.util.HashMap;

public interface ShareServer extends Remote {
    long login(String user, String pwrd) throws RemoteException, AuthenticationFailed;

    HashMap<String, Share> downloadAvailableShares(long securityToken)
            throws RemoteException, AuthenticationFailed;

    void depositToAccount(long securityToken, int amount) throws RemoteException, AuthenticationFailed;

    void withdrawFromAccount(long securityToken, int amount)
            throws RemoteException, AuthenticationFailed, InsufficientFundsException;

    void buyShares(long securityToken, String shareName, int amount)
            throws RemoteException, AuthenticationFailed, InsufficientFundsException;

    void sellShares(long securityToken, String shareName, int amount)
            throws RemoteException, AuthenticationFailed, InsufficientFundsException;

    HashMap<String, ShareHolding> getShareHoldingObjects(long securityToken)
            throws RemoteException, AuthenticationFailed;
}