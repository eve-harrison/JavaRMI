import java.rmi.*;

public interface ShareHolding extends Remote {
    String getNameOfShare();

    int getNumberOfSharesOwned();

    double getCurrentSharePrice();

}
