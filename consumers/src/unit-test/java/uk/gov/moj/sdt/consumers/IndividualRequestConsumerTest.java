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

package uk.gov.moj.sdt.consumers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import uk.gov.moj.sdt.consumers.exception.OutageException;
import uk.gov.moj.sdt.consumers.exception.SoapFaultException;
import uk.gov.moj.sdt.consumers.exception.TimeoutException;
import uk.gov.moj.sdt.domain.IndividualRequest;
import uk.gov.moj.sdt.domain.api.IIndividualRequest;
import uk.gov.moj.sdt.transformers.api.IConsumerTransformer;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.CreateStatusCodeType;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.CreateStatusType;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.ErrorType;
import uk.gov.moj.sdt.ws._2013.sdt.targetapp.indvrequestschema.HeaderType;
import uk.gov.moj.sdt.ws._2013.sdt.targetapp.indvrequestschema.IndividualRequestType;
import uk.gov.moj.sdt.ws._2013.sdt.targetapp.indvresponseschema.IndividualResponseType;
import uk.gov.moj.sdt.ws._2013.sdt.targetappinternalendpoint.ITargetAppInternalEndpointPortType;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class for the individual request consumer.
 *
 * @author Manoj Kulkarni
 */
@ExtendWith(MockitoExtension.class)
class IndividualRequestConsumerTest extends ConsumerTestBase {

    /**
     * Consumer transformer for individual request.
     */
    @Mock
    protected IConsumerTransformer<IndividualResponseType, IndividualRequestType, IIndividualRequest,
            IIndividualRequest> mockTransformer;

    /**
     * Mock Client instance.
     */
    @Mock
    ITargetAppInternalEndpointPortType mockClient;

    @Mock
    SOAPFault soapFault;

    /**
     * Individual Request Consumer instance of the inner class under test.
     */
    IndividualRequestConsumer individualRequestConsumer;

    private static final String TEST_FINISHED_SUCCESSFULLY = "Test finished successfully";
    private static final String GOT_THE_EXCEPTION_AS_EXPECTED = "Got the exception as expected";

    /**
     * Individual Request instance for testing in the methods.
     */
    private IIndividualRequest individualRequest;

    /**
     * Individual Request type for the individual request.
     */
    private IndividualRequestType individualRequestType;

    /**
     * Method to do any pre-test set-up.
     */
    @BeforeEach
    @Override
    public void setUp() {
        IndividualRequestConsumer consumer = new IndividualRequestConsumer(mockTransformer);
        individualRequestConsumer =  Mockito.spy(consumer);

        individualRequestConsumer.setTransformer(mockTransformer);
        individualRequestConsumer.setRethrowOnFailureToConnect(true);

        individualRequest = this.createIndividualRequest();
        individualRequestType = this.createRequestType(individualRequest);
    }

    @Test
    void getClient() {
        final String targetApplicationCode = "";
        final String serviceType = "";
        final String webServiceEndPoint = "";
        final long connectionTimeOut = 0L;
        final long receiveTimeOut = 0L;
        doReturn(mockClient).when(individualRequestConsumer)
            .getClient(anyString(), anyString(), anyString(), anyLong(), anyLong());

        ITargetAppInternalEndpointPortType portType = individualRequestConsumer.getClient(targetApplicationCode,
                serviceType, webServiceEndPoint, connectionTimeOut, receiveTimeOut);
        assertNotNull(portType);
    }

    @Test
    void getTransformer() {
        assertNotNull(individualRequestConsumer.getTransformer());
    }

    /**
     * Test method for successful processing of individual request.
     */
    @Test
    void processIndividualRequestSuccess() {
        final IndividualResponseType individualResponseType = generateResponse();

        when(mockTransformer.transformDomainToJaxb(individualRequest)).thenReturn(individualRequestType);

        when(mockClient.submitIndividual(individualRequestType)).thenReturn(individualResponseType);

        mockTransformer.transformJaxbToDomain(individualResponseType, individualRequest);

        doAnswer((Answer<Void>) invocation -> {
            ((IndividualRequest) invocation.getArgument(1))
                    .setRequestStatus (IIndividualRequest.IndividualRequestStatus.ACCEPTED.getStatus ());
            // required to be null for a void method
            return null;
        }).when(mockTransformer).transformJaxbToDomain(individualResponseType, individualRequest);

        doReturn(mockClient).when(individualRequestConsumer)
            .getClient(anyString(), anyString(), anyString(), anyLong(), anyLong());

        individualRequestConsumer.processIndividualRequest(individualRequest, CONNECTION_TIME_OUT, RECEIVE_TIME_OUT);

        verify(mockTransformer, atLeastOnce()).transformJaxbToDomain(individualResponseType, individualRequest);
        verify(mockTransformer).transformDomainToJaxb(individualRequest);
        verify(mockClient).submitIndividual(individualRequestType);

        assertTrue(true, TEST_FINISHED_SUCCESSFULLY);
    }

