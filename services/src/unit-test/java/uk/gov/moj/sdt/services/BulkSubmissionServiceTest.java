package uk.gov.moj.sdt.services;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import uk.gov.moj.sdt.dao.BulkCustomerDao;
import uk.gov.moj.sdt.dao.GenericDao;
import uk.gov.moj.sdt.dao.TargetApplicationDao;
import uk.gov.moj.sdt.dao.api.IBulkCustomerDao;
import uk.gov.moj.sdt.dao.api.IGenericDao;
import uk.gov.moj.sdt.dao.api.ITargetApplicationDao;
import uk.gov.moj.sdt.domain.BulkCustomer;
import uk.gov.moj.sdt.domain.BulkSubmission;
import uk.gov.moj.sdt.domain.ErrorMessage;
import uk.gov.moj.sdt.domain.IndividualRequest;
import uk.gov.moj.sdt.domain.ServiceRequest;
import uk.gov.moj.sdt.domain.ServiceRouting;
import uk.gov.moj.sdt.domain.ServiceType;
import uk.gov.moj.sdt.domain.TargetApplication;
import uk.gov.moj.sdt.domain.api.IBulkCustomer;
import uk.gov.moj.sdt.domain.api.IBulkSubmission;
import uk.gov.moj.sdt.domain.api.IErrorMessage;
import uk.gov.moj.sdt.domain.api.IIndividualRequest;
import uk.gov.moj.sdt.domain.api.IIndividualRequest.IndividualRequestStatus;
import uk.gov.moj.sdt.domain.api.IServiceRequest;
import uk.gov.moj.sdt.domain.api.IServiceRouting;
import uk.gov.moj.sdt.domain.api.IServiceType;
import uk.gov.moj.sdt.domain.api.ITargetApplication;
import uk.gov.moj.sdt.domain.cache.api.ICacheable;
import uk.gov.moj.sdt.services.utils.IndividualRequestsXmlParser;
import uk.gov.moj.sdt.services.utils.MessagingUtility;
import uk.gov.moj.sdt.services.utils.SdtBulkReferenceGenerator;
import uk.gov.moj.sdt.services.utils.api.IMessagingUtility;
import uk.gov.moj.sdt.services.utils.api.ISdtBulkReferenceGenerator;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;
import uk.gov.moj.sdt.utils.SdtContext;
import uk.gov.moj.sdt.utils.Utilities;
import uk.gov.moj.sdt.utils.concurrent.InFlightMessage;
import uk.gov.moj.sdt.utils.concurrent.api.IInFlightMessage;
import uk.gov.moj.sdt.validators.BulkSubmissionValidator;
import uk.gov.moj.sdt.validators.exception.CustomerReferenceNotUniqueException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ch.qos.logback.classic.Level.DEBUG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class for BulkSubmissionService.
 *
 * @author Manoj Kulkarni
 */
@ExtendWith(MockitoExtension.class)
class BulkSubmissionServiceTest extends AbstractSdtUnitTestBase {

    /**
     * Bulk Customer dao.
     */
    @Mock
    private IBulkCustomerDao mockBulkCustomerDao;

    /**
     * Target Application dao.
     */
    @Mock
    private ITargetApplicationDao mockTargetApplicationDao;

    /**
     * SDT Bulk Reference Generator.
     */
    @Mock
    private ISdtBulkReferenceGenerator mockSdtBulkReferenceGenerator;

    /**
     * Messaging Utility reference.
     */
    @Mock
    private IMessagingUtility mockMessagingUtility;

    /**
     *
     */
    @Mock
    private Map<String, IInFlightMessage> mockConcurrencyMap;

    @Mock
    private BulkSubmissionValidator bulkSubmissionValidator;


    /**
     *
     */
    @Mock
    private ICacheable mockErrorMessagesCache;

    /**
     * Generic dao.
     */
    @Mock
    private IGenericDao mockGenericDao;

    /**
     * Bulk Submission Service for testing.
     */
    private BulkSubmissionService bulkSubmissionService;

    private static final String PATH_TEST_RESOURCES = "src/unit-test/resources/";

    private static final String TEST_XML_VALID_2 = "testXMLValid2.xml";

    private static final String TEST_XML_VALID_3 = "testXMLValid3.xml";

    private static final String TARGET_APPLICATION_FOUND = "Target Application found";

