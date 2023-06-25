package uk.gov.moj.sdt.services;

import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import javax.xml.ws.WebServiceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import uk.gov.moj.sdt.consumers.api.IConsumerGateway;
import uk.gov.moj.sdt.consumers.exception.OutageException;
import uk.gov.moj.sdt.consumers.exception.SoapFaultException;
import uk.gov.moj.sdt.consumers.exception.TimeoutException;
import uk.gov.moj.sdt.dao.api.IIndividualRequestDao;
import uk.gov.moj.sdt.domain.ErrorLog;
import uk.gov.moj.sdt.domain.api.IErrorLog;
import uk.gov.moj.sdt.domain.api.IErrorMessage;
import uk.gov.moj.sdt.domain.api.IGlobalParameter;
import uk.gov.moj.sdt.domain.api.IIndividualRequest;
import uk.gov.moj.sdt.domain.cache.api.ICacheable;
import uk.gov.moj.sdt.services.api.ITargetApplicationSubmissionService;
import uk.gov.moj.sdt.services.messaging.SdtMessage;
import uk.gov.moj.sdt.services.messaging.api.IMessageWriter;
import uk.gov.moj.sdt.services.messaging.api.ISdtMessage;
import uk.gov.moj.sdt.services.utils.GenericXmlParser;
import uk.gov.moj.sdt.utils.SdtContext;
import uk.gov.moj.sdt.utils.cmc.RequestTypeXmlNodeValidator;
import uk.gov.moj.sdt.utils.cmc.exception.CMCException;
import uk.gov.moj.sdt.utils.cmc.exception.CaseOffLineException;
import uk.gov.moj.sdt.utils.mbeans.SdtMetricsMBean;

import static uk.gov.moj.sdt.domain.api.IIndividualRequest.IndividualRequestStatus.FORWARDED;
import static uk.gov.moj.sdt.domain.api.IIndividualRequest.IndividualRequestStatus.REJECTED;

/**
 * Service class that implements the TargetApplicationSubmissionService.
 *
 * @author Manoj Kulkarni
 */
@Service("TargetApplicationSubmissionService")
public class TargetApplicationSubmissionService extends AbstractSdtService implements ITargetApplicationSubmissionService {

    /**
     * Logger object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TargetApplicationSubmissionService.class);

    /**
     * The maximum allowed characters for the soap fault error description.
     */
    private static final int MAX_ALLOWED_CHARS = 2000;

    /**
     * Individual Request Dao to perform operations on the individual request object.
     */
    private IIndividualRequestDao individualRequestDao;

    /**
     * The consumer gateway that will perform the call to the target application
     * web service.
     */
    private IConsumerGateway requestConsumer;

    /**
     * The consumer gateway that will perform the call to the cmc target application
     * web service.
     */
    private IConsumerGateway cmcRequestConsumer;


    /**
     * The ICacheable reference to the global parameters cache.
     */
    @Lazy
    @Qualifier("GlobalParametersCache")
    @Autowired
    private ICacheable globalParametersCache;

    /**
     * This variable holding the message writer reference.
     */
    private IMessageWriter messageWriter;

    /**
     * The ICacheable reference to the error messages cache.
     */
    @Lazy
    @Qualifier("ErrorMessagesCache")
    @Autowired
    private ICacheable errorMessagesCache;

    @Autowired
    public TargetApplicationSubmissionService(@Qualifier("IndividualRequestDao")
                                                  IIndividualRequestDao individualRequestDao,
                                              @Qualifier("IndividualResponseXmlParser")
                                                  GenericXmlParser individualResponseXmlParser,
                                              @Qualifier("ConsumerGateway")
                                                  IConsumerGateway requestConsumer,
                                              @Qualifier("CMCConsumerGateway")
                                                  IConsumerGateway cmcRequestConsumer,
                                              @Qualifier("MessageWriter")
                                                  IMessageWriter messageWriter,
                                              RequestTypeXmlNodeValidator requestTypeXmlNodeValidator) {
        super(individualRequestDao, individualResponseXmlParser, requestTypeXmlNodeValidator);
        this.individualRequestDao = individualRequestDao;
        this.requestConsumer = requestConsumer;
        this.cmcRequestConsumer = cmcRequestConsumer;
        this.messageWriter = messageWriter;
    }

