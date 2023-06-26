package uk.gov.moj.sdt.interceptors.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.moj.sdt.dao.ServiceRequestDao;
import uk.gov.moj.sdt.domain.ServiceRequest;
import uk.gov.moj.sdt.domain.api.IServiceRequest;
import uk.gov.moj.sdt.utils.SdtContext;
import uk.gov.moj.sdt.utils.ServerHostName;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("RequestDaoService")
public class RequestDaoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestDaoService.class);

    private ServiceRequestDao serviceRequestDao;

    @Autowired
    public RequestDaoService(ServiceRequestDao serviceRequestDao) {
        this.serviceRequestDao = serviceRequestDao;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void persistRequest() {

        // Get the raw XML.
        final String rawXml = SdtContext.getContext().getRawInXml();

        // Prepare log message for Hibernate.
        final IServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setBulkCustomerId(extractBulkCustomerId(rawXml));
        serviceRequest.setRequestPayload(rawXml.getBytes());
        serviceRequest.setRequestDateTime(LocalDateTime.now());
        serviceRequest.setRequestType(extractRequestType(rawXml));

        // Get the server Host Name
        final String hostName = ServerHostName.getHostName();
        serviceRequest.setServerHostName(hostName);

        serviceRequestDao.persist(serviceRequest);

        // Retrieve the id assigned in by Hibernate and store it for the outbound service interceptor to use later.
        final Long serviceRequestID = serviceRequest.getId();
        SdtContext.getContext().setServiceRequestId(serviceRequestID);
    }

    /**
     * When the application closes the message output stream the service request
     * must also be persisted. Since Hibernate wraps everything up into
     * transactional units trying to write the output stream value anywhere else
     * in the stack seems to miss the Hibernate persist ending up with an empty
     * response payload being persisted.
     *
     * @param envelope the String envelope value of the output soap message.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void persistEnvelope(final String envelope) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("AbstractServiceRequest creating outbound payload database log for ServiceRequest: {}",
                         SdtContext.getContext().getServiceRequestId());
        }

        final Long serviceRequestId = SdtContext.getContext().getServiceRequestId();

        // If there is no service request id then the ServiceRequestInboundInterceptor has not been called and there
        // will be no service request row in the database.
        if (serviceRequestId != null) {
            // Get the log message for the inbound request so we can add the outbound response to it.
            final IServiceRequest serviceRequest = serviceRequestDao.fetch(ServiceRequest.class, serviceRequestId);

            // Add the response and timestamp to the service request record.
            serviceRequest.setResponsePayload(envelope.getBytes());
            serviceRequest.setResponseDateTime(LocalDateTime.now());

            // Note that bulk reference will be null if this is not a bulk submission.
            final String bulkReference = SdtContext.getContext().getSubmitBulkReference();
            if (bulkReference != null && bulkReference.length() > 0) {
                serviceRequest.setBulkReference(bulkReference);
            }

            serviceRequestDao.persist(serviceRequest);
        }
    }

    /**
     * Retrieve the Request Type from the inbound xmlMessage. Assumes inbound message has already been setup by the
     * XmlInboundInterceptor.
     *
     * @param xml raw XML from which to extract bulk customer id.
     * @return the request type (for example 'bulkRequest')
     */
    private String extractRequestType(final String xml) {
        return extractNodeAttributeValue("request", "requestType", xml);
    }

    /**
     * Extract the bulk customer id (sdt customer id - synonymous). Assumes inbound message has already been setup by
     * the XmlInboundInterceptor.
     * <p>
     * Always available as part of a submission
     * </p>
     *
     * @param xml raw XML from which to extract bulk customer id.
     * @return the bulk customer id
     */
    private String extractBulkCustomerId(final String xml) {
        return extractNodeValue("sdtCustomerId", xml);
    }

    /**
     * Method to extract a text value belonging to a specified node from a string of xml.
     * <p>
     * This method does not use the whole framework of generating a DOM and parsing that. Instead it uses a regular
     * expression to locate and extract a String.
     * </p>
     *
     * @param nodeName the name of an xml node from which to extract value.
     * @param xml      raw XML from which to extract bulk customer id.
     * @return the content of the node or null
     */
    private String extractNodeValue(final String nodeName, final String xml) {
        String nodeValue = "";

        // Find given tag and extract its value.
        //
        // Search for:
        // optional namespace and given start tag name
        // any attributes before the end of the start tag
        // end of start tag
        // tag value
        // given end tag
        // an embedded default namespace
        //
        // Capture:
        // tag value
        final Pattern pattern =
            Pattern.compile("<[\\S&&[^>/]]*?" + nodeName + "[^>]*?>(.*?)</[\\S&&[^>/]]*?" + nodeName);
        final Matcher matcher = pattern.matcher(xml);

        if (matcher.find()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Found matching group[{}]",  matcher.group());
            }

            // Copy the match.
            nodeValue = matcher.group(1);
        }

        return nodeValue;
    }

    /**
     * Method to extract an attribute value belonging to a specified node from a string of xml.
     * <p>
     * This method does not use the whole framework of generating a DOM and parsing that. Instead it uses a regular
     * expression to locate and extract a String.
     * </p>
     *
     * @param nodeName      the name of an xml node from which to extract value.
     * @param attributeName the name of the attribute from which to extract value.
     * @param xml           raw XML from which to extract bulk customer id.
     * @return the content of the node or null
     */
    private String extractNodeAttributeValue(final String nodeName, final String attributeName, final String xml) {
        String attributeValue = "";

        // Find given node and extract ivalue of named attribute.
        //
        // Search for:
        // optional namespace and given start tag name
        // any attributes before the end of the start tag
        // end of start tag
        // tag value
        // given end tag
        // an embedded default namespace
        //
        // Capture:
        // tag value
        final Pattern pattern =
            Pattern.compile("<[\\S&&[^>/]]*?" + nodeName + ".*?" + attributeName + "=[\"\']([^>\"\']*?)[\"\']");
        final Matcher matcher = pattern.matcher(xml);

        if (matcher.find()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Found matching group[{}]", matcher.group());
            }

            // Copy the match.
            attributeValue = matcher.group(1);
        }

        return attributeValue;
    }

}