    /**
     * Setup of the mock dao and injection of other objects.
     */
    @BeforeEach
    @Override
    public void setUp() {
        IndividualRequestsXmlParser individualRequestsXmlParser = new IndividualRequestsXmlParser();

        bulkSubmissionService = new BulkSubmissionService(mockGenericDao,
                                                          mockBulkCustomerDao,
                                                          mockTargetApplicationDao,
                                                          individualRequestsXmlParser,
                                                          mockMessagingUtility,
                                                          mockSdtBulkReferenceGenerator,
                                                          mockErrorMessagesCache,
                                                          bulkSubmissionValidator,
                                                          mockConcurrencyMap);
    }

    /**
     * Test method for the saving of bulk submission.
     *
     * @throws IOException if there is any error in reading the file.
     */
    @Test
    void testSaveBulkSubmission() throws IOException {
        SdtContext.getContext().setRawInXml(Utilities.getRawXml(PATH_TEST_RESOURCES, TEST_XML_VALID_2));


        // Mock the serviceRequest fetch
        final IServiceRequest serviceRequest = new ServiceRequest();
        when(mockGenericDao.fetch(IServiceRequest.class, 1)).thenReturn(serviceRequest);

        // Activate Mock Generic Dao
        final IBulkSubmission bulkSubmission = this.createBulkSubmission();
        when(mockBulkCustomerDao.getBulkCustomerBySdtId(10)).thenReturn(bulkSubmission.getBulkCustomer());

        final String key =
                bulkSubmission.getBulkCustomer().getSdtCustomerId() + bulkSubmission.getCustomerReference();

        // Setup concurrency map as if validator had done it.
        IInFlightMessage inFlightMessage = new InFlightMessage();
        inFlightMessage.setCompetingThreads(new HashMap<>());
        inFlightMessage.getCompetingThreads().put(Thread.currentThread(), Thread.currentThread());

        when(mockConcurrencyMap.get(key)).thenReturn(inFlightMessage);

        // Put a dummy value into the SdtContext
        SdtContext.getContext().setServiceRequestId(1L);

        // Call the bulk submission service
        bulkSubmissionService.saveBulkSubmission(bulkSubmission);

        // Verify the Mock
        verify(mockGenericDao).persist(bulkSubmission);
        verify(mockBulkCustomerDao).getBulkCustomerBySdtId(10);
        verify(mockGenericDao).fetch(IServiceRequest.class, 1);
        verify(mockConcurrencyMap).get(key);

    }

    @Test
    void testSaveBulkSubmissionWithTargetApplicationSet() throws IOException {

        SdtContext.getContext().setRawInXml(Utilities.getRawXml(PATH_TEST_RESOURCES, TEST_XML_VALID_2));

        Logger logger = (Logger) LoggerFactory.getLogger(BulkSubmissionService.class);

        // Set logging level to debug so that afterCompletion logging is captured
        logger.setLevel(DEBUG);

        // Create appender and add to logger
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        // Mock the serviceRequest fetch
        final IServiceRequest serviceRequest = new ServiceRequest();
        when(mockGenericDao.fetch(IServiceRequest.class, 1)).thenReturn(serviceRequest);

        // Activate Mock Generic Dao
        final IBulkSubmission bulkSubmission = this.createBulkSubmission();
        when(mockBulkCustomerDao.getBulkCustomerBySdtId(10)).thenReturn(bulkSubmission.getBulkCustomer());

        final String key =
            bulkSubmission.getBulkCustomer().getSdtCustomerId() + bulkSubmission.getCustomerReference();

        // Setup concurrency map as if validator had done it.
        IInFlightMessage inFlightMessage = new InFlightMessage();
        inFlightMessage.setCompetingThreads(new HashMap<>());
        inFlightMessage.getCompetingThreads().put(Thread.currentThread(), Thread.currentThread());

        when(mockConcurrencyMap.get(key)).thenReturn(inFlightMessage);

        // Put a dummy value into the SdtContext
        SdtContext.getContext().setServiceRequestId(1L);

        ITargetApplication targetApplication = new TargetApplication();
        when(mockTargetApplicationDao.getTargetApplicationByCode(anyString())).thenReturn(targetApplication);

        // Call the bulk submission service
        bulkSubmissionService.saveBulkSubmission(bulkSubmission);

        List<ILoggingEvent> logList = listAppender.list;
        //Check Debug Logging
        assertTrue(verifyLog(logList,TARGET_APPLICATION_FOUND));

        logger.detachAndStopAllAppenders();

        // Verify the Mock
        verify(mockGenericDao).persist(bulkSubmission);
        verify(mockTargetApplicationDao).getTargetApplicationByCode(anyString());
        verify(mockConcurrencyMap).get(key);
        verify(mockBulkCustomerDao).getBulkCustomerBySdtId(10);
        verify(mockGenericDao).fetch(IServiceRequest.class, 1);
    }