    /**
     * Test method for successful processing of individual request with an included error.
     */
    @Test
    void processIndividualRequestError() {
        final IndividualResponseType individualResponseType = generateErrorResponse();

        when(mockTransformer.transformDomainToJaxb(individualRequest)).thenReturn(individualRequestType);

        when(mockClient.submitIndividual(individualRequestType)).thenReturn(individualResponseType);

        mockTransformer.transformJaxbToDomain(individualResponseType, individualRequest);

        doAnswer((Answer<Void>) invocation -> {
            ((IndividualRequest) invocation.getArgument(1))
                .setRequestStatus (IIndividualRequest.IndividualRequestStatus.ACCEPTED.getStatus ());
            // required to be null for a void method
            return null;
        }).when(mockTransformer).transformJaxbToDomain(individualResponseType, individualRequest);

        doReturn(mockClient).when(individualRequestConsumer)
            .getClient(anyString(), anyString(), anyString(), anyLong(), anyLong());

        individualRequestConsumer.processIndividualRequest(individualRequest, CONNECTION_TIME_OUT,
                                                           RECEIVE_TIME_OUT);

        verify(mockTransformer, atLeastOnce()).transformJaxbToDomain(individualResponseType, individualRequest);
        verify(mockTransformer).transformDomainToJaxb(individualRequest);
        verify(mockClient).submitIndividual(individualRequestType);

        assertTrue(true, TEST_FINISHED_SUCCESSFULLY);
    }

    /**
     * Test method for processing of individual request outage error.
     */
    @Test
    void processIndividualRequestOutage ()
    {
        when(mockTransformer.transformDomainToJaxb(individualRequest)).thenReturn(individualRequestType);

        final WebServiceException wsException = mock(WebServiceException.class);
        when(wsException.getCause()).thenReturn(new ConnectException("Server down error"));

        when(mockClient.submitIndividual(individualRequestType)).thenThrow (wsException);

        doReturn(mockClient).when(individualRequestConsumer)
            .getClient(anyString(), anyString(), anyString(), anyLong(), anyLong());

        try {
            this.individualRequestConsumer.processIndividualRequest (individualRequest, CONNECTION_TIME_OUT,
                    RECEIVE_TIME_OUT);

            fail("Expecting an OutageException here.");
        } catch (final OutageException toe) {
            assertTrue(true, GOT_THE_EXCEPTION_AS_EXPECTED);
        }

        verify(mockTransformer).transformDomainToJaxb(individualRequest);
        verify(mockClient).submitIndividual(individualRequestType);

        assertTrue(true, TEST_FINISHED_SUCCESSFULLY);
    }

    /**
     * Test method for processing of individual request outage error.
     * Disables rethrowOnFailureToConnect to trigger the second if/else statement in super.handleClientErrors,
     * this triggers the Thread.sleep loop to keep reattempting handleClientErrors
     * Using Future to cancel out of the Thread.sleep loop for coverage
     */
    @Test
    void processIndividualRequestOutageReattemptLoop() {
        when(mockTransformer.transformDomainToJaxb(individualRequest)).thenReturn(individualRequestType);

        final WebServiceException wsException = new WebServiceException ();
        wsException.initCause (new ConnectException("Server down error"));

        when(mockClient.submitIndividual(individualRequestType)).thenThrow (wsException);

        doReturn(mockClient).when(individualRequestConsumer)
            .getClient(anyString(), anyString(), anyString(), anyLong(), anyLong());

        // Disable exception throw to continue past super.handleClientErrors and trigger Thread.sleep
        individualRequestConsumer.setRethrowOnFailureToConnect(false);

        // Runs the method for half the CONNECTION_TIME_OUT time to trigger InterruptedException on Thread.sleep
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future future = executor.submit(
            () -> individualRequestConsumer.processIndividualRequest(individualRequest, CONNECTION_TIME_OUT,
                                                                     RECEIVE_TIME_OUT)
        );
        try {
            future.get(CONNECTION_TIME_OUT / 4, TimeUnit.MILLISECONDS);
        } catch (Exception  e) {
            future.cancel(true);
        } finally {
            executor.shutdownNow();
        }

        // Successfully throw OutageException after reenabling rethrow
        individualRequestConsumer.setRethrowOnFailureToConnect(true);
        try {
            this.individualRequestConsumer.processIndividualRequest (individualRequest, CONNECTION_TIME_OUT,
                                                                     RECEIVE_TIME_OUT);
            fail("Expecting an OutageException here.");
        } catch (final OutageException toe) {
            assertTrue(true, GOT_THE_EXCEPTION_AS_EXPECTED);
        }


        verify(mockTransformer, times(2)).transformDomainToJaxb(individualRequest);
        verify(mockClient, atLeast(2)).submitIndividual(individualRequestType);
    }

