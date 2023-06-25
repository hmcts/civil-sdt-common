/* Copyrights and Licenses
 *
 * Copyright (c) 2012-2013 by the Ministry of Justice. All rights reserved.
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 * - Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials
 * provided with the distribution.
 * - All advertising materials mentioning features or use of this software must display the
 * following acknowledgment: "This product includes Money Claims OnLine."
 * - Products derived from this software may not be called "Money Claims OnLine" nor may
 * "Money Claims OnLine" appear in their names without prior written permission of the
 * Ministry of Justice.
 * - Redistributions of any form whatsoever must retain the following acknowledgment: "This
 * product includes Money Claims OnLine."
 * This software is provided "as is" and any expressed or implied warranties, including, but
 * not limited to, the implied warranties of merchantability and fitness for a particular purpose are
 * disclaimed. In no event shall the Ministry of Justice or its contributors be liable for any
 * direct, indirect, incidental, special, exemplary, or consequential damages (including, but
 * not limited to, procurement of substitute goods or services; loss of use, data, or profits;
 * or business interruption). However caused any on any theory of liability, whether in contract,
 * strict liability, or tort (including negligence or otherwise) arising in any way out of the use of this
 * software, even if advised of the possibility of such damage.
 *
 * $Id: $
 * $LastChangedRevision: $
 * $LastChangedDate: $
 * $LastChangedBy: $ */

package uk.gov.moj.sdt.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.gov.moj.sdt.response.SubmitQueryResponse;
import uk.gov.moj.sdt.cmc.consumers.util.ResponsesSummaryUtil;
import uk.gov.moj.sdt.consumers.api.IConsumerGateway;
import uk.gov.moj.sdt.consumers.exception.OutageException;
import uk.gov.moj.sdt.consumers.exception.SoapFaultException;
import uk.gov.moj.sdt.consumers.exception.TimeoutException;
import uk.gov.moj.sdt.dao.api.IBulkCustomerDao;
import uk.gov.moj.sdt.domain.ErrorLog;
import uk.gov.moj.sdt.domain.api.IBulkCustomer;
import uk.gov.moj.sdt.domain.api.IBulkCustomerApplication;
import uk.gov.moj.sdt.domain.api.IErrorLog;
import uk.gov.moj.sdt.domain.api.IErrorMessage;
import uk.gov.moj.sdt.domain.api.IGlobalParameter;
import uk.gov.moj.sdt.domain.api.ISubmitQueryRequest;
import uk.gov.moj.sdt.domain.cache.api.ICacheable;
import uk.gov.moj.sdt.services.api.ISubmitQueryService;
import uk.gov.moj.sdt.services.utils.GenericXmlParser;
import uk.gov.moj.sdt.utils.SdtContext;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.ws.WebServiceException;

/**
 * Implementation class for submit query service.
 *
 * @author d130680
 */
@Service("SubmitQueryService")
public class SubmitQueryService implements ISubmitQueryService {

    /**
     * Logger object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SubmitQueryService.class);

    /**
     * The consumer gateway that will perform the call to the target application
     * web service.
     */
    private IConsumerGateway requestConsumer;

    private IConsumerGateway cmcRequestConsumer;

    /**
     * The ICacheable reference to the global parameters cache.
     */
    private ICacheable globalParametersCache;

    /**
     * The ICacheable reference to the error messages cache.
     */
    private ICacheable errorMessagesCache;

    /**
     * Parser for query request.
     */
    private GenericXmlParser queryRequestXmlParser;

    /**
     * Parser for query request.
     */
    private GenericXmlParser queryResponseXmlParser;

    /**
     * Bulk Customer Dao property.
     */
    private IBulkCustomerDao bulkCustomerDao;

    private ResponsesSummaryUtil responsesSummaryUtil;

    /**
     * Place holder object to sync processing.
     */
    private Object lock = new Object();

    @Autowired
    public SubmitQueryService(@Qualifier("ConsumerGateway")
                                  IConsumerGateway requestConsumer,
                              @Qualifier("CMCConsumerGateway")
                              IConsumerGateway cmcRequestConsumer,
                              @Qualifier("GlobalParametersCache")
                                  ICacheable globalParametersCache,
                              @Qualifier("ErrorMessagesCache")
                                  ICacheable errorMessagesCache,
                              @Qualifier("SubmitQueryRequestXmlParser")
                                  GenericXmlParser queryRequestXmlParser,
                              @Qualifier("SubmitQueryResponseXmlParser")
                                  GenericXmlParser queryResponseXmlParser,
                              @Qualifier("BulkCustomerDao")
                                  IBulkCustomerDao bulkCustomerDao,
                              ResponsesSummaryUtil responsesSummaryUtil) {
        this.requestConsumer = requestConsumer;
        this.cmcRequestConsumer = cmcRequestConsumer;
        this.globalParametersCache = globalParametersCache;
        this.errorMessagesCache = errorMessagesCache;
        this.queryRequestXmlParser = queryRequestXmlParser;
        this.queryResponseXmlParser = queryResponseXmlParser;
        this.bulkCustomerDao = bulkCustomerDao;
        this.responsesSummaryUtil = responsesSummaryUtil;
    }

