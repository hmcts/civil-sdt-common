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

/**
 * Unit tests for {@link TargetApplication}.
 *
 * @author Ollie Smith
 */
package uk.gov.moj.sdt.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import uk.gov.moj.sdt.domain.api.IServiceRouting;
import uk.gov.moj.sdt.domain.api.IServiceType;
import uk.gov.moj.sdt.domain.api.ITargetApplication;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;


class TargetApplicationTest extends AbstractSdtUnitTestBase{

    private ITargetApplication targetApplication;

    private IServiceRouting serviceRouting;

    private IServiceType mockServiceType;

    private IServiceType.ServiceTypeName mockServiceTypeName;

    private final Set<IServiceRouting> serviceRoutings = new HashSet<>();

    @BeforeEach
    @Override
    public void setUpLocalTests() {
        MockitoAnnotations.openMocks(this);
        targetApplication = new TargetApplication();
        targetApplication.setId(1L);
        targetApplication.setTargetApplicationName("mcol");

        mockServiceType = Mockito.mock(ServiceType.class);
        mockServiceTypeName = Mockito.mock(IServiceType.ServiceTypeName.class);

        serviceRouting = new ServiceRouting();
        serviceRouting.setServiceType(mockServiceType);
    }


    @DisplayName("Test Target Application")
    @Test
    void testTargetApplicationToString(){

        String expected = "mcol";
        String actual = targetApplication.toString();
        assertNotNull(targetApplication,"BulkCustomer Object should be populated");
        assertTrue(actual.contains(expected),"Should contain something");
    }

    @DisplayName("Test Get Service Routings")
    @Test
    void testGetServiceRoutings(){

        //given
         serviceRoutings.add(serviceRouting);
         targetApplication.setServiceRoutings(serviceRoutings);

         //when
        when(mockServiceTypeName.name()).thenReturn("SUBMIT_QUERY");
        when(mockServiceType.getName()).thenReturn("SUBMIT_QUERY");

        //then
        assertNull(targetApplication.getServiceRouting(IServiceType.ServiceTypeName.SUBMIT_INDIVIDUAL));
        assertEquals(targetApplication.getServiceRouting(IServiceType.ServiceTypeName.SUBMIT_QUERY),serviceRouting);
        assertNotEquals(targetApplication.getServiceRouting(IServiceType.ServiceTypeName.SUBMIT_INDIVIDUAL),serviceRouting);
    }
}
