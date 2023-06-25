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
import uk.gov.moj.sdt.domain.BulkCustomer;
import uk.gov.moj.sdt.domain.ErrorLog;
import uk.gov.moj.sdt.domain.SubmitQueryRequest;
import uk.gov.moj.sdt.domain.api.IBulkCustomer;
import uk.gov.moj.sdt.domain.api.IErrorLog;
import uk.gov.moj.sdt.domain.api.ISubmitQueryRequest;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.StatusCodeType;
import uk.gov.moj.sdt.ws._2013.sdt.submitqueryrequestschema.CriteriaType;
import uk.gov.moj.sdt.ws._2013.sdt.submitqueryrequestschema.CriterionType;
import uk.gov.moj.sdt.ws._2013.sdt.submitqueryrequestschema.HeaderType;
import uk.gov.moj.sdt.ws._2013.sdt.submitqueryrequestschema.SubmitQueryRequestType;
import uk.gov.moj.sdt.ws._2013.sdt.submitqueryresponseschema.SubmitQueryResponseType;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for SubmitQueryTransformer.
 *
 * @author d130680
 */
public class SubmitQueryTransformerTest extends AbstractSdtUnitTestBase {
    /**
     * Submit query transformer.
     */
    private SubmitQueryTransformer transformer;

    /**
     * Set up variables for the test.
     */
    public void setUpLocalTests() {
        transformer = new SubmitQueryTransformer();
    }

    /**
     * Test the transformation from jaxb to domain object.
     */
    @Test
    public void testTransformJaxbToDomain() {

        // Set up the jaxb object to transform
        final SubmitQueryRequestType jaxb = new SubmitQueryRequestType();
        final long sdtCustomerId = 123L;
        final String targetApplicationId = "mcol";
        CriterionType criterionType = new CriterionType();
        criterionType.setCriteriaType("TEST CRITERIA TYPE");
        final CriteriaType criteriaType = new CriteriaType();
        criteriaType.setCriterion(criterionType);

        // Set the header
        final HeaderType header = new HeaderType();
        header.setSdtCustomerId(sdtCustomerId);
        header.setTargetApplicationId(targetApplicationId);
        jaxb.setHeader(header);
        jaxb.setCriteria(criteriaType);

        // Call the transformer
        final ISubmitQueryRequest domain = transformer.transformJaxbToDomain(jaxb);

        // Test the jaxb object has been transformed domain object
        assertEquals(sdtCustomerId, domain.getBulkCustomer().getSdtCustomerId(),
                                "SDT Customer ID does not match");
        assertEquals(targetApplicationId, domain.getTargetApplication().getTargetApplicationCode(),
                                "Target Application ID does not match");
        assertEquals(criterionType.getCriteriaType(), domain.getCriteriaType(),
                                "Criteria Type does not match");

    }

    /**
     * Test the from transformation domain to jaxb object.
     */
    @Test
    public void testTransformDomainToJaxb() {
        // Set up the domain object to transform
        final long sdtCustomerId = 123L;
        final int resultCount = 15;

        // Create the domain object
        final ISubmitQueryRequest domain = new SubmitQueryRequest();
        final IBulkCustomer bulkCustomer = new BulkCustomer();
        bulkCustomer.setSdtCustomerId(sdtCustomerId);
        final IErrorLog errorLog = new ErrorLog();
        errorLog.setErrorCode("ERROR_CODE");
        errorLog.setErrorText("ERROR_TEXT");

        domain.setBulkCustomer(bulkCustomer);
        domain.setResultCount(resultCount);

        domain.setErrorLog(errorLog);

        // Call the transformer
        final SubmitQueryResponseType jaxb = transformer.transformDomainToJaxb(domain);

        // Test the domain object has been transformed to a jaxb object
        assertEquals(sdtCustomerId, jaxb.getSdtCustomerId(), "SDT Customer ID does not match");
        assertEquals(resultCount, jaxb.getResultCount().longValue(), "Result count does not match");
        assertEquals(AbstractTransformer.SDT_SERVICE, jaxb.getSdtService(), "SDT Service does not match");
        assertEquals(StatusCodeType.ERROR, jaxb.getStatus().getCode());
        assertNotNull(jaxb.getResults(), "ResultsType should not be null");
    }
}
