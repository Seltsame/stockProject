/* Создаём таблицу stock в базу данных с помощью flyWay */
CREATE TABLE IF NOT EXISTS stock (

    id BIGSERIAL PRIMARY KEY,
    name varchar(50) NOT NULL,
    city varchar(50) NOT NULL
                                         );
