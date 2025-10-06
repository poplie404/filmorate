INSERT INTO mpa (id, name) VALUES
(1, 'G'),
(2, 'PG'),
(3, 'PG-13'),
(4, 'R'),
(5, 'NC-17');

INSERT INTO genres (id, name) VALUES
(1, 'Comedy'),
(2, 'Drama'),
(3, 'Action'),
(4, 'Thriller'),
(5, 'Horror'),
(6, 'Documentary');

UPDATE genres SET name = 'Комедия' WHERE id = 1;
UPDATE genres SET name = 'Драма' WHERE id = 2;
UPDATE genres SET name = 'Мультфильм' WHERE id = 3;
UPDATE genres SET name = 'Триллер' WHERE id = 4;
UPDATE genres SET name = 'Документальный' WHERE id = 5;
UPDATE genres SET name = 'Боевик' WHERE id = 6;

