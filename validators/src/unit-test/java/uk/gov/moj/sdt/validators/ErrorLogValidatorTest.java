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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.moj.sdt.dao.api.IBulkCustomerDao;
import uk.gov.moj.sdt.domain.ErrorLog;
import uk.gov.moj.sdt.domain.cache.api.ICacheable;

/**
 * Tests for {@link ErrorLogValidatorTest}.
 *
 * @author d120520
 */

@ExtendWith(MockitoExtension.class)
class ErrorLogValidatorTest extends AbstractValidatorUnitTest {
    /**
     * Test subject.
     */
    private ErrorLogValidator validator;

    @Mock
    IBulkCustomerDao bulkCustomerDao;
    @Mock
    ICacheable globalParameterCache;

    @Mock
    ICacheable errorMessagesCache;

    /**
     * Setup of the Validator and Domain class instance.
     */
    @BeforeEach
    public void setUp() {
        // subject of test
        validator = new ErrorLogValidator(bulkCustomerDao, globalParameterCache, errorMessagesCache);
    }

    /**
     * The purpose of this test is to test successful scenario.
     */
    @Test
    public void testSuccess() {
        validator.visit(new ErrorLog(), null);

        // Nothing to verify as validator implementation is empty.
    }
}
