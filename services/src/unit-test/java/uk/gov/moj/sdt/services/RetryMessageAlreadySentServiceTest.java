package uk.gov.moj.sdt.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.moj.sdt.dao.api.IIndividualRequestDao;
import uk.gov.moj.sdt.domain.BulkCustomer;
import uk.gov.moj.sdt.domain.BulkSubmission;
import uk.gov.moj.sdt.domain.GlobalParameter;
import uk.gov.moj.sdt.domain.IndividualRequest;
import uk.gov.moj.sdt.domain.ServiceRouting;
import uk.gov.moj.sdt.domain.ServiceType;
import uk.gov.moj.sdt.domain.TargetApplication;
import uk.gov.moj.sdt.domain.api.IBulkCustomer;
import uk.gov.moj.sdt.domain.api.IBulkSubmission;
import uk.gov.moj.sdt.domain.api.IGlobalParameter;
import uk.gov.moj.sdt.domain.api.IIndividualRequest;
import uk.gov.moj.sdt.domain.api.IServiceRouting;
import uk.gov.moj.sdt.domain.api.IServiceType;
import uk.gov.moj.sdt.domain.api.ITargetApplication;
import uk.gov.moj.sdt.domain.cache.api.ICacheable;
import uk.gov.moj.sdt.services.utils.api.IMessagingUtility;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

/**
 * Test class for the RetryMessageAlreadySentService.
 *
 * @author Manoj Kulkarni
 */
@ExtendWith(MockitoExtension.class)
class RetryMessageAlreadySentServiceTest extends AbstractSdtUnitTestBase {
    /**
     * Mocked Individual Request Dao object.
     */
    @Mock
    private IIndividualRequestDao mockIndividualRequestDao;

    /**
     * The mocked ICacheable reference to the global parameters cache.
     */
    @Mock
    private ICacheable mockCacheable;

    /**
     * Mocked messaging utility reference.
     */
    @Mock
    private IMessagingUtility mockMessagingUtility;

    /**
     * RetryMessageAlreadySentService instance to be tested.
     */
    private RetryMessageAlreadySentService messageTaskService;

    private static final String MAX_FORWARDING_ATTEMPTS = "MAX_FORWARDING_ATTEMPTS";
    private static final String SDT_REQUEST_REF = "TEST_1";
    private static final String DEAD_LETTER_SDT_REQUEST_REF = "TEST_2";
    private static final String STATUS_FORWARDED = "Forwarded";
    private static final String FORWARDING_ATTEMPTS_ON_INDIVIDUAL_REQUEST = "Forwarding attempts on individual request";
    private static final String TEST_COMPLETED_SUCCESSFULLY = "Test completed successfully";
    private static final String STATUS_SET_TO_RECEIVED = "Status set to Received";

    private static final String INDIVIDUAL_REQUEST_DAO_SHOULD_BE_SET_CORRECTLY =
        "The setIndividualRequestDao should be set correctly";

    /**
     * Method to do any pre-test set-up.
     */
    @BeforeEach
    @Override
    public void setUp() {
        messageTaskService = new RetryMessageAlreadySentService(mockIndividualRequestDao,
                                                                mockMessagingUtility,
                                                                mockCacheable);
    }

