package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.model.UserMeal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

/**
Реализовать метод `UserMealsUtil.filteredByCycles` через циклы (`forEach`):
-  должны возвращаться только записи между `startTime` и `endTime`
-  поле `UserMealWithExcess.excess` должно показывать,
превышает ли сумма калорий за весь день значение `caloriesPerDay`
 * */

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<MealTo> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<MealTo> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {

        // считаем сумму калорий в день
        Map<LocalDate, Integer> caloriesSumByDate = new HashMap<>();
        for (UserMeal meal : meals) {
            // получаем дату
            LocalDate mealDate = meal.getDateTime().toLocalDate();
            // калории
            int userMealCalories = meal.getCalories();
            // по ключу-дате получаем калории,
            // если такая дата там присутствует, то к ее значению прибавляем калории
            caloriesSumByDate.put(mealDate, caloriesSumByDate.getOrDefault(mealDate, 0) + userMealCalories);
        }

        List<MealTo> mealTos = new ArrayList<>();
        for (UserMeal meal : meals) {
            // проверяем находится ли данный отрезок времени в промежутке м/у startTime и endTime
            if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                // получаем сумму каллорий
                Integer numberOfCalories = caloriesSumByDate.get(meal.getDateTime().toLocalDate());
                mealTos.add(new MealTo(meal.getDateTime(), meal.getDescription(),
                        meal.getCalories(), numberOfCalories > caloriesPerDay));
            }
        }

        return mealTos;
    }

    public static List<MealTo> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {

        Map<LocalDate, List<UserMeal>> listOfMealByDate = meals
                .stream()
                // получаем 2 даты и список еды с характеристиками
                .collect(Collectors.groupingBy(meal -> meal.getDateTime().toLocalDate()));


        Map<LocalDate, Integer> caloriesSumByDate = meals
                .stream()
                .collect(Collectors.groupingBy(meal -> meal.getDateTime().toLocalDate(),
                        // получаем 2 даты и количество каллорий за день
                        Collectors.summingInt(UserMeal::getCalories)));

        List<MealTo> mealTos = meals
                .stream()
                .filter(meal ->
                        TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime))
                .map(meal -> new MealTo(meal.getDateTime(), meal.getDescription(), meal.getCalories(),
                        caloriesSumByDate.get(meal.getDateTime().toLocalDate()) > caloriesPerDay))
                .collect(Collectors.toList());

        return mealTos;
    }
}
