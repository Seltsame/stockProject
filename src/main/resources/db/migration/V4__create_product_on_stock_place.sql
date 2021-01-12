CREATE TABLE IF NOT EXISTS product_on_stock_place
(
    id               BIGSERIAL PRIMARY KEY,
    product_id       bigint NOT NULL,
    stock_place_id   bigint NOT NULL,
    quantity_product bigint    NOT NULL,

    CONSTRAINT product_on_stock_place_to_stock_place FOREIGN KEY (product_id) REFERENCES product_on_stock_place,
    CONSTRAINT stock_place_on_stock_place_to_stock_place FOREIGN KEY (stock_place_id) REFERENCES product_on_stock_place
)
