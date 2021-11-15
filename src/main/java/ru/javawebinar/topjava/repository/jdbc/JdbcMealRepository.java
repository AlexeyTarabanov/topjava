package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class JdbcMealRepository implements MealRepository {

    // преобразует строку в новый экземпляр указанного сопоставленного целевого класса
    private static final BeanPropertyRowMapper<Meal> ROW_MAPPER = BeanPropertyRowMapper.newInstance(Meal.class);

    // это центральный класс в базовом пакете JDBC
    // упрощает использование JDBC и помогает избежать распространенных ошибок
    // он выполняет основной рабочий процесс JDBC, оставляя код приложения для предоставления SQL и извлечения результатов.
    private final JdbcTemplate jdbcTemplate;

    // расширяет класс JdbcTemplate и инкапсулирует класс JdbcTemplate
    // для поддержки функции именованных параметров
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    // многопоточный многоразовый объект, обеспечивающий простую вставку в таблицу.
    // он обеспечивает обработку метаданных для упрощения кода, необходимого для создания базового оператора вставки.
    // все, что вам нужно предоставить, - это имя таблицы и карта, содержащая имена столбцов и значения столбцов.
    private final SimpleJdbcInsert insertMeal;

    @Autowired
    public JdbcMealRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        // создаем SimpleJdbcInsert с таблицей users используя генератор ключей id
        // то есть при вставке мы primary key не задаем - он автоматически генерируется
        this.insertMeal = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("meals")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public Meal save(Meal meal, int userId) {
        // MapSqlParameterSource - реализация SqlParameterSource, которая содержит заданную карту параметров.
        // этот класс предназначен для передачи простой карты значений параметров методам класса NamedParameterJdbcTemplate.
        MapSqlParameterSource map = new MapSqlParameterSource()
                .addValue("id", meal.getId())
                .addValue("description", meal.getDescription())
                .addValue("calories", meal.getCalories())
                .addValue("dateTime", meal.getDateTime())
                .addValue("userId", userId);

        if (meal.isNew()) {
            Number newId = insertMeal.executeAndReturnKey(map);
            meal.setId(newId.intValue());
        } else {
            // если мы попробуем сделать update по полям id и user_id
            // WHERE id=:id AND user_id=:user_id
            // а еда не наша и meal не принадлежит этому юзеру (userId)
            // тогда update не произойдет и возвратится 0
            if (namedParameterJdbcTemplate.update("" +
                    "UPDATE meals " +
                    "   SET description=:description, calories=:calories, date_time=:date_time " +
                    " WHERE id=:id AND user_id=:user_id", map) == 0) {
                return null;
            }
        }
        return meal;
    }

    @Override
    public boolean delete(int id, int userId) {
        return jdbcTemplate.update("DELETE FROM meals WHERE id=? AND user_id=?", id, userId) != 0;
    }

    @Override
    public Meal get(int id, int userId) {
        List<Meal> meals = jdbcTemplate.query(
                "SELECT * FROM meals WHERE id = ? AND user_id = ?", ROW_MAPPER, id, userId);
        // singleResult - возвращает единственный объект результата из данной коллекции.
        return DataAccessUtils.singleResult(meals);
    }

    @Override
    // возвращает отсортированный список
    // все условия делаем по userId
    public List<Meal> getAll(int userId) {
        return jdbcTemplate.query(
                "SELECT * FROM meals WHERE user_id=? ORDER BY date_time DESC", ROW_MAPPER, userId);
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        return jdbcTemplate.query(
                "SELECT * FROM meals WHERE user_id=?  AND date_time >=  ? AND date_time < ? ORDER BY date_time DESC",
                ROW_MAPPER, userId, startDateTime, endDateTime);
    }
}
