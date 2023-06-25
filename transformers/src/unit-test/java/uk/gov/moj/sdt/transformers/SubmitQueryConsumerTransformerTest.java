/* Copyrights and Licenses
 *
 * Copyright (c) 2010 by the Ministry of Justice. All rights reserved.
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
package uk.gov.moj.sdt.transformers;

import java.lang.reflect.Constructor;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import uk.gov.moj.sdt.domain.SubmitQueryRequest;
import uk.gov.moj.sdt.domain.api.*;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.ErrorType;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.StatusCodeType;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.StatusType;
import uk.gov.moj.sdt.ws._2013.sdt.targetapp.submitqueryrequestschema.SubmitQueryRequestType;
import uk.gov.moj.sdt.ws._2013.sdt.targetapp.submitqueryresponseschema.SubmitQueryResponseType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

/**
 * Unit tests for SubmitQueryConsumerTransformer.
 *
 * @author D274994
 */
public class SubmitQueryConsumerTransformerTest extends AbstractSdtUnitTestBase {
    /**
     * Placeholder for submit query consumer transformer.
     */
    private SubmitQueryConsumerTransformer transformer;

    /**
     * Set up variables for the test.
     */
    public void setUpLocalTests() {
        transformer = new SubmitQueryConsumerTransformer();
    }

    /**
     * Test the transformation from jaxb (SubmitQueryResponseType) to domain
     * (ISubmitQuery) object with status OK.
     */
    @Test
    public void testTransformJaxbToDomainOK() {
        // Set up the jaxb object to transform
        final SubmitQueryResponseType jaxb = new SubmitQueryResponseType();
        final StatusType statusType = new StatusType();
        statusType.setCode(StatusCodeType.OK);
        jaxb.setStatus(statusType);
        jaxb.setResultCount(BigInteger.ZERO);
        final ISubmitQueryRequest domain = new SubmitQueryRequest();

        transformer.transformJaxbToDomain(jaxb, domain);
        assertEquals(jaxb.getStatus().getCode().value(), domain.getStatus(), "Request Status is incorrect");
        assertEquals(jaxb.getResultCount().intValue(), domain.getResultCount(), "Incorrect result count");
    }

    /**
     * Test the transformation from jaxb (SubmitQueryResponseType) to domain
     * (ISubmitQuery) object with status Error.
     */
    @Test
    public void testTransformJaxbToDomainError() {
        // Set up the jaxb object to transform
        final SubmitQueryResponseType jaxb = new SubmitQueryResponseType();
        final StatusType statusType = new StatusType();
        statusType.setCode(StatusCodeType.ERROR);
        jaxb.setStatus(statusType);
        jaxb.setResultCount(BigInteger.valueOf(10));

        final ErrorType errorType = new ErrorType();
        errorType.setCode("ERROR");
        errorType.setDescription("MCOL has found an error in processing the request");
        statusType.setError(errorType);

        final ISubmitQueryRequest domain = new SubmitQueryRequest();

        transformer.transformJaxbToDomain(jaxb, domain);

        final IErrorLog errorLog = domain.getErrorLog();

        assertEquals(jaxb.getStatus().getCode().value(), domain.getStatus(),
                                "Request Status is incorrect");
        assertEquals("MCOL has found an error in processing the request", errorLog.getErrorText(),
                                "Error description is incorrect");
        assertEquals(jaxb.getResultCount().intValue(), domain.getResultCount(),
                                "Incorrect result count");
    }

    @Test
    public void testTransformDomainToJaxb() {
        final ISubmitQueryRequest domain = new SubmitQueryRequest();
        final ITargetApplication mockTargetApplication = Mockito.mock(ITargetApplication.class);
        final IBulkCustomerApplication mockBulkCustomerApplication = Mockito.mock(IBulkCustomerApplication.class);
        final IBulkCustomer mockBulkCustomer = Mockito.mock(IBulkCustomer.class);

        domain.setTargetApplication(mockTargetApplication);
        domain.setBulkCustomer(mockBulkCustomer);

        String targetAppCode = "MOCK_APP_CODE";
        String customerApplicationId = "MOCK_CUSTOMER_APP_ID";

        when(mockTargetApplication.getTargetApplicationCode()).thenReturn(targetAppCode);

        when(mockBulkCustomer.getBulkCustomerApplication(targetAppCode))
            .thenReturn(mockBulkCustomerApplication);

        when(mockBulkCustomerApplication.getCustomerApplicationId()).thenReturn(customerApplicationId);

        final SubmitQueryRequestType jaxb = transformer.transformDomainToJaxb(domain);

        assertEquals(customerApplicationId, jaxb.getHeader().getTargetAppCustomerId(), "targetAppCustomerId is incorrect");
        assertNull(jaxb.getTargetAppDetail().getAny(), "Jaxb targetAppDetail should be empty");
        }
}
