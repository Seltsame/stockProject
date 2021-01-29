INSERT INTO stock (name, city)
values ('searching_Московский', 'searching_Москва_city'),
       ('searching_Спб склад', 'searching_Санкт-Марино_city'),
       ('searching_Адмиралтейский склад', 'searching_Санкт-Петербург_city');

INSERT INTO product(name, price)
values ('searching_носки', 500),
       ('searching_неизвестная хрень', 600),
       ('searching_безумно неизвестная хрень', 700),
       ('searching_баунти', 610);

INSERT INTO stock_place (row, shelf, capacity, stock_id)
values ('searching_Третий', 1, 150, 5), /* stock_id = 5, sp_id = 5*/
       ('searching_Третий', 2, 170, 6), /* stock_id = 6, sp_id = 6 */
       ('searching_Пятый', 3, 180, 6), /* stock_id = 6, sp_id = 7 */
       ('searching_Шестой', 44, 250, 7); /* stock_id = 7, sp_id = 8 */

INSERT INTO product_on_stock_place(product_id, stock_place_id, quantity_product)
values (5, 5, 15),
       (6, 5, 16),
       (6, 6, 17),
       (7, 7, 1),
       (8, 8, 19);