   @Test
    void testSaveBulkSubmissionTargetFoundIsEnqueueableSetToFalse() throws IOException {

        SdtContext.getContext().setRawInXml(Utilities.getRawXml(PATH_TEST_RESOURCES, TEST_XML_VALID_2));
        ITargetApplication targetApplication = new TargetApplication();
        IIndividualRequest individualRequestMock = mock(IIndividualRequest.class);

        List<IIndividualRequest> individualRequests = new ArrayList<>();
        individualRequests.add(individualRequestMock);

        // Mock the serviceRequest fetch
        final IServiceRequest serviceRequest = new ServiceRequest();
        when(mockGenericDao.fetch(IServiceRequest.class, 1)).thenReturn(serviceRequest);

        // Activate Mock Generic Dao
        final IBulkSubmission bulkSubmission = this.createBulkSubmission();
        when(mockBulkCustomerDao.getBulkCustomerBySdtId(10)).thenReturn(bulkSubmission.getBulkCustomer());

        bulkSubmission.setIndividualRequests(individualRequests);

        final String key =
            bulkSubmission.getBulkCustomer().getSdtCustomerId() + bulkSubmission.getCustomerReference();

        // Setup concurrency map as if validator had done it.
        IInFlightMessage inFlightMessage = new InFlightMessage();
        inFlightMessage.setCompetingThreads(new HashMap<>());
        inFlightMessage.getCompetingThreads().put(Thread.currentThread(), Thread.currentThread());

        when(mockConcurrencyMap.get(key)).thenReturn(inFlightMessage);

        // Put a dummy value into the SdtContext
        SdtContext.getContext().setServiceRequestId(1L);

        when(mockTargetApplicationDao.getTargetApplicationByCode(anyString())).thenReturn(targetApplication);
        when(individualRequestMock.getRequestStatus()).thenReturn("Rejected");

        when(individualRequestMock.isEnqueueable()).thenReturn(false);

        // Call the bulk submission service
        bulkSubmissionService.saveBulkSubmission(bulkSubmission);

        verify(mockTargetApplicationDao).getTargetApplicationByCode(anyString());
        verify(mockConcurrencyMap).get(key);
        verify(mockBulkCustomerDao).getBulkCustomerBySdtId(10);
        verify(mockGenericDao).fetch(IServiceRequest.class, 1);
        verify(individualRequestMock).isEnqueueable();
        verify(individualRequestMock).getRequestStatus();
    }


    /**
     * This method tests bulk submission with multiple individual request containing
     * 2 valid and 1 invalid request.
     *
     * @throws IOException if there is any issue
     */
    @Test
    void testSubmissionWithMultipleRequests() throws IOException {

        final String rawXml = Utilities.getRawXml(PATH_TEST_RESOURCES, TEST_XML_VALID_3);
        SdtContext.getContext().setRawInXml(rawXml);

        // Activate Mock Generic Dao
        final IBulkSubmission bulkSubmission = this.createBulkSubmission();
        addValidIndividualRequest(bulkSubmission, "ICustReq124");
        addValidIndividualRequest(bulkSubmission, "ICustReq125");


        // Mock the serviceRequest fetch
        final IServiceRequest serviceRequest = new ServiceRequest();
        when(mockGenericDao.fetch(IServiceRequest.class, 1)).thenReturn(serviceRequest);

        when(mockBulkCustomerDao.getBulkCustomerBySdtId(10)).thenReturn(bulkSubmission.getBulkCustomer());

        final String key =
                bulkSubmission.getBulkCustomer().getSdtCustomerId() + bulkSubmission.getCustomerReference();

        // Setup concurrency map as if validator had done it.
        IInFlightMessage inFlightMessage = new InFlightMessage();
        inFlightMessage.setCompetingThreads(new HashMap<>());
        inFlightMessage.getCompetingThreads().put(Thread.currentThread(), Thread.currentThread());

        when(mockConcurrencyMap.get(key)).thenReturn(inFlightMessage);

        // Put a dummy value into the SdtContext
        SdtContext.getContext().setServiceRequestId(1L);

        // Call the bulk submission service
        bulkSubmissionService.saveBulkSubmission(bulkSubmission);

        // Verify the Mock
        verify(mockGenericDao).persist(bulkSubmission);
        verify(mockConcurrencyMap).get(key);
        verify(mockBulkCustomerDao).getBulkCustomerBySdtId(10);
        verify(mockGenericDao).fetch(IServiceRequest.class, 1);
    }