    /**
     * Test method for processing of individual request timeout error.
     */
    @Test
    void processIndividualRequestTimeout() {
        when(mockTransformer.transformDomainToJaxb(individualRequest)).thenReturn(individualRequestType);

        final WebServiceException wsException = new WebServiceException();
        wsException.initCause(new SocketTimeoutException("Timed out waiting for response"));

        when(mockClient.submitIndividual(individualRequestType)).thenThrow(wsException);

        doReturn(mockClient).when(individualRequestConsumer)
            .getClient(anyString(), anyString(), anyString(), anyLong(), anyLong());

        try {
            this.individualRequestConsumer.processIndividualRequest(individualRequest, CONNECTION_TIME_OUT,
                    RECEIVE_TIME_OUT);

            fail("Expecting a Timeout here.");
        } catch (final TimeoutException toe) {
            assertTrue(true, GOT_THE_EXCEPTION_AS_EXPECTED);
        }

        verify(mockTransformer).transformDomainToJaxb(individualRequest);
        verify(mockClient).submitIndividual(individualRequestType);

        assertTrue(true, TEST_FINISHED_SUCCESSFULLY);
    }

    /**
     * Test method for processing of individual request soap fault error.
     *
     * @throws SOAPException exception
     */
    @Test
    void processIndividualRequestSoapFault() throws SOAPException {
        when(mockTransformer.transformDomainToJaxb(individualRequest)).thenReturn(individualRequestType);

        final WebServiceException wsException = new WebServiceException();
        soapFault.setFaultCode("REQ_FAULT");
        soapFault.setFaultString("Invalid request");

        wsException.initCause(new SOAPFaultException(soapFault));

        when(mockClient.submitIndividual(individualRequestType)).thenThrow(wsException);
        doReturn(mockClient).when(individualRequestConsumer)
            .getClient(anyString(), anyString(), anyString(), anyLong(), anyLong());

        try {
            this.individualRequestConsumer.processIndividualRequest(individualRequest, CONNECTION_TIME_OUT,
                    RECEIVE_TIME_OUT);

            fail("Expecting an Soap Fault here.");
        } catch (final SoapFaultException toe) {
            assertTrue(true, GOT_THE_EXCEPTION_AS_EXPECTED);
        }

        verify(mockTransformer).transformDomainToJaxb(individualRequest);
        verify(mockClient).submitIndividual(individualRequestType);

        assertTrue(true, TEST_FINISHED_SUCCESSFULLY);
    }

    /**
     * @param domainObject the individual request domain object.
     * @return the Jaxb individual request type.
     */
    private IndividualRequestType createRequestType(final IIndividualRequest domainObject) {
        final IndividualRequestType requestType = new IndividualRequestType();
        final HeaderType headerType = new HeaderType();
        headerType.setRequestType("testRequestType");
        headerType.setSdtRequestId(domainObject.getSdtRequestReference());
        headerType.setTargetAppCustomerId("TestCust");

        requestType.setHeader(headerType);

        return requestType;
    }

    /**
     * @return the individual response type
     */
    private IndividualResponseType generateResponse() {
        final IndividualResponseType responseType = new IndividualResponseType();

        final uk.gov.moj.sdt.ws._2013.sdt.targetapp.indvresponseschema.HeaderType headerType =
                new uk.gov.moj.sdt.ws._2013.sdt.targetapp.indvresponseschema.HeaderType();
        headerType.setSdtRequestId("Test");

        responseType.setHeader(headerType);

        final CreateStatusType status = new CreateStatusType();
        status.setCode(CreateStatusCodeType.ACCEPTED);

        responseType.setStatus(status);

        return responseType;
    }

    /**
     * @return the individual response type
     */
    private IndividualResponseType generateErrorResponse() {
        final IndividualResponseType responseType = new IndividualResponseType();

        final uk.gov.moj.sdt.ws._2013.sdt.targetapp.indvresponseschema.HeaderType headerType =
            new uk.gov.moj.sdt.ws._2013.sdt.targetapp.indvresponseschema.HeaderType();
        headerType.setSdtRequestId("Test");

        responseType.setHeader(headerType);

        final CreateStatusType status = new CreateStatusType();
        status.setCode(CreateStatusCodeType.ACCEPTED);

        ErrorType errorType = new ErrorType();
        errorType.setCode("MOCK_CODE");
        errorType.setDescription("MOCK DESCRIPTION");
        status.setError(errorType);

        responseType.setStatus(status);

        return responseType;
    }

}
