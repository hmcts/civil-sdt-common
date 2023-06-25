package uk.gov.moj.sdt.producers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.moj.sdt.producers.config.EndToEndTestConfig;
import uk.gov.moj.sdt.producers.config.SecurityConfig;
import uk.gov.moj.sdt.test.utils.IExecuteScriptService;
import uk.gov.moj.sdt.utils.SpringApplicationContext;
import uk.gov.moj.sdt.ws._2013.sdt.bulkrequestschema.BulkRequestType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkresponseschema.BulkResponseType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkresponseschema.ObjectFactory;
import uk.gov.moj.sdt.ws._2013.sdt.sdtendpoint.ISdtEndpointPortType;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test class for end to end web service tests..
 *
 * @author Robin Compston
 */
@ActiveProfiles("end-to-end-test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { EndToEndTestConfig.class, SecurityConfig.class})
@Sql(scripts = {
        "classpath:database/baseline/initialise_test_database.sql"
        ,"classpath:database/baseline/SubmitBulkTest.sql"
})
public class SubmitBulkTest extends AbstractWebServiceTest<BulkRequestType, BulkResponseType> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubmitBulkTest.class);

    private IExecuteScriptService executeScriptService;

    /**
     * Thirty seconds in milliseconds.
     */
    static final int THIRTY_SECONDS = 30000;
    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
        executeScriptService = SpringApplicationContext.getBean(IExecuteScriptService.class);
    }

    /**
     * Method to call remote submit bulk endpoint to be tested.
     */
    @Test
    public void testValid() {
        this.callWebService(BulkRequestType.class);
    }

    /**
     * Method to call remote submit bulk endpoint to be tested.
     */
    @Test
    public void testCountMismatch() {
        this.callWebService(BulkRequestType.class);
    }

    /**
     * Method to test ability of SDT to handle two concurrent in-memory duplicates (same customer and customer
     * reference), and to allow the first and return a duplicate error for the second.
     */
    @Test
    public void testTwoConcurrentDuplicates() {
        boolean duplicateDetected = false;

        // Used to limit number of attempts.
        int attempts = 0;

        // Keep trying to send pairs of messages until they are both in memory at the same time and detected as
        // concurrent.
        while (!duplicateDetected && attempts < 10) {
            duplicateDetected = this.sendConcurrentDuplicates(attempts, 2);
            attempts++;

            // Must clear out previous failed attempt at concurrent duplicate from database.
            if (!duplicateDetected) {

                initialiseDatabase();
            }
        }
    }

    /**
     * Method to test ability of SDT to handle two concurrent in-memory duplicates (same customer and customer
     * reference), and to allow the first and return a duplicate error for the second.
     */
    @Test
    public void testTenConcurrentDuplicates() {
        boolean duplicateDetected = false;

        // Used to limit number of attempts.
        int attempts = 0;

        // Keep trying to send pairs of messages until they are both in memory at the same time and detected as
        // concurrent.
        while (!duplicateDetected && attempts < 10) {
            duplicateDetected = this.sendConcurrentDuplicates(attempts, 10);
            attempts++;

            // Must clear out previous failed attempt at concurrent duplicate from database.
            if (!duplicateDetected) {
                initialiseDatabase();
            }
        }
    }

    @Override
    protected BulkResponseType callTestWebService(final BulkRequestType request) {
        // Get the SOAP proxy client.
        ISdtEndpointPortType client = getSdtEndpointClient();

        // Call the specific business method for this text - note that a single test can only use one web service
        // business method.
        return client.submitBulk(request);
    }

    @Override
    protected JAXBElement<BulkResponseType> wrapJaxbObject(final BulkResponseType response) {
        // Use the provided factor to create a wrapped instance of the response.
        ObjectFactory objectFactory = new ObjectFactory();
        return objectFactory.createBulkResponse(response);
    }

    /**
     * Method to send two duplicate messages to the application server in close succession in the hope that we will get
     * an in memory concurrent duplicate detected.
     *
     * @param attempts         the number of times we have tried to send a pair of messages.
     * @param concurrencyLevel the number of concurrent threads sending messages.
     * @return true - duplicate detected, false - duplicate not detected.
     */
    protected boolean sendConcurrentDuplicates(final int attempts, final int concurrencyLevel) {
        // Define outside loops.
        ConcurrentMessage concurrentMessage;

        // Create all necessary threads and store in list.
        List<ConcurrentMessage> concurrentMessages = new ArrayList<>(concurrencyLevel);
        for (int i = 0; i < concurrencyLevel; i++) {
            concurrentMessage = new ConcurrentMessage(attempts);
            concurrentMessages.add(concurrentMessage);

            // Start the thread.
            concurrentMessage.start();
        }

        // Wait for all threads to complete.
        for (int i = 0; i < concurrencyLevel; i++) {
            try {
                concurrentMessages.get(i).join(THIRTY_SECONDS);
            } catch (final InterruptedException e) {
                fail("Unexpected interruption of concurrent message thread.");
                // Restore interrupted state...
                Thread.currentThread().interrupt();
            }
        }

        // Count all duplicates.
        int duplicateCount = 0;
        for (int i = 0; i < concurrencyLevel; i++) {
            if (concurrentMessages.get(i).isDuplicate()) {
                duplicateCount++;
            }
        }

        // All should have been duplicates except for one which worked.
        return (duplicateCount == concurrencyLevel - 1);
    }

    /**
     * Inner class to send a single SOAP message in a separate thread
     *
     * @author Robin Compston.
     */
    private class ConcurrentMessage extends Thread {
        /**
         * Flag to indicate server saw this message as a duplicate.
         */
        private boolean duplicate = false;

        /**
         * Suffix to customer reference and request id to make each pair of message unique.
         */
        private int attemptOffset;

        /**
         * Constructor for ConcurrentMessage class.
         *
         * @param attemptOffset suffix to be added to customer reference and request id to make each pair of messages
         *                      unique.
         */
        public ConcurrentMessage(final int attemptOffset) {
            this.attemptOffset = attemptOffset;
        }

        @Override
        public void run() {
            this.testConcurrentDuplicate();
        }

        /**
         * Method name used to fool infrastructure into thinking this is the outer class method name allowing normal
         * method of identifying associated XML files for test.
         */
        public void testConcurrentDuplicate() {
                this.callWebService();
        }

        /**
         * Method is needed to add one extra stack frame to allow the Abstract class to correctly find the method name
         * of the test.
         */
        private void callWebService() {
            // Get the argument to send to the web service.
            BulkRequestType request = getJaxbFromXml(BulkRequestType.class);

            // Adjust the requests to allow pairs of potential duplicates to be distinguished from each other.
            request.getHeader().setCustomerReference(
                    request.getHeader().getCustomerReference() + this.attemptOffset);
            request.getRequests().getRequest().get(0)
                    .setRequestId(request.getRequests().getRequest().get(0).getRequestId() + this.attemptOffset);

            // Call the remote service.
            BulkResponseType response;
            try {
                response = callTestWebService(request);
            } catch (RuntimeException e) {
                LOGGER.error("Error: {}", e.getMessage());
                return;
            }

            // Check the response returned by the web service.
            checkXmlFromJaxb(response);
        }

        /**
         * Return duplicate flag value.
         *
         * @return true - duplicate message detected by server, false - no duplicate detected.
         */
        public boolean isDuplicate() {
            return duplicate;
        }
  }

  public void initialiseDatabase() {
      LOGGER.info(" running sql scripts");
      try {
          Resource[] resources = new Resource[2];
          resources[0] = new ClassPathResource("database/baseline/initialise_test_database.sql");
          resources[1] = new ClassPathResource("database/baseline/SubmitBulkTest.sql");
          Arrays.stream(resources).forEach(e -> executeScriptService.runScript(e));
      } catch (Exception e) {
          throw new SdtRunTimeException("RUNTIME_ERROR", "Failed to initialise database. Check scripts");
     }
  }

}
