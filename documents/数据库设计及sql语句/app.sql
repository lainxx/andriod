create database app;
use app;
CREATE TABLE `new_table` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(80) NULL DEFAULT NULL,
  `password` VARCHAR(80) NULL DEFAULT NULL,
  `name` VARCHAR(80) NULL DEFAULT NULL,
  `age` INT(11) NULL DEFAULT NULL,
  `gender` VARCHAR(80) NULL DEFAULT NULL,
  PRIMARY KEY (`id`));