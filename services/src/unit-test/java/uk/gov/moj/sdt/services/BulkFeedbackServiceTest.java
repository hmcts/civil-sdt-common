package uk.gov.moj.sdt.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.moj.sdt.dao.BulkSubmissionDao;
import uk.gov.moj.sdt.dao.api.IBulkSubmissionDao;
import uk.gov.moj.sdt.domain.ErrorLog;
import uk.gov.moj.sdt.domain.TargetApplication;
import uk.gov.moj.sdt.domain.IndividualRequest;
import uk.gov.moj.sdt.domain.ServiceRouting;
import uk.gov.moj.sdt.domain.GlobalParameter;
import uk.gov.moj.sdt.domain.BulkCustomer;
import uk.gov.moj.sdt.domain.BulkSubmission;
import uk.gov.moj.sdt.domain.BulkFeedbackRequest;
import uk.gov.moj.sdt.domain.api.IServiceType;
import uk.gov.moj.sdt.domain.api.IErrorLog;
import uk.gov.moj.sdt.domain.api.IErrorMessage;
import uk.gov.moj.sdt.domain.api.ITargetApplication;
import uk.gov.moj.sdt.domain.api.IBulkCustomer;
import uk.gov.moj.sdt.domain.api.IBulkSubmission;
import uk.gov.moj.sdt.domain.api.IBulkFeedbackRequest;
import uk.gov.moj.sdt.domain.api.IServiceRouting;
import uk.gov.moj.sdt.domain.api.IGlobalParameter;
import uk.gov.moj.sdt.domain.api.IIndividualRequest.IndividualRequestStatus;
import uk.gov.moj.sdt.domain.ServiceType;
import uk.gov.moj.sdt.domain.cache.api.ICacheable;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;
import uk.gov.moj.sdt.utils.SdtContext;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class for BulkSubmissionService.
 *
 * @author Sally Vonka
 */
@ExtendWith(MockitoExtension.class)
class BulkFeedbackServiceTest extends AbstractSdtUnitTestBase {

    /**
     * Bulk Submission DAO property for looking up the bulk submission object.
     */
    @Mock
    private IBulkSubmissionDao mockBulkSubmissionDao;

    /**
     * Global parameter cache to retrieve data retention period.
     */
    @Mock
    private ICacheable mockGlobalParameterCache;

    /**
     * Bulk Feedback Service for testing.
     */
    private BulkFeedbackService bulkFeedbackService;

    /**
     * The IBulkFeedbackRequest.
     */
    private IBulkFeedbackRequest bulkFeedbackRequest;

    /**
     * Bulk Customer to use for the test.
     */
    private IBulkCustomer bulkCustomer;

    /**
     * The string reference for our bulk request.
     */
    private String reference = " Bulk reference in request ";

    /**
     * requestId.
     */
    private long requestId;

    /**
     * Data retention period.
     */
    private int dataRetentionPeriod;

    private static final String DAO_SHOULD_BE_SET_TO_OBJECT = "Dao should be set to object";

    private static final String CACHE_OBJECT_SHOULD_BE_SET = "GlobalParameterCache should be set to object";

    /**
     * Setup of the mock dao and injection of other objects.
     */
    @BeforeEach
    @Override
    public void setUp() {

        dataRetentionPeriod = 90;

        // create a bulk customer
        bulkCustomer = new BulkCustomer();
        bulkCustomer.setSdtCustomerId(12345L);

        bulkFeedbackRequest = new BulkFeedbackRequest();

        // Setup bulk request for test.
        bulkFeedbackRequest.setId(requestId);
        bulkFeedbackRequest.setSdtBulkReference(reference);
        bulkFeedbackRequest.setBulkCustomer(bulkCustomer);

        bulkFeedbackService = new BulkFeedbackService(mockBulkSubmissionDao, mockGlobalParameterCache);
    }

