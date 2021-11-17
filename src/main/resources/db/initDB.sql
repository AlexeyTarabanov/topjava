DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS meals;
DROP TABLE IF EXISTS users;
DROP SEQUENCE IF EXISTS global_seq;


-- создаю уникальный SEQUENCE
CREATE SEQUENCE global_seq START WITH 100000;

CREATE TABLE users
(
    id               INTEGER PRIMARY KEY DEFAULT nextval('global_seq'),
    name             VARCHAR                           NOT NULL,
    email            VARCHAR                           NOT NULL,
    password         VARCHAR                           NOT NULL,
    registered       TIMESTAMP           DEFAULT now() NOT NULL,
    enabled          BOOL                DEFAULT TRUE  NOT NULL,
    calories_per_day INTEGER             DEFAULT 2000  NOT NULL
);
CREATE
-- нельзя записать в БД пользователя с одним и тем же email
UNIQUE INDEX users_unique_email_idx ON users (email);

CREATE TABLE user_roles
(
    user_id INTEGER NOT NULL,
    role    VARCHAR,
    CONSTRAINT user_roles_idx UNIQUE (user_id, role),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE meals
(
    -- PRIMARY KEY - делает столбец первичным ключом.
    -- id будет генерить автоматически
    -- за счет того, что будет спрашивать следющее значение у последовательности global_seq
    id          INTEGER PRIMARY KEY DEFAULT nextval('global_seq'),
    -- NOT NULL - столбец не может принимать значение NULL
    user_id     INTEGER   NOT NULL,
    date_time   TIMESTAMP NOT NULL,
    description TEXT      NOT NULL,
    calories    INT       NOT NULL,
--  для связи между таблицами применяются FOREIGN KEY (внешние ключи)
--  внешний ключ устанавливается для столбца из зависимой, подчиненной таблицы (referencing table),
--  и указывает на один из столбцов из главной таблицы (referenced table).
--  чтобы установить связь между таблицами,
--  после ключевого слова REFERENCES указывается имя связанной таблицы
--  и далее в скобках имя столбца из этой таблицы, на который будет указывать внешний ключ.
--  после выражения REFERENCES может идти выражение
--  ON DELETE и ON UPDATE,
--  которые уточняют поведение при удалении или обновлении данных.
--  https://metanit.com/sql/postgresql/2.5.php
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- если мы хотим, чтобы столбец имел только уникальные значения, то для него можно определить атрибут UNIQUE.
-- в данном случае значение meals_unique_user_datetime_idx, будет иметь уникальное значение
CREATE UNIQUE INDEX meals_unique_user_datetime_idx ON meals (user_id, date_time);
-- нельзя создать еду для одного пользователя с одним и тем же date_time