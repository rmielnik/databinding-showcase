package io.rmielnik.toast.calculations;

import java.util.Random;

public class CalcService {

    private static final int MAX_SLEEP = 5000;

    private Random random = new Random();

    public int randomSleep() {
        int sleepTime = (int) (MAX_SLEEP * random.nextFloat());
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return sleepTime;
    }

}