    @Test
    void testGetBulkFeedback() {

        final IGlobalParameter globalParameterData = new GlobalParameter();
        globalParameterData.setName(IGlobalParameter.ParameterKey.DATA_RETENTION_PERIOD.name());
        globalParameterData.setValue("90");
        when(mockGlobalParameterCache.getValue(IGlobalParameter.class,
                                               IGlobalParameter.ParameterKey.DATA_RETENTION_PERIOD.name())).thenReturn(globalParameterData);

        // Activate Mock Generic Dao
        final IBulkSubmission bulkSubmission = this.createBulkSubmission();
        addValidIndividualRequest(bulkSubmission, "ICustReq124", IndividualRequestStatus.RECEIVED.getStatus());
        addValidIndividualRequest(bulkSubmission, "ICustReq125", IndividualRequestStatus.REJECTED.getStatus());
        addValidIndividualRequest(bulkSubmission, "ICustReq126", IndividualRequestStatus.RECEIVED.getStatus());

        // Tell the mock dao to return this request
        when(mockBulkSubmissionDao.getBulkSubmissionBySdtRef(bulkCustomer, reference, dataRetentionPeriod))
                .thenReturn(bulkSubmission);
        bulkFeedbackService.getBulkFeedback(bulkFeedbackRequest);

        final Map<String, String> targetApplicationRespMap = SdtContext.getContext().getTargetApplicationRespMap();

        assertEquals(3, targetApplicationRespMap.size());

        verify(mockBulkSubmissionDao).getBulkSubmissionBySdtRef(bulkCustomer, reference, dataRetentionPeriod);

    }

    /**
     * @return Bulk Submission object for the testing.
     */
    private IBulkSubmission createBulkSubmission() {
        final IBulkSubmission bulkSubmission = new BulkSubmission();
        final IBulkCustomer iBulkCustomer = new BulkCustomer();
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

        iBulkCustomer.setId(1L);
        iBulkCustomer.setSdtCustomerId(10L);

        bulkSubmission.setBulkCustomer(iBulkCustomer);

        bulkSubmission.setCreatedDate(LocalDateTime.now());
        bulkSubmission.setCustomerReference("TEST_CUST_REF");
        bulkSubmission.setId(1L);
        bulkSubmission.setNumberOfRequest(2);
        bulkSubmission.setPayload("TEST_XML".getBytes());
        bulkSubmission.setSubmissionStatus("SUBMITTED");

        final IndividualRequest individualRequest = new IndividualRequest();
        individualRequest.setCompletedDate(LocalDateTime.now());
        individualRequest
                .setCreatedDate(LocalDateTime.now());
        individualRequest.setCustomerRequestReference("ICustReq123");
        individualRequest.setId(1L);
        individualRequest.setRequestStatus(IndividualRequestStatus.RECEIVED.getStatus());

        bulkSubmission.addIndividualRequest(individualRequest);

        return bulkSubmission;
    }

    /**
     * @param customerReference the customer reference number
     * @param bulkSubmission    bulk submission
     * @param status            of individual request
     */
    private void addValidIndividualRequest(final IBulkSubmission bulkSubmission, final String customerReference,
                                           final String status) {
        final IndividualRequest individualRequest = new IndividualRequest();
        individualRequest.setCompletedDate(LocalDateTime.now());
        individualRequest
                .setCreatedDate(LocalDateTime.now());
        individualRequest.setCustomerRequestReference(customerReference);
        individualRequest.setId(1L);
        individualRequest.setRequestStatus(status);
        if (IndividualRequestStatus.REJECTED.getStatus().equals(status)) {
            final IErrorLog errorLog =
                    new ErrorLog(
                        IErrorMessage.ErrorCode.DUP_CUST_REQID.name(),
                        "Duplicate Unique Request Identifier submitted {0}");
            individualRequest.setErrorLog(errorLog);
        }
        individualRequest.setTargetApplicationResponse("<response></response>".getBytes());

        bulkSubmission.addIndividualRequest(individualRequest);
    }

    @Test
    void testSetBulkSubmissionDao() {
        IBulkSubmissionDao bulkSubmissionDaoMock = mock(BulkSubmissionDao.class);
        bulkFeedbackService.setBulkSubmissionDao(bulkSubmissionDaoMock);

        IBulkSubmissionDao result = (IBulkSubmissionDao) getAccessibleField(BulkFeedbackService.class,
                                                                            "bulkSubmissionDao",
                                                                            IBulkSubmissionDao.class,
                                                                            bulkFeedbackService);

        assertEquals(bulkSubmissionDaoMock, result, DAO_SHOULD_BE_SET_TO_OBJECT);
    }

    @Test
    void testSetGlobalParametersCache() {

        ICacheable globalParameterCacheMock = mock(ICacheable.class);
        bulkFeedbackService.setGlobalParametersCache(globalParameterCacheMock);

        ICacheable result = (ICacheable) getAccessibleField(BulkFeedbackService.class,
                                                            "globalParametersCache",
                                                            ICacheable.class,
                                                            bulkFeedbackService);

        assertEquals(globalParameterCacheMock, result, CACHE_OBJECT_SHOULD_BE_SET);
    }

}
