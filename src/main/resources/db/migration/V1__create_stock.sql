/* Создаём таблицу stock в базу данных с помощью flyWay */
CREATE TABLE IF NOT EXISTS stock (

    id BIGSERIAL PRIMARY KEY,
    name varchar(20) NOT NULL,
    city varchar(40) NOT NULL
                                      );
