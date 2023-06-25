SET search_path TO public;

------------------------------------------------
-- Create tables
------------------------------------------------
CREATE TABLE bulk_customers
(bulk_customer_id NUMERIC NOT NULL,
sdt_customer_id NUMERIC(8),
version_number NUMERIC DEFAULT 0);

CREATE TABLE bulk_customer_applications
(bulk_customer_applications_id NUMERIC NOT NULL,
bulk_customer_id NUMERIC,
target_application_id NUMERIC,
customer_application_id VARCHAR(32),
version_number NUMERIC DEFAULT 0);

CREATE TABLE purge_job_audit
(audit_id SERIAL PRIMARY KEY,
created_date TIMESTAMP(6),
success NUMERIC);

CREATE TABLE purge_job_audit_messages
(audit_message_id SERIAL PRIMARY KEY,
audit_id NUMERIC,
created_date TIMESTAMP(6),
error_message VARCHAR(32));

CREATE TABLE bulk_submissions
(bulk_submission_id NUMERIC NOT NULL,
bulk_customer_id NUMERIC,
target_application_id NUMERIC,
service_request_id NUMERIC,
sdt_bulk_reference VARCHAR(29),
customer_reference VARCHAR(32),
created_date TIMESTAMP(6),
number_of_requests NUMERIC,
bulk_submission_status VARCHAR(20),
completed_date TIMESTAMP(6),
updated_date TIMESTAMP(6),
error_code VARCHAR(32),
error_text VARCHAR(1000),
version_number NUMERIC DEFAULT 0,
bulk_payload BYTEA);

CREATE TABLE error_logs
(error_log_id NUMERIC NOT NULL,
individual_request_id NUMERIC,
error_code VARCHAR(32),
created_date TIMESTAMP(6),
updated_date TIMESTAMP(6),
version_number NUMERIC DEFAULT 0,
error_text VARCHAR(1000));

CREATE TABLE error_messages
(error_message_id NUMERIC,
error_code VARCHAR(32),
error_text VARCHAR(1000),
error_description VARCHAR(2000),
version_number NUMERIC DEFAULT 0);

CREATE TABLE global_parameters
(global_parameter_id NUMERIC,
parameter_name VARCHAR(32),
parameter_value VARCHAR(32),
parameter_description VARCHAR(2000),
version_number NUMERIC DEFAULT 0);

CREATE TABLE individual_requests
(individual_request_id NUMERIC NOT NULL,
bulk_submission_id NUMERIC,
customer_request_ref VARCHAR(32),
request_status VARCHAR(32),
sdt_bulk_reference VARCHAR(29),
line_number NUMERIC,
sdt_request_reference VARCHAR(37),
created_date TIMESTAMP(6),
updated_date TIMESTAMP(6),
completed_date TIMESTAMP(6),
forwarding_attempts NUMERIC,
dead_letter VARCHAR(1),
internal_system_error VARCHAR(4000),
request_type VARCHAR(50),
version_number NUMERIC DEFAULT 0,
individual_payload BYTEA,
target_application_response BYTEA);

CREATE TABLE service_requests
(service_request_id NUMERIC NOT NULL,
request_payload BYTEA,
request_timestamp TIMESTAMP(6),
response_payload BYTEA,
response_timestamp TIMESTAMP(6),
request_type VARCHAR(32),
sdt_customer_id VARCHAR(32),
sdt_bulk_reference VARCHAR(29),
server_host_name VARCHAR(255),
version_number NUMERIC DEFAULT 0);

CREATE TABLE service_routings
(service_routings_id NUMERIC NOT NULL,
service_type_id NUMERIC,
target_application_id NUMERIC,
web_service_endpoint VARCHAR(255),
version_number NUMERIC DEFAULT 0);

CREATE TABLE service_types
(service_type_id NUMERIC NOT NULL,
service_type_name VARCHAR(50),
service_type_status VARCHAR(1),
service_type_description VARCHAR(2000),
version_number NUMERIC DEFAULT 0);

CREATE TABLE target_applications
(target_application_id NUMERIC NOT NULL,
target_application_code VARCHAR(4),
target_application_name VARCHAR(255),
version_number NUMERIC DEFAULT 0);

