CREATE TABLE IF NOT EXISTS stock (

    id BIGSERIAL PRIMARY KEY,
    name varchar(20) NOT NULL,
    city varchar(40) NOT NULL
                                      );

INSERT INTO stock (name, city) values ('test 1' , 'city test');
INSERT INTO stock (name, city) values ('Морской порт' , 'Морской город');
INSERT INTO stock (name, city) values ('Речной порт' , 'Речной город');

