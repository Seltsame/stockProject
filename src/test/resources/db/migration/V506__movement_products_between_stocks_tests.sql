INSERT INTO stock (name, city)
values ('from склад', 'Владикавказ from'),
 ('to склад', 'Владикавказ to');

INSERT INTO product(name, price)
values ('from товары', 2500),
 ('to товары', 2500);


INSERT INTO stock_place (row, shelf, capacity, stock_id)
values ('from Первый', 1, 200, 9), /* stock_id = 9, sp_id = 10*/
       ('to Второй', 2, 300, 10); /* stock_id = 10, sp_id = 11 */


INSERT INTO product_on_stock_place(product_id, stock_place_id, quantity_product)
values (10, 11, 50),
       (10, 12, 0),
       (11, 12, 0),
       (11, 11, 0);
