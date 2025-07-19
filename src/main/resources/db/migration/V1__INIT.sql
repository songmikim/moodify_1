CREATE TABLE MEMBER (
    seq BIGINT AUTO_INCREMENT,
    email VARCHAR(65) NOT NULL UNIQUE,
    password VARCHAR(65) NOT NULL,
    name VARCHAR(45) NOT NULL,
    mobile VARCHAR(15) NOT NULL,
    authority ENUM('MEMBER', 'ADMIN') DEFAULT 'MEMBER',
    termsAgree TINYINT(1) DEFAULT 0,
    locked TINYINT(1) DEFAULT 0,
    expired DATETIME,
    credentialChangedAt DATETIME,
    createdAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    modifiedAt DATETIME,
    deletedAt DATETIME,
    PRIMARY KEY(seq)
);