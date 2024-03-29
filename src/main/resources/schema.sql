CREATE TABLE ACCOUNT
(
    ACCOUNT_ID    BIGINT auto_increment primary key,
    ACCOUNT_TITLE VARCHAR(255) NOT NULL,
    BALANCE       DECIMAL(18, 2) default 0,
    LAST_UPDATED  TIMESTAMP      default CURRENT_TIMESTAMP
);

CREATE TABLE TRANSACTION_HISTORY
(
    TRANSACTION_HISTORY_ID BIGINT auto_increment primary key,
    ACCOUNT_ID             BIGINT         NOT NULL,
    TRANSACTION_SUMMARY    VARCHAR(1000)  NOT NULL,
    TRANSACTION_AMOUNT     DECIMAL(18, 2) NOT NULL,
    TYPE                   VARCHAR(10)    NOT NULL,
    CLOSING_BALANCE        DECIMAL(18, 2) NOT NULL,
    TRANSACTION_DATE       TIMESTAMP default CURRENT_TIMESTAMP,
    foreign key (ACCOUNT_ID) references ACCOUNT (ACCOUNT_ID)
);

CREATE TABLE TRANSFER_HISTORY
(
    TRANSFER_HISTORY_ID    BIGINT auto_increment primary key,
    FROM_ACCOUNT_ID        BIGINT NOT NULL,
    TO_ACCOUNT_ID          BIGINT NOT NULL,
    TRANSACTION_HISTORY_ID BIGINT NOT NULL,
    AMOUNT                 DECIMAL(18, 2) default 0,
    TRANSFER_DATE          TIMESTAMP      default CURRENT_TIMESTAMP,
    foreign key (FROM_ACCOUNT_ID) references ACCOUNT (ACCOUNT_ID),
    foreign key (TO_ACCOUNT_ID) references ACCOUNT (ACCOUNT_ID),
    foreign key (TRANSACTION_HISTORY_ID) references TRANSACTION_HISTORY (TRANSACTION_HISTORY_ID),
);
