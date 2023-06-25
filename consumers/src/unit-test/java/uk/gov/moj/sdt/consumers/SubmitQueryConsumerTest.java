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
 * $Id$
 * $LastChangedRevision$
 * $LastChangedDate$
 * $LastChangedBy$ */

package uk.gov.moj.sdt.consumers;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import uk.gov.moj.sdt.consumers.exception.OutageException;
import uk.gov.moj.sdt.consumers.exception.SoapFaultException;
import uk.gov.moj.sdt.consumers.exception.TimeoutException;
import uk.gov.moj.sdt.domain.api.IServiceRouting;
import uk.gov.moj.sdt.domain.api.ISubmitQueryRequest;
import uk.gov.moj.sdt.domain.api.ITargetApplication;
import uk.gov.moj.sdt.transformers.api.IConsumerTransformer;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.StatusCodeType;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.StatusType;
import uk.gov.moj.sdt.ws._2013.sdt.targetapp.submitqueryrequestschema.SubmitQueryRequestType;
import uk.gov.moj.sdt.ws._2013.sdt.targetapp.submitqueryresponseschema.SubmitQueryResponseType;
import uk.gov.moj.sdt.ws._2013.sdt.targetappinternalendpoint.ITargetAppInternalEndpointPortType;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;
import java.math.BigInteger;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

/**
 * Test class for the submit query consumer.
 *
 * @author Amit Nigam
 */
@ExtendWith(MockitoExtension.class)
class SubmitQueryConsumerTest extends ConsumerTestBase {

    /**
     * Consumer transformer for submit query.
     */
    @Mock
    IConsumerTransformer<SubmitQueryResponseType, SubmitQueryRequestType, ISubmitQueryRequest, ISubmitQueryRequest>
            mockTransformer;

    /**
     * Mock Client instance.
     */
    @Mock
    ITargetAppInternalEndpointPortType mockClient;

    @Mock
    SOAPFault soapFault;

    SubmitQueryConsumer submitQueryConsumer;

    /**
     * Method to do any pre-test set-up.
     */
    @BeforeEach
    @Override
    public void setUpLocalTests() {
        SubmitQueryConsumer consumer = new SubmitQueryConsumer(mockTransformer);
        consumer.setTransformer(mockTransformer);
        submitQueryConsumer = spy(consumer);

        submitQueryRequest = this.createSubmitQueryRequest();
        submitQueryRequestType = this.createRequestType(submitQueryRequest);
    }

    @Test
    void testGetClient() {
        final String targetApplicationCode = "";
        final String serviceType = "";
        final String webServiceEndPoint = "";
        final long connectionTimeOut = 0L;
        final long receiveTimeOut = 0L;
        doReturn(mockClient).when(submitQueryConsumer).getClient(anyString(), anyString(), anyString(), anyLong(), anyLong());
        ITargetAppInternalEndpointPortType portType = submitQueryConsumer.getClient(targetApplicationCode,
                serviceType, webServiceEndPoint, connectionTimeOut, receiveTimeOut);
        assertNotNull(portType);
    }

