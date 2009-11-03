-----------------------------------------------------------------------------
-- TEST_TABLE
-----------------------------------------------------------------------------

DROP TABLE IF EXISTS test_table;
CREATE TABLE test_table
  (COLUMN0 VARCHAR(32),
   COLUMN1 VARCHAR(32),
   COLUMN2 VARCHAR(32),
   COLUMN3 VARCHAR(32)) TYPE = InnoDB;

-----------------------------------------------------------------------------
-- SECOND_TABLE
-----------------------------------------------------------------------------

DROP TABLE IF EXISTS second_table;
CREATE TABLE second_table
  (COLUMN0 VARCHAR(32),
   COLUMN1 VARCHAR(32),
   COLUMN2 VARCHAR(32),
   COLUMN3 VARCHAR(32)) TYPE = InnoDB;

-----------------------------------------------------------------------------
-- EMPTY_TABLE
-----------------------------------------------------------------------------

DROP TABLE IF EXISTS empty_table;
CREATE TABLE empty_table
  (COLUMN0 VARCHAR(32),
   COLUMN1 VARCHAR(32),
   COLUMN2 VARCHAR(32),
   COLUMN3 VARCHAR(32)) TYPE = InnoDB;

-----------------------------------------------------------------------------
-- PK_TABLE
-----------------------------------------------------------------------------

DROP TABLE IF EXISTS pk_table;
CREATE TABLE pk_table
  (PK0 NUMERIC(38, 0) NOT NULL,
   PK1 NUMERIC(38, 0) NOT NULL,
   PK2 NUMERIC(38, 0) NOT NULL,
   NORMAL0 VARCHAR(32),
   NORMAL1 VARCHAR(32), PRIMARY KEY (PK0, PK1, PK2)) TYPE = InnoDB;

-----------------------------------------------------------------------------
-- ONLY_PK_TABLE
-----------------------------------------------------------------------------

DROP TABLE IF EXISTS only_pk_table;
CREATE TABLE only_pk_table
  (PK0 NUMERIC(38, 0) NOT NULL PRIMARY KEY) TYPE = InnoDB;

-----------------------------------------------------------------------------
-- EMPTY_MULTITYPE_TABLE
-----------------------------------------------------------------------------

DROP TABLE IF EXISTS empty_multitype_table;
CREATE TABLE empty_multitype_table
  (VARCHAR_COL VARCHAR(32),
   NUMERIC_COL NUMERIC(38, 0),
   TIMESTAMP_COL TIMESTAMP,
   VARBINARY_COL VARBINARY(254)) TYPE = InnoDB;
