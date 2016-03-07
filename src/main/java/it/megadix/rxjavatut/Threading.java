package it.megadix.rxjavatut;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Example inspired by "RxJava Threading Examples":
 * http://www.grahamlea.com/2014/07/rxjava-threading-examples/
 * <p>
 * by Graham Lea
 */
public class Threading {

    private Observable<Integer> buildGenerator(int max) {
        Integer[] numbers = new Integer[max];
        int i = 0;
        while (i < max) {
            numbers[i] = ++i;
        }
        return Observable.from(numbers);
    }

    private Action1<Object> debug(final String message) {
        return n -> System.out.println("[" + Thread.currentThread().getName() + "]\t" + message + "\t" + n);
    }

    private Func1<Integer, Integer> shiftUp = n -> n + 10;
    private Func1<Integer, Integer> shiftDown = n -> n - 10;

    private Threading noThreads() {
        System.out.println("noThreads()");

        buildGenerator(5)
                .doOnNext(debug("Generated\t"))
                .map(shiftUp).doOnNext(debug("Shifted Up\t"))
                .map(shiftDown).doOnNext(debug("Shifted Down"))
                .subscribe(debug("Received\t"));

        System.out.println("");

        return this;
    }

    private Threading subscribeOn() throws InterruptedException {
        System.out.println("subscribeOn()");

        // Since subscription now occurs on a different thread, the subscribe() call will return control to the thread
        // that called it almost immediately, so we need to wait until completion using a semaphore
        CountDownLatch latch = new CountDownLatch(1);

        buildGenerator(5)
                .doOnNext(debug("Generated\t"))
                .map(shiftUp).doOnNext(debug("Shifted Up\t"))
                .map(shiftDown).doOnNext(debug("Shifted Down"))
                // on completed, decrement countdown in latch
                .doOnCompleted(latch::countDown)
                // subscribe on a different thread
                // NOTE: the position of this call does NOT influence threading
                .subscribeOn(Schedulers.io())
                .subscribe(debug("Received\t"));

        latch.await();

        System.out.println("");

        return this;
    }

    private Threading observeOn() throws InterruptedException {
        System.out.println("observeOn()");

        CountDownLatch latch = new CountDownLatch(1);

        buildGenerator(100)
                .doOnNext(debug("Generated\t"))
                // observe in another thread, i.e. tell RxJava to process the values using one thread from the specified
                // Scheduler
                // NOTE: the position of this call DOES influence threading!
                .observeOn(Schedulers.io())
                .map(shiftUp).doOnNext(debug("Shifted Up\t"))
                // from this observer onwards, observe in yet another thread
                .observeOn(Schedulers.computation())
                .map(shiftDown).doOnNext(debug("Shifted Down"))
                .doOnCompleted(latch::countDown)
                // subscribe on a different thread
                // NOTE: the position of this call does NOT influence threading!
                .subscribeOn(Schedulers.io())
                .subscribe(debug("Received\t"));

        latch.await();

        System.out.println("");

        return this;
    }

    private Threading delay() throws InterruptedException {
        System.out.println("delay()");

        CountDownLatch latch = new CountDownLatch(1);

        buildGenerator(10)
                .doOnNext(debug("Generated\t"))
                .map(shiftUp).doOnNext(debug("Shifted Up\t"))
                .delay(10, TimeUnit.MILLISECONDS).doOnNext(debug("Delayed"))
                .map(shiftDown).doOnNext(debug("Shifted Down"))
                .doOnCompleted(latch::countDown)
                .subscribe(debug("Received\t"));

        latch.await();

        System.out.println("");

        return this;
    }

    public static void main(String[] args) {
        try {
            new Threading()
                    /*.noThreads()
                    .subscribeOn()
                    .observeOn()*/
                    .delay();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
