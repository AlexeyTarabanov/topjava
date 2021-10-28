<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://topjava.javawebinar.ru/functions" %>

<html>
<head>
    <title>Meal list</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<%--
<section> служит для группировки взаимосвязанного содержимого--%>
<section>
    <%--
    h3 создает заголовок 3-го уровня.
    Заголовок - это название какого-либо блока, обычно набранное большим и жирным шрифтом.
    Заголовок h1 - это самый главный заголовок на странице, в нем должна быть отражена ее основная мысль.
    Тег <a> предназначен для создания ссылок
    href - задает адрес документа, на который следует перейти.--%>
    <h3><a href="index.html">Home</a></h3>
    <%--
    hr - рисует горизонтальную линию--%>
    <hr/>
    <h2>Meals</h2>
    <%--        устанавливаем форму на веб-странице--%>
    <form>
        <form method="get" action="meals">
            <input type="hidden" name="action" value="filter">
            <%--        dl создает список--%>
            <dl>
                <%--            <dt> создает термин--%>
                <%--            <dd> задает определение этого термина--%>
                <dt>From-a Date (inclusive)</dt>
                <dd><input type="date" name="startDate" value="${param.startDate}"></dd>
            </dl>
            <dl>
                <dt>To Date (inclusive)</dt>
                <dd><input type="date" name="endDate" value="${param.endDate}"></dd>
            </dl>
            <dl>
                <dt>From Time (включительно)</dt>
                <dd><input type="time" name="startTime" value="${param.startTime}"></dd>
            </dl>
            <dl>
                <dt>To Time (включительно)</dt>
                <dd><input type="time" name="endTime" value="${param.endTime}"></dd>
            </dl>
            <%--        кнопка отправки данных формы на сервер--%>
            <button type="submit">Filter</button>
        </form>
        <hr/>
        <a href="meals?action=create">Add Meal</a>
        <%-- устанавливает перевод строки в том месте, где этот тег находится
        в отношении Add Meal таблица спутиться на 1 строку вниз
        --%>
        <br><br>
        <%--
        <table> служит контейнером для элементов, определяющих содержимое таблицы.
        border - устанавливает толщину рамки в пикселах
        cellpadding - определяет расстояние между содержимым ячейки таблицы и ее границей
        cellspacing - задаёт расстояние между ячейками таблицы--%>
        <table border="1" cellpadding="8" cellspacing="0">
            <%--
            <thead> предназначен для хранения одной или нескольких строк, которые представлены вверху таблицы
            должен идти в исходном коде сразу после тега--%>
            <thead>
            <%--
            <tr> служит контейнером для создания строки таблицы--%>
            <tr>
                <%--
                <th> предназначен для создания одной ячейки таблицы, которая обозначается как заголовочная
                Текст в такой ячейке отображается браузером обычно жирным шрифтом и выравнивается по центру
                (используется для заголовков) --%>
                <th>Date</th>
                <th>Description</th>
                <th>Calories</th>
                <th></th>
                <th></th>
            </tr>
            </thead>
            <%--
            тег <c:forEach> позволяет задать цикл
            тег из библиотеки JSTL --%>
            <c:forEach items="${meals}" var="meal">
                <%--
                <jsp:useBean> - объявление объекта JavaBean, который будет использоваться на странице JSP
                (объявляем объект класса MealTo)
                id - параметр, идентифицирующий экземпляр объекта в пространстве имен, специфицированном в атрибуте scope.
                Это имя используется для ссылки на компонент JavaBean из страницы JSP.
                scope - атрибут, определяющий область видимости ссылки на экземпляр объекта JavaBean
                (Объект, определенный с областью видимости page, доступен до тех пор, пока не будет отправлен ответ клиенту
                или пока запрос к текущей странице JSP не будет перенаправлен куда-нибудь еще)
                type - дает возможность определить тип переменных скрипта как класс, суперкласс или интерфейс, реализуемый классом.--%>
                <jsp:useBean id="meal" type="ru.javawebinar.topjava.to.MealTo"/>
                <tr data-mealExcess="${meal.excess}">
                    <%--
                    Предназначен для создания одной ячейки таблицы--%>
                    <td>
<%--                        форматируем дату--%>
                            ${fn:formatDateTime(meal.dateTime)}
                    </td>
                    <td>${meal.description}</td>
                    <td>${meal.calories}</td>
                    <td><a href="meals?action=update&id=${meal.id}">Update</a></td>
                    <td><a href="meals?action=delete&id=${meal.id}">Delete</a></td>
                </tr>
            </c:forEach>
        </table>
</section>
</body>
</html>