    /**
     * This method tests the all successful scenario of the queue pending message
     * when the forwarding attempts of individual request is same as the max forwarding attempts.
     */
    @Test
    void queuePendingMessageMaxForwardingAttempts() {
        final IIndividualRequest individualRequest = new IndividualRequest();

        // Set-up the individual request object

        individualRequest.setSdtRequestReference(SDT_REQUEST_REF);
        setUpIndividualRequest(individualRequest);
        individualRequest.setRequestStatus(STATUS_FORWARDED);
        individualRequest.setForwardingAttempts(3);

        final List<IIndividualRequest> individualRequests = new ArrayList<>();
        individualRequests.add(individualRequest);

        final IGlobalParameter maxForwardingAttemptsParam = new GlobalParameter();
        maxForwardingAttemptsParam.setName(MAX_FORWARDING_ATTEMPTS);
        maxForwardingAttemptsParam.setValue("3");
        when(this.mockCacheable.getValue(IGlobalParameter.class, MAX_FORWARDING_ATTEMPTS)).thenReturn(
                maxForwardingAttemptsParam);

        final int maxForwardingAttempts = Integer.parseInt(maxForwardingAttemptsParam.getValue());

        when(mockIndividualRequestDao.getPendingIndividualRequests(maxForwardingAttempts)).thenReturn(
                individualRequests);

        for (IIndividualRequest individualRequestObj : individualRequests) {
            mockMessagingUtility.enqueueRequest(individualRequestObj);

            // Re-set the forwarding attempts on the individual request.
            individualRequestObj.resetForwardingAttempts();
        }

        mockIndividualRequestDao.persistBulk(individualRequests);

        this.messageTaskService.queueMessages();

        verify(mockIndividualRequestDao).getPendingIndividualRequests(maxForwardingAttempts);
        verify(mockCacheable).getValue(IGlobalParameter.class, MAX_FORWARDING_ATTEMPTS);
        verify(mockMessagingUtility, times(2)).enqueueRequest(any());

        assertEquals(0, individualRequests.get(0)
                .getForwardingAttempts(), FORWARDING_ATTEMPTS_ON_INDIVIDUAL_REQUEST);
        assertEquals(IIndividualRequest.IndividualRequestStatus.RECEIVED
                .getStatus(), individualRequests.get(0).getRequestStatus(), STATUS_SET_TO_RECEIVED);

        assertTrue(true, TEST_COMPLETED_SUCCESSFULLY);
    }

    /**
     * This method tests the all successful scenario of the queue pending message
     * when the forwarding attempts of individual request has exceeded the max forwarding attempts.
     */
    @Test
    void queuePendingMessageExceededForwardingAttempts() {
        final IIndividualRequest individualRequest = new IndividualRequest();

        // Set-up the individual request object

        individualRequest.setSdtRequestReference(SDT_REQUEST_REF);
        setUpIndividualRequest(individualRequest);
        individualRequest.setRequestStatus(STATUS_FORWARDED);
        individualRequest.setForwardingAttempts(5);

        final List<IIndividualRequest> individualRequests = new ArrayList<>();
        individualRequests.add(individualRequest);

        final IGlobalParameter maxForwardingAttemptsParam = new GlobalParameter();
        maxForwardingAttemptsParam.setName(MAX_FORWARDING_ATTEMPTS);
        maxForwardingAttemptsParam.setValue("3");
        when(this.mockCacheable.getValue(IGlobalParameter.class, MAX_FORWARDING_ATTEMPTS)).thenReturn(
                maxForwardingAttemptsParam);

        final int maxForwardingAttempts = Integer.parseInt(maxForwardingAttemptsParam.getValue());

        when(mockIndividualRequestDao.getPendingIndividualRequests(maxForwardingAttempts)).thenReturn(
                individualRequests);

        for (IIndividualRequest individualRequestObj : individualRequests) {

            mockMessagingUtility.enqueueRequest(individualRequestObj);

            // Re-set the forwarding attempts on the individual request.
            individualRequestObj.resetForwardingAttempts();
        }

        mockIndividualRequestDao.persistBulk(individualRequests);

        this.messageTaskService.queueMessages();

        verify(mockIndividualRequestDao).getPendingIndividualRequests(maxForwardingAttempts);
        verify(mockCacheable).getValue(IGlobalParameter.class, MAX_FORWARDING_ATTEMPTS);
        verify(mockMessagingUtility, times(2)).enqueueRequest(any());

        assertEquals(0, individualRequests.get(0)
                .getForwardingAttempts(), FORWARDING_ATTEMPTS_ON_INDIVIDUAL_REQUEST);
        assertEquals(IIndividualRequest.IndividualRequestStatus.RECEIVED
                .getStatus(), individualRequests.get(0).getRequestStatus(), STATUS_SET_TO_RECEIVED);

        assertTrue(true, TEST_COMPLETED_SUCCESSFULLY);
    }

