import java.io.Serializable;

public class ShareHoldingFunctionality implements ShareHolding, Serializable {
    private String shareName;
    private int numberOfSharesOwned;
    private double currentSharePrice;

    public ShareHoldingFunctionality(String shareName, int numberOfSharesOwned, double currentSharePrice) {
        this.shareName = shareName;
        this.numberOfSharesOwned = numberOfSharesOwned;
        this.currentSharePrice = currentSharePrice;
    }

    @Override
    public String getNameOfShare() {
        return shareName;
    }

    @Override
    public int getNumberOfSharesOwned() {
        return numberOfSharesOwned;
    }

    @Override
    public double getCurrentSharePrice() {
        return currentSharePrice;
    }
}
