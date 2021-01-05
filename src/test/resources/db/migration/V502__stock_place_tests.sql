/* Записываем тестовые значения stock_place в базу данных с помощью flyWay */
INSERT INTO stock (name, city)
values ('Новосибирск склад', 'Нск'),
       ('Спб склад', 'Спб');

INSERT INTO stock_place (row, shelf, capacity, stock_id)
values ('Первый', 1, 15, 3),
       ('Второй', 1, 16, 4);
