#!/bin/bash

# ------------------------------------------------------------------------
# Windows script to extract all ORACLE ddl in a given schema. 
#
# Usage: extract_schema_ddl.bat
#
# ------------------------------------------------------------------------

# ------------------------------------------------------------------------
# Create directory for scripts and then create separate script of each 
# object type in a given schema, and then for each constraint type.
# ------------------------------------------------------------------------

export ORACLE_SID=mcol13
export ORACLE_HOME=/u01/app/oracle/product/10.2.0/db_1

echo "CLEANING GENERATED DIRECTORY"
rm -r ../generated

echo "EXTRACTING DDL FOR SCHEMA MCOL"
mkdir -p ../generated/MCOL
$ORACLE_HOME/bin/sqlplus -s baj/baj <<LABEL
@extract_objects_ddl.sql MCOL
LABEL
$ORACLE_HOME/bin/sqlplus -s baj/baj <<LABEL
@extract_constraints_ddl.sql MCOL
LABEL
$ORACLE_HOME/bin/sqlplus -s baj/baj <<LABEL
@extract_grants_ddl.sql MCOL
LABEL

echo "EXTRACTING DDL FOR SCHEMA MCOL_CCBC"
mkdir -p ../generated/MCOL_CCBC
$ORACLE_HOME/bin/sqlplus -s baj/baj <<LABEL
@extract_objects_ddl.sql MCOL_CCBC
LABEL
$ORACLE_HOME/bin/sqlplus -s baj/baj <<LABEL
@extract_constraints_ddl.sql MCOL_CCBC
LABEL
$ORACLE_HOME/bin/sqlplus -s baj/baj <<LABEL
@extract_grants_ddl.sql MCOL_CCBC
LABEL

echo "EXTRACTING DDL FOR SCHEMA MCOL_CPC"
mkdir -p ../generated/MCOL_CPC
$ORACLE_HOME/bin/sqlplus -s baj/baj <<LABEL
@extract_objects_ddl.sql MCOL_CPC
LABEL
$ORACLE_HOME/bin/sqlplus -s baj/baj <<LABEL
@extract_constraints_ddl.sql MCOL_CPC
LABEL
$ORACLE_HOME/bin/sqlplus -s baj/baj <<LABEL
@extract_grants_ddl.sql MCOL_CPC
LABEL

echo "EXTRACTING DDL FOR SCHEMA MCOL_EXPORT"
mkdir -p ../generated/MCOL_EXPORT
$ORACLE_HOME/bin/sqlplus -s baj/baj <<LABEL
@extract_objects_ddl.sql MCOL_EXPORT
LABEL
$ORACLE_HOME/bin/sqlplus -s baj/baj <<LABEL
@extract_constraints_ddl.sql MCOL_EXPORT
LABEL
$ORACLE_HOME/bin/sqlplus -s baj/baj <<LABEL
@extract_grants_ddl.sql MCOL_EXPORT
LABEL

echo "EXTRACTING DDL FOR SCHEMA MCOL_BATCH"
mkdir -p ../generated/MCOL_BATCH
$ORACLE_HOME/bin/sqlplus -s baj/baj <<LABEL
@extract_objects_ddl.sql MCOL_BATCH
LABEL
$ORACLE_HOME/bin/sqlplus -s baj/baj <<LABEL
@extract_constraints_ddl.sql MCOL_BATCH
LABEL
$ORACLE_HOME/bin/sqlplus -s baj/baj <<LABEL
@extract_grants_ddl.sql MCOL_BATCH
LABEL

echo "EXTRACTING DDL FOR SCHEMA MCOL_PAYMENT"
mkdir -p ../generated/MCOL_PAYMENT
$ORACLE_HOME/bin/sqlplus -s baj/baj <<LABEL
@extract_objects_ddl.sql MCOL_PAYMENT
LABEL
$ORACLE_HOME/bin/sqlplus -s baj/baj <<LABEL
@extract_constraints_ddl.sql MCOL_PAYMENT
LABEL
$ORACLE_HOME/bin/sqlplus -s baj/baj <<LABEL
@extract_grants_ddl.sql MCOL_PAYMENT
LABEL

echo "EXTRACTING DDL FOR SCHEMA MCOL_TEST"
mkdir -p ../generated/MCOL_TEST
$ORACLE_HOME/bin/sqlplus -s baj/baj <<LABEL
@extract_objects_ddl.sql MCOL_TEST
LABEL
$ORACLE_HOME/bin/sqlplus -s baj/baj <<LABEL
@extract_constraints_ddl.sql MCOL_TEST
LABEL
$ORACLE_HOME/bin/sqlplus -s baj/baj <<LABEL
@extract_grants_ddl.sql MCOL_TEST
LABEL

echo "EXTRACTING DDL FOR SCHEMA MCOL_POP"
mkdir -p ../generated/MCOL_POP
$ORACLE_HOME/bin/sqlplus -s baj/baj <<LABEL
@extract_objects_ddl.sql MCOL_POP
LABEL
$ORACLE_HOME/bin/sqlplus -s baj/baj <<LABEL
@extract_constraints_ddl.sql MCOL_POP
LABEL
$ORACLE_HOME/bin/sqlplus -s baj/baj <<LABEL
@extract_grants_ddl.sql MCOL_POP
LABEL

echo "EXTRACTING DDL FOR SCHEMA APP_SUPPORT"
mkdir -p ../generated/APP_SUPPORT
$ORACLE_HOME/bin/sqlplus -s baj/baj <<LABEL
@extract_objects_ddl.sql APP_SUPPORT
LABEL
$ORACLE_HOME/bin/sqlplus -s baj/baj <<LABEL
@extract_constraints_ddl.sql APP_SUPPORT
LABEL
$ORACLE_HOME/bin/sqlplus -s baj/baj <<LABEL
@extract_grants_ddl.sql APP_SUPPORT
LABEL


echo "OUTPUT WRITTEN TO ../generated"
