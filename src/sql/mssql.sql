if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[EMPTY_MULTITYPE_TABLE]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[EMPTY_MULTITYPE_TABLE]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[EMPTY_TABLE]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[EMPTY_TABLE]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[IDENTITY_TABLE]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[IDENTITY_TABLE]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[ONLY_PK_TABLE]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[ONLY_PK_TABLE]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[PK_TABLE]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[PK_TABLE]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[SECOND_TABLE]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[SECOND_TABLE]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[TEST_TABLE]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[TEST_TABLE]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[TEST_IDENTITY_NOT_PK]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[TEST_IDENTITY_NOT_PK]
GO

CREATE TABLE [dbo].[EMPTY_MULTITYPE_TABLE] (
    [VARCHAR_COL] [varchar] (32) COLLATE SQL_Latin1_General_CP1_CI_AS NULL ,
    [NUMERIC_COL] [numeric](38, 0) NULL ,
    [TIMESTAMP_COL] [timestamp] NULL ,
    [VARBINARY_COL] [varbinary] (254) NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[EMPTY_TABLE] (
    [COLUMN0] [varchar] (32) COLLATE SQL_Latin1_General_CP1_CI_AS NULL ,
    [COLUMN1] [varchar] (32) COLLATE SQL_Latin1_General_CP1_CI_AS NULL ,
    [COLUMN2] [varchar] (32) COLLATE SQL_Latin1_General_CP1_CI_AS NULL ,
    [COLUMN3] [varchar] (32) COLLATE SQL_Latin1_General_CP1_CI_AS NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[IDENTITY_TABLE] (
    [IDENTITY_TABLE_ID] [int] IDENTITY (1, 1) NOT NULL ,
    [COLUMN0] [varchar] (32) COLLATE SQL_Latin1_General_CP1_CI_AS NULL ,
    [COLUMN1] [varchar] (32) COLLATE SQL_Latin1_General_CP1_CI_AS NULL 
) ON [PRIMARY]
GO

ALTER TABLE dbo.IDENTITY_TABLE ADD CONSTRAINT
  PK_IDENTITY_TABLE PRIMARY KEY CLUSTERED 
  (
  IDENTITY_TABLE_ID
  ) ON [PRIMARY]

GO

CREATE TABLE [dbo].[ONLY_PK_TABLE] (
    [PK0] [numeric](38, 0) NOT NULL 
) ON [PRIMARY]
GO

ALTER TABLE dbo.ONLY_PK_TABLE ADD CONSTRAINT
  PK_ONLY_PK_TABLE PRIMARY KEY CLUSTERED 
  (
  PK0
  ) ON [PRIMARY]

GO

CREATE TABLE [dbo].[PK_TABLE] (
    [PK0] [numeric](38, 0) NOT NULL ,
    [PK1] [numeric](38, 0) NOT NULL ,
    [PK2] [numeric](38, 0) NOT NULL ,
    [NORMAL0] [varchar] (32) COLLATE SQL_Latin1_General_CP1_CI_AS NULL ,
    [NORMAL1] [varchar] (32) COLLATE SQL_Latin1_General_CP1_CI_AS NULL 
) ON [PRIMARY]
GO
ALTER TABLE dbo.PK_TABLE ADD CONSTRAINT
  PK_PK_TABLE PRIMARY KEY CLUSTERED 
  (
  PK0,
  PK1,
  PK2
  ) ON [PRIMARY]

GO

CREATE TABLE [dbo].[SECOND_TABLE] (
    [COLUMN0] [varchar] (32) COLLATE SQL_Latin1_General_CP1_CI_AS NULL ,
    [COLUMN1] [varchar] (32) COLLATE SQL_Latin1_General_CP1_CI_AS NULL ,
    [COLUMN2] [varchar] (32) COLLATE SQL_Latin1_General_CP1_CI_AS NULL ,
    [COLUMN3] [varchar] (32) COLLATE SQL_Latin1_General_CP1_CI_AS NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[TEST_TABLE] (
    [COLUMN0] [varchar] (32) COLLATE SQL_Latin1_General_CP1_CI_AS NULL ,
    [COLUMN1] [varchar] (32) COLLATE SQL_Latin1_General_CP1_CI_AS NULL ,
    [COLUMN2] [varchar] (32) COLLATE SQL_Latin1_General_CP1_CI_AS NULL ,
    [COLUMN3] [varchar] (32) COLLATE SQL_Latin1_General_CP1_CI_AS NULL 
) ON [PRIMARY]
GO
CREATE TABLE dbo.TEST_IDENTITY_NOT_PK
  (
  COL01 int NOT NULL IDENTITY (1, 1),
  COL02 varchar(64) NULL
  )  ON [PRIMARY]
GO
