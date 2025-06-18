

create database chukiso;
use chukiso;


CREATE TABLE role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255)
);

 
 
 
CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255),
    password VARCHAR(255),
    role_id BIGINT,
    refreshToken VARCHAR(255),
    refreshTokenExpiry DATETIME,
    FOREIGN KEY (role_id) REFERENCES Role(id)
);
  