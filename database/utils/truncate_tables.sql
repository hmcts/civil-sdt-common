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
-- $Id: truncate_tables.sql 20557 2016-03-15 16:50:12Z williamsdr $
-- $LastChangedRevision: 20557 $
-- $LastChangedDate: 2016-03-15 16:50:12 +0000 (Tue, 15 Mar 2016) $
-- $LastChangedBy: williamsdr $
-- =====================================================================================================================

-- This script truncates all the tables in a given schema
-- constraints for a given owner.

whenever sqlerror exit failure
set serveroutput on

declare
    cursor c1 is
        select owner, table_name
        from all_tables
        where owner in ('PUBLIC');

    v_owner all_tables.owner%type;
    v_table all_tables.table_name%type;
    v_sql_stmt varchar(256);

begin
    open c1;
    loop
        fetch c1 into v_owner, v_table;
        if c1%found then
            v_sql_stmt:='truncate table '||v_owner||'.'||v_table;
            dbms_output.put_line(v_sql_stmt);
            execute immediate v_sql_stmt;
        else
            exit;
        end if;
    end loop;
    close c1;

end;
/

exit success;
