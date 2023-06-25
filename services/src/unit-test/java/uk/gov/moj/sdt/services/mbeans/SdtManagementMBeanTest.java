package uk.gov.moj.sdt.services.mbeans;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.util.ReflectionUtils;
import uk.gov.moj.sdt.dao.api.IIndividualRequestDao;
import uk.gov.moj.sdt.domain.IndividualRequest;
import uk.gov.moj.sdt.domain.api.IIndividualRequest;
import uk.gov.moj.sdt.services.api.ITargetApplicationSubmissionService;
import uk.gov.moj.sdt.services.utils.api.IMessagingUtility;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;
import uk.gov.moj.sdt.utils.mbeans.api.ISdtManagementMBean;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class for the SdtManagementMBean.
 *
 * @author Manoj Kulkarni
 */
@ExtendWith(MockitoExtension.class)
class SdtManagementMBeanTest extends AbstractSdtUnitTestBase {

    /**
     * Mock Individual Request Dao to perform operations on the individual request object.
     */
    @Mock
    private IIndividualRequestDao mockIndividualRequestDao;

    /**
     * This mock messaging utility reference for testing.
     */
    @Mock
    private IMessagingUtility mockMessagingUtility;

    /**
     * This mock variable holding the target application submission service.
     */
    @Mock
    private ITargetApplicationSubmissionService mockTargetAppSubmissionService;

    /**
     * Success message to return after successfully processing the SDT individual request.
     */
    private static final String OK_MESSAGE = "OK";

    /**
     * Error message to return if the mandatory parameters are missing.
     */
    private static final String MANDATORY_PARAMETERS_ERR_MSG =
            "SDT Request Reference and the Request Status should be supplied.";

    /**
     * Error message to return if the invalid request status values are supplied.
     */
    private static final String INVALID_PARAM_VALUES_MSG =
            "Invalid Request Status supplied. The Request Status should be either REJECTED or FORWARDED";

    /**
     * Error message to return if the Sdt Request Reference parameter does not correspond
     * to an Individual Request record in the database.
     */
    private static final String INVALID_SDT_REQUEST_REF_MSG = "SDT Request Reference supplied does not exist.";

    /**
     * Error message to return if the SDT Request Reference parameter is not marked as Dead Letter
     * Queue flag.
     */
    private static final String SDT_REQUEST_NOT_ON_DLQ_MSG =
            "SDT Request Reference supplied is not marked as dead letter and cannot be processed.";

    /**
     * Queue name for testing.
     */
    private static final String TEST_QUEUE_NAME = "TestQ";

    /**
     * Pool size for testing.
     */
    private static final int TEST_POOL_SIZE = 10;

    /**
     * Sdt Request Reference for testing.
     */
    private static final String TEST_SDT_REQ_REF = "Sdt_Req_Test";

    private static final String EXPECTED_AN_ERROR_MESSAGE = "Expected an error message.";

    private static final String RETURN_MESSAGE_SHOULD_HAVE_BEEN_AN_ERROR =
            "The return message should have been an error";

    /**
     * Number of minutes to check that the request is not gone stale.
     */
    private static final int TEST_STALE_DURATION = 90;

    /**
     * The SdtManagementMBean that is under test.
     */
    private ISdtManagementMBean sdtManagementMBean;

    @Mock
    private DefaultMessageListenerContainer messageListenerContainer;

    @Mock
    private IMessagingUtility messagingUtility;

    @Mock
    private ITargetApplicationSubmissionService targetAppSubmissionService;

    /**
     * Method to do any pre-test set-up.
     */
    @BeforeEach
    @Override
    public void setUp() {
        sdtManagementMBean = new SdtManagementMBean(messageListenerContainer,
                                                    mockIndividualRequestDao,
                                                    messagingUtility,
                                                    targetAppSubmissionService);
        // Instantiate all the mocked objects and set them up in the MBean
        this.setPrivateField(SdtManagementMBean.class, sdtManagementMBean, "individualRequestDao",
                IIndividualRequestDao.class, mockIndividualRequestDao);

        this.setPrivateField(SdtManagementMBean.class, sdtManagementMBean, "messagingUtility",
                IMessagingUtility.class, mockMessagingUtility);

        this.setPrivateField(SdtManagementMBean.class, sdtManagementMBean, "targetAppSubmissionService",
                ITargetApplicationSubmissionService.class, mockTargetAppSubmissionService);
    }

