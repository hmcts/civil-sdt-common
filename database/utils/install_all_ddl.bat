@echo off

REM ------------------------------------------------------------------------
REM Windows script to run install_all_ddl.sql. 
REM
REM Usage: install_all_ddl.bat
REM
REM ------------------------------------------------------------------------

echo RECREATE EMPTY SCHEMAS
echo RECREATE EMPTY SCHEMAS > install_all_ddl.out
sqlplus system/elephant @install_all_ddl_setup.sql >> install_all_ddl.out

echo INSTALLING MCOL DDL
echo INSTALLING MCOL DDL >> install_all_ddl.out
sqlplus system/elephant @install_all_ddl.sql generated MCOL >> install_all_ddl.out

echo INSTALLING MCOL_CCBC DDL
echo INSTALLING MCOL_CCBC DDL >> install_all_ddl.out
sqlplus system/elephant @install_all_ddl.sql generated MCOL_CCBC >> install_all_ddl.out

echo INSTALLING MCOL_CPC DDL
echo INSTALLING MCOL_CPC DDL >> install_all_ddl.out
sqlplus system/elephant @install_all_ddl.sql generated MCOL_CPC >> install_all_ddl.out

echo INSTALLING MCOL_EXPORT DDL
echo INSTALLING MCOL_EXPORT DDL >> install_all_ddl.out
sqlplus system/elephant @install_all_ddl.sql generated MCOL_EXPORT >> install_all_ddl.out

echo INSTALLING MCOL_BATCH DDL
echo INSTALLING MCOL_BATCH DDL >> install_all_ddl.out
sqlplus system/elephant @install_all_ddl.sql generated MCOL_BATCH >> install_all_ddl.out

echo INSTALLING MCOL_PAYMENT DDL
echo INSTALLING MCOL_PAYMENT DDL >> install_all_ddl.out
sqlplus system/elephant @install_all_ddl.sql generated MCOL_PAYMENT >> install_all_ddl.out

echo INSTALLING MCOL_TEST DDL
echo INSTALLING MCOL_TEST DDL >> install_all_ddl.out
sqlplus system/elephant @install_all_ddl.sql generated MCOL_TEST >> install_all_ddl.out

echo INSTALLING MCOL_POP DDL
echo INSTALLING MCOL_POP DDL >> install_all_ddl.out
sqlplus system/elephant @install_all_ddl.sql generated MCOL_POP >> install_all_ddl.out

echo INSTALLING APP_SUPPORT DDL
echo INSTALLING APP_SUPPORT DDL >> install_all_ddl.out
sqlplus system/elephant @install_all_ddl.sql generated APP_SUPPORT >> install_all_ddl.out


echo INSTALLING MCOL GRANTS
echo INSTALLING MCOL GRANTS > install_all_grants.out
sqlplus system/elephant @install_all_grants.sql generated MCOL > install_all_grants.out

echo INSTALLING MCOL_CCBC GRANTS
echo INSTALLING MCOL_CCBC GRANTS >> install_all_grants.out
sqlplus system/elephant @install_all_grants.sql generated MCOL_CCBC >> install_all_grants.out

echo INSTALLING MCOL_CPC GRANTS
echo INSTALLING MCOL_CPC GRANTS >> install_all_grants.out
sqlplus system/elephant @install_all_grants.sql generated MCOL_CPC >> install_all_grants.out

echo INSTALLING MCOL_EXPORT GRANTS
echo INSTALLING MCOL_EXPORT GRANTS >> install_all_grants.out
sqlplus system/elephant @install_all_grants.sql generated MCOL_EXPORT >> install_all_grants.out

echo INSTALLING MCOL_BATCH GRANTS
echo INSTALLING MCOL_BATCH GRANTS >> install_all_grants.out
sqlplus system/elephant @install_all_grants.sql generated MCOL_BATCH >> install_all_grants.out

echo INSTALLING MCOL_PAYMENT GRANTS
echo INSTALLING MCOL_PAYMENT GRANTS >> install_all_grants.out
sqlplus system/elephant @install_all_grants.sql generated MCOL_PAYMENT >> install_all_grants.out

echo INSTALLING MCOL_TEST GRANTS
echo INSTALLING MCOL_TEST GRANTS >> install_all_grants.out
sqlplus system/elephant @install_all_grants.sql generated MCOL_TEST >> install_all_grants.out

echo INSTALLING MCOL_POP GRANTS
echo INSTALLING MCOL_POP GRANTS >> install_all_grants.out
sqlplus system/elephant @install_all_grants.sql generated MCOL_POP >> install_all_grants.out

echo INSTALLING APP_SUPPORT GRANTS
echo INSTALLING APP_SUPPORT GRANTS >> install_all_grants.out
sqlplus system/elephant @install_all_grants.sql generated APP_SUPPORT >> install_all_grants.out


echo ALL GRANTS COMPLETED >> install_all_grants.out
