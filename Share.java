import java.io.Serializable;
import java.rmi.*;

public interface Share extends Remote, Serializable {
    String getNameOfShare();

    double getCurrentPrice();

    int getAvailableShares();

    double getTimeUntilPriceUpdate();
}
