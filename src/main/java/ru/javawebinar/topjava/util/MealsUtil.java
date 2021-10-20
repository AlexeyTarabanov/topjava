package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MealsUtil {

    private static final int DEFAULT_CALORIES_PER_DAY = 2000;

    public static final List<Meal> meals = Arrays.asList(
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
    );

    public static void main(String[] args) {

        List<MealTo> mealsTo = getFilteredTos(meals, 2000, LocalTime.of(7, 0), LocalTime.of(12, 0));
        mealsTo.forEach(System.out::println);
    }

    // без фильтрации по времени
    // дополнительный вспомогательный метод
    // подает туда минимальное и максимальное время для того, чтобы там не происходила фильтрация
    public static List<MealTo> getTos(Collection<Meal> meals, int caloriesPerDay) {
        //return filteredByStreams(MEAL_LIST, LocalTime.MIN, LocalTime.MAX, caloriesPerDay);
        return filterByPredicate(meals, caloriesPerDay, meal -> true);
    }

    // с фильтрацией по времени
    public static List<MealTo> getFilteredTos(Collection<Meal> meals, int caloriesPerDay, LocalTime startTime, LocalTime endTime) {
        return filterByPredicate(meals, caloriesPerDay, meal -> TimeUtil.isBetweenHalfOpen(meal.getTime(), startTime, endTime));
    }


    public static List<MealTo> filterByPredicate(Collection<Meal> meals, int caloriesPerDay, Predicate<Meal> filter) {
        Map<LocalDate, Integer> caloriesSumByDate = meals
                .stream()
                // collect - представление результатов в виде коллекций и других структур данных
                .collect(
                        // (группируем по дате) - получаем 2 даты и количество калорий /список еды с характеристиками/
                        Collectors.groupingBy(Meal::getDate,
                                Collectors.summingInt(Meal::getCalories))
//                      Collectors.toMap(Meal::getDate, Meal::getCalories, Integer::sum)
                );

        return meals
                .stream()
                // filter - отфильтровывает записи, возвращает только записи, соответствующие условию
                .filter(filter)
                // map -преобразует каждый элемент коллекции во что-то другое и на выходе получить новую коллекцию
                .map(meal -> createTo(meal, caloriesSumByDate.get(meal.getDate()) > caloriesPerDay))
                // переводим stream обратно в коллекцию
                .collect(Collectors.toList());
    }

    private static MealTo createTo(Meal meal, boolean excess) {
        return new MealTo(meal.getDateTime(), meal.getDescription(), meal.getCalories(), excess);
    }
}
