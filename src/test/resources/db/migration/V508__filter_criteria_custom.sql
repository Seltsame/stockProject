INSERT INTO stock (name, city)
values ('search2_stock_1', 'search2_city_1'), /*id = 11*/
       ('search2_stock_2', 'search2_city_1'), /*id = 12*/
       ('search2_stock_3', 'search2_city_2'); /*id = 13*/

INSERT INTO product(name, price)
values ('search2_product_1', 1100), /*id = 12*/
       ('search2_product_2', 1200), /*id = 13*/
       ('search2_prod_3', 1300); /*id = 14*/

INSERT INTO stock_place (row, shelf, capacity, stock_id)
values ('search2_Первый_ряд', 1, 50, 11), /*sp_id = 14*/
       ('search2_Первый_ряд', 2, 60, 11), /*sp_id = 15*/
       ('search2_Второй_ряд', 2, 70, 12); /*sp_id = 16*/

INSERT INTO product_on_stock_place(product_id, stock_place_id, quantity_product)
values (12, 14, 10),
       (12, 15, 20),
       (12, 16, 30);
