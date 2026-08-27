-- Sample data for testing the member API
-- MySQL version

INSERT INTO member (name, email, age)
VALUES ('John Doe', 'johndoe@example.com', '30')
    ON DUPLICATE KEY UPDATE name = VALUES(name), age = VALUES(age);

INSERT INTO member (name, email, age)
VALUES ('Jane Smith', 'jane@example.com', '25')
    ON DUPLICATE KEY UPDATE name = VALUES(name), age = VALUES(age);

INSERT INTO member (name, email, age)
VALUES ('Alice Johnson', 'alice@example.com', '28')
    ON DUPLICATE KEY UPDATE name = VALUES(name), age = VALUES(age);