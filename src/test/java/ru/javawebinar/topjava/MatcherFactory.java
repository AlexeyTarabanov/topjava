package ru.javawebinar.topjava;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 Сравнение фактических и ожидаемых объектов через AssertJ
 */

public class MatcherFactory {
    public static <T> Matcher<T> usingIgnoringFieldsComparator(String... fieldsToIgnore) {
        return new Matcher<>(fieldsToIgnore);
    }

    public static class Matcher<T> {
        private final String[] fieldsToIgnore;

        private Matcher(String... fieldsToIgnore) {
            this.fieldsToIgnore = fieldsToIgnore;
        }

        // срввниваем объекты, игнорируя поля fieldsToIgnore
        public void assertMatch(T actual, T expected) {
            // usingRecursiveComparison() -
            // подтверждает, что утверждение выполнено успешно, поскольку данные обоих объектов совпадают
            assertThat(actual).usingRecursiveComparison().ignoringFields(fieldsToIgnore).isEqualTo(expected);
        }

        // @SafeVarargs используется для подавления предупреждений о небезопасных операциях во время компиляции
        @SafeVarargs
        // здесь у нас возвращаются пользователи отсортированные по email'у
        // проверяем, что они вернулись строго в этом порядке
        public final void assertMatch(Iterable<T> actual, T... expected) {
            assertMatch(actual, Arrays.asList(expected));
        }

        public void assertMatch(Iterable<T> actual, Iterable<T> expected) {
            assertThat(actual).usingRecursiveFieldByFieldElementComparatorIgnoringFields(fieldsToIgnore).isEqualTo(expected);
        }
    }
}
