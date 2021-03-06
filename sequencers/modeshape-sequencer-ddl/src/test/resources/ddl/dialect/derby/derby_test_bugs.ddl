CREATE TABLE DD_RELT_PRIMRYKY
(
  LGCL_ID       VARCHAR(1000),
  UUID1         BIGINT NOT NULL,
  UUID2         BIGINT NOT NULL,
  UUID_STRING   VARCHAR(44) NOT NULL,
  NAM           VARCHAR(256),
  NAMNSRC       VARCHAR(256),
  COLMNS        BIGINT,
  FORGNKYS      BIGINT,
  TXN_ID        BIGINT NOT NULL
);

CREATE TABLE AUDITENTRIES
(
   TIMESTAMP            VARCHAR(50)            NOT NULL,
   CONTEXT              VARCHAR(64)            NOT NULL,
   ACTIVITY             VARCHAR(64)            NOT NULL,
   RESOURCES            VARCHAR(4000)          NOT NULL,
   PRINCIPAL            VARCHAR(255)            NOT NULL,
   HOSTNAME             VARCHAR(64)            NOT NULL,
   VMID                 VARCHAR(64)            NOT NULL
);

CREATE INDEX RTVIRTULDBSVRSN_IX
    ON RT_VIRTUAL_DBS(VDB_VERSION)
;
CREATE TABLE CS_EXT_FILES  (
   FILE_UID             DECIMAL(19)                          NOT NULL,
   CHKSUM               DECIMAL(20),
   FILE_NAME            VARCHAR(255)		NOT NULL,
   FILE_CONTENTS        BLOB(1000M),
   CONFIG_CONTENTS	    CLOB(20M),
   SEARCH_POS           DECIMAL(10),
   IS_ENABLED           VARCHAR(1),
   FILE_DESC            VARCHAR(4000),
   CREATED_BY           VARCHAR(100),
   CREATION_DATE        VARCHAR(50),
   UPDATED_BY           VARCHAR(100),
   UPDATE_DATE          VARCHAR(50),
   FILE_TYPE            VARCHAR(30))
;
ALTER TABLE CS_EXT_FILES
       ADD   PRIMARY KEY (FILE_UID);
ALTER TABLE CS_EXT_FILES ADD CONSTRAINT CSEXFILS_FIL_NA_UK UNIQUE (FILE_NAME)
;


CREATE TABLE CS_SYSTEM_PROPS (
	PROPERTY_NAME VARCHAR(255),
	PROPERTY_VALUE VARCHAR(255)
)
;

CREATE UNIQUE INDEX SYSPROPS_KEY ON CS_SYSTEM_PROPS (PROPERTY_NAME);
