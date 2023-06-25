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

import org.springframework.stereotype.Component;
import uk.gov.moj.sdt.domain.ErrorLog;
import uk.gov.moj.sdt.domain.api.IBulkCustomerApplication;
import uk.gov.moj.sdt.domain.api.IErrorLog;
import uk.gov.moj.sdt.domain.api.ISubmitQueryRequest;
import uk.gov.moj.sdt.domain.api.ITargetApplication;
import uk.gov.moj.sdt.transformers.api.IConsumerTransformer;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.ErrorType;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.StatusCodeType;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.StatusType;
import uk.gov.moj.sdt.ws._2013.sdt.targetapp.submitqueryrequestschema.HeaderType;
import uk.gov.moj.sdt.ws._2013.sdt.targetapp.submitqueryrequestschema.SubmitQueryRequestType;
import uk.gov.moj.sdt.ws._2013.sdt.targetapp.submitqueryresponseschema.SubmitQueryResponseType;

/**
 * Maps submit query request JAXB object tree to domain object tree and vice
 * versa.
 *
 * @author D274994
 */
// CHECKSTYLE:OFF
@Component("SubmitQueryConsumerTransformer")
public final class SubmitQueryConsumerTransformer extends AbstractTransformer implements
        IConsumerTransformer<SubmitQueryResponseType, SubmitQueryRequestType, ISubmitQueryRequest, ISubmitQueryRequest> {
    // CHECKSTYLE:ON

    /* (non-Javadoc)
     *
     * @see
     * uk.gov.moj.sdt.transformers.api.IConsumerTransformer#transformJaxbToDomain
     * (java.lang.Object, uk.gov.moj.sdt.domain.api.IDomainObject) */
    @Override
    public void transformJaxbToDomain(final SubmitQueryResponseType jaxbInstance,
                                      final ISubmitQueryRequest domainObject) {
        final StatusType status = jaxbInstance.getStatus();
        final StatusCodeType statusCode = status.getCode();
        domainObject.setResultCount(jaxbInstance.getResultCount().intValue());

        if (StatusCodeType.OK.equals(statusCode)) {
            domainObject.setStatus(StatusCodeType.OK.value());
        } else if (StatusCodeType.ERROR.equals(statusCode)) {
            final ErrorType errorType = status.getError();
            if (null != errorType) {
                final IErrorLog errorLog = new ErrorLog(errorType.getCode(), errorType.getDescription());
                domainObject.setStatus(StatusCodeType.ERROR.value());
                domainObject.reject(errorLog);
            }
        }
    }

    /* (non-Javadoc)
     *
     * @see
     * uk.gov.moj.sdt.transformers.api.IConsumerTransformer#transformDomainToJaxb
     * (uk.gov.moj.sdt.domain.api.IDomainObject) */
    @Override
    public SubmitQueryRequestType transformDomainToJaxb(final ISubmitQueryRequest domainObject) {
        final SubmitQueryRequestType jaxb = new SubmitQueryRequestType();
        final HeaderType header = new HeaderType();

        // Populate the header of the SubmitQueryRequestType.
        header.setCriteriaType(domainObject.getCriteriaType());

        final ITargetApplication targetApp = domainObject.getTargetApplication();
        final IBulkCustomerApplication bulkCustomerApplication =
                domainObject.getBulkCustomer().getBulkCustomerApplication(targetApp.getTargetApplicationCode());

        header.setTargetAppCustomerId(bulkCustomerApplication.getCustomerApplicationId());
        jaxb.setHeader(header);

        // Set empty target app detail so the tags needed for enrichment are written.
        jaxb.setTargetAppDetail(new SubmitQueryRequestType.TargetAppDetail());

        return jaxb;
    }
}