    /**
     * This method tests the all successful scenario of the queue pending message
     * when the forwarding attempts of individual request has exceeded the max forwarding attempts.
     * The method also tests for scenario where the request marked with dead letter
     * flag is ignored and not picked up by the service method.
     */
    @Test
    void queuePendingDLQMessageExceededForwardingAttempts() {
        final IIndividualRequest individualRequest = new IndividualRequest();

        // Set-up the individual request object
        individualRequest.setSdtRequestReference(SDT_REQUEST_REF);
        setUpIndividualRequest(individualRequest);
        individualRequest.setRequestStatus(STATUS_FORWARDED);
        individualRequest.setForwardingAttempts(5);

        final IIndividualRequest dlqIndividualRequest = new IndividualRequest();
        dlqIndividualRequest.setSdtRequestReference(DEAD_LETTER_SDT_REQUEST_REF);
        setUpIndividualRequest(dlqIndividualRequest);
        dlqIndividualRequest.setRequestStatus(STATUS_FORWARDED);
        dlqIndividualRequest.setForwardingAttempts(5);
        dlqIndividualRequest.setDeadLetter(true);

        final List<IIndividualRequest> individualRequests = new ArrayList<>();
        individualRequests.add(individualRequest);
        individualRequests.add(dlqIndividualRequest);

        final IGlobalParameter maxForwardingAttemptsParam = new GlobalParameter();
        maxForwardingAttemptsParam.setName(MAX_FORWARDING_ATTEMPTS);
        maxForwardingAttemptsParam.setValue("3");
        when(this.mockCacheable.getValue(IGlobalParameter.class, MAX_FORWARDING_ATTEMPTS)).thenReturn(
                maxForwardingAttemptsParam);

        final int maxForwardingAttempts = Integer.parseInt(maxForwardingAttemptsParam.getValue());

        final List<IIndividualRequest> returnedListOfRequests = new ArrayList<>();
        returnedListOfRequests.add(individualRequest);

        when(mockIndividualRequestDao.getPendingIndividualRequests(maxForwardingAttempts)).thenReturn(
                returnedListOfRequests);

        for (IIndividualRequest individualRequestObj : returnedListOfRequests) {

            mockMessagingUtility.enqueueRequest(individualRequestObj);

            // Re-set the forwarding attempts on the individual request.
            individualRequestObj.resetForwardingAttempts();
        }

        mockIndividualRequestDao.persistBulk(returnedListOfRequests);

        this.messageTaskService.queueMessages();

        verify(mockIndividualRequestDao).getPendingIndividualRequests(maxForwardingAttempts);
        verify(mockCacheable).getValue(IGlobalParameter.class, MAX_FORWARDING_ATTEMPTS);
        verify(mockMessagingUtility, times(2)).enqueueRequest(any());

        assertEquals(0, returnedListOfRequests.get(0)
                .getForwardingAttempts(), FORWARDING_ATTEMPTS_ON_INDIVIDUAL_REQUEST);
        assertEquals(IIndividualRequest.IndividualRequestStatus.RECEIVED
                .getStatus(), returnedListOfRequests.get(0).getRequestStatus(), STATUS_SET_TO_RECEIVED);

        assertTrue(true, TEST_COMPLETED_SUCCESSFULLY);
    }

    /**
     * This method tests the queue pending message method and the condition that
     * there are no pending messages to return from database.
     */
    @Test
    void queuePendingMessageWhenNoMessageFound() {
        final List<IIndividualRequest> individualRequests = new ArrayList<>();

        final IGlobalParameter maxForwardingAttemptsParam = new GlobalParameter();
        maxForwardingAttemptsParam.setName(MAX_FORWARDING_ATTEMPTS);
        maxForwardingAttemptsParam.setValue("3");
        when(this.mockCacheable.getValue(IGlobalParameter.class, MAX_FORWARDING_ATTEMPTS)).thenReturn(
                maxForwardingAttemptsParam);

        final int maxForwardingAttempts = Integer.parseInt(maxForwardingAttemptsParam.getValue());

        when(mockIndividualRequestDao.getPendingIndividualRequests(maxForwardingAttempts)).thenReturn(
                individualRequests);

        this.messageTaskService.queueMessages();

        verify(mockIndividualRequestDao).getPendingIndividualRequests(maxForwardingAttempts);

        assertTrue(true, "Method test completed successfully");
    }

