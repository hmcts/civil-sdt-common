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
 * $Id: $
 * $LastChangedRevision: $
 * $LastChangedDate: $
 * $LastChangedBy: $ */

package uk.gov.moj.sdt.services.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.moj.sdt.dao.api.IGenericDao;
import uk.gov.moj.sdt.services.utils.api.ISdtBulkReferenceGenerator;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class is the implementation of the ISdtBulkReferenceGenerator interface.
 *
 * @author Manoj Kulkarni
 */
@Transactional(propagation = Propagation.REQUIRED)
@Component("SdtBulkReferenceGenerator")
public class SdtBulkReferenceGenerator implements ISdtBulkReferenceGenerator {

    /**
     * DAO to retrieve error messages.
     */
    private IGenericDao genericDao;

    @Autowired
    public SdtBulkReferenceGenerator(@Qualifier("GlobalParametersDao") IGenericDao genericDao) {
        this.genericDao = genericDao;
    }

    @Override
    @Transactional
    public String getSdtBulkReference(final String targetApplication) {
        final int targetAppLength = 4;

        if (targetApplication == null || targetApplication.length() != targetAppLength) {
            throw new IllegalArgumentException("The target application length is expected to be 4 characters.");
        }

        final String refNumberFormat = "{0}-{1}-{2}";
        final SimpleDateFormat dateFormat = new SimpleDateFormat();
        dateFormat.applyLocalizedPattern("yyyyMMddHHmmss");

        final long bulkId = genericDao.getNextSequenceValue("SDT_REF_SEQ");

        final int totalArgs = 3;
        final Object[] args = new Object[totalArgs];
        args[0] = targetApplication.toUpperCase();
        args[1] = dateFormat.format(new Date(System.currentTimeMillis()));
        // Pad the bulk id to make it 9 chars
        args[2] = String.format("%09d", bulkId);

        // Fill in the reference according to the reference format.
        final String refNumber = MessageFormat.format(refNumberFormat, args[0], args[1], args[2]);

        return refNumber;

    }

    /**
     * Setter method for the generic Dao.
     *
     * @param genericDao The generic Dao
     */
    public void setGenericDao(final IGenericDao genericDao) {
        this.genericDao = genericDao;
    }

}
