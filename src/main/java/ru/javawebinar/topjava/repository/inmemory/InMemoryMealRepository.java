package ru.javawebinar.topjava.repository.inmemory;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 Здесь будем хранить данные о еде
 Такая структура данных удобнее, в отличии от списка
 здесь не нужно каждый раз итерироваться для поиска элемента, а
 достаточно воспользоваться ключом для поиска эл-тов
 */

public class InMemoryMealRepository implements MealRepository {

    private final Map<Integer, Meal> repository = new ConcurrentHashMap<>();
    private AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.meals.forEach(meal -> save(meal));
    }

    @Override
    public Meal save(Meal meal) {
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            repository.put(meal.getId(), meal);
            return meal;
        }
        // добавит новый элемент в Map, если элемент с таким ключом там отсутствует
        // в качестве value ему будет присвоен результат выполнения функции mappingFunction.
        // если элемент уже есть — он не будет перезаписан, а останется на месте.
        return repository.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
    }

    @Override
    public boolean delete(int id) {
        if (repository.remove(id) != null) return true;
        else return false;
    }

    @Override
    public Meal get(int id) {
        return repository.get(id);
    }

    @Override
    public Collection<Meal> getAll() {
        // здесь берем все значения из мапы
        return repository.values();
    }
}
