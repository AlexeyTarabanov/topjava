package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import java.util.Comparator;
import java.util.List;
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
public class InMemoryUserRepository extends InMemoryBaseRepository<User> implements UserRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryUserRepository.class);

    public static final int USER_ID = 1;
    public static final int ADMIN_ID = 2;

    @Override
    // список еды возвращать отсортированный в обратном порядке по датам
    public List<User> getAll() {
        log.info("getAll");
        return getCollection().stream()
                // c помощью thenComparing объединяем компараторы
                // сначала сравниваем по имени, потом по емэйлу
                .sorted(Comparator.comparing((User user) -> user.getName()).thenComparing(User::getEmail))
                .collect(Collectors.toList());
    }

    @Override
    public User getByEmail(String email) {
        log.info("getByEmail {}", email);
        return getCollection()
                .stream()
                .filter(user -> user.getEmail().equals(email))
                // получаем первый результат из списка
                .findFirst()
                .orElse(null);
    }
}
