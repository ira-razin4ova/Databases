-- Получить всех студентов, возраст которых находится между 10 и 20
SELECT * FROM user
WHERE age BETWEEN 10 and 20;

-- Получить всех студентов, но отобразить только список их имен
SELECT first_name FROM user;

-- Получить всех студентов, у которых в имени присутствует буква О
SELECT * FROM user
WHERE first_name LIKE '%О%';

-- Получить всех студентов, у которых возраст меньше идентификатора
SELECT * FROM user
WHERE age < 19;

-- Получить всех студентов упорядоченных по возрасту
SELECT * FROM user
ORDER BY age;