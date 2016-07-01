package it.megadix.rxjavatut;

import rx.Observable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;
import java.util.function.IntFunction;

import static it.megadix.rxjavatut.Utils.*;

public class FilterCombineTransformOperators {

    private long maxSleep = 300;

    private <R> Observable<R> createObservable(int num, IntFunction<R> supplier) {

        return Observable.create(subscriber ->
                new Thread(() -> {
                    for (int i = 0; i < num; i++) {
                        if (subscriber.isUnsubscribed()) {
                            return;
                        }

                        R result = supplier.apply(i);

                        subscriber.onNext(result);
                        randomSleep(maxSleep);
                    }

                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onCompleted();
                    }

                }).start()
        );
    }

    /**
     * Create an {@link Observable} that produces <code>num</code> {@link Integer}s.
     *
     * @param num
     */
    private Observable<Integer> createNumbersObservable(int num) {
        return createObservable(num, i -> i);
    }

    /**
     * Create an {@link Observable} that produces <code>num</code> {@link Character}s.
     *
     * @param num
     */
    private Observable<Character> createCharactersObservable(int num) {
        return createObservable(num, i -> (char) (i + 97));
    }

    private FilterCombineTransformOperators example_skip_take_map_reduce() {

        System.out.println("--------------------------------------------------");
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

        System.out.println("--------------------------------------------------");
        System.out.println("Example: merge()");
        System.out.println("Output of both observers is interleaved, i.e. depends on the scheduling of threads of the" +
                " OS & JVM in use");

        CountDownLatch latch = new CountDownLatch(1);

        Observable<Integer> numbers = createNumbersObservable(10);
        Observable<Character> letters = createCharactersObservable(10);

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

        System.out.println("--------------------------------------------------");
        System.out.println("Example: concat()");
        System.out.println("- output of both observers is concatenated, NOT interleaved");
        System.out.println("- the second observable intervenes only after the first calls subscriber.onCompleted()");

        CountDownLatch latch = new CountDownLatch(1);

        Observable<Integer> numbers = createNumbersObservable(10);
        Observable<Character> letters = createCharactersObservable(10);

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

        System.out.println("--------------------------------------------------");
        System.out.println("Example: flatMap()");
        System.out.println("Note that flatMap() internally uses merge(), so output could be interleaved");

        CountDownLatch latch = new CountDownLatch(1);

        List<Observable<Integer>> numbers = new ArrayList<>();
        numbers.add(createNumbersObservable(5));
        numbers.add(createNumbersObservable(5));
        numbers.add(createNumbersObservable(5));

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

        System.out.println("--------------------------------------------------");
        System.out.println("Example: concatMap()");
        System.out.println("Note that concatMap() internally uses concat(), so output is NOT interleaved");

        CountDownLatch latch = new CountDownLatch(1);

        List<Observable<Integer>> numbers = new ArrayList<>();
        numbers.add(createNumbersObservable(5));
        numbers.add(createNumbersObservable(5));
        numbers.add(createNumbersObservable(5));

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
