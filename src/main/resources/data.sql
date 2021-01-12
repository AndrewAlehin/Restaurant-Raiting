INSERT INTO users (name, email, password)
VALUES ('User', 'user@yandex.ru', '{noop}password'),
       ('Admin', 'admin@gmail.com', '{noop}admin');

INSERT INTO user_roles (role, user_id)
VALUES ('USER', 1),
       ('ADMIN', 2),
       ('USER', 2);

INSERT INTO restaurants (name, date)
VALUES ('Ресторан', '2021-01-08'),
       ('Ресторан2', '2021-01-08'),
       ('Ресторан3', '2021-02-08'),
       ('Ресторан4', '2021-03-08'),
       ('Ресторан5', '2021-03-08');

INSERT INTO meals (name, price, rest_id)
VALUES ('price', 500, 1),
       ('Обед', 1000, 1),
       ('Ужин', 500, 1),
       ('Еда на граничное значение', 100, 1),
       ('Завтрак', 500, 1),
       ('Обед2', 1000, 1),
       ('Ужин2', 510, 1),
       ('Админ ланч', 510, 2),
       ('Админ ужин', 1500, 2);
