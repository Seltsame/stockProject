/* Записываем тестовые значения product в базу данных с помощью flyWay */
INSERT INTO stock (name, city)
values ('Новосибирск склад', 'Нск'),
       ('Спб склад', 'Спб');

INSERT INTO stock_place (row, stock_id, shelf, capacity)
values ('Первый', 3, 1, 15),
       ('Второй', 4, 1, 16);

INSERT INTO product (name, price, stock_place_id)
values ('Товар 1', 100, 3),
       ('Товар 2', 200, 4);
