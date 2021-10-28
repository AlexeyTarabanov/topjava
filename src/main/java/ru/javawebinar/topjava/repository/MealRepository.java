package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface MealRepository {

    // обслуживает оба случая (create and update)
    // null if updated meal do not belong to userId
    Meal save(Meal meal, int userId);

    // false if meal do not belong to userId
    boolean delete(int id, int userId);

    // null if meal do not belong to userId
    Meal get(int id, int userId);

    // возвращаем отсортированным по dateTime
    List<Meal> getAll(int userId);

    List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId);
}
