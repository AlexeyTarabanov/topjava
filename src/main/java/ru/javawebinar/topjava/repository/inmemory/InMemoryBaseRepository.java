package ru.javawebinar.topjava.repository.inmemory;

import ru.javawebinar.topjava.model.AbstractBaseEntity;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryBaseRepository<T extends AbstractBaseEntity> {

    // счетчик один, общий для всех хранимых объектов
    private static final AtomicInteger counter = new AtomicInteger(0);
    private final Map<Integer, T> map = new ConcurrentHashMap<>();

    public T save(T entity) {
        if (entity.isNew()) {
            entity.setId(counter.incrementAndGet());
            map.put(entity.getId(), entity);
            return entity;
        }
        // computeIfPresent
        // если элемент с ключом key существует - выполняем функцию
        return map.computeIfPresent(entity.getId(), ((Integer, t) -> entity));
    }

    public boolean delete(int id) {
        if (map.remove(id) != null) return true;
        else return false;
    }

    public T get(int id) {
        return map.get(id);
    }

    Collection<T> getCollection() {
        return map.values();
    }
}
