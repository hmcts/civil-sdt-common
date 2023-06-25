-- Script to extract schema objects
-- N.B. schema name is case sensitive even on Windows

-- Usage: sqlplus system/<password>@<database> @extract_objects_ddl.sql <schema_name>

-- Set spool parameters
SET TERMOUT OFF
SET TRIMSPOOL ON
SET TRIMOUT ON
SET LINESIZE 120
SET LINES 5000
SET PAGESIZE 0
SET PAGES 0
SET LONG 20000000
SET LONGCHUNKSIZE 20000000
SET VERIFY OFF
SET HEADING OFF
SET FEEDBACK OFF
COLUMN TXT FORMAT A120 word_wrapped

BEGIN
	-- Stop script generating environment specific arguments as part of create table command
	DBMS_METADATA.SET_TRANSFORM_PARAM(DBMS_METADATA.session_transform, 'PRETTY', true);
	DBMS_METADATA.SET_TRANSFORM_PARAM(DBMS_METADATA.session_transform, 'SQLTERMINATOR', true);
	DBMS_METADATA.SET_TRANSFORM_PARAM(DBMS_METADATA.session_transform, 'SEGMENT_ATTRIBUTES', false);
	DBMS_METADATA.SET_TRANSFORM_PARAM(DBMS_METADATA.session_transform, 'STORAGE', false);
	DBMS_METADATA.SET_TRANSFORM_PARAM(DBMS_METADATA.session_transform, 'TABLESPACE', false);
	DBMS_METADATA.SET_TRANSFORM_PARAM(DBMS_METADATA.session_transform, 'CONSTRAINTS', false);
	DBMS_METADATA.SET_TRANSFORM_PARAM(DBMS_METADATA.session_transform, 'REF_CONSTRAINTS', false);
	DBMS_METADATA.SET_TRANSFORM_PARAM(DBMS_METADATA.session_transform, 'OID', false);
	DBMS_METADATA.SET_TRANSFORM_PARAM(DBMS_METADATA.session_transform, 'PARTITIONING', false);
	DBMS_METADATA.SET_TRANSFORM_PARAM(DBMS_METADATA.session_transform, 'SEGMENT_ATTRIBUTES', false);
END;
/

-- extract the various types of object, each to its own create script in alphabetic order
SPOOL ../generated/&1/create_tables.sql;
select dbms_metadata.get_ddl(object_type,object_name,'&1') from dba_objects where owner = '&1'
and object_type = 'TABLE' order by object_name;
SPOOL OFF

SPOOL ../generated/&1/create_indexes.sql;
select dbms_metadata.get_ddl(object_type,object_name,'&1') from dba_objects where owner = '&1'
and object_type = 'INDEX' order by object_name;
SPOOL OFF

SPOOL ../generated/&1/create_types.sql;
select dbms_metadata.get_ddl(object_type,object_name,'&1') from dba_objects where owner = '&1'
and object_type = 'TYPE' order by object_name;
SPOOL OFF

SPOOL ../generated/&1/create_type_bodies.sql;
select dbms_metadata.get_ddl(object_type,object_name,'&1') from dba_objects where owner = '&1'
and object_type = 'TYPE_BODY' order by object_name;
SPOOL OFF

SPOOL ../generated/&1/create_procedures.sql;
select dbms_metadata.get_ddl(object_type,object_name,'&1') from dba_objects where owner = '&1'
and object_type = 'PROCEDURE' order by object_name;
SPOOL OFF

SPOOL ../generated/&1/create_packages.sql;
select dbms_metadata.get_ddl(object_type,object_name,'&1') from dba_objects where owner = '&1'
and object_type = 'PACKAGE' order by object_name;
SPOOL OFF

SPOOL ../generated/&1/create_package_bodies.sql;
select dbms_metadata.get_ddl(object_type,object_name,'&1') from dba_objects where owner = '&1'
and object_type = 'PACKAGE_BODY' order by object_name;
SPOOL OFF

-- No semi colons for functions.
SPOOL ../generated/&1/create_functions.sql;
select dbms_metadata.get_ddl(object_type,object_name,'&1') from dba_objects where owner = '&1'
and object_type = 'FUNCTION' order by object_name;
SPOOL OFF

SPOOL ../generated/&1/create_triggers.sql;
select dbms_metadata.get_ddl(object_type,object_name,'&1') from dba_objects where owner = '&1'
and object_type = 'TRIGGER' order by object_name;
SPOOL OFF

SPOOL ../generated/&1/create_synonyms.sql;
select dbms_metadata.get_ddl(object_type,object_name,'&1') from dba_objects where owner = '&1'
and object_type = 'SYNONYM' order by object_name;
SPOOL OFF

SPOOL ../generated/&1/create_views.sql;
select dbms_metadata.get_ddl(object_type,object_name,'&1') from dba_objects where owner = '&1'
and object_type = 'VIEW' order by object_name;
SPOOL OFF

SPOOL ../generated/&1/create_sequences.sql;
select dbms_metadata.get_ddl(object_type,object_name,'&1') from dba_objects where owner = '&1'
and object_type = 'SEQUENCE' order by object_name;
SPOOL OFF

SET TERMOUT ON
SET TRIMSPOOL OFF
SET VERIFY ON
SET HEADING ON

exit;