------------------------------------------------
-- Create indices
------------------------------------------------
CREATE INDEX bca_bulk_customer_id ON bulk_customer_applications (bulk_customer_id);
CREATE INDEX bca_target_application_i ON bulk_customer_applications (target_application_id);
CREATE INDEX bs_bulk_customer_id_i ON bulk_submissions (bulk_customer_id);
CREATE INDEX bs_sdt_bulk_reference_i ON bulk_submissions (sdt_bulk_reference);
CREATE INDEX bs_service_request_id_i ON bulk_submissions (service_request_id);
CREATE INDEX bs_target_application_id_i ON bulk_submissions (target_application_id);
CREATE UNIQUE INDEX bulk_customers_pk ON bulk_customers (bulk_customer_id);
CREATE UNIQUE INDEX bulk_customer_applications_pk ON bulk_customer_applications (bulk_customer_applications_id);
CREATE UNIQUE INDEX bulk_submissions_pk ON bulk_submissions (bulk_submission_id);
CREATE INDEX el_individual_request_id ON error_logs (individual_request_id);
CREATE UNIQUE INDEX error_logs_pk ON error_logs (error_log_id);
CREATE UNIQUE INDEX error_messages_pk ON error_messages (error_message_id);
CREATE UNIQUE INDEX global_parameters_pk ON global_parameters (global_parameter_id);
CREATE UNIQUE INDEX individual_requests_pk ON individual_requests (individual_request_id);
CREATE INDEX ir_bulk_reference_i ON individual_requests (sdt_bulk_reference);
CREATE INDEX ir_bulk_submission_id_i ON individual_requests (bulk_submission_id);
CREATE INDEX ir_lwr_customer_req_ref_cd_i ON individual_requests (LOWER(customer_request_ref), created_date);
CREATE INDEX ir_sdt_request_reference_i ON individual_requests (sdt_request_reference);
CREATE UNIQUE INDEX service_requests_pk ON service_requests (service_request_id);
CREATE UNIQUE INDEX service_routings_pk ON service_routings (service_routings_id);
CREATE UNIQUE INDEX service_types_pk ON service_types (service_type_id);
CREATE INDEX sr_service_type_i ON service_routings (service_type_id);
CREATE INDEX sr_target_application_i ON service_routings (target_application_id);
CREATE INDEX st_service_type_name ON service_types (service_type_name);
CREATE UNIQUE INDEX target_applications_pk ON target_applications (target_application_id);
CREATE INDEX ta_target_application_name ON target_applications (target_application_name);

------------------------------------------------
-- Create Primary Keys
------------------------------------------------
ALTER TABLE bulk_customers ADD CONSTRAINT bulk_customers_pk PRIMARY KEY USING INDEX bulk_customers_pk;
ALTER TABLE bulk_customer_applications ADD CONSTRAINT bulk_customer_applications_pk PRIMARY KEY USING INDEX bulk_customer_applications_pk;
ALTER TABLE bulk_submissions ADD CONSTRAINT bulk_submissions_pk PRIMARY KEY USING INDEX bulk_submissions_pk;
ALTER TABLE error_logs ADD CONSTRAINT error_logs_pk PRIMARY KEY USING INDEX error_logs_pk;
ALTER TABLE error_messages ADD CONSTRAINT error_messages_pk PRIMARY KEY USING INDEX error_messages_pk;
ALTER TABLE global_parameters ADD CONSTRAINT global_parameters_pk PRIMARY KEY USING INDEX global_parameters_pk;
ALTER TABLE individual_requests ADD CONSTRAINT individual_requests_pk PRIMARY KEY USING INDEX individual_requests_pk;
ALTER TABLE service_requests ADD CONSTRAINT service_requests_pk PRIMARY KEY USING INDEX service_requests_pk;
ALTER TABLE service_routings ADD CONSTRAINT service_routings_pk PRIMARY KEY USING INDEX service_routings_pk;
ALTER TABLE service_types ADD CONSTRAINT service_types_pk PRIMARY KEY USING INDEX service_types_pk;
ALTER TABLE target_applications ADD CONSTRAINT target_applications_pk PRIMARY KEY USING INDEX target_applications_pk;

