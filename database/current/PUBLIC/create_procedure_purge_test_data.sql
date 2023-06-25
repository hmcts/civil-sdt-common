SET search_path TO PUBLIC;
TRUNCATE error_logs CASCADE;
TRUNCATE individual_requests CASCADE;
TRUNCATE bulk_submissions CASCADE;
TRUNCATE service_requests CASCADE;
TRUNCATE bulk_customers CASCADE;


INSERT INTO service_requests(
	service_request_id, request_payload, request_timestamp, response_payload, response_timestamp, request_type, sdt_customer_id, sdt_bulk_reference, server_host_name, version_number)
	VALUES (101, 'test 1', '2022-11-17 09:01:02', 'unknown payload 1', '2022-11-17 09:02:03', 'type1', 901, 'REF18', 'www.host1.com', 1);
INSERT INTO service_requests(
	service_request_id, request_payload, request_timestamp, response_payload, response_timestamp, request_type, sdt_customer_id, sdt_bulk_reference, server_host_name, version_number)
	VALUES (102, 'test 2', '2022-11-18 10:02:03', 'unknown payload 2', '2022-11-18 10:03:04', 'type2', 902, 'REF18', 'www.host2.com', 1);
INSERT INTO service_requests(
	service_request_id, request_payload, request_timestamp, response_payload, response_timestamp, request_type, sdt_customer_id, sdt_bulk_reference, server_host_name, version_number)
	VALUES (103, 'test 3', '2022-11-19 11:03:04', 'unknown payload 3', '2022-11-19 11:04:05', 'type3', 903, 'REF18', 'www.host3.com', 1);
INSERT INTO service_requests(
	service_request_id, request_payload, request_timestamp, response_payload, response_timestamp, request_type, sdt_customer_id, sdt_bulk_reference, server_host_name, version_number)
	VALUES (104, 'test 4', '2022-11-20 12:04:05', 'unknown payload 4', '2022-11-20 12:11:13', 'type4', 904, 'REF18', 'www.host4.com', 1);
INSERT INTO service_requests(
	service_request_id, request_payload, request_timestamp, response_payload, response_timestamp, request_type, sdt_customer_id, sdt_bulk_reference, server_host_name, version_number)
	VALUES (105, 'test 5', '2022-11-21 12:04:05', 'unknown payload 5', '2022-11-21 12:11:13', 'type4', 904, 'REF18', 'www.host4.com', 1);
INSERT INTO service_requests(
	service_request_id, request_payload, request_timestamp, response_payload, response_timestamp, request_type, sdt_customer_id, sdt_bulk_reference, server_host_name, version_number)
	VALUES (106, 'test 6', '2022-11-22 09:01:02', 'unknown payload 6', '2022-11-22 09:02:03', 'type1', 901, 'REF18', 'www.host1.com', 1);
INSERT INTO service_requests(
	service_request_id, request_payload, request_timestamp, response_payload, response_timestamp, request_type, sdt_customer_id, sdt_bulk_reference, server_host_name, version_number)
	VALUES (107, 'test 7', '2022-11-25 10:02:03', 'unknown payload 7', '2022-11-25 10:03:04', 'type2', 902, 'REF18', 'www.host2.com', 1);
INSERT INTO service_requests(
	service_request_id, request_payload, request_timestamp, response_payload, response_timestamp, request_type, sdt_customer_id, sdt_bulk_reference, server_host_name, version_number)
	VALUES (108, 'test 8', '2022-11-26 10:02:03', 'unknown payload 8', '2022-11-26 10:03:04', 'type2', 902, 'REF18', 'www.host2.com', 1);
INSERT INTO service_requests(
	service_request_id, request_payload, request_timestamp, response_payload, response_timestamp, request_type, sdt_customer_id, sdt_bulk_reference, server_host_name, version_number)
	VALUES (109, 'test 9', '2022-11-27 10:02:03', 'unknown payload 9', '2022-11-27 10:03:04', 'type2', 902, 'REF18', 'www.host2.com', 1);

INSERT INTO bulk_customers(
	bulk_customer_id, sdt_customer_id, version_number)
	VALUES (201, 901, 1);

INSERT INTO bulk_customers(
	bulk_customer_id, sdt_customer_id, version_number)
	VALUES (202, 902, 1);


INSERT INTO bulk_submissions(
	bulk_submission_id, bulk_customer_id, target_application_id, service_request_id, sdt_bulk_reference, customer_reference, created_date, number_of_requests, bulk_submission_status, completed_date, updated_date, error_code, error_text, version_number, bulk_payload)
	VALUES (301, 201, 1, 101, 'BR001', 'CR001', '2022-11-17 06:30:45', 4, 'STATUS1', '2022-11-17 11:12:13', '2022-11-17 11:29:52', 'ERR23', 'Error message 3', 1, 'Payload 3355');