    /**
     * threshold for incoming requests for each target application.
     */
    private Map<String, Integer> concurrentRequestsInProgress = new HashMap<>();

    @Override
    public void submitQuery(final ISubmitQueryRequest submitQueryRequest) {
        enrich(submitQueryRequest);

        extractAndWriteCriteria();

        if (isThresholdReached(submitQueryRequest)) {
            updateRequestServerBusy(submitQueryRequest);
            return;
        }

        // Call consumer to submit the request to target application.
        try {
            this.sendRequestToTargetApp(submitQueryRequest);

            this.updateCompletedRequest();
        } catch (final TimeoutException e) {
            LOGGER.error("Timeout exception for SDT Bulk Customer [" +
                    submitQueryRequest.getBulkCustomer().getSdtCustomerId() + "] ", e.getMessage());

            // Update the request for timeout error.
            this.updateRequestTimeOut(submitQueryRequest);

        } catch (final OutageException e) {
            LOGGER.error("Outage exception for SDT Bulk Customer [" +
                    submitQueryRequest.getBulkCustomer().getSdtCustomerId() + "] ", e.getMessage());
            // Update the request for outage.
            this.updateRequestOutage(submitQueryRequest);
        } catch (final SoapFaultException e) {
            LOGGER.error("Soap exception for SDT Bulk Customer [" +
                    submitQueryRequest.getBulkCustomer().getSdtCustomerId() + "] ", e.getMessage());
            // Update the request with the soap fault reason
            this.updateRequestSoapError(submitQueryRequest);
        } catch (final WebServiceException e) {
            LOGGER.error("WebService exception for SDT Bulk Customer [" +
                    submitQueryRequest.getBulkCustomer().getSdtCustomerId() + "]", e.getMessage());
            // Update the request with reason
            this.updateRequestSoapError(submitQueryRequest);
        } finally {
            final String targetApp =
                    submitQueryRequest.getTargetApplication().getTargetApplicationCode().toUpperCase();
            synchronized (lock) {
                // decrease concurrent submit query requests count
                this.concurrentRequestsInProgress
                        .put(targetApp, this.concurrentRequestsInProgress.get(targetApp) - 1);
            }
        }
    }

    /**
     * Update request when request times out.
     *
     * @param submitQueryRequest submit query request.
     */
    private void updateRequestTimeOut(final ISubmitQueryRequest submitQueryRequest) {
        // Get the Error message to indicate that call to target application has
        // timed out
        buildTargetAppError(submitQueryRequest);

        // Clear out xml to prevent enrichment
        SdtContext.getContext().setRawOutXml(null);

    }

    /**
     * Update request when no response from server.
     *
     * @param submitQueryRequest submit query request.
     */
    private void updateRequestOutage(final ISubmitQueryRequest submitQueryRequest) {
        // Get the Error message to indicate that there has been no response
        // from the server.
        buildTargetAppError(submitQueryRequest);

        // Clear out xml to prevent enrichment
        SdtContext.getContext().setRawOutXml(null);
    }

    /**
     * Builds target application response.
     *
     * @param submitQueryRequest submit query request object.
     */
    private void buildTargetAppError(final ISubmitQueryRequest submitQueryRequest) {
        // NOTE: At the moment, functionality is common for Timeout and Outage,
        // but two methods to separate
        // implementation.
        final IErrorMessage errorMessageParam =
                this.getErrorMessagesCache().getValue(IErrorMessage.class,
                        IErrorMessage.ErrorCode.TAR_APP_ERROR.name());

        final List<String> replacements = new ArrayList<>();
        replacements.add(getContactDetails());
        final String errorText = MessageFormat.format(errorMessageParam.getErrorText(), replacements.toArray());

        final IErrorLog errorLog = new ErrorLog(errorMessageParam.getErrorCode(), errorText);
        submitQueryRequest.reject(errorLog);
    }

    /**
     * Get the contact Details from the global parameters cache.
     *
     * @return contact details
     */
    private String getContactDetails() {
        final IGlobalParameter globalParameter =
                globalParametersCache.getValue(IGlobalParameter.class,
                        IGlobalParameter.ParameterKey.CONTACT_DETAILS.name());
        return globalParameter.getValue();
    }

