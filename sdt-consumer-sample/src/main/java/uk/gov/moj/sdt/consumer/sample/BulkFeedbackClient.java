package uk.gov.moj.sdt.consumer.sample;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import uk.gov.moj.sdt.ws._2013.mcol.responsedetailschema.ResponseDetailType;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.StatusCodeType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkfeedbackrequestschema.BulkFeedbackRequestType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkfeedbackrequestschema.HeaderType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkfeedbackresponseschema.BulkFeedbackResponseType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkfeedbackresponseschema.BulkRequestStatusType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkfeedbackresponseschema.ResponseType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkfeedbackresponseschema.ResponsesType;
import uk.gov.moj.sdt.ws._2013.sdt.sdtendpoint.ISdtEndpointPortType;

public class BulkFeedbackClient extends AbstractWebServiceClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(BulkFeedbackClient.class);

    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd-MM-YYYY HH:mm:SS");

    private BulkFeedbackResponseType getFeedback(final BulkFeedbackRequestType request) {
        // Get the SOAP proxy client.
        ISdtEndpointPortType client = getSdtEndpointClient();

        return client.getBulkFeedback(request);

    }

    private BulkFeedbackRequestType createRequest() {
        BulkFeedbackRequestType request = new BulkFeedbackRequestType();

        HeaderType headerType = new HeaderType();
        headerType.setSdtBulkReference("MCOL-20140328184740-000000217");
        headerType.setSdtCustomerId(12345678);

        request.setHeader(headerType);
        return request;
    }

    private void outputResponse(BulkFeedbackResponseType response) {

        BulkRequestStatusType bulkRequestStatustType = response.getBulkRequestStatus();
        LOGGER.info("SDT reference: " + bulkRequestStatustType.getSdtBulkReference());
        LOGGER.info("Customer reference: " + bulkRequestStatustType.getCustomerReference());
        LOGGER.info("Bulk status: " + bulkRequestStatustType.getBulkStatus().getCode());
        LOGGER.info("Submitted date: " + DATE_FORMATTER.format(bulkRequestStatustType.getSubmittedDate()
                .getTime()));

        ResponsesType responsesType = response.getResponses();
        for (ResponseType responseType : responsesType.getResponse()) {

            LOGGER.info("Response Id=" + responseType.getRequestId() + ", requestType=" +
                    responseType.getRequestType() + ", status=" + responseType.getStatus().getCode().value()
                    + "\n" + toStringMcolResponseDetail(responseType.getResponseDetail().getMcolResponseDetail()));
        }
    }

    private String toStringMcolResponseDetail(ResponseDetailType detail) {
        if (detail == null) {
            return "MCOL response: null";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("MCOL response: claimNumber=" + detail.getClaimNumber() + ", issueDate=" +
                format(detail.getIssueDate()) + ", serviceDate=" + format(detail.getServiceDate()) +
                ", enforcingCourtCode=" + detail.getEnforcingCourtCode() + ", enforcingCourtName=" +
                detail.getEnforcingCourtName() + ", warrantNumber=" + detail.getWarrantNumber() + ", fee=" +
                detail.getFee() + ", firstPaymentDate=" + format(detail.getFirstPaymentDate()) +
                ", judgmentWarrantStatus=" + detail.getJudgmentWarrantStatus());

        return sb.toString();
    }

    private String format(Calendar calendar) {
        if (calendar != null)
            return DATE_FORMATTER.format(calendar.getTime());
        else
            return "";
    }

    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(APP_CONTEXT_LOCATION);

        BulkFeedbackClient client = applicationContext.getBean(BulkFeedbackClient.class);
        BulkFeedbackResponseType response = client.getFeedback(client.createRequest());
        if (StatusCodeType.OK.equals(response.getBulkRequestStatus().getStatus().getCode())) {
            LOGGER.info("Successful");
            client.outputResponse(response);
        }

        if (StatusCodeType.ERROR.equals(response.getBulkRequestStatus().getStatus().getCode())) {
            LOGGER.info("Error response: code[" +
                    response.getBulkRequestStatus().getStatus().getError().getCode() + "] message[" +
                    response.getBulkRequestStatus().getStatus().getError().getDescription() + "]");
        }

    }

}