    @Test
    void testGetClientImpl() {
        final String targetApplicationCode = "APP_CODE";
        final String serviceType = "SERVICE_TYPE";
        final String webServiceEndPoint = "WEB_SERVICE_ENDPOINT";
        final long connectionTimeOut = 30L;
        final long receiveTimeOut = 20L;
        ITargetAppInternalEndpointPortType portType;

        try(MockedStatic<ClientProxy> mockClientProxy = mockStatic(ClientProxy.class)) {
            Client mockClient = mock(Client.class);
            ITargetAppInternalEndpointPortType mockEndpointPortType
                = mock(ITargetAppInternalEndpointPortType.class,
                       withSettings().extraInterfaces(BindingProvider.class));
            HTTPConduit mockHTTPConduit = mock(HTTPConduit.class);

            mockClientProxy.when(() -> ClientProxy.getClient(any()))
                .thenReturn(mockClient);
            when(submitQueryConsumer.createTargetAppEndPoint())
                .thenReturn(mockEndpointPortType);
            when(mockClient.getConduit())
                .thenReturn(mockHTTPConduit);

            portType = submitQueryConsumer.getClient(targetApplicationCode, serviceType, webServiceEndPoint, connectionTimeOut, receiveTimeOut);

            mockClientProxy.verify(() -> ClientProxy.getClient(any()));
            verify(submitQueryConsumer).createTargetAppEndPoint();
            verify((BindingProvider) mockEndpointPortType, times(2)).getRequestContext();
            verify(mockClient).getConduit();
            verify(mockHTTPConduit).setClient(any(HTTPClientPolicy.class));

            // coverage for this.clientCache.containsKey()
            submitQueryConsumer
                .getClient(targetApplicationCode, serviceType, webServiceEndPoint, connectionTimeOut, receiveTimeOut);
            verify(submitQueryConsumer).createTargetAppEndPoint();
        }

        assertNotNull(portType);

    }

    /**
     * Test to verify submit query consumer does throw expected exception.
     */
    @Test
    void testSubmitQueryRequestTimeout() {
        when(mockTransformer.transformDomainToJaxb(submitQueryRequest)).thenReturn(submitQueryRequestType);

        final WebServiceException wsException = new WebServiceException();
        wsException.initCause(new SocketTimeoutException("Timed out waiting for response"));

        when(mockClient.submitQuery(submitQueryRequestType)).thenThrow(wsException);

        doReturn(mockClient).when(submitQueryConsumer).getClient(anyString(), anyString(), anyString(), anyLong(), anyLong());

        TimeoutException timeoutException = assertThrows(TimeoutException.class, () ->
                this.submitQueryConsumer.processSubmitQuery(submitQueryRequest, CONNECTION_TIME_OUT, RECEIVE_TIME_OUT));

        assertEquals("TIMEOUT_ERROR", timeoutException.getErrorCode());
        assertEquals("Read time out error sending [null]", timeoutException.getErrorDescription());
        assertNull(timeoutException.getCause());
    }

    /**
     * Test to verify submit query consumer does throw expected exception.
     *
     * @throws SOAPException exception
     */
    @Test
    void testSubmitQueryRequestSoapFault() throws SOAPException {
        when(mockTransformer.transformDomainToJaxb(submitQueryRequest)).thenReturn(submitQueryRequestType);

        final WebServiceException wsException = new WebServiceException();
        soapFault.setFaultCode("REQ_FAULT");
        soapFault.setFaultString("Invalid request");

        wsException.initCause(new SOAPFaultException(soapFault));

        when(mockClient.submitQuery(submitQueryRequestType)).thenThrow(wsException);
        doReturn(mockClient).when(submitQueryConsumer).getClient(anyString(), anyString(), anyString(), anyLong(), anyLong());


        SoapFaultException soapFaultException = assertThrows(SoapFaultException.class, () ->
            this.submitQueryConsumer.processSubmitQuery(submitQueryRequest, CONNECTION_TIME_OUT, RECEIVE_TIME_OUT));

        assertEquals("SOAP_FAULT", soapFaultException.getErrorCode());
        assertNull(soapFaultException.getErrorDescription());
        assertNull(soapFaultException.getCause());
    }

    /**
     * Test to verify submit query consumer does throw expected exception.
     */
    @Test
    void testSubmitQueryRequestOutage() {
        when(mockTransformer.transformDomainToJaxb(submitQueryRequest)).thenReturn(submitQueryRequestType);

        final WebServiceException wsException = new WebServiceException();
        wsException.initCause(new ConnectException());

        when(mockClient.submitQuery(submitQueryRequestType)).thenThrow(wsException);
        doReturn(mockClient).when(submitQueryConsumer).getClient(anyString(), anyString(), anyString(), anyLong(), anyLong());

        OutageException outageException = assertThrows(OutageException.class, () ->
            this.submitQueryConsumer.processSubmitQuery(submitQueryRequest, CONNECTION_TIME_OUT, RECEIVE_TIME_OUT));

        assertEquals("OUTAGE_ERROR", outageException.getErrorCode());
        assertNull(outageException.getErrorDescription());
    }

