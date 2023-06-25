INSERT INTO global_parameters (
  global_parameter_id,parameter_name,parameter_value,parameter_description, version_number)
  VALUES ( 1,'DATA_RETENTION_PERIOD',90,'Duration in days, to retain data in the tables subject to a prescribed purge', 0);

INSERT INTO target_applications(
  target_application_id, target_application_code, target_application_name, version_number)
  VALUES (4,'MCOL','mcol live service', 1);

INSERT INTO bulk_customers(
  bulk_customer_id, sdt_customer_id, version_number, ready_for_alternate_service)
  VALUES (1, 12345678, 1, true);

INSERT INTO service_requests(
  service_request_id, request_payload, request_timestamp, response_payload, response_timestamp, request_type, sdt_customer_id, sdt_bulk_reference, server_host_name, version_number)
  VALUES (9, 'Req_PL09', (now() - INTERVAL '2 day'), 'Res_PL111', now(), 'OFFER', 1, 'BR109','localhost', 1);

INSERT INTO service_requests(
  service_request_id, request_payload, request_timestamp, response_payload, response_timestamp, request_type, sdt_customer_id, sdt_bulk_reference, server_host_name, version_number)
  VALUES (3, 'Req_PL102', (now() - INTERVAL '200 day'), 'Res_PL102', now(), 'OFFER', 1, 'BR103','localhost', 1);

INSERT INTO bulk_submissions(
  bulk_submission_id, bulk_customer_id, target_application_id, service_request_id, sdt_bulk_reference, customer_reference, created_date, number_of_requests, bulk_submission_status, completed_date, updated_date, error_code, error_text, version_number, bulk_payload)
  VALUES (11, 1, 4, 3, 'BR103', 'CR104', (now() - INTERVAL '100 day'), 1, 'BSSTATUS1', now(), now(), 'EC104', 'This is error 1', 1, 'BLK PAYLOAD 1');
INSERT INTO bulk_submissions(
  bulk_submission_id, bulk_customer_id, target_application_id, service_request_id, sdt_bulk_reference, customer_reference, created_date, number_of_requests, bulk_submission_status, completed_date, updated_date, error_code, error_text, version_number, bulk_payload)
  VALUES (12, 1, 4, 3, 'BR104', 'CR105', (now() - INTERVAL '60 day'), 1, 'BSSTATUS2', now(), now(), 'EC105', 'This is error 2', 1, 'BLK PAYLOAD 2');
INSERT INTO bulk_submissions(
  bulk_submission_id, bulk_customer_id, target_application_id, service_request_id, sdt_bulk_reference, customer_reference, created_date, number_of_requests, bulk_submission_status, completed_date, updated_date, error_code, error_text, version_number, bulk_payload)
  VALUES (13, 1, 4, 3, 'BR105', 'CR106', (now() - INTERVAL '30 day'), 1, 'BSSTATUS3', now(), now(), 'EC106', 'This is error 3', 1, 'BLK PAYLOAD 3');

INSERT INTO bulk_submissions(
  bulk_submission_id, bulk_customer_id, target_application_id, service_request_id, sdt_bulk_reference, customer_reference, created_date, number_of_requests, bulk_submission_status, completed_date, updated_date, error_code, error_text, version_number, bulk_payload)
  VALUES (16, 1, 4, 9, 'BR106', 'CR107', (now() - INTERVAL '23 day'), 1, 'BSSTATUS1', now(), now(), 'EC107', 'This is error 7', 1, 'BLK PAYLOAD 4');
INSERT INTO bulk_submissions(
  bulk_submission_id, bulk_customer_id, target_application_id, service_request_id, sdt_bulk_reference, customer_reference, created_date, number_of_requests, bulk_submission_status, completed_date, updated_date, error_code, error_text, version_number, bulk_payload)
  VALUES (17, 1, 4, 9, 'BR107', 'CR108', (now() - INTERVAL '23 day'), 1, 'BSSTATUS1', now(), now(), 'EC107', 'This is error 7', 1, 'BLK PAYLOAD 4');

INSERT INTO individual_requests(
  individual_request_id, bulk_submission_id, customer_request_ref, request_status, sdt_bulk_reference, line_number, sdt_request_reference, created_date, updated_date, completed_date, forwarding_attempts, dead_letter, internal_system_error, request_type, version_number, individual_payload, target_application_response)
  VALUES (104, 11, 'CR104', 'COLD', 'BR103', 1, 'RR202', (now() - INTERVAL '100 day'), now(), now(), 3, 'N', 'Error this is indeed', 'ReqType1', 1, 'IndPL_1', 'TAppResp1');
INSERT INTO individual_requests(
  individual_request_id, bulk_submission_id, customer_request_ref, request_status, sdt_bulk_reference, line_number, sdt_request_reference, created_date, updated_date, completed_date, forwarding_attempts, dead_letter, internal_system_error, request_type, version_number, individual_payload, target_application_response)
  VALUES (105, 11, 'CR104', 'COLD', 'BR104', 1, 'RR203', (now() - INTERVAL '90 day'), now(), now(), 3, 'N', 'Error this is indeed', 'ReqType1', 1, 'IndPL_1', 'TAppResp2');
