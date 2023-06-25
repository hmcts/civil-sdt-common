package uk.gov.moj.sdt.producers;

import javax.xml.bind.JAXBElement;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.moj.sdt.producers.config.EndToEndTestConfig;
import uk.gov.moj.sdt.producers.config.SecurityConfig;
import uk.gov.moj.sdt.utils.SpringApplicationContext;
import uk.gov.moj.sdt.ws._2013.sdt.individualupdaterequestschema.UpdateRequestType;
import uk.gov.moj.sdt.ws._2013.sdt.individualupdateresponseschema.ObjectFactory;
import uk.gov.moj.sdt.ws._2013.sdt.individualupdateresponseschema.UpdateResponseType;
import uk.gov.moj.sdt.ws._2013.sdt.sdtinternalendpoint.ISdtInternalEndpointPortType;

/**
 * Test class for end to end update item web service.
 *
 * @author Manoj Kulkarni
 */
@ActiveProfiles("end-to-end-test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { EndToEndTestConfig.class, SecurityConfig.class})
@Sql(scripts = {
        "classpath:database/baseline/initialise_test_database.sql"
        ,"classpath:database/baseline/UpdateItemTest.sql"
})
public class UpdateItemTest extends AbstractWebServiceTest<UpdateRequestType, UpdateResponseType> {

    /**
     * Method to test the update item service.
     */
    @Test
    public void testUpdateItem() {
        this.callWebService(UpdateRequestType.class);
    }

    /**
     * Method to test the update item service with rejected status.
     */
    @Test
    public void testUpdateItemRejected() {
        this.callWebService(UpdateRequestType.class);
    }

    /**
     * Method to test the update item service with multiple requests.
     */
    @Test
    public void testUpdateItemMultiple() {
        this.callWebService(UpdateRequestType.class);
    }

    @Override
    protected UpdateResponseType callTestWebService(final UpdateRequestType request) {
        // Get the SOAP proxy client.
        ISdtInternalEndpointPortType client = (ISdtInternalEndpointPortType) SpringApplicationContext.getBean("ISdtInternalEndpointPortType");

        Client clientProxy = ClientProxy.getClient(client);

        HTTPConduit httpConduit = (HTTPConduit) clientProxy.getConduit();
        HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
        // Specifies the amount of time, in milliseconds, that the client will attempt to establish a connection before
        // it times out
        httpClientPolicy.setConnectionTimeout(10000);
        // Specifies the amount of time, in milliseconds, that the client will wait for a response before it times out.
        httpClientPolicy.setReceiveTimeout(10000);
        httpConduit.setClient(httpClientPolicy);

        // Call the specific business method for this text - note that a single test can only use one web service
        // business method.
        return client.updateItem(request);
    }

    @Override
    protected JAXBElement<UpdateResponseType> wrapJaxbObject(final UpdateResponseType response) {
        // Use the provided factor to create a wrapped instance of the response.
        ObjectFactory objectFactory = new ObjectFactory();
        return objectFactory.createUpdateResponse(response);
    }

}