------------------------------------------------
-- Create Check Constraints
------------------------------------------------
ALTER TABLE bulk_customer_applications ADD CONSTRAINT bca_cai_nn CHECK (customer_application_id IS NOT NULL);
ALTER TABLE bulk_customer_applications ADD CONSTRAINT bca_vn_nn CHECK (version_number IS NOT NULL);
ALTER TABLE bulk_customers ADD CONSTRAINT bc_sci_nn CHECK (sdt_customer_id IS NOT NULL);
ALTER TABLE bulk_customers ADD CONSTRAINT bc_vn_nn CHECK (version_number IS NOT NULL);
ALTER TABLE bulk_submissions ADD CONSTRAINT bs_bci_nn CHECK (bulk_customer_id IS NOT NULL);
ALTER TABLE bulk_submissions ADD CONSTRAINT bs_bp_nn CHECK (bulk_payload IS NOT NULL);
ALTER TABLE bulk_submissions ADD CONSTRAINT bs_bss_nn CHECK (bulk_submission_status IS NOT NULL);
ALTER TABLE bulk_submissions ADD CONSTRAINT bs_cd_nn CHECK (created_date IS NOT NULL);
ALTER TABLE bulk_submissions ADD CONSTRAINT bs_nor_nn CHECK (number_of_requests IS NOT NULL);
ALTER TABLE bulk_submissions ADD CONSTRAINT bs_sbr_nn CHECK (sdt_bulk_reference IS NOT NULL);
ALTER TABLE bulk_submissions ADD CONSTRAINT bs_tai_nn CHECK (target_application_id IS NOT NULL);
ALTER TABLE bulk_submissions ADD CONSTRAINT bs_vn_nn CHECK (version_number IS NOT NULL);
ALTER TABLE error_logs ADD CONSTRAINT el_cd_nn CHECK (created_date IS NOT NULL);
ALTER TABLE error_logs ADD CONSTRAINT el_ec_nn CHECK (error_code IS NOT NULL);
ALTER TABLE error_logs ADD CONSTRAINT el_et_nn CHECK (error_text IS NOT NULL);
ALTER TABLE error_logs ADD CONSTRAINT el_iri_nn CHECK (individual_request_id IS NOT NULL);
ALTER TABLE error_logs ADD CONSTRAINT el_vn_nn CHECK (version_number IS NOT NULL);
ALTER TABLE error_messages ADD CONSTRAINT em_ec_nn CHECK (error_code IS NOT NULL);
ALTER TABLE error_messages ADD CONSTRAINT em_ed_nn CHECK (error_description IS NOT NULL);
ALTER TABLE error_messages ADD CONSTRAINT em_et_nn CHECK (error_text IS NOT NULL);
ALTER TABLE error_messages ADD CONSTRAINT em_vn_nn CHECK (version_number IS NOT NULL);
ALTER TABLE global_parameters ADD CONSTRAINT gp_pn_nn CHECK (parameter_name  IS NOT NULL);
ALTER TABLE global_parameters ADD CONSTRAINT gp_pv_nn CHECK (parameter_value IS NOT NULL);
ALTER TABLE global_parameters ADD CONSTRAINT gp_vn_nn CHECK (version_number IS NOT NULL);
ALTER TABLE individual_requests ADD CONSTRAINT ir_bsi_nn CHECK (bulk_submission_id IS NOT NULL);
ALTER TABLE individual_requests ADD CONSTRAINT ir_cd_nn CHECK (created_date IS NOT NULL);
ALTER TABLE individual_requests ADD CONSTRAINT ir_dl_nn CHECK (dead_letter IS NOT NULL);
ALTER TABLE individual_requests ADD CONSTRAINT ir_ln_nn CHECK (line_number IS NOT NULL);
ALTER TABLE individual_requests ADD CONSTRAINT ir_rs_nn CHECK (request_status IS NOT NULL);
ALTER TABLE individual_requests ADD CONSTRAINT ir_sbr_nn CHECK (sdt_bulk_reference IS NOT NULL);
ALTER TABLE individual_requests ADD CONSTRAINT ir_srr_nn CHECK (sdt_request_reference IS NOT NULL);
ALTER TABLE individual_requests ADD CONSTRAINT ir_vn_nn CHECK (version_number IS NOT NULL);
ALTER TABLE service_requests ADD CONSTRAINT sre_rt_nn CHECK (request_timestamp IS NOT NULL);
ALTER TABLE service_routings ADD CONSTRAINT sr_vn_nn CHECK (version_number IS NOT NULL);
ALTER TABLE service_routings ADD CONSTRAINT sr_wse_nn CHECK (web_service_endpoint IS NOT NULL);
ALTER TABLE service_types ADD CONSTRAINT st_stn_nn CHECK (service_type_name IS NOT NULL);
ALTER TABLE service_types ADD CONSTRAINT st_sts_nn CHECK (service_type_status IS NOT NULL);
ALTER TABLE service_types ADD CONSTRAINT st_vn_nn CHECK (version_number IS NOT NULL);
ALTER TABLE target_applications ADD CONSTRAINT ta_tac_nn CHECK (target_application_code IS NOT NULL);
ALTER TABLE target_applications ADD CONSTRAINT ta_vn_nn CHECK (version_number IS NOT NULL);

