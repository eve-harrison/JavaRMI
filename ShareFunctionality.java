import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.*;

public class ShareFunctionality implements Share, Serializable {

    private String shareName;
    private double currentSharePrice;
    private int sharesAvailable;
    private transient final ScheduledExecutorService stockMarketSimulator = Executors.newScheduledThreadPool(1);
    private long currentTime;
    private long timeTillUpdate;

    public ShareFunctionality(String shareName, double currentSharePrice, int sharesAvailable) {
        this.shareName = shareName;
        this.currentSharePrice = currentSharePrice;
        this.sharesAvailable = sharesAvailable;

        currentTime = System.currentTimeMillis();
        timeTillUpdate = System.currentTimeMillis();
        stockMarketSimulator();
    }

    @Override
    public String getNameOfShare() {
        return shareName;
    }

    @Override
    public double getCurrentPrice() {
        return currentSharePrice;
    }

    @Override
    public int getAvailableShares() {
        return sharesAvailable;
    }

    @Override
    public double getTimeUntilPriceUpdate() {
        return Double.valueOf((timeTillUpdate / 1000) - (currentTime / 1000));
    }

    private void stockMarketSimulator() {
        final Runnable updatePrice = new Runnable() {
            @Override
            public void run() {
                Random random = new Random();
                currentSharePrice += -30 + random.nextFloat() * (60);
                timeTillUpdate = currentTime + 60000;
            }
        };

        final Runnable updateTime = new Runnable() {
            @Override
            public void run() {
                currentTime = System.currentTimeMillis();
            }
        };

        stockMarketSimulator.scheduleAtFixedRate(updatePrice, 60, 60, SECONDS);
        stockMarketSimulator.scheduleAtFixedRate(updateTime, 0, 1, SECONDS);
    }
}