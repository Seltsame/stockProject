/* Создаём stock_place в базу данных с помощью flyWay */
CREATE TABLE IF NOT EXISTS stock_place
(

    id       BIGSERIAL PRIMARY KEY,
    stock_id bigint      NOT NULL ,
    row      varchar(30) NOT NULL,
    rack     int         NOT NULL,
    capacity int         NOT NULL

);