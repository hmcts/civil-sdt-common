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
package uk.gov.moj.sdt.transformers;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.moj.sdt.domain.IndividualRequest;
import uk.gov.moj.sdt.domain.api.IErrorLog;
import uk.gov.moj.sdt.domain.api.IIndividualRequest;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.ErrorType;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.StatusCodeType;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.UpdateStatusCodeType;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.UpdateStatusType;
import uk.gov.moj.sdt.ws._2013.sdt.individualupdaterequestschema.HeaderType;
import uk.gov.moj.sdt.ws._2013.sdt.individualupdaterequestschema.UpdateRequestType;
import uk.gov.moj.sdt.ws._2013.sdt.individualupdateresponseschema.UpdateResponseType;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test case for the UpdateItemTransformer.
 *
 * @author Manoj Kulkarni
 */
class UpdateItemTransformerTest extends AbstractSdtUnitTestBase {
    /**
     * Logger object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateItemTransformerTest.class);

    /**
     * Update Item Transformer for the unit testing.
     */
    private UpdateItemTransformer updateItemTransformer;

    private static final String FOUND_CORRECT_REQUEST_STATUS = "Found correct request status";
    private static final String FOUND_CORRECT_SDT_REQUEST_ID = "Found correct sdt request id";
    private static final String SDT_REQUEST_ID = "MCOL-12200202-121212";

    /**
     * Set up variables for the test.
     */
    @Override
    public void setUpLocalTests() {
        updateItemTransformer = new UpdateItemTransformer();
    }

    /**
     * Test the transformation from jaxb (UpdateRequestType) to domain (IIndividualRequest) object with status
     * RESUBMIT_MESSAGE.
     */
    @Test
    void testTransformJaxbToDomainResubmitMessage() {
        // Set up the jaxb object to transform
        final UpdateRequestType updateRequestType = new UpdateRequestType();
        final HeaderType headerType = new HeaderType();
        headerType.setSdtRequestId(SDT_REQUEST_ID);
        updateRequestType.setHeader(headerType);

        final UpdateStatusType status = new UpdateStatusType();
        status.setCode(UpdateStatusCodeType.RESUBMIT_MESSAGE);
        updateRequestType.setStatus(status);

        final IIndividualRequest domainObject = updateItemTransformer.transformJaxbToDomain(updateRequestType);

        assertNotNull(domainObject);
        assertEquals(SDT_REQUEST_ID, domainObject.getSdtRequestReference(), FOUND_CORRECT_SDT_REQUEST_ID);
        assertEquals(UpdateStatusCodeType.RESUBMIT_MESSAGE.value(),
                domainObject.getRequestStatus(), FOUND_CORRECT_REQUEST_STATUS);
    }

    /**
     * Test the transformation from jaxb (UpdateRequestType) to domain (IIndividualRequest) object with status
     * ACCEPTED.
     */
    @Test
    void testTransformJaxbToDomainAccepted() {
        // Set up the jaxb object to transform
        final UpdateRequestType updateRequestType = new UpdateRequestType();
        final HeaderType headerType = new HeaderType();
        headerType.setSdtRequestId(SDT_REQUEST_ID);
        updateRequestType.setHeader(headerType);

        final UpdateStatusType status = new UpdateStatusType();
        status.setCode(UpdateStatusCodeType.ACCEPTED);
        updateRequestType.setStatus(status);

        final IIndividualRequest domainObject = updateItemTransformer.transformJaxbToDomain(updateRequestType);

        assertNotNull(domainObject);
        assertEquals(SDT_REQUEST_ID, domainObject.getSdtRequestReference(), FOUND_CORRECT_SDT_REQUEST_ID);
        assertEquals(UpdateStatusCodeType.ACCEPTED.value(), domainObject.getRequestStatus(),
                FOUND_CORRECT_REQUEST_STATUS);
    }

    /**
     * Test the transformation from jaxb (UpdateRequestType) to domain (IIndividualRequest) object with status
     * REJECTED.
     */
    @Test
    void testTransformJaxbToDomainRejected() {
        // Set up the jaxb object to transform
        final String errorText = "MCOL has Failed to process the request";
        final UpdateRequestType updateRequestType = new UpdateRequestType();
        final HeaderType headerType = new HeaderType();
        final ErrorType errorType = new ErrorType();
        headerType.setSdtRequestId(SDT_REQUEST_ID);
        updateRequestType.setHeader(headerType);

        final UpdateStatusType status = new UpdateStatusType();
        status.setCode(UpdateStatusCodeType.REJECTED);
        errorType.setCode("FAILURE");
        errorType.setDescription(errorText);
        status.setError(errorType);
        updateRequestType.setStatus(status);

        final IIndividualRequest domainObject = updateItemTransformer.transformJaxbToDomain(updateRequestType);

        final IErrorLog errorLog = domainObject.getErrorLog();

        assertNotNull(domainObject);
        assertEquals(SDT_REQUEST_ID, domainObject.getSdtRequestReference(), FOUND_CORRECT_SDT_REQUEST_ID);
        assertEquals(UpdateStatusCodeType.REJECTED.value(), domainObject.getRequestStatus(),
                FOUND_CORRECT_REQUEST_STATUS);
        assertEquals("FAILURE", errorLog.getErrorCode(), "Found correct error code");
        assertEquals(errorText, errorLog.getErrorText(), "Found correct error text");
    }

    /**
     * This method tests the transformation from the domain object to Jaxb.
     */
    @Test
    void testTransformDomainToJaxb() {
        final IIndividualRequest domainObject = new IndividualRequest();

        final UpdateResponseType updateResponseType = updateItemTransformer.transformDomainToJaxb(domainObject);

        assertEquals(StatusCodeType.OK, updateResponseType.getStatus().getCode(), "Found expected status code");
    }
}
