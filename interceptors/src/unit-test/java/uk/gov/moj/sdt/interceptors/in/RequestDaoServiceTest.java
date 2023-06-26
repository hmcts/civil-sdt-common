package uk.gov.moj.sdt.interceptors.in;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.moj.sdt.dao.ServiceRequestDao;
import uk.gov.moj.sdt.domain.api.IServiceRequest;
import uk.gov.moj.sdt.interceptors.service.RequestDaoService;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;
import uk.gov.moj.sdt.utils.SdtContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RequestDaoServiceTest extends AbstractSdtUnitTestBase {

    @Mock
    ServiceRequestDao mockServiceRequestDao;

    RequestDaoService requestDaoService;

    @Override
    protected void setUpLocalTests() {
        requestDaoService = new RequestDaoService(mockServiceRequestDao);
    }

    /**
     * Test that the process correctly works via mocked out extensions.
     */
    @Test
    void testHandleMessage() {

        // Setup the raw XML as if the XmlInboundInterceptor had run.
        final String xml =
                this.convertStreamToString(ServiceRequestInboundInterceptorTest.class
                        .getResourceAsStream("inRequest.xml"));
        SdtContext.getContext().setRawInXml(xml);

        requestDaoService.persistRequest();
        verify(mockServiceRequestDao).persist(any(IServiceRequest.class));
    }

    /**
     * Utility method to get contents from input stream.
     *
     * @param is input stream from which to extract contents.
     * @return contents of input stream.
     */
    private String convertStreamToString(final InputStream is) {
        /* To convert the InputStream to String we use the BufferedReader.readLine()
         * method. We iterate until the BufferedReader return null which means
         * there's no more data to read. Each line will appended to a StringBuilder
         * and returned as String. */
        final StringBuilder sb = new StringBuilder();

        String line;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (final IOException e) {
            fail(e.getLocalizedMessage());
        } finally {
            try {
                is.close();
            } catch (final IOException e) {
                // Do nothing.
            }
        }

        return sb.toString();
    }
}
