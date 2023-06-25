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
-- $Id: sql_wrapper.sql 18646 2014-03-27 12:12:53Z williamsdr $
-- $LastChangedRevision: 18646 $
-- $LastChangedDate: 2014-03-27 12:12:53 +0000 (Thu, 27 Mar 2014) $
-- $LastChangedBy: williamsdr $
-- =====================================================================================================================

--
-- Description: A wrapper script for running all SQL scripts inside.
-- Usage: sql_wrapper.sql <sql_script>
--
define v_script='&1';

whenever oserror exit 2;
whenever sqlerror exit failure;
set serveroutput on size 10000 echo on lin 1000
alter session set recyclebin=off;
@'&v_script';
exit success;
