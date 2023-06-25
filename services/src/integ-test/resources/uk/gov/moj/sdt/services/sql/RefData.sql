CREATE SEQUENCE SDT_REF_SEQ
  START WITH 1
  INCREMENT BY 1
  NO MINVALUE
  NO MAXVALUE
  CACHE 1;

insert into TARGET_APPLICATIONS (TARGET_APPLICATION_ID, TARGET_APPLICATION_CODE, TARGET_APPLICATION_NAME, VERSION_NUMBER)
values (1, 'MCOL', 'MCOL', 0);

insert into SERVICE_TYPES (SERVICE_TYPE_ID, SERVICE_TYPE_NAME, SERVICE_TYPE_STATUS, SERVICE_TYPE_DESCRIPTION, VERSION_NUMBER)
values (1, 'SUBMIT_INDIVIDUAL', 'A', 'Submit individual request web service', 0);

insert into SERVICE_TYPES (SERVICE_TYPE_ID, SERVICE_TYPE_NAME, SERVICE_TYPE_STATUS, SERVICE_TYPE_DESCRIPTION, VERSION_NUMBER)
values (2, 'SUBMIT_QUERY', 'A', 'Submit query web service', 0);

insert into GLOBAL_PARAMETERS (GLOBAL_PARAMETER_ID, PARAMETER_NAME, PARAMETER_VALUE,  PARAMETER_DESCRIPTION, VERSION_NUMBER)
values (1, 'DATA_RETENTION_PERIOD', 90, '"Duration in days, to retain data in the tables subject to a prescribed purge', 0);

insert into GLOBAL_PARAMETERS (GLOBAL_PARAMETER_ID, PARAMETER_NAME, PARAMETER_VALUE,  PARAMETER_DESCRIPTION, VERSION_NUMBER)
values (2, 'TARGET_APP_TIMEOUT', 15000, 'Period in milliseconds, to wait for response from target application', 0);

insert into GLOBAL_PARAMETERS (GLOBAL_PARAMETER_ID, PARAMETER_NAME, PARAMETER_VALUE,  PARAMETER_DESCRIPTION, VERSION_NUMBER)
values (3, 'MAX_FORWARDING_ATTEMPTS', 3, 'Number of forwarding attempts made to transmit an individual request to target application', 0);

insert into GLOBAL_PARAMETERS (GLOBAL_PARAMETER_ID, PARAMETER_NAME, PARAMETER_VALUE,  PARAMETER_DESCRIPTION, VERSION_NUMBER)
values (4, 'MCOL_MAX_CONCURRENT_INDV_REQ', 5, 'Maximum number of concurrent Individual Requests that can be forwarded to MCOL', 0);

insert into GLOBAL_PARAMETERS (GLOBAL_PARAMETER_ID, PARAMETER_NAME, PARAMETER_VALUE,  PARAMETER_DESCRIPTION, VERSION_NUMBER)
values (5, 'MCOL_INDV_REQ_DELAY', 10, 'Time delay in milliseconds before processing the next Individual Request that can be forwarded to MCOL', 0);

insert into GLOBAL_PARAMETERS (GLOBAL_PARAMETER_ID, PARAMETER_NAME, PARAMETER_VALUE,  PARAMETER_DESCRIPTION, VERSION_NUMBER)
values (6, 'MCOL_MAX_CONCURRENT_QUERY_REQ', 5, 'Maximum number of concurrent Submit Query Requests that can be forwarded to MCOL', 0);

insert into GLOBAL_PARAMETERS (GLOBAL_PARAMETER_ID, PARAMETER_NAME, PARAMETER_VALUE,  PARAMETER_DESCRIPTION, VERSION_NUMBER)
values (7, 'CONTACT_DETAILS', 'tbc', 'Current contact details for outgoing SDT application messages to Bulk Customer System', 0);

insert into GLOBAL_PARAMETERS (GLOBAL_PARAMETER_ID, PARAMETER_NAME, PARAMETER_VALUE,  PARAMETER_DESCRIPTION, VERSION_NUMBER)
values (8, 'TARGET_APP_RESP_TIMEOUT', 30000, 'Period in milliseconds, to wait for the response from target application', 0);

insert into SERVICE_ROUTINGS (SERVICE_ROUTINGS_ID, SERVICE_TYPE_ID, TARGET_APPLICATION_ID, WEB_SERVICE_ENDPOINT, VERSION_NUMBER)
values (1, 1, 1, 'http://localhost:8088/mockTargetAppInternalEndpointHttpPortBinding', 0);

insert into SERVICE_ROUTINGS (SERVICE_ROUTINGS_ID, SERVICE_TYPE_ID, TARGET_APPLICATION_ID, WEB_SERVICE_ENDPOINT, VERSION_NUMBER)
values (2, 2, 1, 'http://localhost:8088/mockTargetAppInternalEndpointHttpPortBinding', 0);

insert into ERROR_MESSAGES (ERROR_MESSAGE_ID, ERROR_CODE, ERROR_TEXT,  ERROR_DESCRIPTION, VERSION_NUMBER)
values (1, 'SDT_INT_ERR', 'A system error has occurred. Please contact {0} for assistance.', 'A system error has occured', 0);

insert into ERROR_MESSAGES (ERROR_MESSAGE_ID, ERROR_CODE, ERROR_TEXT,  ERROR_DESCRIPTION, VERSION_NUMBER)
values (2, 'CUST_NOT_SETUP', 'The Bulk Customer organisation is not setup to send Service Request messages to the {0}. Please contact {1} for assistance.',
        'The SDT Customer ID for the Bulk Customer organisation is not recognised by SDT.', 0);