    /**
     * This method tests bulk submission concurrency issue
     * 2 valid and 1 invalid request.
     *
     * @throws IOException if there is any issue
     */
    @Test
    void testSubmissionWithConcurrenyIssue() throws IOException {

        final String rawXml = Utilities.getRawXml(PATH_TEST_RESOURCES, TEST_XML_VALID_3);
        SdtContext.getContext().setRawInXml(rawXml);

        // Activate Mock Generic Dao
        final IBulkSubmission bulkSubmission = this.createBulkSubmission();
        addValidIndividualRequest(bulkSubmission, "ICustReq124");
        addValidIndividualRequest(bulkSubmission, "ICustReq125");

        mockGenericDao.persist(bulkSubmission);

        // Mock the serviceRequest fetch
        final IServiceRequest serviceRequest = new ServiceRequest();
        when(mockGenericDao.fetch(IServiceRequest.class, 1)).thenReturn(serviceRequest);

        when(mockBulkCustomerDao.getBulkCustomerBySdtId(10)).thenReturn(bulkSubmission.getBulkCustomer());

        final String key =
                bulkSubmission.getBulkCustomer().getSdtCustomerId() + bulkSubmission.getCustomerReference();

        // Setup concurrency map as if validator had done it.
        IInFlightMessage inFlightMessage = new InFlightMessage();
        inFlightMessage.setSdtBulkReference("SDTBULKREFERENCE");
        inFlightMessage.setCompetingThreads(new HashMap<>());
        inFlightMessage.getCompetingThreads().put(Thread.currentThread(), Thread.currentThread());

        when(mockConcurrencyMap.get(key)).thenReturn(inFlightMessage);

        final String errorCodeStr = IErrorMessage.ErrorCode.DUP_CUST_FILEID.toString();
        final IErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setErrorCode(errorCodeStr);
        errorMessage.setErrorDescription("Some Description");
        errorMessage.setErrorText("Some Text");

        when(mockErrorMessagesCache.getValue(IErrorMessage.class, errorCodeStr)).thenReturn(errorMessage);

        // Put a dummy value into the SdtContext
        SdtContext.getContext().setServiceRequestId(1L);

        try {
            // Call the bulk submission service
            bulkSubmissionService.saveBulkSubmission(bulkSubmission);

            fail("Should have thrown exception");
        } catch (final Exception e) {
            if (!(e instanceof CustomerReferenceNotUniqueException) ||
                    !e.getMessage().equals("Failed with code [DUP_CUST_FILEID]; message[Some Text]")) {
                fail("Unexpected exception returned" + e.getStackTrace());
            }
        }
    }

    /**
     * @return Bulk Submission object for the testing.
     */
    private IBulkSubmission createBulkSubmission() {

        final IBulkSubmission bulkSubmission = new BulkSubmission();
        final IBulkCustomer bulkCustomer = new BulkCustomer();
        final ITargetApplication targetApp = new TargetApplication();

        targetApp.setId(1L);
        targetApp.setTargetApplicationCode("MCOL");
        targetApp.setTargetApplicationName("TEST_TargetApp");
        final Set<IServiceRouting> serviceRoutings = new HashSet<>();

        final ServiceRouting serviceRouting = new ServiceRouting();
        serviceRouting.setId(1L);
        serviceRouting.setWebServiceEndpoint("MCOL_END_POINT");

        final IServiceType serviceType = new ServiceType();
        serviceType.setId(1L);
        serviceType.setName("RequestTest1");
        serviceType.setDescription("RequestTestDesc1");
        serviceType.setStatus("RequestTestStatus");

        serviceRouting.setServiceType(serviceType);

        serviceRoutings.add(serviceRouting);

        targetApp.setServiceRoutings(serviceRoutings);

        bulkSubmission.setTargetApplication(targetApp);

        bulkCustomer.setId(1L);
        bulkCustomer.setSdtCustomerId(10L);

        bulkSubmission.setBulkCustomer(bulkCustomer);

        bulkSubmission.setCreatedDate(LocalDateTime.now());
        bulkSubmission.setCustomerReference("TEST_CUST_REF");
        bulkSubmission.setId(1L);
        bulkSubmission.setNumberOfRequest(2);
        bulkSubmission.setPayload("TEST_XML".getBytes(StandardCharsets.UTF_8));
        bulkSubmission.setSubmissionStatus("SUBMITTED");

        final IndividualRequest individualRequest = new IndividualRequest();
        individualRequest.setCompletedDate(LocalDateTime.now());
        individualRequest.setCreatedDate(LocalDateTime.now());
        individualRequest.setCustomerRequestReference("ICustReq123");
        individualRequest.setId(1L);
        individualRequest.setRequestStatus(IndividualRequestStatus.RECEIVED.getStatus());

        bulkSubmission.addIndividualRequest(individualRequest);

        return bulkSubmission;
    }

