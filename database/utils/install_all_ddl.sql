-- Usage: @install_all_ddl <version> <schema>
--    where <version> is baseline or current, and
--          <schema> is the schema to install

SET ECHO ON

alter session set current_schema = &2;

@&1/&2/create_tables.sql
@&1/&2/create_indexes.sql
@&1/&2/create_primary_keys.sql
@&1/&2/create_check_constraints.sql
@&1/&2/create_referential_constraints.sql
@&1/&2/create_unique_constraints.sql
@&1/&2/create_views.sql
@&1/&2/create_sequences.sql
@&1/&2/create_functions.sql
@&1/&2/create_types.sql
@&1/&2/create_type_bodies.sql
@&1/&2/create_packages.sql
@&1/&2/create_package_bodies.sql
@&1/&2/create_procedures.sql
@&1/&2/create_synonyms.sql
@&1/&2/create_triggers.sql

exit;