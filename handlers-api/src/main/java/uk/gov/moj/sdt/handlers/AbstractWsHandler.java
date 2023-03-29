/* Copyrights and Licenses
 *
 * Copyright (c) 2012-2014 by the Ministry of Justice. All rights reserved.
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
 * $Id: AbstractWsHandler.java 16915 2013-08-23 16:12:22Z agarwals $
 * $LastChangedRevision: 16915 $
 * $LastChangedDate: 2013-08-23 17:12:22 +0100 (Fri, 23 Aug 2013) $
 * $LastChangedBy: agarwals $ */
package uk.gov.moj.sdt.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.moj.sdt.validators.exception.api.IBusinessException;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.AbstractResponseType;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.ErrorType;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.StatusCodeType;
import uk.gov.moj.sdt.ws._2013.sdt.baseschema.StatusType;

/**
 * Base class for web service handlers that provides common methods for workflow.
 *
 * @author d276205
 */
public abstract class AbstractWsHandler {
    /**
     * Logger object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractWsHandler.class);

    /**
     * Handle given business exception by transforming into error type and setting on given response type.
     *
     * @param businessException business exception
     * @param responseType      response to be modified.
     */
    // CHECKSTYLE:OFF
    protected void handleBusinessException(final IBusinessException businessException,
                                           final AbstractResponseType responseType)
    // CHECKSTYLE:ON
    {
        LOGGER.error("Business error - {}", businessException);

        final ErrorType errorType = new ErrorType();
        errorType.setCode(businessException.getErrorCode());
        errorType.setDescription(businessException.getErrorDescription());

        populateError(responseType.getStatus(), errorType);
    }

    /**
     * Populates error and status details in given status type object.
     *
     * @param statusType status type.
     * @param errorType  error.
     */
    private void populateError(final StatusType statusType, final ErrorType errorType) {
        statusType.setError(errorType);
        statusType.setCode(StatusCodeType.ERROR);
    }
}
