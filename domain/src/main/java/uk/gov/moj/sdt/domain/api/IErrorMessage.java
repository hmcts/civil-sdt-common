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
 * Interface for all classes implementing {@link IErrorMessage}.
 *
 * @author Manoj Kulkarni
 */
public interface IErrorMessage extends IDomainObject {

    /**
     * Get error code.
     *
     * @return error code
     */
    String getErrorCode();

    /**
     * Set error code.
     *
     * @param errorCode error code
     */
    void setErrorCode(final String errorCode);

    /**
     * Get error Text.
     *
     * @return error text
     */
    String getErrorText();

    /**
     * Set error Text.
     *
     * @param errorText error text
     */
    void setErrorText(final String errorText);

    /**
     * Get error description.
     *
     * @return error description
     */
    String getErrorDescription();

    /**
     * Set error description.
     *
     * @param errorDescription error description
     */
    void setErrorDescription(final String errorDescription);

    /**
     * Error code enums.
     *
     * @author Son Loi
     */
    public enum ErrorCode {
        /**
         * Internal error.
         */
        SDT_INT_ERR,

        /**
         * Request count mismatch.
         */
        REQ_COUNT_MISMATCH,

        /**
         * Customer not setup for target application.
         */
        CUST_NOT_SETUP,

        /**
         * Duplicate user file reference.
         */
        DUP_CUST_FILEID,

        /**
         * Unique Request Identifier has been specified more than once within
         * the originating Bulk Request.
         */
        DUPLD_CUST_REQID,

        /**
         * Duplicate unique request identifier.
         */
        DUP_CUST_REQID,

        /**
         * The supplied SDT Bulk Reference is not listed against the Bulk
         * Customer's Bulk Submissions detail.
         */
        BULK_REF_INVALID,

        /**
         * Target system failed to respond to request and request timed out.
         */
        REQ_NOT_ACK,

        /**
         * The Bulk Customer organisation is recognised by the SDT Service, but
         * is not set up to send a Service Request message to the specified
         * Target Application.
         */
        CUST_ID_INVALID,

        /**
         * The submitted Bulk Request does not contain valid Individual
         * Requests.
         */
        NO_VALID_REQS,

        /**
         * Target system timed out to respond to submit query request.
         */
        TAR_APP_ERROR,

        /**
         * Target system maximum submit query threshold reached.
         */
        TAR_APP_BUSY,

        /**
         * Client data error.
         */
        CUST_XML_ERR
    }
}