    /**
     * This method tests the all successful scenario of the queue pending message
     * when the max forwarding attempts global parameter is not defined in the database.
     */
    @Test
    void queuePendingMessageForwardingAttemptsNotAvailable() {
        final IIndividualRequest individualRequest = new IndividualRequest();

        // Set-up the individual request object

        individualRequest.setSdtRequestReference(SDT_REQUEST_REF);
        setUpIndividualRequest(individualRequest);
        individualRequest.setRequestStatus(STATUS_FORWARDED);
        individualRequest.setForwardingAttempts(3);

        final List<IIndividualRequest> individualRequests = new ArrayList<>();
        individualRequests.add(individualRequest);

        when(this.mockCacheable.getValue(IGlobalParameter.class, MAX_FORWARDING_ATTEMPTS)).thenReturn(null);

        final int maxForwardingAttempts = 3;

        when(mockIndividualRequestDao.getPendingIndividualRequests(maxForwardingAttempts)).thenReturn(
                individualRequests);

        for (IIndividualRequest individualRequestObj : individualRequests) {
            mockMessagingUtility.enqueueRequest(individualRequestObj);

            // Re-set the forwarding attempts on the individual request.
            individualRequestObj.resetForwardingAttempts();
        }

        mockIndividualRequestDao.persistBulk(individualRequests);

        this.messageTaskService.queueMessages();

        verify(mockIndividualRequestDao).getPendingIndividualRequests(maxForwardingAttempts);
        verify(mockCacheable).getValue(IGlobalParameter.class, MAX_FORWARDING_ATTEMPTS);
        verify(mockMessagingUtility, times(2)).enqueueRequest(any());

        assertEquals(0, individualRequests.get(0)
                .getForwardingAttempts(), FORWARDING_ATTEMPTS_ON_INDIVIDUAL_REQUEST);
        assertEquals(IIndividualRequest.IndividualRequestStatus.RECEIVED
                .getStatus(), individualRequests.get(0).getRequestStatus(), STATUS_SET_TO_RECEIVED);

        assertTrue(true, TEST_COMPLETED_SUCCESSFULLY);
    }

    /**
     * Set up a valid individual request object.
     *
     * @param request the individual request
     */
    private void setUpIndividualRequest(final IIndividualRequest request) {
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
        bulkSubmission.setCustomerReference("TEST_CUST_REF");
        bulkSubmission.setId(1L);
        bulkSubmission.setNumberOfRequest(1);
        final List<IIndividualRequest> requests = new ArrayList<>();
        requests.add(request);

        bulkSubmission.setIndividualRequests(requests);

        request.setBulkSubmission(bulkSubmission);
    }

    @Test
    public void setIndividualRequestDaoTest() {

        IIndividualRequestDao individualRequestDaoMock = mock(IIndividualRequestDao.class);
        messageTaskService.setIndividualRequestDao(individualRequestDaoMock);

        Object result = this.getAccessibleField(RetryMessageAlreadySentService.class, "individualRequestDao",
                                                IIndividualRequestDao.class, messageTaskService);

        assertEquals(individualRequestDaoMock, result, INDIVIDUAL_REQUEST_DAO_SHOULD_BE_SET_CORRECTLY);
    }

    @Test
    public void setMessagingUtilityTest() {

        IMessagingUtility messagingUtilityMock = mock(IMessagingUtility.class);
        messageTaskService.setMessagingUtility(messagingUtilityMock);

        Object result = this.getAccessibleField(RetryMessageAlreadySentService.class, "messagingUtility",
                                                IMessagingUtility.class, messageTaskService);

        assertEquals(messagingUtilityMock, result, "MessagingUtility should be correctly set");
    }

    @Test
    public void setGlobalParametersCacheTest() {

        ICacheable globalParametersCacheMock = mock(ICacheable.class);
        messageTaskService.setGlobalParametersCache(globalParametersCacheMock);

        Object result = this.getAccessibleField(RetryMessageAlreadySentService.class, "globalParametersCache",
                                                ICacheable.class, messageTaskService);

        assertEquals(globalParametersCacheMock, result, "GlobalParameters should be correctly set");
    }

}
