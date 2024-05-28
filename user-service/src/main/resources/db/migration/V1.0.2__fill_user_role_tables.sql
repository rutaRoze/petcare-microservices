USE petcareuserdb;

INSERT INTO users (name, surname, email, phone_number)
VALUES
    ('John', 'Doe', 'john@example.com', '1234567890'),
    ('Jane', 'Dodo', 'jane@example.com', '1234567890'),
    ('Alice', 'Done', 'alice@example.com', '1234567890'),
    ('Bob', 'Din', 'bob@example.com', '1234567890'),
    ('Emily', 'Dave', 'emily@example.com', '1234567890'),
    ('David', 'Did', 'david@example.com', '1234567890'),
    ('Sarah', 'Dada', 'sarah@example.com', '1234567890');

INSERT INTO roles (name)
VALUES
    ('Vet'),
    ('Owner');

INSERT INTO user_roles (user_id, role_id)
VALUES
    (1, 1),
    (2, 2),
    (3, 2),
    (4, 2),
    (5, 1),
    (6, 2),
    (7, 2);