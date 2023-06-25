/* Copyrights and Licenses
 *
 * Copyright (c) 2013 by the Ministry of Justice. All rights reserved.
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
 * $Id$
 * $LastChangedRevision$
 * $LastChangedDate$
 * $LastChangedBy$ */
package uk.gov.moj.sdt.producers.sdtws;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.moj.sdt.handlers.api.IWsCreateBulkRequestHandler;
import uk.gov.moj.sdt.handlers.api.IWsReadBulkRequestHandler;
import uk.gov.moj.sdt.handlers.api.IWsReadSubmitQueryHandler;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.BulkStatusCodeType;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.BulkStatusType;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.ErrorType;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.StatusCodeType;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.StatusType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkfeedbackrequestschema.BulkFeedbackRequestType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkfeedbackresponseschema.BulkFeedbackResponseType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkfeedbackresponseschema.BulkRequestStatusType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkrequestschema.BulkRequestType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkrequestschema.HeaderType;
import uk.gov.moj.sdt.ws._2013.sdt.bulkresponseschema.BulkResponseType;
import uk.gov.moj.sdt.ws._2013.sdt.submitqueryrequestschema.SubmitQueryRequestType;
import uk.gov.moj.sdt.ws._2013.sdt.submitqueryresponseschema.SubmitQueryResponseType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.reset;

/**
 * Unit test for {@link SdtEndpointPortType}.
 *
 * @author d276205
 */
@ExtendWith(MockitoExtension.class)
class SdtEndpointPortTypeTest extends AbstractSdtUnitTestBase {

    /**
     * Test subject.
     */
    private SdtEndpointPortType portType;

    /**
     * Mocked IWsCreateBulkRequestHandler instance.
     */
    @Mock
    private IWsCreateBulkRequestHandler mockCreateBulkRequestHandler;

    /**
     * Mocked IWsReadBulkRequestHandler instance.
     */
    @Mock
    private IWsReadBulkRequestHandler mockBulkRequestHandler;

    /**
     * Mocked IWsReadSubmitQueryHandler instance.
     */
    @Mock
    private IWsReadSubmitQueryHandler mockSubmitQueryHandler;

    private Logger mockLogger;

    private static final String RESPONSE_EXPECTED = "Response expected";
    private static final String RUNTIME_EXCEPTION_SHOULD_HAVE_BEEN_THROWN = "Runtime exception should have been thrown";
    private static final String SDT_SYSTEM_COMPONENT_ERROR =
            "A SDT system component error has occurred. Please contact the SDT support team for assistance";

    private static final String MOCK_CODE = "MOCK_CODE";
    private static final String MOCK_ERROR = "MOCK_ERROR";

    /**
     * Set up common for all tests.
     */
    @BeforeEach
    @Override
    public void setUp() {
        mockLogger = mock(Logger.class);
        try (MockedStatic<LoggerFactory> mockLoggerFactory = Mockito.mockStatic(LoggerFactory.class)) {
            mockLoggerFactory.when(() -> LoggerFactory.getLogger(any(Class.class)))
                .thenReturn(mockLogger);
            when(mockLogger.isDebugEnabled())
                .thenReturn(true);

            portType = new SdtEndpointPortType(
                mockCreateBulkRequestHandler,
                mockBulkRequestHandler,
                mockSubmitQueryHandler
            );
        }
    }

    @AfterEach
    public void resetLogger() {
        reset(mockLogger);
    }

    /**
     * Test submit bulk method completes successfully.
     */
    @Test
    void testSubmitBulkSuccess() {
        final BulkResponseType dummyResponse = createBulkResponse();
        final BulkRequestType dummyRequest = createBulkRequest();

        when(mockCreateBulkRequestHandler.submitBulk(dummyRequest)).thenReturn(dummyResponse);

        final BulkResponseType response = portType.submitBulk(dummyRequest);

        verify(mockCreateBulkRequestHandler).submitBulk(dummyRequest);
        assertNotNull(response, RESPONSE_EXPECTED);
        assertEquals(StatusCodeType.OK, response.getStatus().getCode());
        assertEquals(MOCK_ERROR, response.getStatus().getError().getDescription());
        assertEquals(MOCK_CODE, response.getStatus().getError().getCode());
        verify(mockLogger, never()).debug(any());
        verify(mockLogger, never()).isDebugEnabled();
    }

