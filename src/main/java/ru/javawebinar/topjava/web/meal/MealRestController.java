package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;
import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;

/**
 * MealRestController должен уметь обрабатывать запросы:
 * <p>
 * •	отдать свою еду (для отображения в таблице, формат List<MealTo>),
 * запрос БЕЗ параметров.
 * (в классе MealService: метод getAll)
 * •	отдать свою еду, отфильтрованную по startDate, startTime, endDate, endTime
 * (в классе MealService: метод getBetweenInclusive)
 * •	отдать/удалить свою еду по id, параметр запроса - id еды.
 * Если еда с этим id чужая или отсутствует - NotFoundException.
 * (в классе MealService: методы get и delete)
 * •	сохранить/обновить еду, параметр запроса - Meal.
 * Если обновляемая еда с этим id чужая или отсутствует - NotFoundException
 * (в классе MealService: методы create и update)
 */

@Controller
public class MealRestController {

    private static final Logger log = getLogger(MealRestController.class);
    private final MealService service;

    @Autowired
    public MealRestController(MealService service) {
        this.service = service;
    }

    public Meal get(int id) {
        // получаем id залогиненого юзера
        int userId = SecurityUtil.authUserId();
        log.info("get meal {} for user {}", id, userId);
        return service.get(id, userId);
    }

    public void delete(int id) {
        int userId = SecurityUtil.authUserId();
        log.info("delete meal {} for user {}", id, userId);
        service.delete(id, userId);
    }

    public List<MealTo> getAll() {
        int userId = SecurityUtil.authUserId();
        log.info("getAll for user {}", userId);
        return MealsUtil.getTos(service.getAll(userId),
                SecurityUtil.authUserCaloriesPerDay());
    }

    public Meal create(Meal meal) {
        int userId = SecurityUtil.authUserId();
        log.info("create {} for user {}", meal, userId);
        // проверяем, что id = 0
        checkNew(meal);
        return service.create(meal, userId);
    }

    public void update(Meal meal, int id) {
        int userId = SecurityUtil.authUserId();
        log.info("update {} for user {}", meal, id);
        // проверяем, что id консистенты
        // (id = 0 или равен id указанному в параметрах)
        assureIdConsistent(meal, id);
        service.update(meal, userId);
    }

    public List<MealTo> getBetween(@Nullable LocalDate startDate, @Nullable LocalTime startTime,
                                   @Nullable LocalDate endDate, @Nullable LocalTime endTime) {
        int userId = SecurityUtil.authUserId();
        log.info("getBetween dates({} - {}) time ({} - {}) for user {}",
                startDate, endDate, startTime, endTime, userId);
        // получаем из сервиса список еды, отфильтрованный по дате
        List<Meal> mealsDateFiltered = service.getBetweenInclusive(startDate, endDate, userId);
        // преобразуем списое еды в to
        // возвращаем отфильтрованный список по времени
        return MealsUtil.getFilteredTos(mealsDateFiltered,
                SecurityUtil.authUserCaloriesPerDay(), startTime, endTime);
    }
}