package it.megadix.rxjavatut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import rx.Observable;

public class OperatorsExamples {

    Random rand = new Random();

    OperatorsExamples example_skip_take_map_reduce() {

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
                .subscribe(item -> {
                    System.out.println(item);
                });

        return this;
    }

    OperatorsExamples example_flatMap() {

        System.out.println("Example: from(), flatMap(), map()");

        List<List<String>> items = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            List<String> subItems = Arrays.asList("A", "B", "C");
            items.add(subItems);
        }

        Observable.from(items)
                .flatMap(item -> Observable.from(item))
                .map(subItem -> "(" + subItem + ")")
                .subscribe(mappedItem -> {
                    System.out.println(mappedItem);
                });

        return this;
    }

    void randomSleep(long max) {
        try {
            Thread.sleep((long) (rand.nextDouble() * max));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    OperatorsExamples example_merge() {

        System.out.println("Example: merge()");

        long maxSleep = 1000;

        // Observable 1

        Observable numbers = Observable.create(subscriber -> {
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
            }).start();
        });

        // Observable 2

        Observable letters = Observable.create(subscriber -> {
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
            }).start();
        });

        Observable
                .merge(numbers, letters)
                .subscribe(item -> {
                    System.out.print(item);
                    System.out.print("...");
                });

        return this;
    }


    public static void main(String[] args) {
        new OperatorsExamples()
                .example_skip_take_map_reduce()
                .example_flatMap()
                .example_merge();
    }
}
