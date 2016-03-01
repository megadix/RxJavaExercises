package it.megadix.rxjavatut;

import rx.Observable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static it.megadix.rxjavatut.Utils.*;

public class OperatorsExamples {

    private OperatorsExamples example_skip_take_map_reduce() {

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

    private OperatorsExamples example_flatMap() {

        System.out.println("Example: from(), flatMap(), map()");

        List<List<String>> items = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            List<String> subItems = Arrays.asList("A", "B", "C");
            items.add(subItems);
        }

        Observable.from(items)
                .flatMap(Observable::from)
                .map(subItem -> "(" + subItem + ")")
                .subscribe(
                        System.out::println,
                        logError,
                        logFinished);

        return this;
    }


    private OperatorsExamples example_merge() {

        System.out.println("Example: merge()");

        long maxSleep = 300;

        // Observable 1

        Observable<Integer> numbers = Observable.create(subscriber ->
                new Thread(() -> {
                    for (int i = 0; i < 10; i++) {
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

        // Observable 2

        Observable<Character> letters = Observable.create(subscriber ->
                new Thread(() -> {
                    for (int i = 0; i < 10; i++) {
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

        // output of both observers is interleaved, i.e. depends on order of arrival

        Observable
                .merge(numbers, letters)
                .subscribe(
                        item -> {
                            System.out.print(item);
                            System.out.print("...");
                        },
                        logError,
                        logFinished
                );

        return this;
    }


    public static void main(String[] args) {
        new OperatorsExamples()
                .example_skip_take_map_reduce()
                .example_flatMap()
                .example_merge();
    }
}