insert into ERROR_MESSAGES (ERROR_MESSAGE_ID, ERROR_CODE, ERROR_TEXT,  ERROR_DESCRIPTION, VERSION_NUMBER)
values (3, 'CUST_ID_INVALID', 'The Bulk Customer organisation does not have an SDT Customer ID set up. Please contact {1} for assistance.',
        'The Bulk Customer organisation is recognised by the SDT Service, but is not set up to send a Service Request message to the specified Target Application.', 0);

insert into ERROR_MESSAGES (ERROR_MESSAGE_ID, ERROR_CODE, ERROR_TEXT,  ERROR_DESCRIPTION, VERSION_NUMBER)
values (4, 'DUP_CUST_FILEID', 'Duplicate User File Reference {0} supplied. This was previously used to submit a Bulk Request on {1} and the SDT Bulk Reference {2} was allocated.',
        'A duplicate User File Reference is identified by SDT for the Bulk Customer.', 0);

insert into ERROR_MESSAGES (ERROR_MESSAGE_ID, ERROR_CODE, ERROR_TEXT,  ERROR_DESCRIPTION, VERSION_NUMBER)
values (5, 'REQ_COUNT_MISMATCH', 'Unexpected Total Number of Requests identified. {0} requested identified, {1} requests expected in Bulk Request {2}.',
        'The Total Number of Requests identified by SDT does not match the Total Number of Requests expected (provided by the Submit Bulk Request).', 0);

insert into ERROR_MESSAGES (ERROR_MESSAGE_ID, ERROR_CODE, ERROR_TEXT,  ERROR_DESCRIPTION, VERSION_NUMBER)
values (6, 'BULK_REF_INVALID', 'There is no Bulk Request submission associated with your account for the supplied SDT Bulk Reference {0}.',
        'The supplied SDT Bulk Reference is not listed against the Bulk Customers Bulk Submissions detail', 0);


insert into ERROR_MESSAGES (ERROR_MESSAGE_ID, ERROR_CODE, ERROR_TEXT,  ERROR_DESCRIPTION, VERSION_NUMBER)
values (7, 'TAR_APP_BUSY', 'The Target Application is currently busy and cannot process your Submit Query request. Please try again later.',
        'SDT has reached the maximum number of concurrent Submit Query requests that can be forwarded to the Target Application for processing.', 0);

insert into ERROR_MESSAGES (ERROR_MESSAGE_ID, ERROR_CODE, ERROR_TEXT,  ERROR_DESCRIPTION, VERSION_NUMBER)
values (8, 'TAR_APP_ERROR', 'The system encountered a problem when processing your Submit Query request. Please try again later or contact {0} for assistance.',
        'The Target Appliation does not send a response to SDT within the expected timescale, or an error message is received.', 0);

insert into ERROR_MESSAGES (ERROR_MESSAGE_ID, ERROR_CODE, ERROR_TEXT,  ERROR_DESCRIPTION, VERSION_NUMBER)
values (9, 'DUP_CUST_REFID', 'Duplicate User File Reference {0} supplied',
        'A duplicate User File Reference is identified by SDT for the Bulk Customer.', 0);

insert into ERROR_MESSAGES (ERROR_MESSAGE_ID, ERROR_CODE, ERROR_TEXT,  ERROR_DESCRIPTION, VERSION_NUMBER)
values (10, 'NO_VALID_REQS', 'The submitted Bulk Request {0} does not contain valid individual Requests.',
        'The Submit Bulk Request message does not contain Individual Requests deemed to be valid. The Individual Requests have all failed SDT format validation and been rejected and processing has been completed as far as possible.', 0);

insert into ERROR_MESSAGES (ERROR_MESSAGE_ID, ERROR_CODE, ERROR_TEXT,  ERROR_DESCRIPTION, VERSION_NUMBER)
values (11, 'DUPLD_CUST_REQID', 'Unique Request Identifier has been specified more than once within the originating Bulk Request',
        'SDT identifies that a Unique Request Identifier has been associated with more than one Individual Request within the same Bulk Request.', 0);

insert into ERROR_MESSAGES (ERROR_MESSAGE_ID, ERROR_CODE, ERROR_TEXT,  ERROR_DESCRIPTION, VERSION_NUMBER)
values (12, 'DUP_CUST_REQID', 'Duplicate Unique Request Identifier submitted {0}.',
        'SDT identifies that a Unique Request Identifier has been associated with more than one Individual Request within the same Bulk Request.', 0);

insert into ERROR_MESSAGES (ERROR_MESSAGE_ID, ERROR_CODE, ERROR_TEXT,  ERROR_DESCRIPTION, VERSION_NUMBER)
values (13, 'REQ_NOT_ACK', 'Individual Request not acknowledged by Target Application. Please contact {0} for assistance.',
        'The Target Application does not send back an acknowledgement response within the expected period, or returns a response indicating that there was an error with the transmission.', 0);

insert into ERROR_MESSAGES (ERROR_MESSAGE_ID, ERROR_CODE, ERROR_TEXT,  ERROR_DESCRIPTION, VERSION_NUMBER)
values (14, 'CUST_XML_ERR', 'Individual Request format could not be processed by the Target Application. Please check the data and resubmit the request, or contact {0} for assistance.',
        'Client data has caused SOAP Fault error and rejected the request.', 0);
