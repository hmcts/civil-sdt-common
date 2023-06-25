package uk.gov.moj.sdt.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import uk.gov.moj.sdt.dao.PurgeNativeCallFunction;

@Service
public class PurgeService {

    private static Logger LOGGER = LoggerFactory.getLogger(PurgeService.class);

    @Value("${sdt.purge.commitInterval}")
    private String commitIntervalString;

    private PurgeNativeCallFunction purgeNativeCallFunction;

    public PurgeService(PurgeNativeCallFunction purgeNativeCallFunction) {
        this.purgeNativeCallFunction = purgeNativeCallFunction;
    }

    @Scheduled(cron = "${sdt.purge.cron}")
    public void transactPurgeProc() {
        LOGGER.debug("EXECUTING: the Scheduled Purge Job");

        purgeNativeCallFunction.executePurgeStoredProc(getCommitIntervalInteger());

        LOGGER.debug("COMPLETED: the Scheduled Purge Job");
    }

    public void setCommitIntervalString(String commitIntervalString) {
        this.commitIntervalString = commitIntervalString;
    }

    public Integer getCommitIntervalInteger() {
        LOGGER.debug("getCommitInterval");
        Integer commitInterval = 500;
        LOGGER.info("default commitInterval to {}", commitInterval);
        if (null != commitIntervalString && !commitIntervalString.isEmpty()) {
            Integer parmCommitInterval = Integer.parseInt(commitIntervalString);
            if (parmCommitInterval > 0) {
                commitInterval = parmCommitInterval;
                LOGGER.info("commitInterval obtained = {}", commitInterval);
            }
        }
        return commitInterval;
    }

}
