#!/usr/bin/env bash

set -e

# Civil SDT Repl database
if [ -z "$SDT_DB_PASSWORD" ] || [ -z "$SDT_DB_USERNAME" ]; then
  echo "ERROR: Missing environment variable. Set value for both 'SDT_DB_USERNAME' and 'SDT_DB_PASSWORD'."
  exit 1
fi

psql -v ON_ERROR_STOP=1 --username postgres --set USERNAME=${SDT_DB_USERNAME} --set PASSWORD=${SDT_DB_PASSWORD} <<-EOSQL
  CREATE USER :USERNAME WITH PASSWORD ':PASSWORD';

  CREATE DATABASE civil_sdt
    WITH OWNER = :USERNAME
    ENCODING = 'UTF-8'
    CONNECTION LIMIT = -1;
EOSQL
