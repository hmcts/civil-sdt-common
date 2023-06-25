@echo off

REM ------------------------------------------------------------------------
REM Windows script to extract all ORACLE ddl in a given schema. 
REM
REM Usage: extract_schema_ddl.bat
REM
REM ------------------------------------------------------------------------

REM ------------------------------------------------------------------------
REM Create directory for scripts and then create separate script of each 
REM object type in a given schema, and then for each constraint type.
REM ------------------------------------------------------------------------


echo EXTRACTING DDL FOR SCHEMA PUBLIC
rmdir /s /q ..\generated
mkdir ..\generated\PUBLIC
sqlplus system/elephant @extract_objects_ddl.sql PUBLIC
sqlplus system/elephant @extract_constraints_ddl.sql PUBLIC
sqlplus system/elephant @extract_grants_ddl.sql PUBLIC

echo.
echo.
echo.
echo.
echo OUTPUT WRITTEN TO .\generated
