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

package uk.gov.moj.sdt.services;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.gov.moj.sdt.dao.api.IBulkSubmissionDao;
import uk.gov.moj.sdt.domain.api.IBulkCustomer;
import uk.gov.moj.sdt.domain.api.IBulkFeedbackRequest;
import uk.gov.moj.sdt.domain.api.IBulkSubmission;
import uk.gov.moj.sdt.domain.api.IGlobalParameter;
import uk.gov.moj.sdt.domain.api.IIndividualRequest;
import uk.gov.moj.sdt.domain.cache.api.ICacheable;
import uk.gov.moj.sdt.services.api.IBulkFeedbackService;
import uk.gov.moj.sdt.utils.SdtContext;

/**
 * Implementation class for bulk feedback service.
 *
 * @author d130680
 */
@Service("BulkFeedbackService")
public class BulkFeedbackService implements IBulkFeedbackService {

    /**
     * Bulk Submission DAO property for looking up the bulk submission object.
     */
    private IBulkSubmissionDao bulkSubmissionDao;
    /**
     * Global parameter cache to retrieve data retention period.
     */
    private ICacheable globalParametersCache;

    @Autowired
    public BulkFeedbackService(@Qualifier("BulkSubmissionDao") IBulkSubmissionDao bulkSubmissionDao,
                               @Qualifier("GlobalParametersCache") ICacheable globalParametersCache) {
        this.bulkSubmissionDao = bulkSubmissionDao;
        this.globalParametersCache = globalParametersCache;
    }

    @Override
    public IBulkSubmission getBulkFeedback(final IBulkFeedbackRequest bulkFeedbackRequest) {
        final IBulkCustomer bulkCustomer = bulkFeedbackRequest.getBulkCustomer();
        final String sdtBulkReference = bulkFeedbackRequest.getSdtBulkReference();
        final IBulkSubmission bulkSubmission;

        // Call DAO to fetch domain details
        bulkSubmission =
                bulkSubmissionDao.getBulkSubmissionBySdtRef(bulkCustomer, sdtBulkReference, getDataRetentionPeriod());

        // Map individual request domain object(s) to the response(s)
        final List<IIndividualRequest> individualRequests = bulkSubmission.getIndividualRequests();
        final Map<String, String> targetApplicationRespMap = new HashMap<>();

        for (IIndividualRequest individualRequest : individualRequests) {
            if (null != individualRequest.getTargetApplicationResponse()) {
                // As Individual Request is valid, place in Target Application Response Map
                targetApplicationRespMap.put(individualRequest.getCustomerRequestReference(),
                        new String(individualRequest.getTargetApplicationResponse(), StandardCharsets.UTF_8));
            }
        }

        // Set the target response map in threadlocal for the outbound interceptor to pick up
        SdtContext.getContext().setTargetApplicationRespMap(targetApplicationRespMap);

        return bulkSubmission;
    }

    /**
     * Get the data retention period from the global parameters cache.
     *
     * @return data retention period
     */
    private int getDataRetentionPeriod() {
        final IGlobalParameter globalParameter =
                globalParametersCache.getValue(IGlobalParameter.class,
                        IGlobalParameter.ParameterKey.DATA_RETENTION_PERIOD.name());
        return Integer.parseInt(globalParameter.getValue());
    }

    /**
     * @param bulkSubmissionDao IBulkSubmissionDao
     */
    public void setBulkSubmissionDao(final IBulkSubmissionDao bulkSubmissionDao) {
        this.bulkSubmissionDao = bulkSubmissionDao;
    }

    /**
     * Set the global parameter cache.
     *
     * @param globalParametersCache global parameter cache
     */
    public void setGlobalParametersCache(final ICacheable globalParametersCache) {
        this.globalParametersCache = globalParametersCache;
    }
}