    /**
     * Test method for the uncache method.
     */
    @Test
    void uncacheTest() {
        assertEquals(0, this.sdtManagementMBean.getCacheResetControl(),
                "The cache re-set counter should be zero.");

        this.sdtManagementMBean.uncache();

        assertEquals(1, this.sdtManagementMBean.getCacheResetControl(),
                "The cache re-set counter should be incremented.");
    }

    /**
     * Test method for the setMdbPoolSize. This method will check that if the
     * messsage listener container is not set then it returns an appropriate message.
     */
    @Test
    void setMdbPoolSizeUnknownMessageContainer() {
        final String returnVal = this.sdtManagementMBean.setMdbPoolSize(TEST_QUEUE_NAME, TEST_POOL_SIZE);

        assertEquals("mdb pool [" + TEST_QUEUE_NAME + "] not found", returnVal,
                RETURN_MESSAGE_SHOULD_HAVE_BEEN_AN_ERROR);
    }

    /**
     * Test method for setMdbPoolSize. This method will check that if the
     * pool size is invalid, an appropriate error message is returned.
     */
    @Test
    void setMdbPoolSizePoolSizeInvalid() {
        final DefaultMessageListenerContainer messageListenerContainerObj = new DefaultMessageListenerContainer();
        messageListenerContainerObj.setDestinationName(TEST_QUEUE_NAME);

        this.sdtManagementMBean.setMessageListenerContainer(messageListenerContainerObj);

        String returnVal = this.sdtManagementMBean.setMdbPoolSize(TEST_QUEUE_NAME, TEST_POOL_SIZE + 41);

        assertEquals("MDB pool size can only be set between 1 and 50.", returnVal,
                RETURN_MESSAGE_SHOULD_HAVE_BEEN_AN_ERROR);

        returnVal = this.sdtManagementMBean.setMdbPoolSize(TEST_QUEUE_NAME, TEST_POOL_SIZE - 10);

        assertEquals("MDB pool size can only be set between 1 and 50.", returnVal,
                RETURN_MESSAGE_SHOULD_HAVE_BEEN_AN_ERROR);
    }

    /**
     * Test method for setMdbPoolSize. A test for the positive conditions scenario.
     */
    @Test
    void setMdbPoolSize() {
        final DefaultMessageListenerContainer messageListenerContainerObj = new DefaultMessageListenerContainer();
        messageListenerContainerObj.setDestinationName(TEST_QUEUE_NAME);

        this.sdtManagementMBean.setMessageListenerContainer(messageListenerContainerObj);

        final String returnVal = this.sdtManagementMBean.setMdbPoolSize(TEST_QUEUE_NAME, TEST_POOL_SIZE);

        assertEquals("mdb pool [" + TEST_QUEUE_NAME + "] size changed from 1 to " + TEST_POOL_SIZE, returnVal,
                "The return message is incorrect");

        assertEquals(10, messageListenerContainerObj.getMaxConcurrentConsumers(),
                "The message listener container max concurrent pool size should have been changed");
    }

    /**
     * Test method for requeueOldIndividualRequests. This method tests the scenario
     * where there are no old individual requests to process.
     */
    @Test
    void requeueOldRequestsNotFound() {
        final List<IIndividualRequest> individualRequests = new ArrayList<>();
        when(mockIndividualRequestDao.getStaleIndividualRequests(TEST_STALE_DURATION)).thenReturn(individualRequests);

        this.sdtManagementMBean.requeueOldIndividualRequests(TEST_STALE_DURATION);

        verify(mockIndividualRequestDao).getStaleIndividualRequests(TEST_STALE_DURATION);

        assertTrue(true, "Not expected to call the method to requeue requests");
    }

    /**
     * Test method for requeueOldIndividualRequests. This method tests the scenario
     * where there are some old individual requests to process.
     */
    @Test
    void requeueOldRequests() {
        final IIndividualRequest individualRequest = new IndividualRequest();
        individualRequest.setSdtRequestReference("SDT_REQ_1");
        individualRequest.setForwardingAttempts(3);
        final List<IIndividualRequest> individualRequests = new ArrayList<>();
        individualRequests.add(individualRequest);

        when(mockIndividualRequestDao.getStaleIndividualRequests(TEST_STALE_DURATION)).thenReturn(
                individualRequests);

        mockMessagingUtility.enqueueRequest(individualRequest);

        individualRequest.resetForwardingAttempts();

        mockIndividualRequestDao.persistBulk(individualRequests);

        this.sdtManagementMBean.requeueOldIndividualRequests(TEST_STALE_DURATION);

        verify(mockIndividualRequestDao).getStaleIndividualRequests(TEST_STALE_DURATION);
        verify(mockMessagingUtility, atLeastOnce()).enqueueRequest(individualRequest);

        assertTrue(true, "All tests passed");
    }

