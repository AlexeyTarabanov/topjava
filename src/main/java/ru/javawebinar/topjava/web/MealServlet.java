package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.StringUtils;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.web.meal.MealRestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class MealServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(MealServlet.class);

    // через класс ConfigurableApplicationContext, Spring поднимает свои бины
    // на основе xml конфигурации, которая находится в ClassPath-е
    private ConfigurableApplicationContext springContext;
    // Сервлет обращается к контролеру, контроллер вызывает сервис, сервис - репозиторий.
    private MealRestController mealController;

    @Override
    // Вызывается контейнером сервлета, чтобы указать сервлету, что сервлет вводится в эксплуатацию
    public void init() {
        // поднимает контекст Спринга при старте сервлета
        springContext = new ClassPathXmlApplicationContext("spring/spring-app.xml");
        mealController = springContext.getBean(MealRestController.class);
    }

    @Override
    // Вызывается контейнером сервлета, чтобы указать сервлету, что сервлет выводится из эксплуатации.
    public void destroy() {
        springContext.close();
        super.destroy();
    }

    @Override
    // doPost: обрабатывает запросы POST (отправка данных)
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // setCharacterEncoding
        // - заменяет имя кодировки, используемой в тексте запроса
        // метод должен быть вызван перед чтением параметров запроса
        request.setCharacterEncoding("UTF-8");

        Meal meal = new Meal(
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")));

        log.info(meal.isNew() ? "Create {}" : "Update {}", meal);
        // // возвращает: истина, если строка не равна нулю, ее длина больше 0 и она не содержит только пробелов.
        if (StringUtils.hasLength(request.getParameter("id"))) {
            mealController.update(meal, getId(request));
        } else {
            mealController.create(meal);
        }
        // переадресовываем на meals
        response.sendRedirect("meals");
    }

    @Override
    // получение данных
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        switch (action == null ? "all" : action) {
            case "delete":
                int id = getId(request);
                log.info("Delete {}", id);
                mealController.delete(id);
                response.sendRedirect("meals");
                break;
            case "create":
            case "update":
                final Meal meal = "create".equals(action) ?
                        new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000) :
                        mealController.get(getId(request));
                request.setAttribute("meal", meal);
                request.getRequestDispatcher("/mealForm.jsp").forward(request, response);
                break;
            case "all":
            default:
                log.info("getAll");
                request.setAttribute("meals",
                        mealController.getAll());
                request.getRequestDispatcher("/meals.jsp").forward(request, response);
                break;
        }
    }

    private int getId(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("id"));
        return Integer.parseInt(paramId);
    }
}
