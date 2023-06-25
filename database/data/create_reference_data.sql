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

-- This script populates reference data.

whenever sqlerror exit failure
set serveroutput on

alter session set current_schema=sdt_user;

INSERT INTO "PUBLIC"."GLOBAL_PARAMETERS" ( global_parameter_id,parameter_name,parameter_value,parameter_description)
VALUES ( 1,'DATA_RETENTION_PERIOD',90
,'Duration in days, to retain data in the tables subject to a prescribed purge');

INSERT INTO "PUBLIC"."GLOBAL_PARAMETERS" ( global_parameter_id,parameter_name,parameter_value,parameter_description)
VALUES ( 2,'TARGET_APP_TIMEOUT',15000
,'Period in milliseconds, to wait for next re-try to reach the target application');

INSERT INTO "PUBLIC"."GLOBAL_PARAMETERS" ( global_parameter_id,parameter_name,parameter_value,parameter_description)
VALUES ( 3,'MAX_FORWARDING_ATTEMPTS',3
,'Number of forwarding attempts made to transmit an individual request to target application');

INSERT INTO "PUBLIC"."GLOBAL_PARAMETERS" ( global_parameter_id,parameter_name,parameter_value,parameter_description)
VALUES ( 4,'MCOL_MAX_CONCURRENT_INDV_REQ',5
,'Maximum number of concurrent Individual Requests that can be forwarded to MCOL');

INSERT INTO "PUBLIC"."GLOBAL_PARAMETERS" ( global_parameter_id,parameter_name,parameter_value,parameter_description)
VALUES ( 5,'MCOL_INDV_REQ_DELAY',10
,'Time delay in milliseconds before processing the next Individual Request that can be forwarded to MCOL');

INSERT INTO "PUBLIC"."GLOBAL_PARAMETERS" ( global_parameter_id,parameter_name,parameter_value,parameter_description)
VALUES ( 6,'MCOL_MAX_CONCURRENT_QUERY_REQ',5
,'Maximum number of concurrent Submit Query Requests that can be forwarded to MCOL');

INSERT INTO "PUBLIC"."GLOBAL_PARAMETERS" ( global_parameter_id,parameter_name,parameter_value,parameter_description)
VALUES ( 7,'CONTACT_DETAILS','tbc'
,'Current contact details for outgoing SDT application messages to Bulk Customer System');

INSERT INTO "PUBLIC"."GLOBAL_PARAMETERS" ( global_parameter_id,parameter_name,parameter_value,parameter_description)
VALUES ( 8,'TARGET_APP_RESP_TIMEOUT',30000
,'Period in milliseconds, read timeout to wait for response from target application');


INSERT INTO "PUBLIC"."SERVICE_TYPES" (service_type_id,service_type_name,service_type_status, service_type_description)
VALUES (1,'SUBMIT_INDIVIDUAL','A', 'Submit individual request web service');
INSERT INTO "PUBLIC"."SERVICE_TYPES" (service_type_id,service_type_name,service_type_status, service_type_description)
VALUES (2,'SUBMIT_QUERY','A', 'Submit query web service');


INSERT INTO "PUBLIC"."TARGET_APPLICATIONS" ( target_application_id,target_application_code,target_application_name)
VALUES (1,'MCOL','mcol live service');
INSERT INTO "PUBLIC"."TARGET_APPLICATIONS" ( target_application_id,target_application_code,target_application_name)
VALUES (2,'PCOL','pcol live service');
INSERT INTO "PUBLIC"."TARGET_APPLICATIONS" ( target_application_id,target_application_code,target_application_name)
VALUES (3,'C_MC','mcol commissioning service');
INSERT INTO "PUBLIC"."TARGET_APPLICATIONS" ( target_application_id,target_application_code,target_application_name)
VALUES (4,'C_PC','pcol commissioning service');

INSERT INTO "PUBLIC"."SERVICE_ROUTINGS" (service_routings_id, service_type_id,target_application_id,web_service_endpoint)
VALUES (1,1,1,'http://localhost:8888/mcol-web-services/service');
INSERT INTO "PUBLIC"."SERVICE_ROUTINGS" (service_routings_id, service_type_id,target_application_id,web_service_endpoint)
VALUES (2,2,1,'http://localhost:8888/mcol-web-services/service');

