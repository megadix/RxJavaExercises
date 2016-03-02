package it.megadix.rxjavatut;

import rx.functions.Action0;
import rx.functions.Action1;

import java.util.Random;

public class Utils {

    private static Random rand = new Random();

    /**
     * Log a "Finished!" message to stdout
     */
    public static Action0 logFinished = () -> System.out.println("Finished!");

    /**
     * Log a Throwable message
     */
    public static Action1<Throwable> logError = throwable ->
            System.err.println("An error happened: " + throwable.getMessage());

    /**
     * Sleep the specified number of milliseconds, logging to stderr in case of InterruptedException
     *
     * @param millis number of milliseconds to sleep
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Random sleep, uses {@link #sleep sleep()}
     *
     * @param max maximum number of milliseconds to sleep
     */
    public static void randomSleep(long max) {
        sleep((long) (rand.nextDouble() * max));
    }
}
