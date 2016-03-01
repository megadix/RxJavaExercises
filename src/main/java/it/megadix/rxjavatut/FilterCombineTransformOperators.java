package it.megadix.rxjavatut;

import rx.Observable;
import rx.functions.Func1;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static it.megadix.rxjavatut.Utils.*;

public class FilterCombineTransformOperators {

    private long maxSleep = 300;

    private Observable<Integer> createNumbersObservable(int num) {
        return Observable.create(subscriber ->
                new Thread(() -> {
                    for (int i = 0; i < num; i++) {
                        if (subscriber.isUnsubscribed()) {
                            return;
                        }
                        subscriber.onNext(i);
                        randomSleep(maxSleep);
                    }

                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onCompleted();
                    }
                }).start()
        );
    }

    private Observable<Character> createCharactersObservable(int num) {
        return Observable.create(subscriber ->
                new Thread(() -> {
                    for (int i = 0; i < num; i++) {
                        if (subscriber.isUnsubscribed()) {
                            return;
                        }
                        subscriber.onNext((char) (i + 97));
                        randomSleep(maxSleep);
                    }

                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onCompleted();
                    }
                }).start()
        );
    }

    private FilterCombineTransformOperators example_skip_take_map_reduce() {

        System.out.println("Example: from(), skip(), take(), map(), reduce()");

        List<Integer> items = new ArrayList<>(100);
        for (int i = 0; i < 100; i++) {
            items.add(i);
        }

        Observable
                .from(items)
                .skip(10)
                .take(5)
                .map(item -> item * 2)
                .reduce((a, b) -> a + b)
                .subscribe(
                        System.out::println,
                        logError,
                        logFinished);

        return this;
    }

    private FilterCombineTransformOperators example_merge() throws InterruptedException {

        System.out.println("Example: merge()");

        CountDownLatch latch = new CountDownLatch(1);

        Observable<Integer> numbers = createNumbersObservable(10);
        Observable<Character> letters = createCharactersObservable(10);

        // output of both observers is interleaved, i.e. depends on order of arrival

        Observable
                .merge(numbers, letters)
                .subscribe(
                        item -> {
                            System.out.print(item);
                            System.out.print("...");
                        },
                        (throwable) -> {
                            logError.call(throwable);
                            latch.countDown();
                        },
                        () -> {
                            latch.countDown();
                            System.out.println("");
                        });

        latch.await();

        return this;
    }

    private FilterCombineTransformOperators example_concat() throws InterruptedException {

        System.out.println("Example: concat()");

        CountDownLatch latch = new CountDownLatch(1);

        Observable<Integer> numbers = createNumbersObservable(10);
        Observable<Character> letters = createCharactersObservable(10);

        // output of both observers is concatenated, NOT interleaved

        Observable
                .concat(numbers, letters)
                .subscribe(
                        item -> {
                            System.out.print(item);
                            System.out.print("...");
                        },
                        (throwable) -> {
                            logError.call(throwable);
                            latch.countDown();
                        },
                        () -> {
                            latch.countDown();
                            System.out.println("");
                        });

        latch.await();

        return this;
    }

    private FilterCombineTransformOperators example_flatMap() throws InterruptedException {

        System.out.println("Example: flatMap()");

        CountDownLatch latch = new CountDownLatch(1);

        List<Observable<Integer>> numbers = new ArrayList<>();
        numbers.add(createNumbersObservable(5));
        numbers.add(createNumbersObservable(5));
        numbers.add(createNumbersObservable(5));

        // flatMap() internally uses merge(), so output can be interleaved

        Observable
                .from(numbers)
                .flatMap(observable -> observable.map(n -> "[" + n + "] "))
                .subscribe(
                        item -> {
                            System.out.print(item);
                            System.out.print("...");
                        },
                        (throwable) -> {
                            logError.call(throwable);
                            latch.countDown();
                        },
                        () -> {
                            latch.countDown();
                            System.out.println("");
                        });

        latch.await();

        return this;
    }

    private FilterCombineTransformOperators example_concatMap() throws InterruptedException {

        System.out.println("Example: concatMap()");

        CountDownLatch latch = new CountDownLatch(1);

        List<Observable<Integer>> numbers = new ArrayList<>();
        numbers.add(createNumbersObservable(5));
        numbers.add(createNumbersObservable(5));
        numbers.add(createNumbersObservable(5));

        // concatMap() internally uses concat(), so output is NOT interleaved

        Observable
                .from(numbers)
                .concatMap(observable -> observable.map(n -> "[" + n + "] "))
                .subscribe(
                        item -> {
                            System.out.print(item);
                            System.out.print("...");
                        },
                        (throwable) -> {
                            logError.call(throwable);
                            latch.countDown();
                        },
                        () -> {
                            latch.countDown();
                            System.out.println("");
                        });

        latch.await();

        return this;
    }

    public static void main(String[] args) {
        try {
            new FilterCombineTransformOperators()
                    .example_skip_take_map_reduce()
                    .example_merge()
                    .example_concat()
                    .example_flatMap()
                    .example_concatMap();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