    /**
     * Test method for successful processing of submit query.
     */
    @Test
    void testSubmitQuerySuccess() {
        final SubmitQueryResponseType submitQueryResponseType = new SubmitQueryResponseType();

        when(mockTransformer.transformDomainToJaxb(submitQueryRequest)).thenReturn(submitQueryRequestType);
        when(mockClient.submitQuery(submitQueryRequestType)).thenReturn(submitQueryResponseType);
        mockTransformer.transformJaxbToDomain(submitQueryResponseType, submitQueryRequest);

        doAnswer ((Answer<Void>) invocation -> {
            ((SubmitQueryResponseType) invocation.getArgument(0)).setResultCount (BigInteger.valueOf(1));
            final StatusType statusType = new StatusType ();
            statusType.setCode (StatusCodeType.OK);
            ((SubmitQueryResponseType) invocation.getArgument(0)).setStatus (statusType);
            // required to be null for a void method
            return null;
        }).when(mockTransformer).transformJaxbToDomain(submitQueryResponseType, submitQueryRequest);

        doReturn(mockClient).when(submitQueryConsumer).getClient(anyString(), anyString(), anyString(), anyLong(), anyLong());

        this.submitQueryConsumer.processSubmitQuery(submitQueryRequest, CONNECTION_TIME_OUT, RECEIVE_TIME_OUT);

        verify(mockTransformer).transformDomainToJaxb(any());
        verify(mockClient).submitQuery(any());

        assertEquals(submitQueryResponseType.getStatus().getCode().value(),
                submitQueryRequest.getStatus(), "Status code is not equal.");
        assertEquals(submitQueryResponseType.getResultCount().intValue(),
                submitQueryRequest.getResultCount(), "Result count is not equal.");
    }

    /**
     * Test to verify transformer correctly set in setUpLocalTests().
     */
    @Test
    void getTransformer() {
        assertNotNull(submitQueryConsumer.getTransformer());
    }

    /**
     * Test to verify submitQueryConsumer.createTargetAppEndPoint() returns null as intended.
     */
    @Test
    void createTargetAppEndPoint() {
        ITargetAppInternalEndpointPortType targetAppInternalEndpointPortType = submitQueryConsumer.createTargetAppEndPoint();
        assertNull(targetAppInternalEndpointPortType);
    }

    /**
     * Test to verify submit query consumer does throw expected exception.
     * Handles the final else statement of AbstractWsConsumer.handleClientErrors
     */
    @Test
    void testSubmitQueryRequestOtherException() {
        when(mockTransformer.transformDomainToJaxb(submitQueryRequest)).thenReturn(submitQueryRequestType);

        final WebServiceException wsException = new WebServiceException();
        wsException.initCause(new OutageException("MISC_OUTAGE", "Misc outage exception"));

        when(mockClient.submitQuery(submitQueryRequestType)).thenThrow(wsException);

        doReturn(mockClient).when(submitQueryConsumer).getClient(anyString(), anyString(), anyString(), anyLong(), anyLong());

        WebServiceException webServiceException = assertThrows(WebServiceException.class, () ->
            this.submitQueryConsumer.processSubmitQuery(submitQueryRequest, CONNECTION_TIME_OUT, RECEIVE_TIME_OUT));

        assertEquals("The following exception occured [MISC_OUTAGE] message[Misc outage exception]", webServiceException.getCause().getMessage());
        assertNotNull(webServiceException.getCause());
    }
}
