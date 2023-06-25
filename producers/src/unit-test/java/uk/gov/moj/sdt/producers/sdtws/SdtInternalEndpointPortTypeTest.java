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
import uk.gov.moj.sdt.handlers.api.IWsUpdateItemHandler;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.ErrorType;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.StatusCodeType;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.StatusType;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.UpdateStatusCodeType;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.UpdateStatusType;
import uk.gov.moj.sdt.ws._2013.sdt.individualupdaterequestschema.HeaderType;
import uk.gov.moj.sdt.ws._2013.sdt.individualupdaterequestschema.UpdateRequestType;
import uk.gov.moj.sdt.ws._2013.sdt.individualupdateresponseschema.UpdateResponseType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link SdtInternalEndpointPortType}.
 *
 * @author d276205
 */
@ExtendWith(MockitoExtension.class)
class SdtInternalEndpointPortTypeTest extends AbstractSdtUnitTestBase {

    /**
     * Test subject.
     */
    private SdtInternalEndpointPortType portType;

    /**
     * Mocked IWsUpdateItemHandler instance.
     */
    @Mock
    private IWsUpdateItemHandler mockUpdateItemHandler;

    private Logger mockLogger;


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
            portType = new SdtInternalEndpointPortType(mockUpdateItemHandler);
        }
    }

    @AfterEach
    public void resetLogger() {
        reset(mockLogger);
    }

    /**
     * Test update item method completes successfully.
     */
    @Test
    void testUpdateItemSuccess() {
        when(mockUpdateItemHandler.updateItem(any(UpdateRequestType.class))).thenReturn(createUpdateResponse());

        final UpdateResponseType response = portType.updateItem(createUpdateRequest());

        verify(mockUpdateItemHandler).updateItem(any(UpdateRequestType.class));
        assertNotNull(response, "Response expected");
        assertEquals("MOCK_ERROR", response.getStatus().getError().getDescription());
        assertEquals("MOCK_CODE", response.getStatus().getError().getCode());
        verify(mockLogger).isDebugEnabled();
    }

    /**
     * Test update item method handles exceptions successfully.
     */
    @Test
    void testUpdateItemException() {
        when(mockUpdateItemHandler.updateItem(any(UpdateRequestType.class))).thenThrow(new RuntimeException("test"));

        try {
            portType.updateItem(createUpdateRequest());
            fail("Runtime exception should have been thrown");
        } catch (final RuntimeException re) {
            assertEquals(
                    "A SDT system component error has occurred. Please contact the SDT support team for assistance",
                    re.getMessage());
        }

        verify(mockUpdateItemHandler).updateItem(any(UpdateRequestType.class));
        verify(mockLogger, never()).isDebugEnabled();
    }

    /**
     * Creates dummy request.
     *
     * @return dummy request.
     */
    private UpdateRequestType createUpdateRequest() {
        final UpdateRequestType request = new UpdateRequestType();

        final HeaderType header = new HeaderType();
        header.setSdtRequestId("12345678");
        final UpdateStatusType statusType = new UpdateStatusType();
        statusType.setCode(UpdateStatusCodeType.ACCEPTED);
        final ErrorType errorType = new ErrorType();
        errorType.setDescription("MOCK_ERROR");
        errorType.setCode("MOCK_CODE");
        statusType.setError(errorType);
        request.setStatus(statusType);

        request.setHeader(header);
        return request;
    }

    /**
     * Creates dummy response.
     *
     * @return dummy response.
     */
    private UpdateResponseType createUpdateResponse() {
        final UpdateResponseType response = new UpdateResponseType();

        final StatusType statusType = new StatusType();
        statusType.setCode(StatusCodeType.OK);
        final ErrorType errorType = new ErrorType();
        errorType.setDescription("MOCK_ERROR");
        errorType.setCode("MOCK_CODE");
        statusType.setError(errorType);
        response.setStatus(statusType);
        return response;
    }

}