INSERT INTO bulk_submissions(
	bulk_submission_id, bulk_customer_id, target_application_id, service_request_id, sdt_bulk_reference, customer_reference, created_date, number_of_requests, bulk_submission_status, completed_date, updated_date, error_code, error_text, version_number, bulk_payload)
	VALUES (302, 201, 1, 102, 'BR002', 'CR002', '2022-11-18 06:33:48', 4, 'STATUS2', '2022-11-18 11:15:24', '2022-11-18 11:24:45', 'ERR23', 'Error message 4', 1, 'Payload 3356');
INSERT INTO bulk_submissions(
	bulk_submission_id, bulk_customer_id, target_application_id, service_request_id, sdt_bulk_reference, customer_reference, created_date, number_of_requests, bulk_submission_status, completed_date, updated_date, error_code, error_text, version_number, bulk_payload)
	VALUES (303, 201, 1, 103, 'BR003', 'CR002', '2022-11-18 06:33:48', 4, 'STATUS2', '2022-11-18 11:15:24', '2022-11-18 11:24:45', 'ERR23', 'Error message 4', 1, 'Payload 3356');
INSERT INTO bulk_submissions(
	bulk_submission_id, bulk_customer_id, target_application_id, service_request_id, sdt_bulk_reference, customer_reference, created_date, number_of_requests, bulk_submission_status, completed_date, updated_date, error_code, error_text, version_number, bulk_payload)
	VALUES (304, 201, 1, 101, 'BR004', 'CR003', '2022-11-19 06:40:53', 4, 'STATUS3', '2022-11-19 11:20:36', '2022-11-19 11:38:52', 'ERR23', 'Error message 5', 1, 'Payload 3357');
INSERT INTO bulk_submissions(
	bulk_submission_id, bulk_customer_id, target_application_id, service_request_id, sdt_bulk_reference, customer_reference, created_date, number_of_requests, bulk_submission_status, completed_date, updated_date, error_code, error_text, version_number, bulk_payload)
	VALUES (305, 201, 1, 101, 'BR005', 'CR003', '2022-11-19 06:40:53', 4, 'STATUS3', '2022-11-19 11:20:36', '2022-11-19 11:38:52', 'ERR23', 'Error message 5', 1, 'Payload 3357');
INSERT INTO bulk_submissions(
	bulk_submission_id, bulk_customer_id, target_application_id, service_request_id, sdt_bulk_reference, customer_reference, created_date, number_of_requests, bulk_submission_status, completed_date, updated_date, error_code, error_text, version_number, bulk_payload)
	VALUES (306, 201, 1, 101, 'BR006', 'CR004', '2022-11-19 06:45:57', 4, 'STATUS4', '2022-11-19 11:26:45', '2022-11-19 11:39:57', 'ERR23', 'Error message 6', 1, 'Payload 3358');
INSERT INTO bulk_submissions(
	bulk_submission_id, bulk_customer_id, target_application_id, service_request_id, sdt_bulk_reference, customer_reference, created_date, number_of_requests, bulk_submission_status, completed_date, updated_date, error_code, error_text, version_number, bulk_payload)
	VALUES (307, 201, 1, 103, 'BR007', 'CR004', '2022-11-19 06:45:57', 4, 'STATUS4', '2022-11-19 11:26:45', '2022-11-19 11:39:57', 'ERR23', 'Error message 6', 1, 'Payload 3358');
INSERT INTO bulk_submissions(
	bulk_submission_id, bulk_customer_id, target_application_id, service_request_id, sdt_bulk_reference, customer_reference, created_date, number_of_requests, bulk_submission_status, completed_date, updated_date, error_code, error_text, version_number, bulk_payload)
	VALUES (308, 201, 1, 104, 'BR008', 'CR005', '2022-11-18 06:45:57', 4, 'STATUS4', '2022-11-19 11:26:45', '2022-11-19 11:39:57', 'ERR23', 'Error message 6', 1, 'Payload 3358');
INSERT INTO bulk_submissions(
	bulk_submission_id, bulk_customer_id, target_application_id, service_request_id, sdt_bulk_reference, customer_reference, created_date, number_of_requests, bulk_submission_status, completed_date, updated_date, error_code, error_text, version_number, bulk_payload)
	VALUES (309, 201, 1, 105, 'BR009', 'CR005', '2022-11-18 06:45:57', 4, 'STATUS4', '2022-11-19 11:26:45', '2022-11-19 11:39:57', 'ERR23', 'Error message 6', 1, 'Payload 3358');
