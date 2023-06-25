DROP SEQUENCE IF EXISTS purge_job_audit_seq;
DROP SEQUENCE IF EXISTS purge_job_audit_messages_seq;
DROP TABLE IF EXISTS purge_job_audit CASCADE;
DROP TABLE IF EXISTS purge_job_audit_messages CASCADE;

CREATE TABLE purge_job_audit
(purge_job_id NUMERIC UNIQUE NOT NULL,
job_start_date TIMESTAMP(6),
job_end_date TIMESTAMP(6),
retention_date TIMESTAMP(6),
count_of_error_logs_deleted NUMERIC,
count_of_individual_requests_deleted NUMERIC,
count_of_bulk_submissions_deleted NUMERIC,
count_of_service_requests_deleted NUMERIC,
success NUMERIC);

CREATE TABLE purge_job_audit_messages
(purge_job_message_id NUMERIC UNIQUE NOT NULL,
purge_job_id NUMERIC REFERENCES purge_job_audit (purge_job_id),
message_date TIMESTAMP(6),
log_message VARCHAR(256));

-- add indexes --
CREATE UNIQUE INDEX purge_job_audit_pk ON purge_job_audit (purge_job_id);
CREATE UNIQUE INDEX purge_job_audit_messages_pk ON purge_job_audit_messages (purge_job_message_id);

-- add constraints --
ALTER TABLE purge_job_audit ADD CONSTRAINT purge_job_audit_pk PRIMARY KEY USING INDEX purge_job_audit_pk;
ALTER TABLE purge_job_audit_messages ADD CONSTRAINT purge_job_audit_messages_pk PRIMARY KEY USING INDEX purge_job_audit_messages_pk;

CREATE SEQUENCE purge_job_audit_seq;
CREATE SEQUENCE purge_job_audit_messages_seq;

CREATE OR REPLACE PROCEDURE purge (i_CommitInterval IN INT) language plpgsql AS '
DECLARE i_retention_period VARCHAR(32);
DECLARE t_retention_date TIMESTAMP;
DECLARE t_job_start_date TIMESTAMP;
DECLARE t_job_end_date TIMESTAMP;
DECLARE i_purge_job_id NUMERIC;

-- cursor1 service requests created before given retention date
c1 CURSOR
    FOR SELECT service_request_id
        FROM service_requests
        WHERE request_timestamp < t_retention_date;

-- cursor2 bulk submissions with service requests created before given retention date
c2 CURSOR
    FOR SELECT bulk_submission_id
        FROM bulk_submissions
        WHERE service_request_id IN (
            SELECT  service_request_id
            FROM service_requests
            WHERE request_timestamp < t_retention_date);

-- cursor3 individual requests for bulk submissions
--   with service requests created before given retention date
c3 CURSOR
    FOR SELECT individual_request_id,sdt_request_reference
        FROM individual_requests
        WHERE bulk_submission_id IN  (
            SELECT bulk_submission_id
            FROM bulk_submissions
            WHERE service_request_id IN (
                SELECT service_request_id
                FROM service_requests
                WHERE request_timestamp < t_retention_date));

-- cursor4 error logs for individual requests for bulk submissions
--   with service requests created before given retention date
c4 CURSOR
    FOR SELECT error_log_id
        FROM error_logs
        WHERE individual_request_id IN (
            SELECT individual_request_id
            FROM individual_requests
            WHERE bulk_submission_id IN (
                SELECT bulk_submission_id
                FROM bulk_submissions
                WHERE service_request_id IN (
                    SELECT service_request_id
                    FROM service_requests
                    WHERE request_timestamp < t_retention_date)));

n_iteration   INT;

