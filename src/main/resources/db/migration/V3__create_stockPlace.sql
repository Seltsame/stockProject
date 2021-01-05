/* Создаём stock_place в базу данных с помощью flyWay */
CREATE TABLE IF NOT EXISTS stock_place
(

    id       BIGSERIAL PRIMARY KEY,

    row      varchar(30) NOT NULL,
    shelf    int         NOT NULL,
    capacity int         NOT NULL,
    stock_id bigint      NOT NULL

);