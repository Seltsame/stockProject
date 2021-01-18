/* Записываем тестовые значения stock_place в базу данных с помощью flyWay */
INSERT INTO stock (name, city)
values ('Новосибирск склад', 'Нск'),
       ('Спб склад', 'Спб');

INSERT INTO stock_place (row, stock_id, shelf, capacity)
values ('Первый', 3, 1, 15),
       ('Второй', 4, 1, 16);