INSERT INTO bulk_submissions(
	bulk_submission_id, bulk_customer_id, target_application_id, service_request_id, sdt_bulk_reference, customer_reference, created_date, number_of_requests, bulk_submission_status, completed_date, updated_date, error_code, error_text, version_number, bulk_payload)
	VALUES (310, 202, 1, 106, 'BR010', 'CR006', '2022-11-23 06:30:45', 4, 'STATUS1', '2022-11-23 11:12:13', '2022-11-23 11:29:52', 'ERR23', 'Error message 7', 1, 'Payload 3355');
INSERT INTO bulk_submissions(
	bulk_submission_id, bulk_customer_id, target_application_id, service_request_id, sdt_bulk_reference, customer_reference, created_date, number_of_requests, bulk_submission_status, completed_date, updated_date, error_code, error_text, version_number, bulk_payload)
	VALUES (311, 202, 1, 107, 'BR011', 'CR006', '2022-11-23 06:30:45', 4, 'STATUS1', '2022-11-23 11:12:13', '2022-11-23 11:29:52', 'ERR23', 'Error message 7', 1, 'Payload 3355');
INSERT INTO bulk_submissions(
	bulk_submission_id, bulk_customer_id, target_application_id, service_request_id, sdt_bulk_reference, customer_reference, created_date, number_of_requests, bulk_submission_status, completed_date, updated_date, error_code, error_text, version_number, bulk_payload)
	VALUES (312, 202, 1, 105, 'BR012', 'CR007', '2022-11-23 06:33:48', 4, 'STATUS2', '2022-11-23 11:15:24', '2022-11-23 11:24:45', 'ERR23', 'Error message 8', 1, 'Payload 3356');
INSERT INTO bulk_submissions(
	bulk_submission_id, bulk_customer_id, target_application_id, service_request_id, sdt_bulk_reference, customer_reference, created_date, number_of_requests, bulk_submission_status, completed_date, updated_date, error_code, error_text, version_number, bulk_payload)
	VALUES (313, 202, 1, 106, 'BR013', 'CR008', '2022-11-26 06:40:53', 4, 'STATUS3', '2022-11-26 11:20:36', '2022-11-26 11:38:52', 'ERR23', 'Error message 9', 1, 'Payload 3357');
INSERT INTO bulk_submissions(
	bulk_submission_id, bulk_customer_id, target_application_id, service_request_id, sdt_bulk_reference, customer_reference, created_date, number_of_requests, bulk_submission_status, completed_date, updated_date, error_code, error_text, version_number, bulk_payload)
	VALUES (314, 202, 1, 106, 'BR014', 'CR009', '2022-11-27 06:45:57', 4, 'STATUS4', '2022-11-27 11:26:45', '2022-11-27 11:39:57', 'ERR23', 'Error message 10', 1, 'Payload 3358');

INSERT INTO individual_requests(
	individual_request_id, bulk_submission_id, customer_request_ref, request_status, sdt_bulk_reference, line_number, sdt_request_reference, created_date, updated_date, completed_date, forwarding_attempts, dead_letter, internal_system_error, request_type, version_number, individual_payload, target_application_response)
	VALUES (401, 301, 'CR101', 'STATUS3', 'BREF11', 1, 'RREF001', '2022-11-18 14:35:45', '2022-11-18 14:35:56', '2022-11-18 14:53:56', 2, 'N', 'Int Syst Err', 'ReqType1', 1, 'Ind pay 1', 'Targ app 1');
INSERT INTO individual_requests(
	individual_request_id, bulk_submission_id, customer_request_ref, request_status, sdt_bulk_reference, line_number, sdt_request_reference, created_date, updated_date, completed_date, forwarding_attempts, dead_letter, internal_system_error, request_type, version_number, individual_payload, target_application_response)
	VALUES (402, 301, 'CR101', 'STATUS2', 'BREF12', 2, 'RREF002', '2022-11-18 14:38:47', '2022-11-18 14:39:56', '2022-11-18 14:52:45', 2, 'N', 'Int Syst Err', 'ReqType1', 1, 'Ind pay 2', 'Targ app 2');
INSERT INTO individual_requests(
	individual_request_id, bulk_submission_id, customer_request_ref, request_status, sdt_bulk_reference, line_number, sdt_request_reference, created_date, updated_date, completed_date, forwarding_attempts, dead_letter, internal_system_error, request_type, version_number, individual_payload, target_application_response)
	VALUES (403, 301, 'CR101', 'STATUS4', 'BREF13', 3, 'RREF003', '2022-11-18 14:43:49', '2022-11-18 14:46:59', '2022-11-18 14:50:34', 2, 'N', 'Int Syst Err', 'ReqType1', 1, 'Ind pay 3', 'Targ app 3');
