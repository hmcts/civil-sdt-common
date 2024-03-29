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
package uk.gov.moj.sdt.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.gov.moj.sdt.dao.api.IBulkCustomerDao;
import uk.gov.moj.sdt.dao.api.IIndividualRequestDao;
import uk.gov.moj.sdt.domain.ErrorLog;
import uk.gov.moj.sdt.domain.api.IBulkCustomer;
import uk.gov.moj.sdt.domain.api.IErrorLog;
import uk.gov.moj.sdt.domain.api.IErrorMessage;
import uk.gov.moj.sdt.domain.api.IIndividualRequest;
import uk.gov.moj.sdt.domain.cache.api.ICacheable;
import uk.gov.moj.sdt.utils.visitor.api.ITree;
import uk.gov.moj.sdt.validators.api.IIndividualRequestValidator;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link IIndividualRequestValidator}.
 *
 * @author Saurabh Agarwal
 */
@Component("IndividualRequestValidator")
public class IndividualRequestValidator extends AbstractSdtValidator implements IIndividualRequestValidator {
    /**
     * Individual request dao.
     */
    private IIndividualRequestDao individualRequestDao;

    @Autowired
    public IndividualRequestValidator(@Qualifier("BulkCustomerDao")
                                          IBulkCustomerDao bulkCustomerDao,
                                      @Qualifier("GlobalParametersCache")
                                          ICacheable globalParameterCache,
                                      @Qualifier("ErrorMessagesCache")
                                          ICacheable errorMessagesCache,
                                      @Qualifier("IndividualRequestDao")
                                          IIndividualRequestDao individualRequestDao) {
        super(bulkCustomerDao, globalParameterCache, errorMessagesCache);
        this.individualRequestDao = individualRequestDao;
    }

    @Override
    public void visit(final IIndividualRequest individualRequest, final ITree tree) {
        final IBulkCustomer bulkCustomer = individualRequest.getBulkSubmission().getBulkCustomer();

        // Validate user file reference is unique across data retention period for individual request
        final String customerRequestReference = individualRequest.getCustomerRequestReference();

        // Get the data retention period
        final int dataRetention = super.getDataRetentionPeriod();
        final IIndividualRequest invalidIndividualRequest =
                individualRequestDao.getIndividualRequest(bulkCustomer, customerRequestReference, dataRetention);

        if (invalidIndividualRequest != null) {
            // Set the error in the error log and continue rather than throw an exception
            final List<String> replacements = new ArrayList<>();
            replacements.add(customerRequestReference);
            final String description = getErrorMessage(replacements, IErrorMessage.ErrorCode.DUP_CUST_REQID);

            final IErrorLog errorLog = new ErrorLog(IErrorMessage.ErrorCode.DUP_CUST_REQID.name(), description);

            // Change the status to rejected
            individualRequest.markRequestAsRejected(errorLog);
        }
    }

    /**
     * Set individual request dao.
     *
     * @param individualRequestDao individual request dao
     */
    public void setIndividualRequestDao(final IIndividualRequestDao individualRequestDao) {
        this.individualRequestDao = individualRequestDao;
    }
}