    /**
     * Test method for processDLQRequest. Calls the method with null values for
     * the Sdt Request Reference and the Request Status.
     */
    @Test
    void processDLQRequestWithNullParams() {
        final String returnVal = this.sdtManagementMBean.processDlqRequest(null, null);

        assertEquals(MANDATORY_PARAMETERS_ERR_MSG, returnVal, EXPECTED_AN_ERROR_MESSAGE);
    }

    /**
     * Test method for processDLQRequest. Calls the method with valid value for
     * the Sdt Request Reference and Invalid value for the Request Status.
     */
    @Test
    void processDLQRequestWithInvalidStatus() {
        final String returnVal = this.sdtManagementMBean.processDlqRequest(TEST_SDT_REQ_REF, "RECEIVED");

        assertEquals(INVALID_PARAM_VALUES_MSG, returnVal, EXPECTED_AN_ERROR_MESSAGE);
    }

    /**
     * Test method for processDLQRequest. Calls the method with invalid value for the
     * Sdt Request Reference such that does not exist in database.
     */
    @Test
    void processDLQRequestWithInvalidSdtRequest() {
        final String invalidSdtReqReference = "INVALID_SDT_REQ_REF";

        when(mockIndividualRequestDao.getRequestBySdtReference(invalidSdtReqReference)).thenReturn(null);

        final String returnVal =
                this.sdtManagementMBean.processDlqRequest(invalidSdtReqReference,
                        IIndividualRequest.IndividualRequestStatus.FORWARDED.getStatus());

        verify(mockIndividualRequestDao).getRequestBySdtReference(invalidSdtReqReference);

        assertEquals(INVALID_SDT_REQUEST_REF_MSG, returnVal, EXPECTED_AN_ERROR_MESSAGE);
    }

    /**
     * Test method for processDLQRequest. Test the scenario where the supplied
     * Sdt Request Reference is associated with an Individual Request but has the DLQ flag
     * false.
     */
    @Test
    void processDLQRequestNotOnDLQ() {
        final IIndividualRequest individualRequest = new IndividualRequest();
        individualRequest.setSdtRequestReference(TEST_SDT_REQ_REF);
        individualRequest.setDeadLetter(false);

        when(mockIndividualRequestDao.getRequestBySdtReference(TEST_SDT_REQ_REF)).thenReturn(individualRequest);

        final String returnVal =
                this.sdtManagementMBean.processDlqRequest(TEST_SDT_REQ_REF,
                        IIndividualRequest.IndividualRequestStatus.FORWARDED.getStatus());

        verify(mockIndividualRequestDao).getRequestBySdtReference(TEST_SDT_REQ_REF);

        assertEquals(SDT_REQUEST_NOT_ON_DLQ_MSG, returnVal, EXPECTED_AN_ERROR_MESSAGE);
    }