INSERT INTO individual_requests(
	individual_request_id, bulk_submission_id, customer_request_ref, request_status, sdt_bulk_reference, line_number, sdt_request_reference, created_date, updated_date, completed_date, forwarding_attempts, dead_letter, internal_system_error, request_type, version_number, individual_payload, target_application_response)
	VALUES (404, 301, 'CR101', 'STATUS1', 'BREF14', 4, 'RREF004', '2022-11-18 14:48:53', '2022-11-18 14:57:34', '2022-11-18 14:58:21', 2, 'N', 'Int Syst Err', 'ReqType1', 1, 'Ind pay 4', 'Targ app 4');
INSERT INTO individual_requests(
	individual_request_id, bulk_submission_id, customer_request_ref, request_status, sdt_bulk_reference, line_number, sdt_request_reference, created_date, updated_date, completed_date, forwarding_attempts, dead_letter, internal_system_error, request_type, version_number, individual_payload, target_application_response)
	VALUES (405, 305, 'CR101', 'STATUS3', 'BREF11', 1, 'RREF005', '2022-11-23 14:35:45', '2022-11-23 14:35:56', '2022-11-23 14:53:56', 2, 'N', 'Int Syst Err', 'ReqType1', 1, 'Ind pay 1', 'Targ app 1');
INSERT INTO individual_requests(
	individual_request_id, bulk_submission_id, customer_request_ref, request_status, sdt_bulk_reference, line_number, sdt_request_reference, created_date, updated_date, completed_date, forwarding_attempts, dead_letter, internal_system_error, request_type, version_number, individual_payload, target_application_response)
	VALUES (406, 306, 'CR101', 'STATUS2', 'BREF12', 2, 'RREF006', '2022-11-26 14:38:47', '2022-11-26 14:39:56', '2022-11-23 14:52:45', 2, 'N', 'Int Syst Err', 'ReqType1', 1, 'Ind pay 2', 'Targ app 2');
INSERT INTO individual_requests(
	individual_request_id, bulk_submission_id, customer_request_ref, request_status, sdt_bulk_reference, line_number, sdt_request_reference, created_date, updated_date, completed_date, forwarding_attempts, dead_letter, internal_system_error, request_type, version_number, individual_payload, target_application_response)
	VALUES (407, 307, 'CR101', 'STATUS4', 'BREF13', 3, 'RREF007', '2022-11-27 14:43:49', '2022-11-27 14:46:59', '2022-11-23 14:50:34', 2, 'N', 'Int Syst Err', 'ReqType1', 1, 'Ind pay 3', 'Targ app 3');
INSERT INTO individual_requests(
	individual_request_id, bulk_submission_id, customer_request_ref, request_status, sdt_bulk_reference, line_number, sdt_request_reference, created_date, updated_date, completed_date, forwarding_attempts, dead_letter, internal_system_error, request_type, version_number, individual_payload, target_application_response)
	VALUES (408, 308, 'CR101', 'STATUS1', 'BREF14', 4, 'RREF008', '2022-11-28 14:48:53', '2022-11-28 14:57:34', '2022-11-23 14:58:21', 2, 'N', 'Int Syst Err', 'ReqType1', 1, 'Ind pay 4', 'Targ app 4');

INSERT INTO error_logs(
	error_log_id, individual_request_id, error_code, created_date, updated_date, version_number, error_text)
	VALUES (501, 401, 'ERR01', '2022-11-18 14:35:45', '2022-11-18 14:35:45', 1, 'Error text 1');
INSERT INTO error_logs(
	error_log_id, individual_request_id, error_code, created_date, updated_date, version_number, error_text)
	VALUES (502, 402, 'ERR02', '2022-11-18 14:38:47', '2022-11-18 14:38:47', 1, 'Error text 2');
INSERT INTO error_logs(
	error_log_id, individual_request_id, error_code, created_date, updated_date, version_number, error_text)
	VALUES (503, 403, 'ERR03', '2022-11-19 14:43:49', '2022-11-19 14:43:49', 1, 'Error text 3');
INSERT INTO error_logs(
	error_log_id, individual_request_id, error_code, created_date, updated_date, version_number, error_text)
	VALUES (504, 404, 'ERR04', '2022-11-19 14:48:53', '2022-11-19 14:48:53', 1, 'Error text 4');
