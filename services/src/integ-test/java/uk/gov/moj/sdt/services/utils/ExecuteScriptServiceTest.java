package uk.gov.moj.sdt.services.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import uk.gov.moj.sdt.dao.api.IBulkCustomerDao;
import uk.gov.moj.sdt.dao.api.IBulkSubmissionDao;
import uk.gov.moj.sdt.dao.api.IIndividualRequestDao;
import uk.gov.moj.sdt.dao.config.DaoTestConfig;
import uk.gov.moj.sdt.domain.BulkCustomer;
import uk.gov.moj.sdt.domain.api.IBulkSubmission;
import uk.gov.moj.sdt.domain.api.IIndividualRequest;
import uk.gov.moj.sdt.test.utils.AbstractIntegrationTest;
import uk.gov.moj.sdt.test.utils.ExecuteScriptService;
import uk.gov.moj.sdt.test.utils.TestConfig;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test {@link ExecuteScriptService} run script methods.
 *
 * @author Mark Dathorne
 */
@ActiveProfiles("integ")
@SpringBootTest(classes = { TestConfig.class, DaoTestConfig.class})
@Sql(scripts = {
        "classpath:uk/gov/moj/sdt/services/sql/drop_and_recreate_empty_public_schema.sql",
        "classpath:uk/gov/moj/sdt/services/sql/initialise_test_database.sql",
        "classpath:uk/gov/moj/sdt/services/sql/executeScriptTest1.sql"
})
class ExecuteScriptServiceTest extends AbstractIntegrationTest {

    private ExecuteScriptService executeScriptService;

    /**
     * Bulk Submission DAO.
     */
    private IBulkSubmissionDao bulkSubmissionDao;

    /**
     * Individual Request DAO.
     */
    private IIndividualRequestDao individualRequestDao;

    /**
     * Bulk Customer to use for the test.
     */
    private BulkCustomer bulkCustomer;

    /**
     * Data retention period.
     */
    private int dataRetentionPeriod;

    /**
     * SDT Bulk Reference.
     */
    private String sdtBulkReference;

    @BeforeEach
    public void setup() {
        bulkSubmissionDao = this.applicationContext.getBean(IBulkSubmissionDao.class);
        individualRequestDao = this.applicationContext.getBean(IIndividualRequestDao.class);
        executeScriptService = this.applicationContext.getBean(ExecuteScriptService.class);
        IBulkCustomerDao bulkCustomerDao = this.applicationContext.getBean(IBulkCustomerDao.class);
        bulkCustomer = bulkCustomerDao.fetch(BulkCustomer.class, 10711L);
        dataRetentionPeriod = 90;
        sdtBulkReference = "MCOL-10012013010101-100099999";
    }

    @Test
    void testExecuteScript() {
        IBulkSubmission bulkSubmission = bulkSubmissionDao.getBulkSubmissionBySdtRef(bulkCustomer, sdtBulkReference, dataRetentionPeriod);
        assertNull(bulkSubmission);

        try {
            Resource[] resources = new Resource[1];
            resources[0] = new ClassPathResource("uk/gov/moj/sdt/services/sql/executeScriptTest2.sql");
            Arrays.stream(resources).forEach(e -> executeScriptService.executeScript(e));

            bulkSubmission = bulkSubmissionDao.getBulkSubmissionBySdtRef(bulkCustomer, sdtBulkReference,
                    dataRetentionPeriod);
            assertNotNull(bulkSubmission);
            assertEquals(14, bulkSubmission.getId());

        } catch (Exception e) {
            fail("RUNTIME_ERROR: " + e.getMessage());
        }
    }

    @Test
    void testRunScript() {
        // check the records do not exist yet
        IBulkSubmission bulkSubmission = bulkSubmissionDao.getBulkSubmissionBySdtRef(bulkCustomer, sdtBulkReference,
                dataRetentionPeriod);
        assertNull(bulkSubmission);

        IIndividualRequest individualRequest = individualRequestDao.getIndividualRequest(bulkCustomer,
                "USER_REQUEST_ID_BF1", dataRetentionPeriod);
        assertNull(individualRequest);

        individualRequest = individualRequestDao.getIndividualRequest(bulkCustomer,
                "USER_REQUEST_ID_BF2", dataRetentionPeriod);
        assertNotNull(individualRequest);

        individualRequest = individualRequestDao.getIndividualRequest(bulkCustomer,
                "USER_REQUEST_ID_BF3", dataRetentionPeriod);
        assertNotNull(individualRequest);

        try {
            Resource[] resources = new Resource[2];
            resources[0] = new ClassPathResource("uk/gov/moj/sdt/services/sql/executeScriptTest3.sql");
            resources[1] = new ClassPathResource("uk/gov/moj/sdt/services/sql/executeScriptTest4.sql");
            Arrays.stream(resources).forEach(e -> executeScriptService.runScript(e));

            // now check the records DO exist
            bulkSubmission = bulkSubmissionDao.getBulkSubmission(bulkCustomer, "REF1",
                    dataRetentionPeriod);
            assertNotNull(bulkSubmission);
            assertEquals(14, bulkSubmission.getId());

            individualRequest = individualRequestDao.getIndividualRequest(bulkCustomer,
                    "USER_REQUEST_ID_BF1", dataRetentionPeriod);
            assertNotNull(individualRequest);
            assertEquals(1, individualRequest.getId());

            individualRequest = individualRequestDao.getIndividualRequest(bulkCustomer,
                    "USER_REQUEST_ID_BF2", dataRetentionPeriod);
            assertNotNull(individualRequest);
            assertEquals(2, individualRequest.getId());

            individualRequest = individualRequestDao.getIndividualRequest(bulkCustomer,
                    "USER_REQUEST_ID_BF3", dataRetentionPeriod);
            assertNotNull(individualRequest);
            assertEquals(3, individualRequest.getId());

        } catch (Exception e) {
            fail("RUNTIME_ERROR: " + e.getMessage());
        }
    }

}
