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
-- $Id: compile_invalid_objects.sql 14671 2010-08-17 13:01:16Z barrettmr $
-- $LastChangedRevision: 14671 $
-- $LastChangedDate: 2010-08-17 14:01:16 +0100 (Tue, 17 Aug 2010) $
-- $LastChangedBy: barrettmr $
-- =====================================================================================================================

whenever sqlerror exit failure
set serveroutput on

declare
    cursor c1 is
        select owner, object_name, object_type
        from dba_objects
        where owner in ('PUBLIC')
        and status='INVALID'
        and object_type in ('VIEW','SYNONYM','PACKAGE','PACKAGE BODY','PROCEDURE','FUNCTION','TRIGGER')
        order by 3;

    v_owner all_objects.owner%type;
    v_object_name all_objects.object_name%type;
    v_object_type all_objects.object_type%type;

    v_sql_stmt varchar(256);
begin
    loop
        open c1;
        fetch c1 into v_owner, v_object_name, v_object_type;
        exit when c1%NOTFOUND;
        loop
            begin
                if v_object_type='PACKAGE BODY'
                then
                    v_sql_stmt:='alter package '||v_owner||'.'||v_object_name||' compile body';
                else
                    v_sql_stmt:='alter '||v_object_type||' '||v_owner||'.'||v_object_name||' compile';
                end if;
                dbms_output.put_line(v_sql_stmt);
                execute immediate v_sql_stmt;
            exception
                when others then
                    raise_application_error(-20000, 'An unexpected error occurred when attempting to compile an object');
            end;

            fetch c1 into v_owner, v_object_name, v_object_type;
            exit when c1%NOTFOUND;
        end loop;
        close c1;
    end loop;
end;
/

exit success;
