package ru.javawebinar.topjava.util;

import org.springframework.lang.Nullable;

public class Util {

    // @Nullable
    // дает понять, что метод принимает значения null
    // и что если вы переопределите метод, вы также должны принять значения null
    public static <T extends Comparable<T>> boolean isBetweenHalfOpen (T value, @Nullable T start, @Nullable T end) {
        // value = 10.00, start = 7.00, end = 12.00,

        if ((start == null || value.compareTo(start) >= 0) && (end == null || value.compareTo(end) < 0)) return true;
        else return false;

    }
}
