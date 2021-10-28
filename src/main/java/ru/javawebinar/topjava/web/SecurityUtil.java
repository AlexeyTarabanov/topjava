package ru.javawebinar.topjava.web;

import static ru.javawebinar.topjava.util.MealsUtil.DEFAULT_CALORIES_PER_DAY;

public class SecurityUtil {

    private static int id = 1;

    // id залогиненого юзера
    public static int authUserId() {
        return id;
    }

    public static void setId(int id) {
        SecurityUtil.id = id;
    }

    // его норма калорий за день
    public static int authUserCaloriesPerDay() {
        return DEFAULT_CALORIES_PER_DAY;
    }
}