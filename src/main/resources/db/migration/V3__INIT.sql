CREATE TABLE FILE_INFO (
    seq BIGINT AUTO_INCREMENT,
    gid VARCHAR(45) NOT NULL,
    location VARCHAR(45),
    fileName VARCHAR(100) NOT NULL,
    extension VARCHAR(40) NOT NULL,
    contentType VARCHAR(60) NOT NULL,
    done TINYINT(1) DEFAULT 0,
    createdBy VARCHAR(65),
    createdAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    modifiedAt DATETIME,
    deletedAt DATETIME,
    PRIMARY KEY(seq)
);