INSERT INTO error_logs(
	error_log_id, individual_request_id, error_code, created_date, updated_date, version_number, error_text)
	VALUES (505, 405, 'ERR01', '2022-11-20 14:35:45', '2022-11-20 14:35:45', 1, 'Error text 5');
INSERT INTO error_logs(
	error_log_id, individual_request_id, error_code, created_date, updated_date, version_number, error_text)
	VALUES (506, 406, 'ERR02', '2022-11-21 14:38:47', '2022-11-21 14:38:47', 1, 'Error text 6');
INSERT INTO error_logs(
	error_log_id, individual_request_id, error_code, created_date, updated_date, version_number, error_text)
	VALUES (507, 407, 'ERR03', '2022-11-22 14:43:49', '2022-11-22 14:43:49', 1, 'Error text 7');
INSERT INTO error_logs(
	error_log_id, individual_request_id, error_code, created_date, updated_date, version_number, error_text)
	VALUES (508, 408, 'ERR04', '2022-11-23 14:48:53', '2022-11-23 14:48:53', 1, 'Error text 8');
INSERT INTO error_logs(
	error_log_id, individual_request_id, error_code, created_date, updated_date, version_number, error_text)
	VALUES (509, 408, 'ERR04', '2022-11-24 14:48:53', '2022-11-24 14:48:53', 1, 'Error text 8');
INSERT INTO error_logs(
	error_log_id, individual_request_id, error_code, created_date, updated_date, version_number, error_text)
	VALUES (510, 408, 'ERR04', '2022-11-25 14:48:53', '2022-11-25 14:48:53', 1, 'Error text 8');
INSERT INTO error_logs(
	error_log_id, individual_request_id, error_code, created_date, updated_date, version_number, error_text)
	VALUES (511, 408, 'ERR04', '2022-11-25 14:48:53', '2022-11-23 14:48:53', 1, 'Error text 8');
INSERT INTO error_logs(
	error_log_id, individual_request_id, error_code, created_date, updated_date, version_number, error_text)
	VALUES (512, 408, 'ERR04', '2022-11-25 14:48:53', '2022-11-25 14:48:53', 1, 'Error text 8');
INSERT INTO error_logs(
	error_log_id, individual_request_id, error_code, created_date, updated_date, version_number, error_text)
	VALUES (513, 408, 'ERR04', '2022-11-26 14:48:53', '2022-11-26 14:48:53', 1, 'Error text 8');
INSERT INTO error_logs(
	error_log_id, individual_request_id, error_code, created_date, updated_date, version_number, error_text)
	VALUES (514, 408, 'ERR04', '2022-11-26 14:48:53', '2022-11-26 14:48:53', 1, 'Error text 8');
INSERT INTO error_logs(
	error_log_id, individual_request_id, error_code, created_date, updated_date, version_number, error_text)
	VALUES (516, 408, 'ERR04', '2022-11-26 14:48:53', '2022-11-26 14:48:53', 1, 'Error text 8');
INSERT INTO error_logs(
	error_log_id, individual_request_id, error_code, created_date, updated_date, version_number, error_text)
	VALUES (517, 408, 'ERR04', '2022-11-24 14:48:53', '2022-11-24 14:48:53', 1, 'Error text 8');
INSERT INTO error_logs(
	error_log_id, individual_request_id, error_code, created_date, updated_date, version_number, error_text)
	VALUES (518, 408, 'ERR04', '2022-11-24 14:48:53', '2022-11-24 14:48:53', 1, 'Error text 8');
INSERT INTO error_logs(
	error_log_id, individual_request_id, error_code, created_date, updated_date, version_number, error_text)
	VALUES (519, 408, 'ERR04', '2022-11-25 14:48:53', '2022-11-25 14:48:53', 1, 'Error text 8');
INSERT INTO error_logs(
	error_log_id, individual_request_id, error_code, created_date, updated_date, version_number, error_text)
	VALUES (520, 408, 'ERR04', '2022-11-26 14:48:53', '2022-11-26 14:48:53', 1, 'Error text 8');
INSERT INTO error_logs(
	error_log_id, individual_request_id, error_code, created_date, updated_date, version_number, error_text)
	VALUES (521, 408, 'ERR04', '2022-11-27 14:48:53', '2022-11-27 14:48:53', 1, 'Error text 8');
INSERT INTO error_logs(
	error_log_id, individual_request_id, error_code, created_date, updated_date, version_number, error_text)
	VALUES (522, 408, 'ERR04', '2022-11-28 14:48:53', '2022-11-28 14:48:53', 1, 'Error text 8');

