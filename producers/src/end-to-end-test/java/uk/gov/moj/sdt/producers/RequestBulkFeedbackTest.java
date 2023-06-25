package uk.gov.moj.sdt.producers;

import javax.xml.bind.JAXBElement;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import uk.gov.moj.sdt.producers.config.EndToEndTestConfig;
import uk.gov.moj.sdt.producers.config.SecurityConfig;
import uk.gov.moj.sdt.ws._2013.sdt.bulkfeedbackrequestschema.BulkFeedbackRequestType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkfeedbackresponseschema.BulkFeedbackResponseType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkfeedbackresponseschema.ObjectFactory;
import uk.gov.moj.sdt.ws._2013.sdt.sdtendpoint.ISdtEndpointPortType;

/**
 * Test class for end to end web service tests..
 *
 * @author Sally Vonka
 */
@ActiveProfiles("end-to-end-test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { EndToEndTestConfig.class, SecurityConfig.class})
@Sql(scripts = {
        "classpath:database/baseline/initialise_test_database.sql"
        ,"classpath:database/baseline/RequestBulkFeedbackTest.sql"
})
public class RequestBulkFeedbackTest extends AbstractWebServiceTest<BulkFeedbackRequestType, BulkFeedbackResponseType> {
    /**
     * Method to call remote request bulk feedback endpoint to be tested.
     */
    @Test
    public void testValidRequestBulkFeedback() {
        this.callWebService(BulkFeedbackRequestType.class);
    }

    /**
     * Method to call remote request bulk feedback endpoint to be tested.
     */
    @Test
    public void testInvalidRequestBulkFeedback() {
        this.callWebService(BulkFeedbackRequestType.class);
    }

    @Override
    protected BulkFeedbackResponseType callTestWebService(final BulkFeedbackRequestType request) {
        // Get the SOAP proxy client.
        ISdtEndpointPortType client = getSdtEndpointClient();

        // Call the specific business method for this text - note that a single test can only use one web service
        // business method.
        return client.getBulkFeedback(request);
    }

    @Override
    protected JAXBElement<BulkFeedbackResponseType> wrapJaxbObject(final BulkFeedbackResponseType response) {
        // Use the provided factor to create a wrapped instance of the response.
        ObjectFactory objectFactory = new ObjectFactory();
        return objectFactory.createBulkFeedbackResponse(response);
    }

}
