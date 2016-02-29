package it.megadix.rxjavatut;

import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;

public class Hello {

    public static void main(String[] args) {
        List<String> names = Arrays.asList("Alice", "Bob", "Chris", "Dave", "Elaine", "Fred");

        System.out.println("Using interfaces:");

        Observable.from(names).subscribe(new Action1<String>() {
            @Override
            public void call(String name) {
                System.out.println(name);
            }
        });

        System.out.println("\nUsing lambdas:");

        Observable.from(names).subscribe((String name) -> {
            System.out.println(name);
        });

    }
}
