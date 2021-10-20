package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.repository.inmemory.InMemoryMealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {

    private static final Logger log = getLogger(MealServlet.class);

    private MealRepository repository;

    // Вызывается контейнером сервлета, чтобы указать сервлету,
    // что сервлет вводится в эксплуатацию
    @Override
    public void init() throws ServletException {
        repository = new InMemoryMealRepository();
    }

    // обрабатывает запросы POST (отправка данных)
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // setCharacterEncoding
        // - заменяет имя кодировки, используемой в тексте запроса
        // метод должен быть вызван перед чтением параметров запроса
        req.setCharacterEncoding("UTF-8");

        // запрашиваем id
        String id = req.getParameter("id");

        Meal meal = new Meal(id.isEmpty() ? null : Integer.valueOf(id),
                LocalDateTime.parse(req.getParameter("dateTime")),
                req.getParameter("description"),
                Integer.parseInt(req.getParameter("calories")));

        // регистрирует сообщение на уровне INFO в соответствии с указанным форматом и аргументом
        log.info(meal.isNew() ? "Create {}" : "Update {}", meal);
        repository.save(meal);
        // переадресовываем на meals
        resp.sendRedirect("meals");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // запрашиваем action
        String action = req.getParameter("action");

        switch (action == null ? "all" : action) {
            case "delete":
                int id = getId(req);
                log.info("Delete {}", id);
                repository.delete(id);
                // переадресовывает запрос на meals
                resp.sendRedirect("meals");
                break;
            case "create":
            case "update":
                final Meal meal = action.equals("create") ?
                        // localTime дает время в формате hh:mm:ss, nnn
                        // localTime.truncatedTo(ChronoUnit.MINUTES) - сокращает до минут
                        new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000) :
                        repository.get(getId(req));
                // получем meal
                req.setAttribute("meal", meal);
                // перенаправляем запрос на /mealForm.jsp
                req.getRequestDispatcher("/mealForm.jsp").forward(req, resp);
                break;
            case "all":
            default:
                log.info("getAll");
                req.setAttribute("meals",
                        MealsUtil.getTos(repository.getAll(), MealsUtil.DEFAULT_CALORIES_PER_DAY));
                req.getRequestDispatcher("/meals.jsp").forward(req, resp);
                break;
        }
    }

    // получаем id из запроса
    private int getId(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("id"));
        return Integer.parseInt(paramId);
    }
}
