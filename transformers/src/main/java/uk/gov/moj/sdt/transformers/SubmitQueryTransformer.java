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

import java.math.BigInteger;

import org.springframework.stereotype.Component;
import uk.gov.moj.sdt.domain.BulkCustomer;
import uk.gov.moj.sdt.domain.SubmitQueryRequest;
import uk.gov.moj.sdt.domain.TargetApplication;
import uk.gov.moj.sdt.domain.api.ISubmitQueryRequest;
import uk.gov.moj.sdt.transformers.api.ITransformer;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.ErrorType;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.StatusCodeType;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.StatusType;
import uk.gov.moj.sdt.ws._2013.sdt.submitqueryrequestschema.CriteriaType;
import uk.gov.moj.sdt.ws._2013.sdt.submitqueryrequestschema.CriterionType;
import uk.gov.moj.sdt.ws._2013.sdt.submitqueryrequestschema.HeaderType;
import uk.gov.moj.sdt.ws._2013.sdt.submitqueryrequestschema.SubmitQueryRequestType;
import uk.gov.moj.sdt.ws._2013.sdt.submitqueryresponseschema.ResultsType;
import uk.gov.moj.sdt.ws._2013.sdt.submitqueryresponseschema.SubmitQueryResponseType;

/**
 * Maps bulk request JAXB object tree to domain object tree and vice versa.
 *
 * @author d130680
 */
@Component("SubmitQueryTransformer")
public final class SubmitQueryTransformer extends AbstractTransformer implements
        ITransformer<SubmitQueryRequestType, SubmitQueryResponseType, ISubmitQueryRequest, ISubmitQueryRequest> {

    @Override
    public ISubmitQueryRequest transformJaxbToDomain(final SubmitQueryRequestType submitQueryRequestType) {
        final ISubmitQueryRequest submitQueryRequest = new SubmitQueryRequest();
        final HeaderType headerType = submitQueryRequestType.getHeader();

        // Map customer
        submitQueryRequest.setBulkCustomer(mapToBulkCustomer(headerType));

        // Map the target application
        final TargetApplication targetApplication = new TargetApplication();
        targetApplication.setTargetApplicationCode(headerType.getTargetApplicationId());
        submitQueryRequest.setTargetApplication(targetApplication);

        // Get the criteria type
        submitQueryRequest.setCriteriaType(this.getCriterion(submitQueryRequestType.getCriteria()));

        // Get the query reference type
        submitQueryRequest.setQueryReference(headerType.getQueryReference());

        return submitQueryRequest;
    }

    @Override
    public SubmitQueryResponseType transformDomainToJaxb(final ISubmitQueryRequest submitQueryRequest) {
        final SubmitQueryResponseType submitQueryResponseType = new SubmitQueryResponseType();

        // Maps some values.
        submitQueryResponseType.setSdtCustomerId(submitQueryRequest.getBulkCustomer().getSdtCustomerId());
        submitQueryResponseType.setResultCount(BigInteger.valueOf(submitQueryRequest.getResultCount()));

        // Set dummy results so the tags we need are written.
        final ResultsType resultsType = new ResultsType();
        submitQueryResponseType.setResults(resultsType);

        submitQueryResponseType.setSdtService(AbstractTransformer.SDT_SERVICE);

        // Set the status
        final StatusType status = new StatusType();
        submitQueryResponseType.setStatus(status);
        status.setCode(StatusCodeType.OK);

        if (submitQueryRequest.hasError()) {
            status.setCode(StatusCodeType.ERROR);
            final ErrorType errorType = new ErrorType();
            errorType.setCode(submitQueryRequest.getErrorLog().getErrorCode());
            errorType.setDescription(submitQueryRequest.getErrorLog().getErrorText());
            status.setError(errorType);
        }

        return submitQueryResponseType;
    }

    /**
     * Maps the header to a Bulk Customer object.
     *
     * @param headerType header type
     * @return bulk customer
     */
    private BulkCustomer mapToBulkCustomer(final HeaderType headerType) {
        final BulkCustomer bulkCustomer = new BulkCustomer();

        bulkCustomer.setSdtCustomerId(headerType.getSdtCustomerId());
        bulkCustomer.setReadyForAlternateService(false);
        return bulkCustomer;

    }

    /**
     * This method will return the string criterion for the given criteria type.
     *
     * @param criteria the criteria type object contained in the SubmitQuery reqeuest type
     * @return the string criterion associated with the criteria type.
     */
    private String getCriterion(final CriteriaType criteria) {
        if (criteria != null) {
            final CriterionType criterion = criteria.getCriterion();
            return criterion.getCriteriaType();
        }

        return null;
    }

}