    @Override
    public void processRequestToSubmit(final String sdtRequestReference, Boolean caseOffLine) {
        // Look for the individual request matching this unique request reference.
        final IIndividualRequest individualRequest = this.getIndRequestBySdtReference(sdtRequestReference);

        // Proceed ahead if the Individual Request is found.
        if (individualRequest != null) {
            LOGGER.debug("Process individual request [{}].", individualRequest.getSdtBulkReference());

            // Check the configurable delay from system parameter and delay the consumer call for that time.
            this.delayRequestProcessing(individualRequest);

            // Update the status of the requested to Forwarded before calling the consumer
            this.updateForwardingRequest(individualRequest);

            // Make call to consumer to submit the request to target application.
            try {
                this.sendRequestToTargetApp(individualRequest, caseOffLine);

                this.updateCompletedRequest(individualRequest, !isCMCRequestType(individualRequest));
            } catch (final TimeoutException e) {
                LOGGER.error("Timeout exception for SDT reference [{}]", individualRequest.getSdtRequestReference());

                // Update the individual request with the reason code for error REQ_NOT_ACK
                this.updateRequestTimeOut(individualRequest);

                // Re-queue message again
                this.reQueueRequest(individualRequest);
            } catch (final SoapFaultException e) {
                // Update the individual request with the soap fault reason
                this.handleSoapFaultAndWebServiceException(individualRequest, e.getMessage());
            } catch (final WebServiceException e) {

                LOGGER.error("Exception calling target application for SDT reference [{}] - {}",
                        individualRequest.getSdtRequestReference(), e.getMessage());

                this.handleSoapFaultAndWebServiceException(individualRequest, e.getMessage());

            } catch (final CMCException irte) {
                String errorMessage = String.format("%s [ %s ] - %s", "Exception calling target application for SDT reference",
                                                    individualRequest.getSdtRequestReference(),
                                                    irte.getMessage());
                LOGGER.error(errorMessage);

                updateRequestRejected(individualRequest);
                updateCompletedRequest(individualRequest, !isCMCRequestType(individualRequest));
            } catch (final CaseOffLineException ce) {
                String errorMessage = String.format("Case is Offline for Reference [ %s ] - %s - ReQueue Request",
                                                    individualRequest.getSdtRequestReference(),
                                                    ce.getMessage());
                LOGGER.warn(errorMessage);
                this.reQueueRequest(individualRequest, true);
            }
        } else {
            LOGGER.error("SDT Reference {} read from message queue not found in database for individual request.",
                    sdtRequestReference);
        }
    }

    @Override
    public void processDLQRequest(final IIndividualRequest individualRequest, final String requestStatus) {
        // Format the request status parameter to the value acceptable by database.
        final String requestStatusVal = StringUtils.capitalize(requestStatus.toLowerCase());

        // Re-set the Dead Letter flag to false.
        individualRequest.setDeadLetter(false);

        if (REJECTED.getStatus().equals(requestStatusVal)) {
            // If the request status is rejected, add an error log entry, mark as
            // REJECTED and persist the record. Update the bulk submission status
            // to COMPLETED by checking if all the individual requests are either
            // REJECTED or ACCEPTED.
            updateRequestRejected(individualRequest);

        } else {
            // If the request status is FORWARDED, update the status and
            // persist the record in database.
            individualRequest.setRequestStatus(FORWARDED.getStatus());
            individualRequest.setUpdatedDate(LocalDateTime.now());
            this.getIndividualRequestDao().persist(individualRequest);
        }

    }

