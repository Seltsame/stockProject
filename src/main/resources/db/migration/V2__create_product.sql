/* Создаём таблицу product в базу данных с помощью flyWay */
CREATE TABLE IF NOT EXISTS product
(
    id               BIGSERIAL PRIMARY KEY,
    name             varchar(50) NOT NULL,
    price            decimal     NOT NULL /*вместо double */

);