------------------------------------------------
-- Create Referential constraints
------------------------------------------------
ALTER TABLE bulk_customer_applications ADD CONSTRAINT bca_bulk_customer_fk FOREIGN KEY (bulk_customer_id)
  REFERENCES bulk_customers (bulk_customer_id);
ALTER TABLE bulk_customer_applications ADD CONSTRAINT bca_target_application_fk FOREIGN KEY (target_application_id)
  REFERENCES target_applications (target_application_id);
ALTER TABLE bulk_submissions ADD CONSTRAINT bs_customer_id_fk FOREIGN KEY (bulk_customer_id)
  REFERENCES bulk_customers (bulk_customer_id);
ALTER TABLE bulk_submissions ADD CONSTRAINT bs_service_request_id_fk FOREIGN KEY (service_request_id)
  REFERENCES service_requests (service_request_id);
ALTER TABLE bulk_submissions ADD CONSTRAINT bs_target_application_fk FOREIGN KEY (target_application_id)
  REFERENCES target_applications (target_application_id);
ALTER TABLE error_logs ADD CONSTRAINT el_individual_request_fk FOREIGN KEY (individual_request_id)
  REFERENCES individual_requests (individual_request_id);
ALTER TABLE individual_requests ADD CONSTRAINT ir_bulk_submission_fk FOREIGN KEY (bulk_submission_id)
  REFERENCES bulk_submissions (bulk_submission_id);
ALTER TABLE service_routings ADD CONSTRAINT sr_service_type_fk FOREIGN KEY (service_type_id)
  REFERENCES service_types (service_type_id);
ALTER TABLE service_routings ADD CONSTRAINT sr_target_application_fk FOREIGN KEY (target_application_id)
  REFERENCES target_applications (target_application_id);

------------------------------------------------
-- Create Unique constraints for PUBLIC
------------------------------------------------
ALTER TABLE bulk_submissions ADD CONSTRAINT bs_sbr_uni UNIQUE (sdt_bulk_reference);
ALTER TABLE service_types ADD CONSTRAINT rt_rtn_uni UNIQUE (service_type_name);
ALTER TABLE target_applications ADD CONSTRAINT ta_tan_uni UNIQUE (target_application_name);

------------------------------------------------
-- Create Sequences for PUBLIC
------------------------------------------------
-- BIGINT/NUMERIC = 18 digits, but seq was formerly 27 digits! Consider type change
CREATE SEQUENCE bulk_cust_app_seq MINVALUE 1 MAXVALUE  999999999999999999 INCREMENT BY 1 START WITH 1 NO CYCLE;
CREATE SEQUENCE bulk_cust_seq MINVALUE 1 MAXVALUE 999999999999999999 INCREMENT BY 1 START WITH 1 NO CYCLE;
CREATE SEQUENCE bulk_sub_seq MINVALUE 1 MAXVALUE 999999999999999999 INCREMENT BY 1 START WITH 1 NO CYCLE;
CREATE SEQUENCE err_log_seq MINVALUE 1 MAXVALUE 999999999999999999 INCREMENT BY 1 START WITH 1 NO CYCLE;
CREATE SEQUENCE err_mesg_seq MINVALUE 1 MAXVALUE 999999999999999999 INCREMENT BY 1 START WITH 1 NO CYCLE;
CREATE SEQUENCE glb_par_seq MINVALUE 1 MAXVALUE 999999999 INCREMENT BY 1 START WITH 1 CYCLE;
CREATE SEQUENCE ind_req_seq MINVALUE 1 MAXVALUE 999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 2000 NO CYCLE;
CREATE SEQUENCE sdt_ref_seq MINVALUE 1 MAXVALUE 999999999 INCREMENT BY 1 START WITH 1 CYCLE;
CREATE SEQUENCE ser_rou_seq MINVALUE 1 MAXVALUE 999999999 INCREMENT BY 1 START WITH 1 CYCLE;
CREATE SEQUENCE ser_typ_seq MINVALUE 1 MAXVALUE 999999999 INCREMENT BY 1 START WITH 1 CYCLE;
CREATE SEQUENCE srv_req_seq MINVALUE 1 MAXVALUE 999999999999999999 INCREMENT BY 1 START WITH 1 NO CYCLE;
CREATE SEQUENCE tar_app_seq MINVALUE 1 MAXVALUE 999999999 INCREMENT BY 1 START WITH 1 CYCLE;

