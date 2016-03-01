package it.megadix.rxjavatut;

import rx.Observable;

import java.util.concurrent.CountDownLatch;

import static it.megadix.rxjavatut.Utils.logError;
import static it.megadix.rxjavatut.Utils.sleep;

public class ErrorHandling {

    private Observable<Object> createSampleObservable() {
        return Observable.create(subscriber ->
                new Thread(() -> {
                    if (subscriber.isUnsubscribed()) {
                        return;
                    }

                    subscriber.onNext("One");
                    subscriber.onNext("Two");
                    subscriber.onNext("Three");

                    subscriber.onError(new RuntimeException("Simulate exception handling"));

                    subscriber.onNext("Four");
                    subscriber.onNext("Five");

                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onCompleted();
                    }
                }).start()
        );
    }

    private ErrorHandling example_onErrorStop() throws InterruptedException {
        System.out.println("example_onErrorStop()");

        CountDownLatch latch = new CountDownLatch(1);

        Observable<Object> observable = createSampleObservable();

        observable.subscribe(
                System.out::println,
                (throwable) -> {
                    logError.call(throwable);
                    latch.countDown();
                },
                latch::countDown);

        latch.await();
        sleep(1000);

        return this;
    }

    private ErrorHandling example_onErrorReturn() throws InterruptedException {
        System.out.println("example_onErrorReturn()");

        CountDownLatch latch = new CountDownLatch(1);

        Observable<Object> observable = createSampleObservable();

        observable
                .onErrorReturn(Throwable::getMessage)
                .subscribe(
                        System.out::println,
                        (throwable) -> {
                            logError.call(throwable);
                            latch.countDown();
                        },
                        latch::countDown);

        latch.await();
        sleep(1000);

        return this;
    }

    private ErrorHandling example_onExceptionResumeNext() {
        System.out.println("example_onExceptionResumeNext()");

        CountDownLatch latch = new CountDownLatch(1);

        Observable<Object> observable1 = createSampleObservable();
        Observable<Object> observable2 = Observable.from(new Object[]{"Recovery 1", "Recovery 2", "Recovery 3"});

        observable1
                .onExceptionResumeNext(observable2)
                .subscribe(
                        System.out::println,
                        (throwable) -> {
                            logError.call(throwable);
                            latch.countDown();
                        },
                        latch::countDown);

        return this;
    }

    public static void main(String[] args) {
        try {
            new ErrorHandling()
                    .example_onErrorStop()
                    .example_onErrorReturn()
                    .example_onExceptionResumeNext();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
