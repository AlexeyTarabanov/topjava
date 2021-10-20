<%@ page import="ru.javawebinar.topjava.util.TimeUtil" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: user
  Date: 20.10.2021
  Time: 21:46
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Meals</title>
    <style>
        .normal {
            color: green;
        }
        .excess {
            color: red;
        }
    </style>
</head>
<body>
<section> <%--
служит для группировки взаимосвязанного содержимого--%>
    <h3><a href="index.html">Home</a></h3>
<%--h3 - создает заголовок 3-го уровня--%>
<%--a -  предназначен для создания ссылок--%>
    <hr>
<%--рисует горизонтальную линию--%>
    <h2>Meals</h2>
    <a href="meals?action=create">Add Meal</a>
    <br><%--
    устанавливает перевод строки в том месте, где этот тег находится
    в отношении Add Meal таблица спуститься на 1 строку вниз--%>
    <table border="1" cellpadding="8" cellspacing="0"><%--
    table - контейнер для элементов, определяющих содержимое таблицы.
    cellpadding - определяет расстояние между содержимым ячейки таблицы и ее границей
    cellspacing - задаёт расстояние между ячейками таблицыcellspacing - задаёт расстояние между ячейками таблицы--%>
        <thead><%--
        предназначен для хранения одной или нескольких строк, которые представлены вверху таблицы--%>
        <tr><%--
        служит контейнером для создания строки таблицы.--%>
            <th>Date</th><%--
            предназначен для создания одной ячейки таблицы, которая обозначается как заголовочная
            Текст в такой ячейке отображается браузером обычно жирным шрифтом и выравнивается по центру
            (используется для заголовков)--%>
            <th>Description</th>
            <th>Calories</th>
            <th></th>
            <th></th><%--
            добавил дополнительно 2 пустых ячейки--%>
        </tr>
        </thead>
        <c:forEach items="${meals}" var="meal"><%--
        forEach - позволяет задать цикл (тег из библиотеки JSTL)--%>
            <jsp:useBean id="meal" scope="page" type="ru.javawebinar.topjava.model.MealTo"/><%--
            jsp:useBean - объявляем объект класса MealTo
            scope - атрибут, определяющий область видимости ссылки на экземпляр объекта JavaBean
            page - доступен до тех пор, пока не будет отправлен ответ клиенту
            или пока запрос к текущей странице JSP не будет перенаправлен куда-нибудь еще
            type - дает возможность определить тип переменных скрипта как класс, суперкласс или интерфейс, реализуемый классом--%>
            <tr class="${meal.excess ? 'excess' : 'normal'}">
                <td><%=TimeUtil.toString(meal.getDateTime())%></td><%--
                td - предназначен для создания одной ячейки таблицы--%>
                <td>${meal.description}</td>
                <td>${meal.calories}</td>
                <td><a href="meals?action=update&id=${meal.id}">Update</a></td>
                <td><a href="meals?action=delete&id=${meal.id}">Delete</a></td><%--
                заполнил эти ячейки данными--%>
            </tr>
        </c:forEach>
    </table>
</section>
</body>
</html>
