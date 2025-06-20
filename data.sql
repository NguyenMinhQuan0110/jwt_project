

create database chukiso;
use chukiso;
CREATE TABLE `user` (
   `id` bigint NOT NULL AUTO_INCREMENT,
   `email` varchar(255) DEFAULT NULL,
   `name` varchar(255) DEFAULT NULL,
   `password` varchar(255) DEFAULT NULL,
   `role_id` bigint DEFAULT NULL,
   `refresh_token` varchar(255) DEFAULT NULL,
   `refresh_token_expiry` datetime(6) DEFAULT NULL,
   `block` bit(1) DEFAULT NULL,
   `reason` varchar(255) DEFAULT NULL,
   `loginfail` int DEFAULT NULL,
   PRIMARY KEY (`id`),
   KEY `FKn82ha3ccdebhokx3a8fgdqeyy` (`role_id`),
   CONSTRAINT `FKn82ha3ccdebhokx3a8fgdqeyy` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`)
 ) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `role` (
   `id` bigint NOT NULL AUTO_INCREMENT,
   `name` varchar(255) DEFAULT NULL,
   PRIMARY KEY (`id`)
 ) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
  