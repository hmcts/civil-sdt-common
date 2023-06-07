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
package uk.gov.moj.sdt.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.OutputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class SdtContextTest extends AbstractSdtUnitTestBase
{
    private static final String FIELD_CUSTOMER_IDAM_ID = "customerIdamId";

    private List<Runnable> synchronisationTasks;

    private SdtContext sdtContext;

    @Mock
    private Runnable runnableMock;

    @BeforeEach
    @Override
    public void setUp() {
        sdtContext = SdtContext.getContext();
    }

    @Test
    void addSynchronisationTaskTest() {

        //given
        boolean returnValue = false;

        //when
        returnValue = sdtContext.addSynchronisationTask(runnableMock);
        this.synchronisationTasks = null;
        //then
        assertTrue(returnValue);
    }

    @Test
    void addSynchronisationTasksCheckTest() {

        //given

        //when
        sdtContext.clearSynchronisationTasks();

        //then
        assertNull(this.synchronisationTasks);
    }

    @Test
    void originalOutputStreamTest() {
        //given
        OutputStream outputStream = Mockito.mock(OutputStream.class);

        //when
        sdtContext.setOriginalOutputStream(outputStream);

        //then
        assertNotNull(sdtContext.getOriginalOutputStream());
    }

    @Test
    void clearSynchronisationTasksTest() {
        //given

        //when
        sdtContext.clearSynchronisationTasks();

        //then
        assertNull(sdtContext.getSynchronisationTasks());
    }

    @Test
    void setCustomerIdamIdTest() {
        String testIdamId = "set_test_email@test.com";

        sdtContext.setCustomerIdamId(testIdamId);

        String idamId = (String) getAccessibleField(SdtContext.class, FIELD_CUSTOMER_IDAM_ID, String.class, sdtContext);
        assertEquals(testIdamId, idamId, "SdtContext customer IDAM id set to unexpected value");
    }

    @Test
    void getCustomerIdamIdTest() {
        String testIdamId = "get_test_email@test.com";

        setAccessibleField(SdtContext.class, FIELD_CUSTOMER_IDAM_ID, String.class, sdtContext, testIdamId);

        String idamId = sdtContext.getCustomerIdamId();
        assertEquals(testIdamId, idamId, "Unexpected value returned from SdtContext customer IDAM id");
    }
}
