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

package uk.gov.moj.sdt.domain.api;

/**
 * Submit query request domain object.
 *
 * @author d130680
 */
public interface ISubmitQueryRequest extends IDomainObject {

    /**
     * Get the Bulk Customer.
     *
     * @return bulk customer
     */
    IBulkCustomer getBulkCustomer();

    /**
     * Set the Bulk Customer.
     *
     * @param bulkCustomer bulk customer
     */
    void setBulkCustomer(final IBulkCustomer bulkCustomer);

    /**
     * Get target application.
     *
     * @return target application
     */
    ITargetApplication getTargetApplication();

    /**
     * Set target application.
     *
     * @param targetApplication target application
     */
    void setTargetApplication(final ITargetApplication targetApplication);

    /**
     * get criteria type.
     *
     * @return the criteria type associated with the submit query request.
     */
    String getCriteriaType();

    /**
     * set criteria type.
     *
     * @param criteriaType the criteria type associated with the submit query request.
     */
    void setCriteriaType(final String criteriaType);

    /**
     * Get result count.
     *
     * @return result count
     */
    int getResultCount();

    /**
     * Set result count.
     *
     * @param resultCount result count
     */
    void setResultCount(final int resultCount);

    /**
     * Get status.
     *
     * @return status
     */
    String getStatus();

    /**
     * Set status.
     *
     * @param status status
     */
    void setStatus(final String status);

    /**
     * Get the target application response.
     *
     * @return the target application response
     */
    String getTargetApplicationResponse();

    /**
     * set target application response.
     *
     * @param targetAppResponse the target application response.
     */
    void setTargetApplicationResponse(final String targetAppResponse);

    /**
     * get query reference.
     *
     * @return the query reference.
     */
    String getQueryReference();

    /**
     * set query reference.
     *
     * @param queryReference the query reference.
     */
    void setQueryReference(final String queryReference);

    /**
     * Get error log.
     *
     * @return error message
     */
    IErrorLog getErrorLog();

    /**
     * Sets the error log.
     *
     * @param errorLog the error log
     */
    void setErrorLog(final IErrorLog errorLog);

    /**
     * Does request have error?.
     *
     * @return true if error else false
     */
    boolean hasError();

    /**
     * Mark request as rejected with error log.
     *
     * @param errorLog error log.
     */
    void reject(IErrorLog errorLog);

    /**
     * The status of the Query Request - one of "Ok" or "Error".
     */
    public enum Status {
        /**
         * Ok.
         */
        OK("Ok"),

        /**
         * Error.
         */
        ERROR("Error");

        /**
         * request status.
         */
        private String status;

        /**
         * Constructor.
         *
         * @param s status
         */
        private Status(final String s) {

            this.status = s;
        }

        /**
         * Get the request status.
         *
         * @return request status
         */
        public String getStatus() {
            return status;
        }
    }

}
