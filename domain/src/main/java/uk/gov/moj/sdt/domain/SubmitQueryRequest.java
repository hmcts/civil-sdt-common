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

package uk.gov.moj.sdt.domain;

import uk.gov.moj.sdt.domain.api.IBulkCustomer;
import uk.gov.moj.sdt.domain.api.IErrorLog;
import uk.gov.moj.sdt.domain.api.ISubmitQueryRequest;
import uk.gov.moj.sdt.domain.api.ITargetApplication;

/**
 * Interface for submit query request domain object.
 *
 * @author d130680
 */
public class SubmitQueryRequest extends AbstractDomainObject implements ISubmitQueryRequest {

    /**
     * Bulk customer.
     */
    private IBulkCustomer bulkCustomer;

    /**
     * Target application to send the request to, e.g. mcol.
     */
    private ITargetApplication targetApplication;

    /**
     * The criteria type of the Submit Query Request.
     */
    private String criteriaType;

    /**
     * The total count of the results from the query.
     */
    private int resultCount;

    /**
     * Stores request status.
     */
    private String status;

    /**
     * Stores the error message.
     */
    private IErrorLog errorLog;

    /**
     * Response from the target application, used in commissioning app.
     */
    private String targetApplicationResponse;

    /**
     * Used in commissioning application to send various response.
     */
    private String queryReference;

    /**
     * Constructs an instance of {@link SubmitQueryRequest}.
     */
    public SubmitQueryRequest() {
        this.status = ISubmitQueryRequest.Status.OK.getStatus();
    }

    @Override
    public IBulkCustomer getBulkCustomer() {
        return bulkCustomer;
    }

    @Override
    public void setBulkCustomer(final IBulkCustomer bulkCustomer) {
        this.bulkCustomer = bulkCustomer;
    }

    @Override
    public ITargetApplication getTargetApplication() {
        return targetApplication;
    }

    @Override
    public void setTargetApplication(final ITargetApplication targetApplication) {
        this.targetApplication = targetApplication;
    }

    @Override
    public String getCriteriaType() {
        return this.criteriaType;
    }

    @Override
    public void setCriteriaType(final String criteriaType) {
        this.criteriaType = criteriaType;
    }

    @Override
    public int getResultCount() {
        return resultCount;
    }

    @Override
    public void setResultCount(final int resultCount) {
        this.resultCount = resultCount;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public void setStatus(final String status) {
        this.status = status;
    }

    @Override
    public IErrorLog getErrorLog() {
        return errorLog;
    }

    @Override
    public boolean hasError() {
        return errorLog != null;
    }

    @Override
    public void reject(final IErrorLog errorLog) {
        setErrorLog(errorLog);
    }

    @Override
    public void setErrorLog(final IErrorLog errorLog) {
        this.errorLog = errorLog;
        this.status = ISubmitQueryRequest.Status.ERROR.getStatus();
    }

    @Override
    public String getTargetApplicationResponse() {
        return targetApplicationResponse;
    }

    @Override
    public void setTargetApplicationResponse(final String targetApplicationResponse) {
        this.targetApplicationResponse = targetApplicationResponse;
    }

    @Override
    public String getQueryReference() {
        return this.queryReference;
    }

    @Override
    public void setQueryReference(final String queryReference) {
        this.queryReference = queryReference;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(getHashId(this) + "[");
        sb.append(super.toString());
        sb.append(", bulkCustomer=").append(this.getBulkCustomer());
        sb.append(", targetApplication=").append(this.getTargetApplication());
        sb.append(", criteriaType=").append(this.getCriteriaType());
        sb.append(", resultCount=").append(this.getResultCount());
        sb.append(", status=").append(this.getStatus());
        sb.append(", errorLog=").append(this.getErrorLog());
        sb.append(", criteriaType=").append(this.getCriteriaType());
        sb.append(", targetApplicationResponse=").append(this.getTargetApplicationResponse());
        sb.append(", queryReference=").append(this.getQueryReference());
        sb.append("]");
        return sb.toString();
    }
}
