package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.util.CollectionUtils;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.util.Util;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static ru.javawebinar.topjava.repository.inmemory.InMemoryUserRepository.ADMIN_ID;
import static ru.javawebinar.topjava.repository.inmemory.InMemoryUserRepository.USER_ID;

public class InMemoryMealRepository implements MealRepository {

    private final Map<Integer, Map<Integer, Meal>> usersMealsMap = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
//        MealsUtil.meals.forEach(meal -> this.save(meal));
        MealsUtil.meals.forEach(meal -> this.save(meal, USER_ID));
        save(new Meal(LocalDateTime.of(2021, Month.OCTOBER, 27, 14, 0), "Админ ланч", 510), ADMIN_ID);
        save(new Meal(LocalDateTime.of(2021, Month.OCTOBER, 27, 21, 0), "Админ ужин", 1500), ADMIN_ID);
    }

    @Override
    public Meal save(Meal meal, int userId) {
        Map<Integer, Meal> meals = usersMealsMap
                // computeIfAbsent - если значения нет, выполняем функцию
                .computeIfAbsent(userId, id -> new ConcurrentHashMap<Integer, Meal>(id));
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            meals.put(meal.getId(), meal);
            return meal;
        }

        // handle case: update, but not present in storage
        return meals.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
    }

    // теперь все методы будут начинаться с метода get()
    // получаем мапу еды для конкретного юзера
    // если этого юзера нет, создаем новый ConcurrentHashMap
    // метод save()

    @Override
    public boolean delete(int id, int userId) {
        Map<Integer, Meal> meals = usersMealsMap.get(userId);
        if (meals != null) {
            if (meals.remove(id) != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Meal get(int id, int userId) {
        Map<Integer, Meal> meals = usersMealsMap.get(userId);
        return meals == null ? null : meals.get(id);
    }

    @Override
    public Collection<Meal> getAll(int userId) {
        return filterByPredicate(userId, meal -> true);
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        return filterByPredicate(userId,
                meal -> Util.isBetweenHalfOpen(meal.getDateTime(), startDateTime, endDateTime));
    }

    // метод полностью описывает функционал методов
    // getAll() и getBetweenHalfOpen().
    // разница последнего только в том , что у него есть фильтрация isBetweenHalfOpen,
    // поэтому в методе getAll() Predicate всегда будет true (мы ничего не фильтруем)
    public List<Meal> filterByPredicate(int userId, Predicate<Meal> filter) {
        // получаем мапу еды для конкретного юзера
        Map<Integer, Meal> meals = usersMealsMap.get(userId);
        // CollectionUtils.isEmpty()
        // можно использовать для проверки, не является ли список пустым
        return CollectionUtils.isEmpty(meals) ? Collections.emptyList() :
                meals.values()
                .stream()
                .filter(filter)
                // сортируем по времени в обратном порядке
                .sorted(Comparator.comparing((Meal meal1) -> meal1.getDateTime()).reversed())
                .collect(Collectors.toList());
    }
}

