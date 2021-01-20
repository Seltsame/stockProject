INSERT INTO stock (name, city)
values ('Склад свободный', 'Владикавказ свободный');

INSERT INTO product(name, price)
values ('свободные товары', 1500);


INSERT INTO stock_place (row, shelf, capacity, stock_id)
values ('свободный Первый', 1, 200, 8), /* stock_id = 8, sp_id = 10*/
       ('свободный Второй', 2, 300, 8); /* stock_id = 8, sp_id = 11 */


INSERT INTO product_on_stock_place(product_id, stock_place_id, quantity_product)
values (9, 10, 50);
