
-- DROP TABLE XMLTYPES;
-- CREATE TABLE XMLTYPES
--    (XMLVARCHAR DB2XML.XMLVARCHAR,
--    XMLCLOB DB2XML.XMLCLOBNOT LOGGED NOT COMPACT,
--    XMLFILE DB2XML.XMLFILE);

-----------------------------------------------------------------------------
-- TEST_TABLE
-----------------------------------------------------------------------------

DROP TABLE TEST_TABLE;
CREATE TABLE TEST_TABLE
  (COLUMN0 VARCHAR(32),
   COLUMN1 VARCHAR(32),
   COLUMN2 VARCHAR(32),
   COLUMN3 VARCHAR(32));

-----------------------------------------------------------------------------
-- SECOND_TABLE
-----------------------------------------------------------------------------
DROP TABLE SECOND_TABLE;
CREATE TABLE SECOND_TABLE
  (COLUMN0 VARCHAR(32),
   COLUMN1 VARCHAR(32),
   COLUMN2 VARCHAR(32),
   COLUMN3 VARCHAR(32));

-----------------------------------------------------------------------------
-- EMPTY_TABLE
-----------------------------------------------------------------------------
DROP TABLE EMPTY_TABLE;
CREATE TABLE EMPTY_TABLE
  (COLUMN0 VARCHAR(32),
   COLUMN1 VARCHAR(32),
   COLUMN2 VARCHAR(32),
   COLUMN3 VARCHAR(32));

-----------------------------------------------------------------------------
-- PK_TABLE
-----------------------------------------------------------------------------
DROP TABLE PK_TABLE;
CREATE TABLE PK_TABLE
  (PK0 DECIMAL(31, 0) NOT NULL,
   PK1 DECIMAL(31, 0) NOT NULL,
   PK2 DECIMAL(31, 0) NOT NULL,
   NORMAL0 VARCHAR(32),
   NORMAL1 VARCHAR(32), PRIMARY KEY (PK0, PK1, PK2));

-----------------------------------------------------------------------------
-- ONLY_PK_TABLE
-----------------------------------------------------------------------------
DROP TABLE ONLY_PK_TABLE;
CREATE TABLE ONLY_PK_TABLE
  (PK0 NUMERIC(31, 0) NOT NULL PRIMARY KEY);

-----------------------------------------------------------------------------
-- EMPTY_MULTITYPE_TABLE
-----------------------------------------------------------------------------
DROP TABLE EMPTY_MULTITYPE_TABLE;
CREATE TABLE EMPTY_MULTITYPE_TABLE
  (VARCHAR_COL VARCHAR(32),
   NUMERIC_COL DECIMAL(31, 0),
   TIMESTAMP_COL TIMESTAMP,
   VARBINARY_COL VARCHAR(254) FOR BIT DATA);

-----------------------------------------------------------------------------
-- IDENTITY_TABLE
-----------------------------------------------------------------------------
DROP TABLE IDENTITY_TABLE;
CREATE TABLE IDENTITY_TABLE
  (IDENTITY_TABLE_ID BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY ( START WITH +0 , INCREMENT BY +1 , NO CACHE ) NOT NULL,
   COLUMN0 VARCHAR(32),
   COLUMN1 VARCHAR(32), PRIMARY KEY (IDENTITY_TABLE_ID));

-----------------------------------------------------------------------------
-- IDENTITY_TABLE
-----------------------------------------------------------------------------
DROP TABLE TEST_IDENTITY_NOT_PK;
CREATE TABLE TEST_IDENTITY_NOT_PK
  (COL01 BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY ( START WITH +0 , INCREMENT BY +1 , NO CACHE ) NOT NULL,
   COL02 VARCHAR(64));