    /**
     * Test method for processDQLRequest. This method tests the status change
     * for an valid Individual Request to Forwarded.
     */
    @Test
    void processDLQRequestForwarded() {
        final IIndividualRequest individualRequest = new IndividualRequest();
        individualRequest.setSdtRequestReference(TEST_SDT_REQ_REF);
        individualRequest.setDeadLetter(true);
        individualRequest.setRequestStatus(IIndividualRequest.IndividualRequestStatus.FORWARDED.getStatus());
        individualRequest.setForwardingAttempts(3);

        assertEquals(IIndividualRequest.IndividualRequestStatus.FORWARDED.getStatus(),
                individualRequest.getRequestStatus(),
                "The status is not as expected");

        assertEquals(3, individualRequest.getForwardingAttempts(),
                "The forwarding attempts should be greater than zero");

        when(mockIndividualRequestDao.getRequestBySdtReference(TEST_SDT_REQ_REF)).thenReturn(individualRequest);

        mockTargetAppSubmissionService.processDLQRequest(individualRequest,
                IIndividualRequest.IndividualRequestStatus.FORWARDED.getStatus());

        doAnswer((Answer<Void>) invocation -> {
            ((IndividualRequest) invocation.getArgument(0))
                    .setRequestStatus (IIndividualRequest.IndividualRequestStatus.FORWARDED.getStatus ());
            ((IndividualRequest) invocation.getArgument(0)).setDeadLetter(false);
            ((IndividualRequest) invocation.getArgument(0)).setUpdatedDate(LocalDateTime.now());
            // required to be null for a void method
            return null;
        }).when(mockTargetAppSubmissionService).processDLQRequest(individualRequest,
                IIndividualRequest.IndividualRequestStatus.FORWARDED.getStatus());

        final String returnVal =
                this.sdtManagementMBean.processDlqRequest(TEST_SDT_REQ_REF,
                        IIndividualRequest.IndividualRequestStatus.FORWARDED.getStatus());

        verify(mockIndividualRequestDao).getRequestBySdtReference(TEST_SDT_REQ_REF);
        verify(mockTargetAppSubmissionService, atLeastOnce()).processDLQRequest(individualRequest,
                IIndividualRequest.IndividualRequestStatus.FORWARDED.getStatus());

        assertEquals(OK_MESSAGE, returnVal, "The return value is OK");
    }

    /**
     * Test method for processDQLRequest. This method tests the status change
     * for an valid Individual Request to Rejected.
     */
    @Test
    void processDLQRequestRejected() {
        final IIndividualRequest individualRequest = new IndividualRequest();
        individualRequest.setSdtRequestReference(TEST_SDT_REQ_REF);
        individualRequest.setDeadLetter(true);
        individualRequest.setRequestStatus(IIndividualRequest.IndividualRequestStatus.FORWARDED.getStatus());
        individualRequest.setForwardingAttempts(3);

        assertEquals(IIndividualRequest.IndividualRequestStatus.FORWARDED.getStatus(),
                individualRequest.getRequestStatus(), "The status is not as expected");

        assertEquals(3, individualRequest.getForwardingAttempts(),
                "The forwarding attempts should be greater than zero");

        when(mockIndividualRequestDao.getRequestBySdtReference(TEST_SDT_REQ_REF)).thenReturn(individualRequest);

        mockTargetAppSubmissionService.processDLQRequest(individualRequest,
                IIndividualRequest.IndividualRequestStatus.REJECTED.getStatus());

        doAnswer((Answer<Void>) invocation -> {
            ((IndividualRequest) invocation.getArgument(0))
                    .setRequestStatus(IIndividualRequest.IndividualRequestStatus.REJECTED.getStatus());
            ((IndividualRequest) invocation.getArgument(0)).setDeadLetter(false);
            ((IndividualRequest) invocation.getArgument(0)).setUpdatedDate(LocalDateTime.now());
            // required to be null for a void method
            return null;
        }).when(mockTargetAppSubmissionService).processDLQRequest(individualRequest,
                IIndividualRequest.IndividualRequestStatus.REJECTED.getStatus());

        final String returnVal =
                this.sdtManagementMBean.processDlqRequest(TEST_SDT_REQ_REF,
                        IIndividualRequest.IndividualRequestStatus.REJECTED.getStatus());

        verify(mockIndividualRequestDao).getRequestBySdtReference(TEST_SDT_REQ_REF);
        verify(mockTargetAppSubmissionService, atLeastOnce()).processDLQRequest(individualRequest,
                IIndividualRequest.IndividualRequestStatus.REJECTED.getStatus());

        assertEquals(OK_MESSAGE, returnVal, "The return value is OK");
    }

    /**
     * This is a method to modify a private field with no accessor methods.
     *
     * @param concreteClazz        this is the class with the field accessor being changed (e.g.
     *                             MyClazz.class)
     * @param classInstance        this is the instance with the field state being changed (e.g.
     *                             myClazz)
     * @param instanceVariableName this is the field name (e.g. "myString")
     * @param variableClazz        this is the class of the field's type (e.g. String.class)
     * @param newFieldState        the new value of the filed (e.g. "a new value")
     */
    public void setPrivateField(final Class<?> concreteClazz, final Object classInstance,
                                final String instanceVariableName, final Class<?> variableClazz,
                                final Object newFieldState) {
        final Field field = ReflectionUtils.findField(concreteClazz, instanceVariableName, variableClazz);
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, classInstance, newFieldState);
    }

}
