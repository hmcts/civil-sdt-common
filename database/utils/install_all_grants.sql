-- Usage: @install_all_grants <version> <schema>
--    where <version> is baseline or current, and
--          <schema> is the schema to install

SET ECHO ON

alter session set current_schema = &2;

@&1/&2/create_grants.sql

exit;