------------------------------------------------
-- Create Reference Data
------------------------------------------------
INSERT INTO global_parameters (global_parameter_id,parameter_name,parameter_value,parameter_description)
VALUES ( 1,'DATA_RETENTION_PERIOD',90,'Duration in days, to retain data in the tables subject to a prescribed purge');
INSERT INTO global_parameters (global_parameter_id,parameter_name,parameter_value,parameter_description)
VALUES ( 2,'TARGET_APP_TIMEOUT',15000,'Period in milliseconds, to wait for next re-try to reach the target application');
INSERT INTO global_parameters (global_parameter_id,parameter_name,parameter_value,parameter_description)
VALUES ( 3,'MAX_FORWARDING_ATTEMPTS',3,'Number of forwarding attempts made to transmit an individual request to target application');
INSERT INTO global_parameters (global_parameter_id,parameter_name,parameter_value,parameter_description)
VALUES ( 4,'MCOL_MAX_CONCURRENT_INDV_REQ',5,'Maximum number of concurrent Individual Requests that can be forwarded to MCOL');
INSERT INTO global_parameters (global_parameter_id,parameter_name,parameter_value,parameter_description)
VALUES ( 5,'MCOL_INDV_REQ_DELAY',10,'Time delay in milliseconds before processing the next Individual Request that can be forwarded to MCOL');
INSERT INTO global_parameters (global_parameter_id,parameter_name,parameter_value,parameter_description)
VALUES ( 6,'MCOL_MAX_CONCURRENT_QUERY_REQ',5,'Maximum number of concurrent Submit Query Requests that can be forwarded to MCOL');
INSERT INTO global_parameters (global_parameter_id,parameter_name,parameter_value,parameter_description)
VALUES ( 7,'CONTACT_DETAILS','tbc','Current contact details for outgoing SDT application messages to Bulk Customer System');
INSERT INTO global_parameters (global_parameter_id,parameter_name,parameter_value,parameter_description)
VALUES ( 8,'TARGET_APP_RESP_TIMEOUT',30000,'Period in milliseconds, read timeout to wait for response from target application');

INSERT INTO service_types (service_type_id,service_type_name,service_type_status,service_type_description)
VALUES (1,'SUBMIT_INDIVIDUAL','A', 'Submit individual request web service');
INSERT INTO service_types (service_type_id,service_type_name,service_type_status,service_type_description)
VALUES (2,'SUBMIT_QUERY','A', 'Submit query web service');

INSERT INTO target_applications (target_application_id,target_application_code,target_application_name)
VALUES (1,'MCOL','mcol live service');
INSERT INTO target_applications (target_application_id,target_application_code,target_application_name)
VALUES (2,'PCOL','pcol live service');
INSERT INTO target_applications (target_application_id,target_application_code,target_application_name)
VALUES (3,'C_MC','mcol commissioning service');
INSERT INTO target_applications (target_application_id,target_application_code,target_application_name)
VALUES (4,'C_PC','pcol commissioning service');

INSERT INTO service_routings (service_routings_id,service_type_id,target_application_id,web_service_endpoint)
VALUES (1,1,1,'http://localhost:8888/mcol-web-services/service');
INSERT INTO service_routings (service_routings_id,service_type_id,target_application_id,web_service_endpoint)
VALUES (2,2,1,'http://localhost:8888/mcol-web-services/service');