    /**
     * Test submit bulk method handles exceptions successfully.
     */
    @Test
    void testSubmitBulkException() {
        when(mockCreateBulkRequestHandler.submitBulk(any())).thenThrow(new RuntimeException("test"));

        try {
            portType.submitBulk(createBulkRequest());
            fail(RUNTIME_EXCEPTION_SHOULD_HAVE_BEEN_THROWN);
        } catch (final RuntimeException re) {
            assertEquals(SDT_SYSTEM_COMPONENT_ERROR, re.getMessage());
        }

        verify(mockCreateBulkRequestHandler).submitBulk(any());
        verify(mockLogger, never()).isDebugEnabled();
    }

    /**
     * Test bulk feedback method completes successfully.
     */
    @Test
    void testBulkFeedbackSuccess() {
        when(mockBulkRequestHandler.getBulkFeedback(any(BulkFeedbackRequestType.class)))
                .thenReturn(createBulkFeedbackResponse());

        final BulkFeedbackResponseType response = portType.getBulkFeedback(createBulkFeedbackRequestType());

        assertNotNull(response, RESPONSE_EXPECTED);
        assertEquals(MOCK_ERROR, response.getBulkRequestStatus().getBulkStatus().getError().getDescription());
        assertEquals(MOCK_CODE, response.getBulkRequestStatus().getBulkStatus().getError().getCode());
        verify(mockBulkRequestHandler).getBulkFeedback(any(BulkFeedbackRequestType.class));
        verify(mockLogger).debug(anyString(), anyLong());
    }

    /**
     * Test bulk feedback method handles exceptions successfully.
     */
    @Test
    void testBulkFeedbackException() {
        when(mockBulkRequestHandler.getBulkFeedback(any(BulkFeedbackRequestType.class)))
                .thenThrow(new RuntimeException("test"));

        try {
            portType.getBulkFeedback(createBulkFeedbackRequestType());
            fail(RUNTIME_EXCEPTION_SHOULD_HAVE_BEEN_THROWN);
        } catch (final RuntimeException re) {
            assertEquals(SDT_SYSTEM_COMPONENT_ERROR, re.getMessage());
        }

        verify(mockBulkRequestHandler).getBulkFeedback(any(BulkFeedbackRequestType.class));
        verify(mockLogger, never()).isDebugEnabled();
    }

    /**
     * Test submit query method completes successfully.
     */
    @Test
    void testSubmitQuerySuccess() {
        when(mockSubmitQueryHandler.submitQuery(any(SubmitQueryRequestType.class)))
                .thenReturn(createSubmitQueryResponse());

        final SubmitQueryResponseType response = portType.submitQuery(createsubmitQueryRequestType());

        assertNotNull(response, RESPONSE_EXPECTED);
        verify(mockSubmitQueryHandler).submitQuery(any(SubmitQueryRequestType.class));

        assertEquals(MOCK_ERROR, response.getStatus().getError().getDescription());
        assertEquals(MOCK_CODE, response.getStatus().getError().getCode());
        verify(mockLogger, never()).debug(anyString(), anyLong());
    }

    /**
     * Test submit query method handles exceptions successfully.
     */
    @Test
    void testSubmitQueryException() {
        when(mockSubmitQueryHandler.submitQuery(any(SubmitQueryRequestType.class)))
                .thenThrow(new RuntimeException("test"));

        try {
            portType.submitQuery(createsubmitQueryRequestType());
            fail(RUNTIME_EXCEPTION_SHOULD_HAVE_BEEN_THROWN);
        } catch (final RuntimeException re) {
            assertEquals(SDT_SYSTEM_COMPONENT_ERROR, re.getMessage());
        }

        verify(mockSubmitQueryHandler).submitQuery(any(SubmitQueryRequestType.class));
        verify(mockLogger, never()).isDebugEnabled();
    }

