#!/bin/sh
#
#Copyrights and Licenses
#
#Copyright (c) 2008-2009 by the Ministry of Justice. All rights reserved.
#Redistribution and use in source and binary forms, with or without modification, are permitted
#provided that the following conditions are met:
#    -  Redistributions of source code must retain the above copyright notice, this list of conditions
#        and the following disclaimer.
#    -  Redistributions in binary form must reproduce the above copyright notice, this list of
#        conditions and the following disclaimer in the documentation and/or other materials
#        provided with the distribution.
#    -  All advertising materials mentioning features or use of this software must display the
#       following acknowledgment: "This product includes Money Claims OnLine."
#    -  Products derived from this software may not be called "Money Claims OnLine" nor may
#       "Money Claims OnLine" appear in their names without prior written permission of the
#        Ministry of Justice.
#    -  Redistributions of any form whatsoever must retain the following acknowledgment: "This
#         product includes Money Claims OnLine."
#This software is provided "as is" and any expressed or implied warranties, including, but
#not limited to, the implied warranties of merchantability and fitness for a particular purpose are
#disclaimed. In no event shall the Ministry of Justice or its contributors be liable for any
#direct, indirect, incidental, special, exemplary, or consequential damages (including, but
#not limited to, procurement of substitute goods or services; loss of use, data, or profits;
#or business interruption). However caused any on any theory of liability, whether in contract,
#strict liability, or tort (including negligence or otherwise) arising in any way out of the use of this
#software, even if advised of the possibility of such damage.
#
#$Id: housekeeping.sh 14409 2014-05-12 15:18:56Z williamsdr $
#$LastChangedRevision: 14409 $
#$LastChangedDate: 2014-05-12 16:18:56 +0100 (Tue, 12 May 2014) $
#$LastChangedBy: williamsdr $
#
#################################################################################
#
# Installation: This script requires an environment varibale called BATCH_HOME 
# that should be set to the directory location of this script.
# e.g. export BATCH_HOME=/opt/moj/oracle/batch/
# This should be configured in the .bash_profile on the relevant unix user.
#
#################################################################################
function checkError {
  if [ $? -ne 0 ]
  then
    echo "An error occured whilst running the script."
    exit 1
  fi
}

function show_usage {
  echo "Usage: $BASE_NAME -s oracle_sid"
  echo "                  -p purge"
  echo "                  -c commit_interval"
  exit 8
}

# Set the environment variables.
BASE="$(readlink -f $0)"
echo "Started ${BASE} on $(date)"
trap 'rc=$?; printf "=========== ERROR ===========\nAn unexpected error occured\nRC($rc) at line $LINENO\nin ${BASE}\n===========  END  ===========\n"; exit $rc' ERR
BASE_NAME="$(basename ${BASE})"; BASE_PATH="$(dirname ${BASE})"; BASE_EXT=${BASE##*.}; 
BASE_NAME_NO_PATH=${BASE_NAME##*/}; BASE_NAME_NO_EXT=${BASE_NAME_NO_PATH%.*}; 
LOG_FILE=${BASE_PATH}"/"${BASE_NAME}".log"
BATCH_PROPERTIES="${BATCH_HOME}/batch.properties"

# Set up default values
v_oracle_sid=""
v_schema=""
v_procedure=""
v_commit_interval=""

# Parse Command Arguments
while getopts ":s:p:c:" opt; do
  case $opt in 
    s ) v_oracle_sid="$OPTARG" ;;
    p ) v_procedure="$OPTARG" ;;
    c ) v_commit_interval="$OPTARG" ;;
    * ) show_usage ;;
  esac
done

shift $(($OPTIND - 1))

# Validate the parameters
if [[ $v_oracle_sid == "" ]]
then
  show_usage
fi

# Set the schema

export ORACLE_SID=$v_oracle_sid

v_db_user=$(cat ${BATCH_PROPERTIES}|grep sdt.batch.dbconnection.user|cut -f 2 -d "="|sed 's/^[ ]//g')
if [[ $v_db_user == "" ]]
then
  echo "database user account property not found in ${BATCH_PROPERTIES}"
  exit 1
fi
  
v_db_password=$(cat ${BATCH_PROPERTIES}|grep sdt.batch.dbconnection.password|cut -f 2 -d "="|sed 's/^[ ]//g')
if [[ $v_db_password == "" ]]
then
  echo "database user password property not found in ${BATCH_PROPERTIES}"
  exit 1
fi

v_db_schema=$(cat ${BATCH_PROPERTIES}|grep sdt.batch.dbconnection.schema|cut -f 2 -d "="|sed 's/^[ ]//g')
if [[ $v_db_schema == "" ]]
then
  echo "database schema property not found in ${BATCH_PROPERTIES}"
  exit 1
fi

echo "==> Calling Procedure '${v_procedure}'...."
sqlplus -s /nolog <<EOF
connect ${v_db_user}/${v_db_password}@${v_oracle_sid}
whenever sqlerror exit failure rollback;
set echo off ver off feed off pages 0
set serveroutput on
alter session set current_schema=$v_db_schema;
exec $v_procedure ($v_commit_interval)
commit;
exit success;
EOF
checkError

echo "Successfully completed ${BASE} on $(date)"
exit 0