INSERT INTO error_messages (error_message_id,error_code,error_text,error_description)
VALUES (1,'SDT_INT_ERR','A system error has occurred. Please contact {0} for assistance.'
,'A system error has occured');
INSERT INTO error_messages (error_message_id,error_code,error_text,error_description)
VALUES (2,'CUST_NOT_SETUP',
'The Bulk Customer organisation is not setup to send Service Request messages to the {0}. Please contact {1} for assistance.',
'The SDT Customer ID for the Bulk Customer organisation is not recognised by SDT.');
INSERT INTO error_messages (error_message_id,error_code,error_text,error_description)
VALUES (3,'CUST_ID_INVALID',
'The Bulk Customer organisation does not have an SDT Customer ID set up. Please contact {1} for assistance.',
'The Bulk Customer organisation is recognised by the SDT Service, but is not set up to send a Service Request message to the specified Target Application.');
INSERT INTO error_messages (error_message_id,error_code,error_text,error_description)
VALUES (4,'DUP_CUST_FILEID','Duplicate User File Reference {0} supplied. This was previously used to submit a Bulk Request on {1} and the SDT Bulk Reference {2} was allocated.',
'A duplicate User File Reference is identified by SDT for the Bulk Customer.');
INSERT INTO error_messages (error_message_id,error_code,error_text,error_description)
VALUES (5,'REQ_COUNT_MISMATCH','Unexpected Total Number of Requests identified. {0} requested identified, {1} requests expected in Bulk Request {2}.',
'The Total Number of Requests identified by SDT does not match the Total Number of Requests expected (provided by the Submit Bulk Request).');
INSERT INTO error_messages (error_message_id,error_code,error_text,error_description)
VALUES (6,'BULK_REF_INVALID','There is no Bulk Request submission associated with your account for the supplied SDT Bulk Reference {0}.',
'The supplied SDT Bulk Reference is not listed against the Bulk Customers Bulk Submissions detail');
INSERT INTO error_messages (error_message_id,error_code,error_text,error_description)
VALUES (7,'TAR_APP_BUSY','The Target Application is currently busy and cannot process your Submit Query request. Please try again later.',
'SDT has reached the maximum number of concurrent Submit Query requests that can be forwarded to the Target Application for processing.');
INSERT INTO error_messages (error_message_id,error_code,error_text,error_description)
VALUES (8,'TAR_APP_ERROR','The system encountered a problem when processing your Submit Query request. Please try again later or contact {0} for assistance.',
'The Target Appliation does not send a response to SDT within the expected timescale, or an error message is received.');
INSERT INTO error_messages (error_message_id,error_code,error_text,error_description)
VALUES (9,'DUP_CUST_REFID','Duplicate User File Reference {0} supplied','A duplicate User File Reference is identified by SDT for the Bulk Customer.');
INSERT INTO error_messages (error_message_id,error_code,error_text,error_description)
VALUES (10,'NO_VALID_REQS','The submitted Bulk Request {0} does not contain valid individual Requests.',
'The Submit Bulk Request message does not contain Individual Requests deemed to be valid. The Individual Requests have all failed SDT format validation and been rejected and processing has been completed as far as possible.');
INSERT INTO error_messages (error_message_id,error_code,error_text,error_description)
VALUES (11,'DUPLD_CUST_REQID','Unique Request Identifier has been specified more than once within the originating Bulk Request.',
'SDT identifies that a Unique Request Identifier has been associated with more than one Individual Request within the same Bulk Request.');
INSERT INTO error_messages (error_message_id,error_code,error_text,error_description)
VALUES (12,'DUP_CUST_REQID','Duplicate Unique Request Identifier submitted {0}.','SDT identifies that a Unique Request Identifier has been associated with more than one Individual Request within the same Bulk Request.');
INSERT INTO error_messages (error_message_id,error_code,error_text,error_description)
VALUES (13,'REQ_NOT_ACK','Individual Request not acknowledged by Target Application. Please contact {0} for assistance.',
'The Target Application does not send back an acknowledgement response within the expected period, or returns a response indicating that there was an error with the transmission.');
INSERT INTO error_messages (error_message_id,error_code,error_text,error_description)
VALUES (14,'CUST_XML_ERR','Individual Request format could not be processed by the Target Application. Please check the data and resubmit the request, or contact {0} for assistance.',
'Client data has caused SOAP Fault error and rejected the request.');
