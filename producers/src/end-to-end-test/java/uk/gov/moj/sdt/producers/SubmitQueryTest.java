package uk.gov.moj.sdt.producers;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.moj.sdt.producers.config.EndToEndTestConfig;
import uk.gov.moj.sdt.producers.config.SecurityConfig;
import uk.gov.moj.sdt.ws._2013.sdt.sdtendpoint.ISdtEndpointPortType;
import uk.gov.moj.sdt.ws._2013.sdt.submitqueryrequestschema.SubmitQueryRequestType;
import uk.gov.moj.sdt.ws._2013.sdt.submitqueryresponseschema.ObjectFactory;
import uk.gov.moj.sdt.ws._2013.sdt.submitqueryresponseschema.SubmitQueryResponseType;

import javax.xml.bind.JAXBElement;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
        ,"classpath:database/baseline/SubmitQueryTest.sql"
})
public class SubmitQueryTest extends AbstractWebServiceTest<SubmitQueryRequestType, SubmitQueryResponseType> {

    /**
     * Method to call remote submit query endpoint to be tested.
     */
    @Test
    @Disabled
    public void testValid() {
        this.callWebService(SubmitQueryRequestType.class);
    }

    /**
     * Method to call remote submit query endpoint to be tested.
     */
    @Test
    public void testErrorResult() {
        try {
            this.callWebService(SubmitQueryRequestType.class);
        } catch (SdtRunTimeException e) {
            assertTrue(e.getMessage().contains("The content of element")
                    && e.getMessage().contains("criterion' is not complete"),
                    "Unexpected exception message in SOAPFaultException [" + e.getMessage() + "]");
        }
    }

    /**
     * Scenario - Invalid SDT Customer details.
     */
    @Test
    public void testInvalidCustomer() {
        this.callWebService(SubmitQueryRequestType.class);
    }

    /**
     * Scenario - Customer does not have access to target application.
     */
    @Test
    public void testInvalidTargetApp() {
        this.callWebService(SubmitQueryRequestType.class);
    }

    @Override
    protected SubmitQueryResponseType callTestWebService(final SubmitQueryRequestType request) {
        // Get the SOAP proxy client.
        ISdtEndpointPortType client = getSdtEndpointClient();

        // Call the specific business method for this text - note that a single test can only use one web service
        // business method.
        return client.submitQuery(request);
    }

    @Override
    protected JAXBElement<SubmitQueryResponseType> wrapJaxbObject(final SubmitQueryResponseType response) {
        // Use the provided factor to create a wrapped instance of the response.
        ObjectFactory objectFactory = new ObjectFactory();
        return objectFactory.createSubmitQueryResponse(response);
    }

}