INSERT INTO individual_requests(
  individual_request_id, bulk_submission_id, customer_request_ref, request_status, sdt_bulk_reference, line_number, sdt_request_reference, created_date, updated_date, completed_date, forwarding_attempts, dead_letter, internal_system_error, request_type, version_number, individual_payload, target_application_response)
  VALUES (106, 11, 'CR104', 'COLD', 'BR105', 1, 'RR204', now(), now(), now(), 3, 'N', 'Error this is indeed', 'ReqType1', 1, 'IndPL_1', 'TAppResp3');
INSERT INTO individual_requests(
  individual_request_id, bulk_submission_id, customer_request_ref, request_status, sdt_bulk_reference, line_number, sdt_request_reference, created_date, updated_date, completed_date, forwarding_attempts, dead_letter, internal_system_error, request_type, version_number, individual_payload, target_application_response)
  VALUES (107, 12, 'CR104', 'COLD', 'BR103', 1, 'RR202', (now() - INTERVAL '100 day'), now(), now(), 3, 'N', 'Error this is indeed', 'ReqType1', 1, 'IndPL_1', 'TAppResp4');
INSERT INTO individual_requests(
  individual_request_id, bulk_submission_id, customer_request_ref, request_status, sdt_bulk_reference, line_number, sdt_request_reference, created_date, updated_date, completed_date, forwarding_attempts, dead_letter, internal_system_error, request_type, version_number, individual_payload, target_application_response)
  VALUES (108, 12, 'CR104', 'COLD', 'BR104', 1, 'RR203', (now() - INTERVAL '90 day'), now(), now(), 3, 'N', 'Error this is indeed', 'ReqType1', 1, 'IndPL_1', 'TAppResp5');
INSERT INTO individual_requests(
  individual_request_id, bulk_submission_id, customer_request_ref, request_status, sdt_bulk_reference, line_number, sdt_request_reference, created_date, updated_date, completed_date, forwarding_attempts, dead_letter, internal_system_error, request_type, version_number, individual_payload, target_application_response)
  VALUES (109, 13, 'CR104', 'COLD', 'BR105', 1, 'RR204', now(), now(), now(), 3, 'N', 'Error this is indeed', 'ReqType1', 1, 'IndPL_1', 'TAppResp6');

INSERT INTO individual_requests(
  individual_request_id, bulk_submission_id, customer_request_ref, request_status, sdt_bulk_reference, line_number, sdt_request_reference, created_date, updated_date, completed_date, forwarding_attempts, dead_letter, internal_system_error, request_type, version_number, individual_payload, target_application_response)
  VALUES (110, 16, 'CR104', 'COLD', 'BR105', 1, 'RR204', now(), now(), now(), 3, 'N', 'Error this is indeed', 'ReqType1', 1, 'IndPL_1', 'TAppResp6');
INSERT INTO individual_requests(
  individual_request_id, bulk_submission_id, customer_request_ref, request_status, sdt_bulk_reference, line_number, sdt_request_reference, created_date, updated_date, completed_date, forwarding_attempts, dead_letter, internal_system_error, request_type, version_number, individual_payload, target_application_response)
  VALUES (111, 17, 'CR104', 'COLD', 'BR105', 1, 'RR204', now(), now(), now(), 3, 'N', 'Error this is indeed', 'ReqType1', 1, 'IndPL_1', 'TAppResp6');

INSERT INTO error_logs(
  error_log_id, individual_request_id, error_code, created_date, updated_date, version_number, error_text)
  VALUES (1001, 104, 'CODE2', (now() - INTERVAL '100 day'), now(), 1, 'Error number 1');
INSERT INTO error_logs(
  error_log_id, individual_request_id, error_code, created_date, updated_date, version_number, error_text)
  VALUES (1002, 105, 'CODE3', (now() - INTERVAL '90 day'), now(), 1, 'Error number 2');
INSERT INTO error_logs(
  error_log_id, individual_request_id, error_code, created_date, updated_date, version_number, error_text)
  VALUES (1003, 106, 'CODE4', (now() - INTERVAL '20 day'), now(), 1, 'Error number 3');
INSERT INTO error_logs(
  error_log_id, individual_request_id, error_code, created_date, updated_date, version_number, error_text)
  VALUES (1004, 107, 'CODE2', (now() - INTERVAL '100 day'), now(), 1, 'Error number 1');
INSERT INTO error_logs(
  error_log_id, individual_request_id, error_code, created_date, updated_date, version_number, error_text)
  VALUES (1005, 109, 'CODE3', (now() - INTERVAL '90 day'), now(), 1, 'Error number 2');
INSERT INTO error_logs(
  error_log_id, individual_request_id, error_code, created_date, updated_date, version_number, error_text)
  VALUES (1006, 109, 'CODE4', (now() - INTERVAL '20 day'), now(), 1, 'Error number 3');

INSERT INTO error_logs(
  error_log_id, individual_request_id, error_code, created_date, updated_date, version_number, error_text)
  VALUES (1007, 110, 'CODE4', (now() - INTERVAL '20 day'), now(), 1, 'Error number 3');
INSERT INTO error_logs(
  error_log_id, individual_request_id, error_code, created_date, updated_date, version_number, error_text)
  VALUES (1008, 111, 'CODE4', (now() - INTERVAL '20 day'), now(), 1, 'Error number 3');