    /**
     * Updates the request object. This method updates the request status to Rejected
     * and creates an entry in the error log table for the CUST_XML_ERR error message.
     * This method is called after the service team has established that the DLQ request
     * is caused by the Client's data error.
     *
     * @param individualRequest the individual request to be marked.
     */
    private void updateRequestRejected(final IIndividualRequest individualRequest) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Update individual request [{}] as status REJECTED following Service team's investigation " +
                    "of DLQ Request.", individualRequest.getSdtBulkReference());
        }

        final IErrorMessage errorMessage =
                this.getErrorMessagesCache().getValue(IErrorMessage.class,
                        IErrorMessage.ErrorCode.CUST_XML_ERR.name());

        // Get the global parameter value for the Contact name
        final IGlobalParameter contactNameParameter =
                this.getGlobalParametersCache().getValue(IGlobalParameter.class,
                        IGlobalParameter.ParameterKey.CONTACT_DETAILS.name());

        final String contactName = contactNameParameter != null ? contactNameParameter.getValue() : "";

        // Now create an ErrorLog object with the ErrorMessage object and the
        // IndividualRequest object
        final IErrorLog errorLog =
                new ErrorLog(errorMessage.getErrorCode(), MessageFormat.format(errorMessage.getErrorText(),
                        contactName));

        // Please note that the SDT internal error field has already been set in this
        // request object when the error was caught during the request evaluation by the
        // MDB queue processing.

        individualRequest.markRequestAsRejected(errorLog);

        // now complete the request. We don't want to extract the raw target app response here
        // so we set the second parameter as false.
        this.updateCompletedRequest(individualRequest, false);
    }

    /**
     * Update the request object. This method is to be called to update the request object
     * to bring it in the request status state of FORWARDED
     *
     * @param individualRequest the IndividualRequest object that contains the
     *                          response payload from the target application.
     */
    private void updateForwardingRequest(final IIndividualRequest individualRequest) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Update individual request [{}] with status FORWARDED.",
                    individualRequest.getSdtBulkReference());
        }

        individualRequest.incrementForwardingAttempts();
        this.getIndividualRequestDao().persist(individualRequest);

        // Setup raw XML associated with a single request so that it can be picked up by the outbound
        // interceptor and injected into the outbound XML.
        SdtContext.getContext().setRawOutXml(
                null == individualRequest.getRequestPayload() ? "" :
                        new String(individualRequest.getRequestPayload(), StandardCharsets.UTF_8));
    }

    /**
     * Updates the request object. This method is called when the send request to the target application
     * times out.
     *
     * @param individualRequest the individual request to be marked with reason as not acknowledged
     */
    private void updateRequestTimeOut(final IIndividualRequest individualRequest) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Update individual request [{}] as timed out.", individualRequest.getSdtBulkReference());
        }

        // Set the updated date.
        // We've already incremented the forwarding attempts and set the status to forwarded so
        // no need to do that here again.
        individualRequest.setUpdatedDate(LocalDateTime.now());

        // Get the Error message to indicate that call to target application has timed out
        final IErrorMessage errorMessageParam =
                this.getErrorMessagesCache().getValue(IErrorMessage.class,
                        IErrorMessage.ErrorCode.REQ_NOT_ACK.name());

        // Set the error message text in the internal error field of the individual request
        individualRequest.setInternalSystemError(errorMessageParam.getErrorText());

        // now persist the request.
        this.getIndividualRequestDao().persist(individualRequest);
    }

    /**
     * Send the individual request to target application for submission.
     *
     * @param individualRequest the individual request to be sent to target application.
     * @param caseOffLine       when case is offLine, then invoke mcol service
     * @throws OutageException  when the target web service is not responding.
     * @throws TimeoutException when the target web service does not respond back in time.
     */
    private void sendRequestToTargetApp(final IIndividualRequest individualRequest, Boolean caseOffLine)
            throws OutageException, TimeoutException {
        LOGGER.debug("Send individual request [{}] to target application.", individualRequest.getSdtBulkReference());

        final IGlobalParameter connectionTimeOutParam =
                this.globalParametersCache.getValue(IGlobalParameter.class,
                        IGlobalParameter.ParameterKey.TARGET_APP_TIMEOUT.name());
        final IGlobalParameter requestTimeOutParam =
                this.globalParametersCache.getValue(IGlobalParameter.class,
                        IGlobalParameter.ParameterKey.TARGET_APP_RESP_TIMEOUT.name());

        long requestTimeOut = 0;
        long connectionTimeOut = 0;

        if (requestTimeOutParam != null) {
            requestTimeOut = Long.parseLong(requestTimeOutParam.getValue());
        }

        if (connectionTimeOutParam != null) {
            connectionTimeOut = Long.parseLong(connectionTimeOutParam.getValue());
        }

        this.getRequestConsumer(individualRequest, caseOffLine).individualRequest(individualRequest, connectionTimeOut, requestTimeOut);
    }

    /**
     * Store exception message as internal error. Mark the request as dead message and write to corresponding target
     * application's dead letter queue (DLQ).
     *
     * @param individualRequest the individual request to be marked.
     * @param errorMessage      the exception message when the request has failed.
     */
    // CHECKSTYLE:OFF
    private void handleSoapFaultAndWebServiceException(final IIndividualRequest individualRequest,
                                                       final String errorMessage)
    // CHECKSTYLE:ON
    {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Update individual request [{}] with internal error and send to dead letter queue.",
                    individualRequest.getSdtBulkReference());
        }

        // Truncate the error message to fit database column length
        String internalError = errorMessage;
        if (errorMessage != null && errorMessage.length() > MAX_ALLOWED_CHARS) {
            internalError = errorMessage.substring(0, MAX_ALLOWED_CHARS);
        }
        individualRequest.setInternalSystemError(internalError);
        individualRequest.setDeadLetter(true);

        // now persist the request.
        this.getIndividualRequestDao().persist(individualRequest);

        // Create a new message for DLQ.
        final ISdtMessage messageObj = new SdtMessage();
        messageObj.setSdtRequestReference(individualRequest.getSdtRequestReference());

        final String targetAppCode =
                individualRequest.getBulkSubmission().getTargetApplication().getTargetApplicationCode();

        // Write to dead letter queue.
        this.getMessageWriter().queueMessage(messageObj, targetAppCode, true);
    }

    /**
     * @return individual request dao
     */
    @Override
    public IIndividualRequestDao getIndividualRequestDao() {
        return individualRequestDao;
    }

    /**
     * @param individualRequestDao individual request dao
     */
    @Override
    public void setIndividualRequestDao(final IIndividualRequestDao individualRequestDao) {
        this.individualRequestDao = individualRequestDao;
    }

    /**
     * @return the request consumer.
     */
    private IConsumerGateway getRequestConsumer(IIndividualRequest individualRequest, Boolean caseOffLine) {
        if (isCMCRequestType(individualRequest, true)
            && (caseOffLine == null || !caseOffLine)) {
            return cmcRequestConsumer;
        }
        return requestConsumer;
    }

    /**
     * Get the global parameters cache.
     *
     * @return the ICacheable interface for the Global parameters
     */
    private ICacheable getGlobalParametersCache() {
        return globalParametersCache;
    }

    /**
     * Sets the global parameters cache.
     *
     * @param globalParametersCache the global parameters cache.
     */
    public void setGlobalParametersCache(final ICacheable globalParametersCache) {
        this.globalParametersCache = globalParametersCache;
    }

    /**
     * Reads an system parameter to add delay to the request processing if the
     * system parameter has non null value.
     *
     * @param request IndividualRequest object
     */
    private void delayRequestProcessing(final IIndividualRequest request) {
        // Get the target Application code.
        final String targetApplication =
                request.getBulkSubmission().getTargetApplication().getTargetApplicationCode();
        // Pre-fix the target application code to the global parameter key for the request delay
        final String delayParamName =
                targetApplication.toUpperCase() + "_" + IGlobalParameter.ParameterKey.INDV_REQ_DELAY.name();

        // Get the global parameter for the individual request delay
        final String individualReqProcessingDelay = this.getSystemParameter(delayParamName);

        if (individualReqProcessingDelay != null) {
            // If the global parameter is available, proceed with delaying the request processing
            final long delay = Long.parseLong(individualReqProcessingDelay);
            try {
                LOGGER.debug("Delay request processing for {} milliseconds.", delay);

                Thread.sleep(delay);
            } catch (final InterruptedException ie) {
                LOGGER.warn("Delay operation interrupted by interrupt exception {}", ie.getMessage());
            }
        }
    }

    /**
     * Return the named parameter value from global parameters.
     *
     * @param parameterName the name of the parameter.
     * @return value of the parameter name as stored in the database.
     */
    private String getSystemParameter(final String parameterName) {
        final IGlobalParameter globalParameter =
                this.getGlobalParametersCache().getValue(IGlobalParameter.class, parameterName);

        if (globalParameter == null) {
            return null;
        }

        return globalParameter.getValue();

    }

    /**
     * @param individualRequest the individual request.
     */
    private void reQueueRequest(final IIndividualRequest individualRequest) {
        reQueueRequest(individualRequest, false);
    }

    private void reQueueRequest(final IIndividualRequest individualRequest, Boolean caseOffline) {
        // Check the forwarding attempts has not exceeded the max forwarding attempts count.
        if (this.canRequestBeRequeued(individualRequest)) {
            LOGGER.debug("Re-queuing request for SDT reference [{}]", individualRequest.getSdtRequestReference());

            // Create a new message to enqueue.
            final ISdtMessage messageObj = new SdtMessage();
            messageObj.setSdtRequestReference(individualRequest.getSdtRequestReference());
            messageObj.setCaseOffLine(caseOffline);

            SdtMetricsMBean.getMetrics().upRequestRequeues();

            final String targetAppCode =
                individualRequest.getBulkSubmission().getTargetApplication().getTargetApplicationCode();

            this.getMessageWriter().queueMessage(messageObj, targetAppCode, false);
        } else {
            LOGGER.error("Maximum forwarding attempts exceeded for request {}",
                    individualRequest.getSdtRequestReference());
        }
    }

    /**
     * @param individualRequest the individual request object.
     * @return true if the request forwarding attempts is less than the maximum forwarding attempts parameter.
     */
    private boolean canRequestBeRequeued(final IIndividualRequest individualRequest) {
        // Get the max forwarding attempts system parameter.
        final String maxForwardingAttemptStr =
                this.getSystemParameter(IGlobalParameter.ParameterKey.MAX_FORWARDING_ATTEMPTS.name());

        return (individualRequest.getForwardingAttempts() <= Integer.parseInt(maxForwardingAttemptStr));
    }

    /**
     * @return messageWriter the MessageWriter for the messaging API.
     */
    private IMessageWriter getMessageWriter() {
        return messageWriter;
    }

    /**
     * @param messageWriter the message writer.
     */
    public void setMessageWriter(final IMessageWriter messageWriter) {
        this.messageWriter = messageWriter;
    }

    /**
     * @return cacheable interface for the error messages cache.
     */
    public ICacheable getErrorMessagesCache() {
        return errorMessagesCache;
    }

    /**
     * @param errorMessagesCache the error messages cache.
     */
    public void setErrorMessagesCache(final ICacheable errorMessagesCache) {
        this.errorMessagesCache = errorMessagesCache;
    }

}
