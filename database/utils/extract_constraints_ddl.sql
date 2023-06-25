-- Script to extract schema constraints
-- N.B. schema name is case sensitive even on Windows

-- Usage: sqlplus system/<password>@<database> @extract_constraints_ddl.sql <schema_name>

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

-- extract primary keys create script in alphabetic order
SPOOL ../generated/&1/create_primary_keys.sql;
select replace(dbms_metadata.get_ddl('CONSTRAINT',constraint_name, '&1'), ' ENABLE;', ' using index &1.' || '.' || index_name || ' ENABLE;')  
from dba_constraints where owner = '&1'
and constraint_type = 'P' order by constraint_name;
SPOOL OFF

-- extract check constraints create script in alphabetic order
SPOOL ../generated/&1/create_check_constraints.sql;
select dbms_metadata.get_ddl('CONSTRAINT',constraint_name, '&1') from dba_constraints where owner = '&1'
and constraint_type = 'C' order by constraint_name;
SPOOL OFF

-- extract unique constraints create script in alphabetic order
SPOOL ../generated/&1/create_unique_constraints.sql;
select dbms_metadata.get_ddl('CONSTRAINT',constraint_name, '&1') from dba_constraints where owner = '&1'
and constraint_type = 'U' order by constraint_name;
SPOOL OFF

-- extract referential constraints create script in alphabetic order
SPOOL ../generated/&1/create_referential_constraints.sql;
select dbms_metadata.get_ddl('REF_CONSTRAINT',constraint_name, '&1') from dba_constraints where owner = '&1'
and constraint_type = 'R' order by constraint_name;
SPOOL OFF


SET TERMOUT ON
SET TRIMSPOOL OFF
SET VERIFY ON
SET HEADING ON

exit;
