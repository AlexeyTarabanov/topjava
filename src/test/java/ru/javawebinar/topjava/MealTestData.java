package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import static java.time.LocalDateTime.of;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

public class MealTestData {

    public static final MatcherFactory.Matcher<Meal> MEAL_MATCHER = MatcherFactory.usingIgnoringFieldsComparator();

    public static final int NOT_FOUND = 10;
    public static final int MEAL1_ID = START_SEQ + 2;
    public static final int ADMIN_MEAL_ID = START_SEQ + 9;

    // InMemoryMealRepository
    // MealsUtil

    public static final Meal meal1 = new Meal(of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500);
    public static final Meal meal2 = new Meal(of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000);
    public static final Meal meal3 = new Meal(of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500);
    public static final Meal meal4 = new Meal(of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100);
    public static final Meal meal5 = new Meal(of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000);
    public static final Meal meal6 = new Meal(of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500);
    public static final Meal meal7 = new Meal(of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410);
    public static final Meal adminMeal1 = new Meal(ADMIN_MEAL_ID, of(2020, Month.JANUARY, 31, 14, 0), "Админ ланч", 510);
    public static final Meal adminMeal2 = new Meal(ADMIN_MEAL_ID + 1, of(2020, Month.JANUARY, 31, 21, 0), "Админ ужин", 1500);

    public static final List<Meal> meals = Arrays.asList(meal1, meal2, meal3, meal4, meal5, meal6, meal7);

    public static Meal getNew() {
        return new Meal(null, of(2020, Month.FEBRUARY, 1, 18, 0), "Созданный ужин", 300);
    }

    public static Meal getUpdated() {
        return new Meal(MEAL1_ID, meal1.getDateTime().plus(2, ChronoUnit.MINUTES), "Обновленный завтрак", 200);
    }
}