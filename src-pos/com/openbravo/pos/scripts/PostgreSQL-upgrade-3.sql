--    POS-tech is a point of sales software
--    Copyright (C) 2012 SARL SCOP Scil
--    http://trac.scil.coop/pos-tech
--
--    This file is part of POS-Tech
--
--    POS-tech is free software: you can redistribute it and/or modify
--    it under the terms of the GNU General Public License as published by
--    the Free Software Foundation, either version 3 of the License, or
--    (at your option) any later version.
--
--    POS-tech is distributed in the hope that it will be useful,
--    but WITHOUT ANY WARRANTY; without even the implied warranty of
--    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
--    GNU General Public License for more details.
--
--    You should have received a copy of the GNU General Public License
--    along with POS-tech. If not, see <http://www.gnu.org/licenses/>.

-- Database upgrade script for POSTGRESQL

-- db v3 - v4

-- final script
CREATE TABLE SUBGROUPS (
    ID VARCHAR NOT NULL,
    COMPOSITION VARCHAR NOT NULL,
    NAME VARCHAR NOT NULL,
    IMAGE BYTEA,
    DISPORDER INTEGER DEFAULT NULL,
    PRIMARY KEY(ID),
    CONSTRAINT SUBGROUPS_FK_1 FOREIGN KEY (COMPOSITION) REFERENCES PRODUCTS(ID) ON DELETE CASCADE
);

CREATE TABLE SUBGROUPS_PROD (
    SUBGROUP VARCHAR NOT NULL,
    PRODUCT VARCHAR NOT NULL,
    PRIMARY KEY (SUBGROUP, PRODUCT),
    CONSTRAINT SUBGROUPS_PROD_FK_1 FOREIGN KEY (SUBGROUP) REFERENCES SUBGROUPS(ID) ON DELETE CASCADE,
    CONSTRAINT SUBGROUPS_PROD_FK_2 FOREIGN KEY (PRODUCT) REFERENCES PRODUCTS(ID) ON DELETE CASCADE
);

INSERT INTO CATEGORIES(ID, NAME) VALUES ('0', 'Formules');
INSERT INTO CATEGORIES(ID, NAME) VALUES ('-1', 'Pré-paiement');

CREATE TABLE TARIFFAREAS (
    ID VARCHAR NOT NULL,
    NAME VARCHAR NOT NULL,
    TARIFFORDER INTEGER DEFAULT 0,
    PRIMARY KEY(ID)
);
CREATE UNIQUE INDEX TARIFFAREAS_NAME_INX ON TARIFFAREAS(NAME);

CREATE TABLE TARIFFAREAS_PROD (
    TARIFFID VARCHAR NOT NULL,
    PRODUCTID VARCHAR NOT NULL,
    PRICESELL DOUBLE PRECISION NOT NULL,
    PRIMARY KEY (TARIFFID, PRODUCTID),
    CONSTRAINT TARIFFAREAS_PROD_FK_1 FOREIGN KEY (TARIFFID) REFERENCES TARIFFAREAS(ID) ON DELETE CASCADE,
    CONSTRAINT TARIFFAREAS_PROD_FK_2 FOREIGN KEY (PRODUCTID) REFERENCES PRODUCTS(ID) ON DELETE CASCADE
);

ALTER TABLE CATEGORIES ADD COLUMN DISPORDER INTEGER DEFAULT NULL;
ALTER TABLE PRODUCTS ADD COLUMN DELETED BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE TICKETS ADD COLUMN TARIFFAREA VARCHAR DEFAULT NULL;
ALTER TABLE TICKETS ADD CONSTRAINT TICKETS_TARIFFAREA FOREIGN KEY (TARIFFAREA) REFERENCES TARIFFAREAS(ID);

ALTER TABLE CUSTOMERS ADD COLUMN PREPAID DOUBLE PRECISION DEFAULT 0 NOT NULL;

UPDATE APPLICATIONS SET NAME = $APP_NAME{}, VERSION = 4 WHERE ID = $APP_ID{};
