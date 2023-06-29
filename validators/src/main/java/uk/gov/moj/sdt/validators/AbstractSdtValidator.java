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
package uk.gov.moj.sdt.validators;

import org.springframework.beans.factory.annotation.Qualifier;
import uk.gov.moj.sdt.dao.api.IBulkCustomerDao;
import uk.gov.moj.sdt.domain.api.IBulkCustomer;
import uk.gov.moj.sdt.domain.api.IErrorMessage;
import uk.gov.moj.sdt.domain.api.IGlobalParameter;
import uk.gov.moj.sdt.domain.cache.api.ICacheable;
import uk.gov.moj.sdt.domain.visitor.AbstractDomainObjectVisitor;
import uk.gov.moj.sdt.validators.exception.AbstractBusinessException;
import uk.gov.moj.sdt.validators.exception.CustomerNotFoundException;
import uk.gov.moj.sdt.validators.exception.CustomerNotSetupException;
import uk.gov.moj.sdt.validators.exception.CustomerReferenceNotUniqueException;
import uk.gov.moj.sdt.validators.exception.DuplicateUserRequestIdentifierException;
import uk.gov.moj.sdt.validators.exception.InvalidBulkReferenceException;
import uk.gov.moj.sdt.validators.exception.RequestCountMismatchException;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for validators providing common methods.
 *
 * @author d130680
 */
public abstract class AbstractSdtValidator extends AbstractDomainObjectVisitor {
    /**
     * Bulk customer dao.
     */
    private IBulkCustomerDao bulkCustomerDao;

    /**
     * Global parameter cache to retrieve data retention period.
     */
    private ICacheable globalParameterCache;

    /**
     * Error messages cache.
     */
    private ICacheable errorMessagesCache;

    protected AbstractSdtValidator(@Qualifier("BulkCustomerDao")
                                    IBulkCustomerDao bulkCustomerDao,
                                @Qualifier("GlobalParametersCache")
                                    ICacheable globalParameterCache,
                                @Qualifier("ErrorMessagesCache")
                                    ICacheable errorMessagesCache) {
        this.bulkCustomerDao = bulkCustomerDao;
        this.globalParameterCache = globalParameterCache;
        this.errorMessagesCache = errorMessagesCache;
    }

    /**
     * Check that the bulk customer exists has access to the target application.
     *
     * @param sdtCustomerId         bulk customer
     * @param targetApplicationCode target application
     */
    public void checkCustomerHasAccess(final long sdtCustomerId, final String targetApplicationCode) {
        final IBulkCustomer bulkCustomer = bulkCustomerDao.getBulkCustomerBySdtId(sdtCustomerId);

        if (bulkCustomer != null && !bulkCustomer.hasAccess(targetApplicationCode)) {
            List<String> replacements = null;
            replacements = new ArrayList<>();
            replacements.add(targetApplicationCode);
            replacements.add(getContactDetails());
            createValidationException(replacements, IErrorMessage.ErrorCode.CUST_NOT_SETUP);
        }
    }

    /**
     * Get the data retention period from the global parameters cache.
     *
     * @return data retention period
     */
    public int getDataRetentionPeriod() {
        final IGlobalParameter globalParameter =
                globalParameterCache.getValue(IGlobalParameter.class,
                        IGlobalParameter.ParameterKey.DATA_RETENTION_PERIOD.name());
        return Integer.parseInt(globalParameter.getValue());
    }

    /**
     * Get the contact Details from the global parameters cache.
     *
     * @return contact details
     */
    public String getContactDetails() {
        final IGlobalParameter globalParameter =
                globalParameterCache.getValue(IGlobalParameter.class,
                        IGlobalParameter.ParameterKey.CONTACT_DETAILS.name());
        return globalParameter.getValue();
    }

    /**
     * get the error message from the error message cache, putting the placeholders into the message.
     *
     * @param replacements a list of Strings containing the replacements to go into the error message.
     * @param errorCode    String, the error code of the exception to be thrown and the message to be reported.
     * @return error message
     */
    public String getErrorMessage(final List<String> replacements, final IErrorMessage.ErrorCode errorCode) {
        final String errorString = errorCode.toString();
        final IErrorMessage errorMessage = errorMessagesCache.getValue(IErrorMessage.class, errorString);
        return MessageFormat.format(errorMessage.getErrorText(), replacements.toArray());
    }

    /**
     * Create and throw a validation error exception according to the errorCode passed in.
     *
     * @param replacements a list of Strings containing the replacements to go into the error message.
     * @param errorCode    String, the error code of the exception to be thrown and the message to be reported.
     * @throws AbstractBusinessException super class of the exception to be thrown.
     */
    public void createValidationException(final List<String> replacements, final IErrorMessage.ErrorCode errorCode)
            throws AbstractBusinessException {
        final String errorCodeStr = errorCode.toString();
        final IErrorMessage errorMessage = errorMessagesCache.getValue(IErrorMessage.class, errorCodeStr);

        switch (errorCode) {
            case CUST_NOT_SETUP: {
                throw new CustomerNotSetupException(errorCodeStr, errorMessage.getErrorText(), replacements);
            }
            case CUST_ID_INVALID: {
                throw new CustomerNotFoundException(errorCodeStr, errorMessage.getErrorText(), replacements);
            }
            case DUP_CUST_FILEID: {
                // CHECKSTYLE:OFF
                throw new CustomerReferenceNotUniqueException(errorCodeStr, errorMessage.getErrorText(), replacements);
                // CHECKSTYLE:ON
            }
            case REQ_COUNT_MISMATCH: {
                throw new RequestCountMismatchException(errorCodeStr, errorMessage.getErrorText(), replacements);
            }
            case BULK_REF_INVALID: {
                throw new InvalidBulkReferenceException(errorCodeStr, errorMessage.getErrorText(), replacements);
            }
            case DUP_CUST_REQID: {
                throw new DuplicateUserRequestIdentifierException(errorCodeStr, errorMessage.getErrorText(),
                        replacements);
            }
            default:
                break;
        }

    }

    /**
     * Set bulk customer dao.
     *
     * @param bulkCustomerDao bulk customer dao
     */
    public void setBulkCustomerDao(final IBulkCustomerDao bulkCustomerDao) {
        this.bulkCustomerDao = bulkCustomerDao;
    }

    /**
     * Returns bulk customer dao.
     *
     * @return IBulkCustomerDao instance
     */
    public IBulkCustomerDao getBulkCustomerDao() {
        return bulkCustomerDao;
    }

    /**
     * Set the global parameter cache.
     *
     * @param globalParameterCache global parameter cache
     */
    public void setGlobalParameterCache(final ICacheable globalParameterCache) {
        this.globalParameterCache = globalParameterCache;
    }

    /**
     * Set the error messages cache.
     *
     * @param errorMessagesCache error messages cache
     */
    public void setErrorMessagesCache(final ICacheable errorMessagesCache) {
        this.errorMessagesCache = errorMessagesCache;
    }
}
