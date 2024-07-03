USE petcareauthdb;

DROP TABLE IF EXISTS auth_user;
DROP TABLE IF EXISTS role;
DROP TABLE IF EXISTS auth_user_role;

CREATE TABLE auth_user (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    user_profile_id BIGINT NOT NULL
);

CREATE TABLE role (
    role_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE auth_user_role (
    user_id BIGINT,
    role_id BIGINT,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES auth_user(user_id),
    FOREIGN KEY (role_id) REFERENCES role(role_id)
);