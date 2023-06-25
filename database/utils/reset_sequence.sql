-- =====================================================================================================================
-- Copyrights and Licenses
--
-- Copyright (c) 2008-2009 by the Ministry of Justice. All rights reserved.
-- Redistribution and use in source and binary forms, with or without modification, are permitted
-- provided that the following conditions are met:
--     -  Redistributions of source code must retain the above copyright notice, this list of conditions
--         and the following disclaimer.
--     -  Redistributions in binary form must reproduce the above copyright notice, this list of
--         conditions and the following disclaimer in the documentation and/or other materials
--         provided with the distribution.
--     -  All advertising materials mentioning features or use of this software must display the
--        following acknowledgment: "This product includes Money Claims OnLine."
--     -  Products derived from this software may not be called "Money Claims OnLine" nor may
--        "Money Claims OnLine" appear in their names without prior written permission of the
--         Ministry of Justice.
--     -  Redistributions of any form whatsoever must retain the following acknowledgment: "This
--          product includes Money Claims OnLine."
-- This software is provided "as is" and any expressed or implied warranties, including, but
-- not limited to, the implied warranties of merchantability and fitness for a particular purpose are
-- disclaimed. In no event shall the Ministry of Justice or its contributors be liable for any
-- direct, indirect, incidental, special, exemplary, or consequential damages (including, but
-- not limited to, procurement of substitute goods or services; loss of use, data, or profits;
-- or business interruption). However caused any on any theory of liability, whether in contract,
-- strict liability, or tort (including negligence or otherwise) arising in any way out of the use of this
-- software, even if advised of the possibility of such damage.
--
-- $Id: reset_sequence.sql 4730 2009-03-12 16:56:26Z lowtherr $
-- $LastChangedRevision: 4730 $
-- $LastChangedDate: 2009-03-12 16:56:26 +0000 (Thu, 12 Mar 2009) $
-- $LastChangedBy: lowtherr $
-- =====================================================================================================================

-- Script to reset the sequence used by Hibernate, so that
-- nextval will return the next available base on the contents
-- of the database.
whenever sqlerror exit failure
set serveroutput on

declare
    cursor c1 is
        select tab.owner, tab.table_name, seqs.sequence_name
        from all_tables tab, all_sequences seqs
        where tab.owner like 'SDT%'
        and seqs.sequence_owner = tab.owner
        and (
            seqs.sequence_name = tab.table_name||'_SEQ'
            or seqs.sequence_name = tab.table_name||'_SEQUENCE'
            );

    v_owner all_tables.owner%type;
    v_table_name all_tables.table_name%type;
    v_sequence_name all_sequences.sequence_name%type;

    cursor c2 is
        select con_cols.position, con_cols.column_name
        from all_constraints cons, all_cons_columns con_cols, all_tab_columns tab_cols
        where con_cols.owner = v_owner
        and con_cols.table_name = v_table_name
        and cons.constraint_type = 'P'
        and cons.constraint_name = con_cols.constraint_name
        and cons.owner = con_cols.owner
        and con_cols.owner = tab_cols.owner
        and con_cols.table_name = tab_cols.table_name
        and con_cols.column_name = tab_cols.column_name
        and tab_cols.data_type = 'NUMBER' -- Only process PK's that are defined as NUMBER
        order by con_cols.table_name, con_cols.position;

    v_column_position all_cons_columns.position%type;
    v_column_name all_cons_columns.column_name%type;
    v_sql_stmt varchar(256);
    v_seq_exists varchar(256);

    i integer default 0;
    j integer default 0;

begin
    open c1;
    loop
        fetch c1 into v_owner, v_table_name, v_sequence_name;
        if c1%found then
            open c2;
            loop
                fetch c2 into v_column_position, v_column_name;
                if c2%found then
                    v_sql_stmt:='select coalesce(max('||v_column_name||'),0) from '||v_owner||'.'||v_table_name;
                    execute immediate v_sql_stmt into i;

                    v_sql_stmt:='select '||v_owner||'.'||v_sequence_name||'.nextval from dual';
                    execute immediate v_sql_stmt into j;

                    if i-j <> 0 then
                        dbms_output.put_line('Resetting sequence '||v_owner||'.'||v_sequence_name||' to start at '||cast(i + 1 as varchar2));
                        v_sql_stmt:='alter sequence '||v_owner||'.'||v_sequence_name||' increment by '||cast(i-j as varchar2);
                        execute immediate v_sql_stmt;

                        v_sql_stmt:='select '||v_owner||'.'||v_sequence_name||'.nextval from dual';
                        execute immediate v_sql_stmt into j;

                        v_sql_stmt:='alter sequence '||v_owner||'.'||v_sequence_name||' increment by 1';
                        execute immediate v_sql_stmt;
                    end if;
                else
                    exit;
                end if;
            end loop;
            close c2;
        else
            exit;
        end if;
    end loop;
    close c1;
end;
/

exit success;