BEGIN

    -- retention period
	SELECT parameter_value
    INTO i_retention_period
    FROM global_parameters
    WHERE parameter_name = ''DATA_RETENTION_PERIOD'';

    -- job start date
	SELECT now()
    INTO t_job_start_date;
    RAISE NOTICE ''Job start date: %'', t_job_start_date;

    -- retention_date calculated using retention period and now
	SELECT t_job_start_date::DATE - (i_retention_period || '' DAY'')::INTERVAL
    INTO t_retention_date;
    RAISE NOTICE ''Retention period date: %'', t_retention_date;

	-- get job audit id
	SELECT nextval(''purge_job_audit_seq'')
	INTO i_purge_job_id;

    RAISE NOTICE ''i_purge_job_id is %'', i_purge_job_id;

    -- create job audit
	INSERT INTO purge_job_audit
  	(purge_job_id, job_start_date, retention_date, success)
  	VALUES (i_purge_job_id, t_job_start_date, t_retention_date, 0);

    RAISE NOTICE ''Purge retention period is % days, deleting all data prior to %'', i_retention_period, t_retention_date;

    INSERT INTO purge_job_audit_messages
    (purge_job_message_id, purge_job_id, message_date,log_message)
    VALUES (nextval(''purge_job_audit_messages_seq''), i_purge_job_id, now(),
			''Purge retention period is '' || i_retention_period ||
			'' days, deleting all data prior to '' || t_retention_date);

    -- delete error logs
    RAISE NOTICE ''Commencing deletion of error_logs records'';

    INSERT INTO purge_job_audit_messages
    (purge_job_message_id, purge_job_id, message_date,log_message)
    VALUES (nextval(''purge_job_audit_messages_seq''), i_purge_job_id, now(),
			''Commencing deletion of error_logs records'');

	n_iteration := 0;
    FOR c4_rec IN c4 LOOP
        DELETE FROM error_logs
        WHERE error_log_id = c4_rec.error_log_id;

	    RAISE NOTICE ''Deleted error_logs : %'', c4_rec.error_log_id;

        n_iteration := n_iteration + 1;
    END LOOP;

    RAISE NOTICE ''% records deleted from error_logs table.'', n_iteration;

    INSERT INTO purge_job_audit_messages
    (purge_job_message_id, purge_job_id, message_date,log_message)
    VALUES (nextval(''purge_job_audit_messages_seq''), i_purge_job_id, now(),
			n_iteration || '' records deleted from error_logs table.'');

	-- update count of deletions - error logs
    UPDATE purge_job_audit
  	SET count_of_error_logs_deleted=n_iteration
  	WHERE purge_job_id=i_purge_job_id;

    RAISE NOTICE ''Commencing deletion of individual_request records'';

    n_iteration := 0;
    FOR c3_rec IN c3 LOOP
        DELETE FROM individual_requests
        WHERE individual_request_id = c3_rec.individual_request_id;

        RAISE NOTICE ''Deleted individual_request : %'', c3_rec.sdt_request_reference;

	    INSERT INTO purge_job_audit_messages
		(purge_job_message_id, purge_job_id, message_date,log_message)
    	VALUES (nextval(''purge_job_audit_messages_seq''), i_purge_job_id, now(),
				''Deleted individual_request : '' || c3_rec.sdt_request_reference);

	    n_iteration := n_iteration + 1;
    END LOOP;

    RAISE NOTICE ''% records deleted from individual_requests table.'', n_iteration;

    INSERT INTO purge_job_audit_messages
    (purge_job_message_id, purge_job_id, message_date,log_message)
    VALUES (nextval(''purge_job_audit_messages_seq''), i_purge_job_id, now(),
	        n_iteration || '' records deleted from individual_requests table.'');

	-- update count of deletions - error logs
    UPDATE purge_job_audit
  	SET count_of_individual_requests_deleted =n_iteration
  	WHERE purge_job_id=i_purge_job_id;

    RAISE NOTICE ''Commencing deletion of bulk_submissions records'';

    INSERT INTO purge_job_audit_messages
    (purge_job_message_id, purge_job_id, message_date,log_message)
    VALUES (nextval(''purge_job_audit_messages_seq''), i_purge_job_id, now(),
			''Commencing deletion of bulk_submissions records'');

    n_iteration := 0;
    FOR c2_rec IN c2 LOOP
        DELETE FROM bulk_submissions
        WHERE bulk_submission_id = c2_rec.bulk_submission_id;

        RAISE NOTICE ''Deleted bulk_submissions : %'', c2_rec.bulk_submission_id;

        n_iteration := n_iteration + 1;
    END LOOP;

    RAISE NOTICE ''% records deleted from bulk_submissions table.'', n_iteration;

    INSERT INTO purge_job_audit_messages
    (purge_job_message_id, purge_job_id, message_date,log_message)
    VALUES (nextval(''purge_job_audit_messages_seq''), i_purge_job_id, now(),
			n_iteration || '' records deleted from bulk_submissions table.'');

	UPDATE purge_job_audit
  	SET count_of_bulk_submissions_deleted=n_iteration
  	WHERE purge_job_id=i_purge_job_id;

    RAISE NOTICE ''Commencing deletion of service_requests records'';

    INSERT INTO purge_job_audit_messages
    (purge_job_message_id, purge_job_id, message_date,log_message)
    VALUES (nextval(''purge_job_audit_messages_seq''), i_purge_job_id, now(),
	        ''Commencing deletion of service_requests records'');

    n_iteration := 0;
    FOR c1_rec IN c1 LOOP
        DELETE FROM service_requests
        WHERE service_request_id = c1_rec.service_request_id;

	    RAISE NOTICE ''Deleted service_requests : %'', c1_rec.service_request_id;

        n_iteration := n_iteration + 1;
    END LOOP;

    RAISE NOTICE ''% records deleted from service_requests table.'', n_iteration;

    INSERT INTO purge_job_audit_messages
    (purge_job_message_id, purge_job_id, message_date,log_message)
    VALUES (nextval(''purge_job_audit_messages_seq''), i_purge_job_id, now(),
			n_iteration || '' records deleted from service_requests table.'');

	UPDATE purge_job_audit
  	SET count_of_service_requests_deleted=n_iteration,job_end_date=now(), success=1
  	WHERE purge_job_id=i_purge_job_id;

  END;

'
