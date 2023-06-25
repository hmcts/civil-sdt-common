package uk.gov.moj.sdt.consumer.sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import uk.gov.moj.sdt.ws._2013.mcol.claimschema.ClaimType;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.SotSignatureType;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.StatusCodeType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkrequestschema.BulkRequestType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkrequestschema.HeaderType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkrequestschema.RequestItemType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkrequestschema.RequestsType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkresponseschema.BulkResponseType;
import uk.gov.moj.sdt.ws._2013.sdt.sdtendpoint.ISdtEndpointPortType;

/**
 * Sample to call Submit Bulk web service.
 *
 * @author Saurabh
 */
public class SubmitBulkRequestClient extends AbstractWebServiceClient {
    /**
     * Logger instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SubmitBulkRequestClient.class);

    /**
     * Calls Submit Bulk web service on SDT endpoint.
     *
     * @param request bulk request
     * @return response received from SDT.
     */
    private BulkResponseType submitBulk(final BulkRequestType request) {
        // Get the SOAP proxy client.
        ISdtEndpointPortType client = getSdtEndpointClient();

        return client.submitBulk(request);
    }

    /**
     * Creates bulk request.
     *
     * @return bulk request.
     */
    private BulkRequestType createRequest() {
        BulkRequestType request = new BulkRequestType();

        HeaderType headerType = new HeaderType();
        headerType.setCustomerReference("1");
        headerType.setRequestCount(1);
        headerType.setSdtCustomerId(12345678);
        headerType.setTargetApplicationId("MCOL");

        request.setHeader(headerType);
        RequestsType requestsType = new RequestsType();

        RequestItemType requestItemType = new RequestItemType();
        requestItemType.setRequestId("1-1");
        requestItemType.setRequestType("mcolClaim");
        ClaimType claimType = new ClaimType();
        SotSignatureType sotSignatureType = new SotSignatureType();
        sotSignatureType.setFlag(true);
        sotSignatureType.setName("test");
        claimType.setSotSignature(sotSignatureType);
        requestItemType.setMcolClaim(claimType);

        requestsType.getRequest().add(requestItemType);

        request.setRequests(requestsType);

        return request;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(APP_CONTEXT_LOCATION);

        SubmitBulkRequestClient client = applicationContext.getBean(SubmitBulkRequestClient.class);
        BulkResponseType response = client.submitBulk(client.createRequest());
        if (StatusCodeType.OK.equals(response.getStatus().getCode())) {
            LOGGER.info("Successful call...Bulk reference = " + response.getSdtBulkReference());
        }

        if (StatusCodeType.ERROR.equals(response.getStatus().getCode())) {
            LOGGER.info("Error response: code[" + response.getStatus().getError().getCode() + "] message[" +
                    response.getStatus().getError().getDescription() + "]");
        }

    }

}
