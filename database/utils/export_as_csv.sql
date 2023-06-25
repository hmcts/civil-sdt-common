whenever sqlerror exit failure
whenever oserror exit failure
set echo off ver off feed off pages 0
set serveroutput on
set linesize 4000
set trimspool on

define v_qualified_table_name='&1'
define v_target_path='&2'

spool &v_target_path/&v_qualified_table_name..csv

declare
    v_sql_stmt varchar2(4000);
    v_column_list varchar2(4000);
    v_select_list varchar2(4000);
    v_csv_data varchar2(4000);
    v_key_column_name varchar2(256);

    v_column_id all_tab_columns.column_id%type;
    v_column_name all_tab_columns.column_name%type;
    v_data_type all_tab_columns.data_type%type;

    is_windows number(1);

    cursor c1 is
        select column_id, column_name, data_type
        from all_tab_columns
        where rtrim(owner)||'.'||rtrim(table_name)=upper('&v_qualified_table_name')
        --and column_name<>'TIME_STAMP'
        order by column_id;

    cursor c3 is
        select con_cols.column_name
        from all_constraints cons, all_cons_columns con_cols
        where rtrim(con_cols.owner)||'.'||rtrim(con_cols.table_name)=upper('&v_qualified_table_name')
        and cons.constraint_type = 'P'
        and cons.constraint_name = con_cols.constraint_name
        and cons.owner = con_cols.owner
        order by con_cols.table_name, con_cols.position;

    type c2type is ref cursor;
    c2 c2type;
begin
    select case when instr(dbms_utility.port_string,'WIN_NT') <> 0 then 1 else 0 end
    into is_windows
    from dual;

    v_column_list:=''; -- used for header
    v_select_list:=''; -- used for single column select
    open c1;
    loop
        fetch c1 into v_column_id,v_column_name,v_data_type;
        if c1%found
        then
            v_column_list:=v_column_list||v_column_name||',';
            if v_data_type in ('FLOAT','NUMBER')
            then
                v_select_list:=v_select_list||'to_char('||v_column_name||')';
            elsif v_data_type in ('DATE')
            then
                v_select_list:=v_select_list||'to_char('||v_column_name||',''DD-MM-YYYY'')';
            else
                v_select_list:=v_select_list||'''"''||replace('||v_column_name||',''"'',''""'')||''"''';
            end if;
            v_select_list:=v_select_list||'||'',''||';
        else
            exit;
        end if;
    end loop;
    close c1;

    v_column_list:=substr(v_column_list,1,length(v_column_list)-1);
    dbms_output.put_line(v_column_list||chr(13));

    -- Get name of primary key column
    open c3;
    fetch c3 into v_key_column_name;
    close c3;

    v_select_list:=substr(v_select_list,1,length(v_select_list)-7);
    if v_key_column_name is not null
    then
        v_sql_stmt:='select '||v_select_list||' from &v_qualified_table_name order by '||v_key_column_name;
    else
      v_sql_stmt:='select '||v_select_list||' from &v_qualified_table_name';
    end if;

    open c2 for v_sql_stmt;
    loop
        fetch c2 into v_csv_data;
        if c2%found
        then
            if is_windows=1
            then
                dbms_output.put_line(v_csv_data);
            else
                dbms_output.put_line(v_csv_data||chr(13));
            end if;
        else
            exit;
        end if;
    end loop;
    close c2;
end;
/

spool off
exit success;
