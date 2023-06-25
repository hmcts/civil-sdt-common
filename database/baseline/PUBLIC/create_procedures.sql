
  CREATE OR REPLACE PROCEDURE "PUBLIC"."PURGE" (i_CommitInterval  IN NUMBER) AS
  fh1               UTL_FILE.FILE_TYPE;
  i_RetentionPeriod global_parameters.parameter_value%TYPE;
  filename          VARCHAR2(50);
  CURSOR c1 (nParam NUMBER)
            IS SELECT service_request_id
               FROM service_requests
               WHERE request_timestamp < (TRUNC(SYSTIMESTAMP) - i_RetentionPeriod);

  CURSOR c2 (nParam NUMBER)
            IS SELECT bulk_submission_id
               FROM bulk_submissions
               WHERE service_request_id IN (
                   SELECT  service_request_id
                   FROM service_requests
                   WHERE request_timestamp < (TRUNC(SYSTIMESTAMP) - i_RetentionPeriod));

  CURSOR c3 (nParam NUMBER)
            IS SELECT individual_request_id,sdt_request_reference
               FROM individual_requests
               WHERE bulk_submission_id IN  (
                   SELECT bulk_submission_id
                   FROM bulk_submissions
                   WHERE service_request_id IN (
                       SELECT service_request_id
                       FROM service_requests
                       WHERE request_timestamp < (TRUNC(SYSTIMESTAMP) - i_RetentionPeriod)));

  CURSOR c4 (nParam NUMBER)
            IS SELECT error_log_id
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
                           WHERE request_timestamp < (TRUNC(SYSTIMESTAMP) - i_RetentionPeriod))));


  nIteration   NUMBER;
  BEGIN
    SELECT parameter_value
    INTO i_RetentionPeriod
    FROM global_parameters
    WHERE parameter_name = 'DATA_RETENTION_PERIOD';
    filename := 'sdt_purge_log'||to_char(sysdate,'yyyymmddhh24miss')||'.log';
    fh1 := UTL_FILE.FOPEN('SDT_PURGE_LOGDIR',filename,'W',255);
    UTL_FILE.PUT_LINE(fh1,'Purge logfile generated on '
                         || to_char(sysdate,'DD-MON-YYYY HH24:MI:SS'));
    UTL_FILE.PUT_LINE(fh1,'Purge retention period applied is '
                         || to_char(i_RetentionPeriod)
                         || ' days, meaning we are deleting all data prior to '
                         || to_char(TRUNC(SYSTIMESTAMP) - i_RetentionPeriod,'DD-MON-YYYY HH24:MI:SS'));
    UTL_FILE.PUT_LINE(fh1,'Commencing deletion of error_logs records');
    nIteration := 0;
    FOR c4_rec IN c4(i_RetentionPeriod) LOOP
      DELETE FROM error_logs
      WHERE error_log_id = c4_rec.error_log_id;
      nIteration := nIteration + 1;
      IF MOD(nIteration,i_CommitInterval)=0 THEN
        COMMIT;
      END IF;
    END LOOP;
    COMMIT;
    UTL_FILE.PUT_LINE(fh1,to_char(nIteration)||' records deleted from error_logs table.');


    UTL_FILE.PUT_LINE(fh1,'Commencing deletion of individual_request records');
    nIteration := 0;
    FOR c3_rec IN c3(i_RetentionPeriod) LOOP
      DELETE FROM individual_requests
      WHERE individual_request_id = c3_rec.individual_request_id;
       UTL_FILE.PUT_LINE(fh1,'Deleted individual_request : '||c3_rec.sdt_request_reference );
      nIteration := nIteration + 1;
      IF MOD(nIteration,i_CommitInterval)=0 THEN
        COMMIT;
      END IF;
    END LOOP;
    COMMIT;
    UTL_FILE.PUT_LINE(fh1,to_char(nIteration)||' records deleted from individual_requests table.');

    UTL_FILE.PUT_LINE(fh1,'Commencing deletion of bulk_submissions records');
    nIteration := 0;
    FOR c2_rec IN c2(i_RetentionPeriod) LOOP
      DELETE FROM bulk_submissions
      WHERE bulk_submission_id = c2_rec.bulk_submission_id;

      nIteration := nIteration + 1;
      IF MOD(nIteration,i_CommitInterval)=0 THEN
        COMMIT;
      END IF;
    END LOOP;
    COMMIT;
    UTL_FILE.PUT_LINE(fh1,to_char(nIteration)||' records deleted from bulk_submissions table.');

    UTL_FILE.PUT_LINE(fh1,'Commencing deletion of service_requests records');
    nIteration := 0;
    FOR c1_rec IN c1(i_RetentionPeriod) LOOP
      DELETE FROM service_requests
      WHERE service_request_id = c1_rec.service_request_id;
      nIteration := nIteration + 1;
      IF MOD(nIteration,i_CommitInterval)=0 THEN
        COMMIT;
      END IF;
    END LOOP;
    COMMIT;
    UTL_FILE.PUT_LINE(fh1,to_char(nIteration)||' records deleted from service_requests table.');

  UTL_FILE.FCLOSE(fh1);
  END;
/


