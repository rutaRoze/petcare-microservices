USE petcareauthdb;

DROP TABLE IF EXISTS auth_user;

CREATE TABLE auth_user (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    user_profile_id BIGINT NOT NULL
);