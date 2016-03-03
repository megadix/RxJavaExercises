package it.megadix.rxjavatut;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.concurrent.CountDownLatch;

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
                .doOnNext(debug("+ Generated\t"))
                .map(shiftUp).doOnNext(debug("| Shifted Up"))
                .map(shiftDown).doOnNext(debug("| Shifted Down"))
                .subscribe(debug("+ Received\t"));

        return this;
    }

    private Threading subscribeOn_IOScheduler() throws InterruptedException {
        System.out.println("subscribeOn_IOScheduler()");

        // Since subscription now occurs on a different thread, the subscribe() call will return control to the thread
        // that called it almost immediately, so we need to wait until completion using a semaphore
        CountDownLatch latch = new CountDownLatch(1);

        buildGenerator(5)
                .subscribeOn(Schedulers.io())
                .doOnNext(debug("+ Generated\t"))
                .map(shiftUp).doOnNext(debug("| Shifted Up"))
                .map(shiftDown).doOnNext(debug("| Shifted Down"))
                // decrement semaphore
                .doOnCompleted(latch::countDown)
                .subscribe(debug("+ Received\t"));

        latch.await();

        return this;
    }

    public static void main(String[] args) {
        try {
            new Threading()
                    .noThreads()
                    .subscribeOn_IOScheduler();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