    /**
     * Update request with SOAP error details.
     *
     * @param submitQueryRequest submit query request
     */
    private void updateRequestSoapError(final ISubmitQueryRequest submitQueryRequest) {
        final IErrorMessage errorMessageParam =
                this.getErrorMessagesCache().getValue(IErrorMessage.class,
                        IErrorMessage.ErrorCode.SDT_INT_ERR.name());

        final List<String> replacements = new ArrayList<>();
        replacements.add(getContactDetails());
        final String errorText = MessageFormat.format(errorMessageParam.getErrorText(), replacements.toArray());

        final IErrorLog errorLog = new ErrorLog(errorMessageParam.getErrorCode(), errorText);
        submitQueryRequest.reject(errorLog);

        // Clear out xml to prevent enrichment
        SdtContext.getContext().setRawOutXml(null);
    }

    /**
     * Update request when it is processed successfully.
     */
    private void updateCompletedRequest() {
        final String targetAppResponse = queryResponseXmlParser.parse();

        // Setup raw XML from target application for addition to raw out stream
        // in interceptor.
        SdtContext.getContext().setRawOutXml(targetAppResponse);

        LOGGER.debug("rawOutXml {}", SdtContext.getContext().getRawOutXml());
    }

    /**
     * Check if the number maximum number of concurrent threads has been
     * reached?
     *
     * @param submitQueryRequest submit query request.
     * @return true if threshold is at surface, false otherwise.
     */
    private synchronized boolean isThresholdReached(final ISubmitQueryRequest submitQueryRequest) {
        boolean maxReached = false;

        // 1. get target application code e.g. MCOL.
        final String targetAppCode =
                submitQueryRequest.getTargetApplication().getTargetApplicationCode().toUpperCase();

        // 2. retrieve max concurrent submit query requests allowed for this
        // target application
        final String concurrentQueryReqParamName =
                targetAppCode + "_" + IGlobalParameter.ParameterKey.MAX_CONCURRENT_QUERY_REQ.name();
        final String maxConcurrentQueryRequests = this.getSystemParameter(concurrentQueryReqParamName);

        int requestsInProgress;

        // 3. retrieve number of requests in progress from local map.
        if (this.concurrentRequestsInProgress.containsKey(targetAppCode)) {
            requestsInProgress = this.concurrentRequestsInProgress.get(targetAppCode);
        } else {
            // this is first request for this target app
            requestsInProgress = 0;
            this.concurrentRequestsInProgress.put(targetAppCode, requestsInProgress);
        }

        // 4. if within - increase value in map and process request
        if (requestsInProgress < Integer.valueOf(maxConcurrentQueryRequests)) {
            this.concurrentRequestsInProgress.put(targetAppCode, requestsInProgress + 1);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Increment concurrent requests. Current requests in progress [{}]",
                        concurrentRequestsInProgress.size());
            }
        } else
        // reject request
        {
            maxReached = true;
            LOGGER.warn("Threshold reached for target app [{}] - in progress [{}] max allowed [{}]",
                    targetAppCode, requestsInProgress, maxConcurrentQueryRequests);
        }

        return maxReached;
    }

    /**
     * Populate request with server busy message.
     *
     * @param submitQueryRequest request.
     */
    private void updateRequestServerBusy(final ISubmitQueryRequest submitQueryRequest) {

        // Get the Error message to indicate that maximum submit query reached.
        final IErrorMessage errorMessageParam =
                this.getErrorMessagesCache().getValue(IErrorMessage.class,
                        IErrorMessage.ErrorCode.TAR_APP_BUSY.name());

        IErrorLog errorLog = null;
        errorLog = new ErrorLog(errorMessageParam.getErrorCode(), errorMessageParam.getErrorText());
        submitQueryRequest.reject(errorLog);

        // Clear out xml to prevent enrichment
        SdtContext.getContext().setRawOutXml(null);

        LOGGER.error("Request rejected for customer[{}] with error [{}]",
                submitQueryRequest.getBulkCustomer().getSdtCustomerId(), errorLog);
    }

    /**
     * Enrich submit query instance to prepare for persistence.
     *
     * @param submitQueryRequest submit query request
     */
    private void enrich(final ISubmitQueryRequest submitQueryRequest) {
        LOGGER.debug("Enrich submit query instance to prepare for persistence");

        // Get the Bulk Customer from the customer dao for the SDT customer Id
        final IBulkCustomer bulkCustomer =
                this.getBulkCustomerDao().getBulkCustomerBySdtId(
                        submitQueryRequest.getBulkCustomer().getSdtCustomerId());

        submitQueryRequest.setBulkCustomer(bulkCustomer);

        final IBulkCustomerApplication bulkCustomerApplication =
                bulkCustomer.getBulkCustomerApplication(submitQueryRequest.getTargetApplication()
                        .getTargetApplicationCode());

        submitQueryRequest.setTargetApplication(bulkCustomerApplication.getTargetApplication());
    }

    /**
     * Extract criteria xml from raw xml that would have been saved by inbound
     * interceptor. Write extracted fragment to threadlocal for outbound
     * interceptor.
     */
    private void extractAndWriteCriteria() {

        LOGGER.debug("Extract and setup criteria for outbound interceptor");

        final String criteriaXml = this.queryRequestXmlParser.parse();

        // Setup criteria XML so that it can be picked up by the outbound
        // interceptor and injected into the outbound XML.
        SdtContext.getContext().setRawOutXml(criteriaXml);

    }

    /**
     * Send the submit query request to target application for submission.
     *
     * @param submitQueryRequest the submit query request to be sent to target application.
     * @throws OutageException  when the target web service is not responding.
     * @throws TimeoutException when the target web service does not respond back in time.
     */
    private void sendRequestToTargetApp(final ISubmitQueryRequest submitQueryRequest)
            throws OutageException, TimeoutException {
        final IGlobalParameter connectionTimeOutParam =
                this.globalParametersCache.getValue(IGlobalParameter.class,
                        IGlobalParameter.ParameterKey.TARGET_APP_TIMEOUT.name());
        final IGlobalParameter requestTimeOutParam =
                this.globalParametersCache.getValue(IGlobalParameter.class, "TARGET_APP_RESP_TIMEOUT");

        long requestTimeOut = 0;
        long connectionTimeOut = 0;

        if (requestTimeOutParam != null) {
            requestTimeOut = Long.parseLong(requestTimeOutParam.getValue());
        }

        if (connectionTimeOutParam != null) {
            connectionTimeOut = Long.parseLong(connectionTimeOutParam.getValue());
        }

        LOGGER.debug("Send submit query request to target application");

        SubmitQueryResponse mcolSubmitQueryResponse = requestConsumer.submitQuery(submitQueryRequest, connectionTimeOut, requestTimeOut);
        SubmitQueryResponse cmcSubmitQueryResponse = cmcRequestConsumer.submitQuery(submitQueryRequest, connectionTimeOut, requestTimeOut);

        // adjust results count
        String summaryResultsXML = "";
        if (null != cmcSubmitQueryResponse && null != cmcSubmitQueryResponse.getClaimDefencesResults()
                && !cmcSubmitQueryResponse.getClaimDefencesResults().isEmpty()) {
            submitQueryRequest.setResultCount(submitQueryRequest.getResultCount()
                    + cmcSubmitQueryResponse.getClaimDefencesResults().size());

            summaryResultsXML = responsesSummaryUtil.getSummaryResults(mcolSubmitQueryResponse,
                    cmcSubmitQueryResponse.getClaimDefencesResults());
        }

        // Set summary results XML to be picked up later
        SdtContext.getContext().setClaimDefencesSummaryResultsXml(summaryResultsXML);
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

    /**
     * Sets the global parameters cache.
     *
     * @param globalParametersCache the global parameters cache.
     */
    public void setGlobalParametersCache(final ICacheable globalParametersCache) {
        this.globalParametersCache = globalParametersCache;
    }

    /**
     * Setter for queryResponseXmlParser.
     *
     * @param queryResponseXmlParser the queryResponseXmlParser to set
     */
    public void setQueryResponseXmlParser(final GenericXmlParser queryResponseXmlParser) {
        this.queryResponseXmlParser = queryResponseXmlParser;
    }

    /**
     * Setter for queryRequestXmlParser.
     *
     * @param queryRequestXmlParser the queryRequestXmlParser to set
     */
    public void setQueryRequestXmlParser(final GenericXmlParser queryRequestXmlParser) {
        this.queryRequestXmlParser = queryRequestXmlParser;
    }

    /**
     * Get the bulk customer DAO bean.
     *
     * @return the Bulk Customer DAO.
     */
    public IBulkCustomerDao getBulkCustomerDao() {
        return bulkCustomerDao;
    }

    /**
     * Sets the Bulk Customer DAO object.
     *
     * @param bulkCustomerDao the Bulk Customer Dao.
     */
    public void setBulkCustomerDao(final IBulkCustomerDao bulkCustomerDao) {
        this.bulkCustomerDao = bulkCustomerDao;
    }

    /**
     * Sets the consumer gateway.
     *
     * @param requestConsumer the request consumer.
     */
    public void setRequestConsumer(final IConsumerGateway requestConsumer) {
        this.requestConsumer = requestConsumer;
    }

    /**
     * Return the named parameter value from global parameters.
     *
     * @param parameterName the name of the parameter.
     * @return value of the parameter name as stored in the database.
     */
    private String getSystemParameter(final String parameterName) {
        final IGlobalParameter globalParameter =
                this.globalParametersCache.getValue(IGlobalParameter.class, parameterName);

        if (globalParameter == null) {
            return null;
        }

        return globalParameter.getValue();
    }
}
