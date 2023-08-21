DROP TABLE IF EXISTS record;
DROP TABLE IF EXISTS invalid_record;

CREATE TABLE record (
    reference bigint NOT NULL,
    accountNumber varchar(18) NOT NULL,
    description varchar(50),
    startBalance numeric(10,2) NOT NULL,
    mutation numeric(10,2) NOT NULL,
    endBalance numeric(10,2) NOT NULL,
    PRIMARY KEY (reference)
);

CREATE TABLE invalid_record (
    id bigint NOT NULL AUTO_INCREMENT,
    reference bigint NOT NULL,
    accountNumber varchar(18) NOT NULL,
    description varchar(50),
    startBalance numeric(10,2) NOT NULL,
    mutation numeric(10,2) NOT NULL,
    endBalance numeric(10,2) NOT NULL,
    reason varchar(100),
    PRIMARY KEY (id)
);