    /**
     * Creates dummy bulk submission request.
     *
     * @return dummy request.
     */
    private BulkRequestType createBulkRequest() {
        final BulkRequestType request = new BulkRequestType();

        final HeaderType header = new HeaderType();
        header.setCustomerReference("1");
        header.setRequestCount(1);
        header.setSdtCustomerId(12345678);
        header.setTargetApplicationId("mcol");

        request.setHeader(header);
        return request;
    }

    /**
     * Creates dummy bulk submission response.
     *
     * @return dummy response.
     */
    private BulkResponseType createBulkResponse() {
        final BulkResponseType response = new BulkResponseType();

        final StatusType statusType = new StatusType();
        statusType.setCode(StatusCodeType.OK);
        final ErrorType errorType = new ErrorType();
        errorType.setDescription(MOCK_ERROR);
        errorType.setCode(MOCK_CODE);
        statusType.setError(errorType);
        response.setStatus(statusType);
        return response;
    }

    /**
     * Creates dummy request.
     *
     * @return dummy request.
     */
    private BulkFeedbackRequestType createBulkFeedbackRequestType() {
        final BulkFeedbackRequestType request = new BulkFeedbackRequestType();

        final uk.gov.moj.sdt.ws._2013.sdt.bulkfeedbackrequestschema.HeaderType header =
                new uk.gov.moj.sdt.ws._2013.sdt.bulkfeedbackrequestschema.HeaderType();
        header.setSdtBulkReference("123");
        header.setSdtCustomerId(12345678);

        request.setHeader(header);
        return request;
    }

    /**
     * Creates dummy bulk feedback response.
     *
     * @return dummy response.
     */
    private BulkFeedbackResponseType createBulkFeedbackResponse() {
        final BulkFeedbackResponseType response = new BulkFeedbackResponseType();

        final StatusType statusType = new StatusType();
        statusType.setCode(StatusCodeType.OK);

        final BulkRequestStatusType bulkRequestStatus = new BulkRequestStatusType();
        bulkRequestStatus.setCustomerReference("123");
        bulkRequestStatus.setRequestCount(0L);
        bulkRequestStatus.setSdtBulkReference("123");
        bulkRequestStatus.setSdtService("sdt");
        bulkRequestStatus.setStatus(statusType);

        final BulkStatusType bulkStatusType = new BulkStatusType();
        bulkStatusType.setCode(BulkStatusCodeType.COMPLETED);
        final ErrorType errorType = new ErrorType();
        errorType.setDescription(MOCK_ERROR);
        errorType.setCode(MOCK_CODE);
        bulkStatusType.setError(errorType);
        bulkRequestStatus.setBulkStatus(bulkStatusType);

        response.setBulkRequestStatus(bulkRequestStatus);
        return response;
    }

    /**
     * Creates dummy request.
     *
     * @return dummy request.
     */
    private SubmitQueryRequestType createsubmitQueryRequestType() {
        final SubmitQueryRequestType request = new SubmitQueryRequestType();

        final uk.gov.moj.sdt.ws._2013.sdt.submitqueryrequestschema.HeaderType header =
                new uk.gov.moj.sdt.ws._2013.sdt.submitqueryrequestschema.HeaderType();
        header.setTargetApplicationId("mcol");
        header.setSdtCustomerId(12345678);

        request.setHeader(header);
        return request;
    }

    /**
     * Creates dummy submit query response.
     *
     * @return dummy response.
     */
    private SubmitQueryResponseType createSubmitQueryResponse() {
        final SubmitQueryResponseType response = new SubmitQueryResponseType();

        final StatusType statusType = new StatusType();
        statusType.setCode(StatusCodeType.OK);
        final ErrorType errorType = new ErrorType();
        errorType.setDescription(MOCK_ERROR);
        errorType.setCode(MOCK_CODE);
        statusType.setError(errorType);

        response.setSdtCustomerId(123);
        response.setStatus(statusType);
        response.setSdtService("sdt");
        return response;
    }

}
