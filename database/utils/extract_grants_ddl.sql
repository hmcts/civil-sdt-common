-- Script to extract schema objects
-- N.B. schema name is case sensitive even on Windows

-- Usage: sqlplus system/<password>@<database> @extract_grants_ddl.sql <schema_name>

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
SET SERVEROUTPUT ON SIZE 50000
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
	DBMS_METADATA.SET_TRANSFORM_PARAM(DBMS_METADATA.session_transform, 'SEGMENT_ATTRIBUTES', false);
END;
/

-- extract the various types of object, each to its own create script in alphabetic order
SPOOL ../generated/&1/create_grants.sql;

DECLARE
  CURSOR object_grantees
  IS
  SELECT DISTINCT grantee 
    FROM dba_tab_privs 
   WHERE owner = '&1';
   
  CURSOR object_grants(p_grantee IN VARCHAR2)
  IS
  SELECT SUBSTR(dbms_metadata.get_granted_ddl('OBJECT_GRANT',p_grantee),1,200000) grant_statement
    FROM dual;
     
  CURSOR system_grants(p_grantee IN VARCHAR2)
  IS
  SELECT SUBSTR(dbms_metadata.get_granted_ddl('SYSTEM_GRANT',p_grantee),1,200000) grant_statement
    FROM dual;
     
  CURSOR sys_grants
  IS
  SELECT substr(dbms_metadata.get_granted_ddl('OBJECT_GRANT',grantee),1,200000) grant_statement
    FROM dba_tab_privs WHERE owner = 'SYS' and grantee = '&1';
     
  procedure print_clob(p_clob IN clob)
  is
    v_offset      NUMBER DEFAULT 1;
    v_chunck_size NUMBER := 4000;

  begin

    LOOP
      EXIT WHEN v_offset > length(p_clob);
      DBMS_OUTPUT.PUT_LINE(substr(p_clob, v_offset, v_chunck_size));
      v_offset := v_offset + v_chunck_size;

    END LOOP;
  end print_clob;
  
   
BEGIN

  DBMS_OUTPUT.PUT_LINE('-----------------');
  DBMS_OUTPUT.PUT_LINE('--- Object Grants');
  DBMS_OUTPUT.PUT_LINE('-----------------');

  FOR r_users IN object_grantees LOOP
    FOR r_grants IN object_grants(p_grantee => r_users.grantee) LOOP
      print_clob(r_grants.grant_statement);
    END LOOP;
  END LOOP;
  
  DBMS_OUTPUT.PUT_LINE('-----------------');
  DBMS_OUTPUT.PUT_LINE('--- System Grants');
  DBMS_OUTPUT.PUT_LINE('-----------------');

  FOR r_grants IN system_grants(p_grantee => 'PUBLIC') LOOP
      print_clob(r_grants.grant_statement);
  END LOOP;

  DBMS_OUTPUT.PUT_LINE('-----------------');
  DBMS_OUTPUT.PUT_LINE('--- Sys Grants');
  DBMS_OUTPUT.PUT_LINE('-----------------');

  FOR r_grants IN sys_grants LOOP
    print_clob(r_grants.grant_statement);
  END LOOP;
  
EXCEPTION
  WHEN OTHERS THEN
    COMMIT;
  
END;
/

SPOOL OFF

SET TERMOUT ON
SET TRIMSPOOL OFF
SET VERIFY ON
SET HEADING ON

exit;

