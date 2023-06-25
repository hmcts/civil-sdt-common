package uk.gov.moj.sdt.consumers;

import uk.gov.moj.sdt.domain.BulkCustomer;
import uk.gov.moj.sdt.domain.BulkCustomerApplication;
import uk.gov.moj.sdt.domain.BulkSubmission;
import uk.gov.moj.sdt.domain.IndividualRequest;
import uk.gov.moj.sdt.domain.ServiceRouting;
import uk.gov.moj.sdt.domain.ServiceType;
import uk.gov.moj.sdt.domain.SubmitQueryRequest;
import uk.gov.moj.sdt.domain.TargetApplication;
import uk.gov.moj.sdt.domain.api.IBulkCustomer;
import uk.gov.moj.sdt.domain.api.IBulkCustomerApplication;
import uk.gov.moj.sdt.domain.api.IBulkSubmission;
import uk.gov.moj.sdt.domain.api.IIndividualRequest;
import uk.gov.moj.sdt.domain.api.IServiceRouting;
import uk.gov.moj.sdt.domain.api.IServiceType;
import uk.gov.moj.sdt.domain.api.ISubmitQueryRequest;
import uk.gov.moj.sdt.domain.api.ITargetApplication;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.ErrorType;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.StatusCodeType;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.StatusType;
import uk.gov.moj.sdt.ws._2013.sdt.targetapp.submitqueryrequestschema.HeaderType;
import uk.gov.moj.sdt.ws._2013.sdt.targetapp.submitqueryrequestschema.SubmitQueryRequestType;
import uk.gov.moj.sdt.ws._2013.sdt.targetapp.submitqueryresponseschema.SubmitQueryResponseType;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Base Consumer Test class.
 *
 * @author Mark Dathorne
 */
class ConsumerTestBase extends AbstractSdtUnitTestBase {

    /**
     * Submit query request instance for testing in the methods.
     */
    protected ISubmitQueryRequest submitQueryRequest;

    /**
     * Submit query Request type for the query request.
     */
    protected SubmitQueryRequestType submitQueryRequestType;

    /**
     * Connection time out constant.
     */
    protected static final long CONNECTION_TIME_OUT = 30000;

    /**
     * Received time out constant.
     */
    protected static final long RECEIVE_TIME_OUT = 60000;


    /**
     * @param domainObject the submit query domain object.
     * @return the Jaxb submit query request type.
     */
    protected SubmitQueryRequestType createRequestType(final ISubmitQueryRequest domainObject) {
        final SubmitQueryRequestType requestType = new SubmitQueryRequestType();
        final HeaderType headerType = new HeaderType();
        headerType.setTargetAppCustomerId("TestCust");
        headerType.setCriteriaType("TEST_CRITERIA");
        requestType.setHeader(headerType);

        return requestType;
    }

    /**
     * @return submit query request domain object
     */
    protected ISubmitQueryRequest createSubmitQueryRequest() {
        final IBulkCustomer bulkCustomer = new BulkCustomer();
        bulkCustomer.setId(1L);
        bulkCustomer.setSdtCustomerId(10L);

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
        serviceType.setName(IServiceType.ServiceTypeName.SUBMIT_QUERY.name());
        serviceType.setDescription("RequestTestDesc1");
        serviceType.setStatus("RequestTestStatus");

        serviceRouting.setServiceType(serviceType);
        serviceRoutings.add(serviceRouting);
        targetApp.setServiceRoutings(serviceRoutings);

        final Set<IBulkCustomerApplication> bulkCustomerApplications = new HashSet<>();
        final BulkCustomerApplication bulkCustomerApplication = new BulkCustomerApplication();
        bulkCustomerApplication.setBulkCustomer(bulkCustomer);
        bulkCustomerApplication.setTargetApplication(targetApp);
        bulkCustomerApplications.add(bulkCustomerApplication);
        bulkCustomer.setBulkCustomerApplications(bulkCustomerApplications);

        final ISubmitQueryRequest sSubmitQueryRequest = new SubmitQueryRequest();
        sSubmitQueryRequest.setBulkCustomer(bulkCustomer);
        sSubmitQueryRequest.setTargetApplication(targetApp);
        sSubmitQueryRequest.setResultCount(1);
        return sSubmitQueryRequest;
    }

    /**
     * @return individual request domain object
     */
    protected IIndividualRequest createIndividualRequest() {
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
        serviceType.setName(IServiceType.ServiceTypeName.SUBMIT_INDIVIDUAL.name());
        serviceType.setDescription("RequestTestDesc1");
        serviceType.setStatus("RequestTestStatus");

        serviceRouting.setServiceType(serviceType);
        serviceRouting.setTargetApplication(targetApp);

        serviceRoutings.add(serviceRouting);

        targetApp.setServiceRoutings(serviceRoutings);

        bulkSubmission.setTargetApplication(targetApp);

        bulkCustomer.setId(1L);
        bulkCustomer.setSdtCustomerId(10L);

        bulkSubmission.setBulkCustomer(bulkCustomer);
        bulkSubmission.setCustomerReference("TEST_CUST_REF");
        bulkSubmission.setId(1L);
        bulkSubmission.setNumberOfRequest(1);

        final IIndividualRequest iIndividualRequest = new IndividualRequest();
        iIndividualRequest.setSdtRequestReference("Test");

        final List<IIndividualRequest> requests = new ArrayList<>();
        requests.add(iIndividualRequest);

        bulkSubmission.setIndividualRequests(requests);

        iIndividualRequest.setBulkSubmission(bulkSubmission);

        return iIndividualRequest;
    }


    protected SubmitQueryResponseType createSubmitQueryResponseType() {
        ErrorType errorType = new ErrorType();
        errorType.setCode("ERR_CODE");
        errorType.setDescription("ERROR_DESCRIPTION");

        StatusType statusType = new StatusType();
        statusType.setCode(StatusCodeType.OK);
        statusType.setError(errorType);

        SubmitQueryResponseType submitQueryResponseType = new SubmitQueryResponseType();
        submitQueryResponseType.setTargetAppCustomerId("1");
        submitQueryResponseType.setResultCount(BigInteger.valueOf(1));
        submitQueryResponseType.setStatus(statusType);

        return submitQueryResponseType;
    }

}
