-----------------------------------------------------------------------------
-- TEST_TABLE
-----------------------------------------------------------------------------

CREATE TABLE test_table
  (column0 VARCHAR(32),
   column1 VARCHAR(32),
   column2 VARCHAR(32),
   column3 VARCHAR(32)) TYPE = InnoDB;

-----------------------------------------------------------------------------
-- SECOND_TABLE
-----------------------------------------------------------------------------

CREATE TABLE second_table
  (COLUMN0 VARCHAR(32),
   COLUMN1 VARCHAR(32),
   COLUMN2 VARCHAR(32),
   COLUMN3 VARCHAR(32)) TYPE = InnoDB;

-----------------------------------------------------------------------------
-- EMPTY_TABLE
-----------------------------------------------------------------------------

CREATE TABLE empty_table
  (COLUMN0 VARCHAR(32),
   COLUMN1 VARCHAR(32),
   COLUMN2 VARCHAR(32),
   COLUMN3 VARCHAR(32)) TYPE = InnoDB;

/*
-----------------------------------------------------------------------------
-- ESCAPED TABLE
-----------------------------------------------------------------------------

CREATE TABLE "ESCAPED TABLE"
  (COLUMN0 VARCHAR(32),
   COLUMN1 VARCHAR(32),
   COLUMN2 VARCHAR(32),
   "COLUMN 3" VARCHAR(32)) TYPE = InnoDB;
*/

-----------------------------------------------------------------------------
-- PK_TABLE
-----------------------------------------------------------------------------

CREATE TABLE pk_table
  (PK0 NUMERIC(38, 0) NOT NULL,
   PK1 NUMERIC(38, 0) NOT NULL,
   PK2 NUMERIC(38, 0) NOT NULL,
   NORMAL0 VARCHAR(32),
   NORMAL1 VARCHAR(32), PRIMARY KEY (PK0, PK1, PK2)) TYPE = InnoDB;

-----------------------------------------------------------------------------
-- ONLY_PK_TABLE
-----------------------------------------------------------------------------

CREATE TABLE only_pk_table
  (pk0 NUMERIC(38, 0) NOT NULL PRIMARY KEY) TYPE = InnoDB;

-----------------------------------------------------------------------------
-- EMPTY_MULTITYPE_TABLE
-----------------------------------------------------------------------------

CREATE TABLE empty_multitype_table
  (VARCHAR_COL VARCHAR(32),
   NUMERIC_COL NUMERIC(38, 0),
   TIMESTAMP_COL TIMESTAMP,
   VARBINARY_COL VARBINARY(254)) TYPE = InnoDB;