    /**
     * @param customerReference the customer reference number
     * @param bulkSubmission    bulk submission
     */
    private void addValidIndividualRequest(final IBulkSubmission bulkSubmission, final String customerReference) {
        final IndividualRequest individualRequest = new IndividualRequest();
        individualRequest.setCompletedDate(LocalDateTime.now());
        individualRequest.setCreatedDate(LocalDateTime.now());
        individualRequest.setCustomerRequestReference(customerReference);
        individualRequest.setId(1L);
        individualRequest.setRequestStatus(IndividualRequestStatus.RECEIVED.getStatus());

        bulkSubmission.addIndividualRequest(individualRequest);
    }


    @Test
    public void setErrorMessagesCacheTest() throws IllegalAccessException, NoSuchFieldException {

        ICacheable errorMessagesCacheMock = Mockito.mock(ICacheable.class);

        bulkSubmissionService.setErrorMessagesCache(errorMessagesCacheMock);

        Field errorMessagesCacheField = BulkSubmissionService.class.getDeclaredField("errorMessagesCache");

        errorMessagesCacheField.setAccessible(true);

        ICacheable result = (ICacheable) errorMessagesCacheField.get(bulkSubmissionService);

        assertEquals(errorMessagesCacheMock, result, "The errorMessagesCache should be set to the expected object");
    }


    @Test
    public void setMockSdtBulkReferenceGeneratorTest() {

        ISdtBulkReferenceGenerator sdtBulkReferenceGeneratorMock = mock(SdtBulkReferenceGenerator.class);

        bulkSubmissionService.setSdtBulkReferenceGenerator(sdtBulkReferenceGeneratorMock);

        Object result = this.getAccessibleField(BulkSubmissionService.class,"sdtBulkReferenceGenerator",
                                                SdtBulkReferenceGenerator.class,bulkSubmissionService);

        assertEquals(sdtBulkReferenceGeneratorMock, result, "The sdtBulkReferenceGenerator should be set to the object");
    }

    @Test
    public void setTargetApplicationDaoTest() {

        ITargetApplicationDao targetApplicationDaoMock =mock(TargetApplicationDao.class);

        bulkSubmissionService.setTargetApplicationDao(targetApplicationDaoMock);

        ITargetApplicationDao result = bulkSubmissionService.getTargetApplicationDao();

        assertEquals(targetApplicationDaoMock, result, "The TargetApplicationDao should be set to the expected object");
    }

    @Test
    public void setIndividualRequestXmlParserTest() {

        IndividualRequestsXmlParser individualRequestsXmlParserMock = mock(IndividualRequestsXmlParser.class);

        bulkSubmissionService.setIndividualRequestsXmlparser(individualRequestsXmlParserMock);

        IndividualRequestsXmlParser result = bulkSubmissionService.getIndividualRequestsXmlParser();

        assertEquals(individualRequestsXmlParserMock, result, "The individualRequestsXmlParser was not set to the expected object.");
    }

    @Test
    public void setMessagingUtilityTest() {

        IMessagingUtility messagingUtilityMock = mock(MessagingUtility.class);

        bulkSubmissionService.setMessagingUtility(messagingUtilityMock);

        IMessagingUtility result = bulkSubmissionService.getMessagingUtility();

        assertEquals(messagingUtilityMock, result, "The MessagingUtility was not set to the expected object.");
    }

    @Test
    public void setGenericDaoTest() {

        IGenericDao genericDaoMock = mock(GenericDao.class);

        bulkSubmissionService.setGenericDao(genericDaoMock);

        IGenericDao result = bulkSubmissionService.getGenericDao();

        assertEquals(genericDaoMock, result, "The GenericDao was not set to the expected object.");
    }

    @Test
    public void setBulkCustomerDaoTest() {

        IBulkCustomerDao bulkCustomerDaoMock = mock(BulkCustomerDao.class);

        bulkSubmissionService.setBulkCustomerDao(bulkCustomerDaoMock);

        IBulkCustomerDao result = bulkSubmissionService.getBulkCustomerDao();

        assertEquals(bulkCustomerDaoMock, result, "The BulkCustomerDao was not set to the expected object.");
    }


    /**
     * Method to check logging message in log list
     * @param logList
     * @param message
     * @return boolean
     */
    private static boolean verifyLog(List<ILoggingEvent> logList, String message) {
        boolean verifyLog = false;
        for (ILoggingEvent log : logList)
            if (log.getFormattedMessage().contains(message)) {
                verifyLog = true;
                break;
            }
        return verifyLog;
    }
}
