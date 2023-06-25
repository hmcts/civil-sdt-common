package uk.gov.moj.sdt.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.moj.sdt.dao.api.IBulkSubmissionDao;
import uk.gov.moj.sdt.dao.api.IIndividualRequestDao;
import uk.gov.moj.sdt.dao.config.DaoTestConfig;
import uk.gov.moj.sdt.domain.BulkSubmission;
import uk.gov.moj.sdt.domain.ErrorLog;
import uk.gov.moj.sdt.domain.GlobalParameter;
import uk.gov.moj.sdt.domain.IndividualRequest;
import uk.gov.moj.sdt.domain.PurgeJobAudit;
import uk.gov.moj.sdt.domain.PurgeJobAuditMessage;
import uk.gov.moj.sdt.domain.ServiceRequest;
import uk.gov.moj.sdt.test.utils.AbstractIntegrationTest;
import uk.gov.moj.sdt.test.utils.TestConfig;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test {@link PurgeNativeCallFunction} query methods.
 *
 * @author Mark Dathorne
 */
@ActiveProfiles("integ")
@SpringBootTest(classes = { TestConfig.class, DaoTestConfig.class})
@Sql(scripts = {"classpath:db/migration/V0002__purge.sql",
        "classpath:uk/gov/moj/sdt/dao/sql/PurgeDaoTest.sql"})
@Transactional
class PurgeNativeCallFunctionTest extends AbstractIntegrationTest {
    /**
     * Logger object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PurgeNativeCallFunctionTest.class);

    private PurgeNativeCallFunction purgeNativeCallFunction;

    private PurgeJobAuditDao purgeJobAuditDao;
    private PurgeJobAuditMessageDao purgeJobAuditMessageDao;
    private ErrorLogDao errorLogDao;
    private GlobalParametersDao globalParametersDao;
    private ServiceRequestDao serviceRequestDao;
    private IBulkSubmissionDao bulkSubmissionDao;
    private IIndividualRequestDao individualRequestDao;

    @BeforeEach
    public void setup() {
        purgeNativeCallFunction = this.applicationContext.getBean(PurgeNativeCallFunction.class);

        purgeJobAuditDao = (PurgeJobAuditDao) this.applicationContext.getBean("PurgeJobAuditDao");
        purgeJobAuditMessageDao = (PurgeJobAuditMessageDao) this.applicationContext.getBean("PurgeJobAuditMessageDao");
        errorLogDao = (ErrorLogDao) this.applicationContext.getBean("ErrorLogDao");
        globalParametersDao = (GlobalParametersDao) this.applicationContext.getBean("GlobalParametersDao");
        serviceRequestDao = (ServiceRequestDao) this.applicationContext.getBean("ServiceRequestDao");
        bulkSubmissionDao = (IBulkSubmissionDao) this.applicationContext.getBean("BulkSubmissionDao");
        individualRequestDao = (IIndividualRequestDao) this.applicationContext.getBean("IndividualRequestDao");
    }

    /**
     * Tests {@link GenericDao} fetch.
     */
    @Test
    void runPurge() {
        LOGGER.info("TEST STARTING: run the Purge");

        checkTestDataBeforeRunningPurge();

        purgeNativeCallFunction.executePurgeStoredProc(1000);

        checkTestDataAfterRunningPurge();

        LOGGER.info("TEST COMPLETED: the Purge has been run");
    }

    private void checkTestDataBeforeRunningPurge() {

        // check the retention date global parameter is present
        assertTrue(globalParameterIsPresentForId(1L));

        // check no audit records are present
        assertFalse(purgeJobAuditIsPresentForId(1L));
        assertFalse(purgeJobAuditMessageIsPresentForId(1L));

        // check test data is present
        assertTrue(serviceRequestIsPresentForId(3L));
        assertTrue(serviceRequestIsPresentForId(9L));
        assertTrue(bulkSubmissionIsPresentForId(13L));
        assertTrue(individualRequestIsPresentForId(109L));
        assertTrue(errorLogIsPresentForId(1001L));
        assertTrue(errorLogIsPresentForId(1006L));

    }

    private void checkTestDataAfterRunningPurge() {

        // check audit records are now present
        assertTrue(purgeJobAuditIsPresentForId(1L));
        assertTrue(purgeJobAuditMessageIsPresentForId(1L));

        // check service request that shpuld not be deleted is still present
        assertTrue(serviceRequestIsPresentForId(9L));
        // check service request that shpuld be deleted is no longer present
        assertFalse(serviceRequestIsPresentForId(3L));

        // check bulk submission that shpuld be deleted is no longer present
        assertFalse(bulkSubmissionIsPresentForId(11L));
        assertFalse(bulkSubmissionIsPresentForId(12L));
        assertFalse(bulkSubmissionIsPresentForId(13L));

        // check bulk submission that shpuld NOT be deleted is still present
        assertTrue(bulkSubmissionIsPresentForId(16L));
        assertTrue(bulkSubmissionIsPresentForId(17L));

        // check individual request that shpuld be deleted is no longer present
        assertFalse(individualRequestIsPresentForId(109L));

        // check individual request that should NOT be deleted is stil longer present
        assertTrue(individualRequestIsPresentForId(110L));
        assertTrue(individualRequestIsPresentForId(111L));

        // check error log that shpuld be deleted is no longer present
        assertFalse(errorLogIsPresentForId(1001L));
        assertFalse(errorLogIsPresentForId(1006L));

        // check error log that shpuld NOT be deleted is still present
        assertTrue(errorLogIsPresentForId(1007L));
        assertTrue(errorLogIsPresentForId(1008L));
    }

    private Boolean globalParameterIsPresentForId(Long id) {
        GlobalParameter globalParameter = globalParametersDao.fetch(GlobalParameter.class, id);
        return (null != globalParameter);
    }

    private Boolean serviceRequestIsPresentForId(Long id) {
        serviceRequestDao.getEntityManager().clear();
        ServiceRequest serviceRequest = serviceRequestDao.fetch(ServiceRequest.class, id);
        return (null != serviceRequest);
    }

    private Boolean bulkSubmissionIsPresentForId(Long id) {
        bulkSubmissionDao.getEntityManager().clear();
        BulkSubmission bulkSubmission = bulkSubmissionDao.fetch(BulkSubmission.class, id);
        return (null != bulkSubmission);
    }

    private Boolean individualRequestIsPresentForId(Long id) {
        individualRequestDao.getEntityManager().clear();
        IndividualRequest individualRequest = individualRequestDao.fetch(IndividualRequest.class, id);
        return (null != individualRequest);
    }

    private Boolean errorLogIsPresentForId(Long id) {
        errorLogDao.getEntityManager().clear();
        ErrorLog errorLog = errorLogDao.fetch(ErrorLog.class, id);
        return (null != errorLog);
    }

    private Boolean purgeJobAuditIsPresentForId(Long id) {
        purgeJobAuditDao.getEntityManager().clear();
        PurgeJobAudit purgeJobAudit = purgeJobAuditDao.fetch(PurgeJobAudit.class, id);
        return (null != purgeJobAudit);
    }

    private Boolean purgeJobAuditMessageIsPresentForId(Long id) {
        purgeJobAuditMessageDao.getEntityManager().clear();
        PurgeJobAuditMessage purgeJobAuditMessage = purgeJobAuditMessageDao.fetch(PurgeJobAuditMessage.class, id);
        return (null != purgeJobAuditMessage);
    }
}