INSERT INTO "PUBLIC"."ERROR_MESSAGES" (error_message_id,error_code,error_text,error_description)
VALUES (1,'SDT_INT_ERR','A system error has occurred. Please contact {0} for assistance.'
,'A system error has occured');
INSERT INTO "PUBLIC"."ERROR_MESSAGES" (error_message_id,error_code,error_text,error_description)
VALUES (2,'CUST_NOT_SETUP'
,'The Bulk Customer organisation is not setup to send Service Request messages to the {0}. Please contact {1} for assistance.'
,'The SDT Customer ID for the Bulk Customer organisation is not recognised by SDT.');
INSERT INTO "PUBLIC"."ERROR_MESSAGES" (error_message_id,error_code,error_text,error_description)
VALUES (3,'CUST_ID_INVALID'
,'The Bulk Customer organisation does not have an SDT Customer ID set up. Please contact {1} for assistance.'
,'The Bulk Customer organisation is recognised by the SDT Service, but is not set up to send a Service Request message to the specified Target Application.');
INSERT INTO "PUBLIC"."ERROR_MESSAGES" (error_message_id,error_code,error_text,error_description)
VALUES (4,'DUP_CUST_FILEID'
,'Duplicate User File Reference {0} supplied. This was previously used to submit a Bulk Request on {1} and the SDT Bulk Reference {2} was allocated.'
,'A duplicate User File Reference is identified by SDT for the Bulk Customer.');
INSERT INTO "PUBLIC"."ERROR_MESSAGES" (error_message_id,error_code,error_text,error_description)
VALUES (5,'REQ_COUNT_MISMATCH'
,'Unexpected Total Number of Requests identified. {0} requested identified, {1} requests expected in Bulk Request {2}.'
,'The Total Number of Requests identified by SDT does not match the Total Number of Requests expected (provided by the Submit Bulk Request).');
INSERT INTO "PUBLIC".error_messages (error_message_id,error_code,error_text,error_description)
VALUES (6,'BULK_REF_INVALID','There is no Bulk Request submission associated with your account for the supplied SDT Bulk Reference {0}.'
,'The supplied SDT Bulk Reference is not listed against the Bulk Customers Bulk Submissions detail');
INSERT INTO "PUBLIC"."ERROR_MESSAGES" (error_message_id,error_code,error_text,error_description)
VALUES (7,'TAR_APP_BUSY','The Target Application is currently busy and cannot process your Submit Query request. Please try again later.'
,'SDT has reached the maximum number of concurrent Submit Query requests that can be forwarded to the Target Application for processing.');
INSERT INTO "PUBLIC"."ERROR_MESSAGES" (error_message_id,error_code,error_text,error_description)
VALUES (8,'TAR_APP_ERROR'
,'The system encountered a problem when processing your Submit Query request. Please try again later or contact {0} for assistance.'
,'The Target Appliation does not send a response to SDT within the expected timescale, or an error message is received.');
INSERT INTO "PUBLIC"."ERROR_MESSAGES" (error_message_id,error_code,error_text,error_description)
VALUES (9,'DUP_CUST_REFID','Duplicate User File Reference {0} supplied'
,'A duplicate User File Reference is identified by SDT for the Bulk Customer.');
INSERT INTO "PUBLIC"."ERROR_MESSAGES" (error_message_id,error_code,error_text,error_description)
VALUES (10,'NO_VALID_REQS','The submitted Bulk Request {0} does not contain valid individual Requests.'
,'The Submit Bulk Request message does not contain Individual Requests deemed to be valid. The Individual Requests have all failed SDT format validation and been rejected and processing has been completed as far as possible.');
INSERT INTO "PUBLIC"."ERROR_MESSAGES" (error_message_id,error_code,error_text,error_description)
VALUES (11,'DUPLD_CUST_REQID','Unique Request Identifier has been specified more than once within the originating Bulk Request.'
,'SDT identifies that a Unique Request Identifier has been associated with more than one Individual Request within the same Bulk Request.');
INSERT INTO "PUBLIC"."ERROR_MESSAGES" (error_message_id,error_code,error_text,error_description)
VALUES (12,'DUP_CUST_REQID','Duplicate Unique Request Identifier submitted {0}.'
,'SDT identifies that a Unique Request Identifier has been associated with more than one Individual Request within the same Bulk Request.');
INSERT INTO "PUBLIC"."ERROR_MESSAGES" (error_message_id,error_code,error_text,error_description)
VALUES (13,'REQ_NOT_ACK','Individual Request not acknowledged by Target Application. Please contact {0} for assistance.'
,'The Target Application does not send back an acknowledgement response within the expected period, or returns a response indicating that there was an error with the transmission.');
INSERT INTO "PUBLIC"."ERROR_MESSAGES" (error_message_id,error_code,error_text,error_description)
VALUES (14,'CUST_XML_ERR','Individual Request format could not be processed by the Target Application. Please check the data and resubmit the request, or contact {0} for assistance.'
,'Client data has caused SOAP Fault error and rejected the request.');

COMMIT;
