package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 repository
 Data Access layer (уровень доступа к данным):
 хранит модели, описывающие используемые сущности,
 также здесь размещаются специфичные классы для работы с разными технологиями доступа к данным,
 например, класс контекста данных Entity Framework.
 Здесь также хранятся репозитории, через которые уровень бизнес-логики взаимодействует с базой данных.
 Здесь будут происходить все операции CRUD

 Здесь будем хранить данные о пользовтеле
 */

@Repository
public class InMemoryUserRepository implements UserRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryUserRepository.class);

    public static final int USER_ID = 1;
    public static final int ADMIN_ID = 2;

    private final Map<Integer, User> usersMap = new ConcurrentHashMap<>();
    private AtomicInteger counter = new AtomicInteger();

    @Override
    // false, если не найден
    public boolean delete(int id) {
        log.info("delete {}", id);
        for (Integer key : usersMap.keySet()) {
            if (key == id) {
                usersMap.remove(key);
                return true;
            }
        }
        return false;
    }

    @Override
    public User save(User user) {
        log.info("save {}", user);
        if (user.isNew()) {
            user.setId(counter.incrementAndGet());
            usersMap.put(user.getId(), user);
            return user;
        }
        return usersMap.put(user.getId(), user);
    }

    @Override
    public User get(int id) {
        log.info("get {}", id);
        return usersMap.get(id);
    }

    @Override
    // список еды возвращать отсортированный в обратном порядке по датам
    public List<User> getAll() {
        log.info("getAll");
        return usersMap.values()
                .stream()
                .sorted(Comparator.comparing((User user) -> user.getName()).thenComparing(User::getEmail))
                .collect(Collectors.toList());
    }

    @Override
    public User getByEmail(String email) {
        log.info("getByEmail {}", email);
        return usersMap.values()
                .stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst()
                // получаем первый результат из списка
                .orElse(null);
    }
}
