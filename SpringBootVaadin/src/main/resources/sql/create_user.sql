CREATE USER 'nbuser'@'localhost' IDENTIFIED BY 'nbuser';
GRANT ALL PRIVILEGES ON * . * TO 'nbuser'@'localhost';

-- postgresql
CREATE USER nbuser WITH PASSWORD 'nbuser';
CREATE DATABASE solver OWNER nbuser;

