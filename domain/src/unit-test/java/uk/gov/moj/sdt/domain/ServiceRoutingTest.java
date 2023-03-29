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
 * Unit tests for {@link ServiceRouting}.
 *
 * @author Ollie Smith
 */
package uk.gov.moj.sdt.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.moj.sdt.domain.api.IServiceRouting;
import uk.gov.moj.sdt.domain.api.IServiceType;
import uk.gov.moj.sdt.domain.api.ITargetApplication;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceRoutingTest extends AbstractSdtUnitTestBase{

    private IServiceRouting serviceRouting;

    @Mock
    ITargetApplication mockTargetApplication;

    @Mock
    IServiceType mockServiceType;

    @BeforeEach
    @Override
    public void setUp() {
        serviceRouting = new ServiceRouting();
        serviceRouting.setId(1L);
        serviceRouting.setServiceType(mockServiceType);
        serviceRouting.setTargetApplication(mockTargetApplication);
        serviceRouting.setWebServiceEndpoint("mcol");
        when(mockServiceType.toString()).thenReturn("MOCK_SERVICE_TYPE");
        when(mockTargetApplication.toString()).thenReturn("MOCK_TARGET_APP");
    }

    @Test
    @DisplayName("Test Service Routing toString")
    void testServiceRoutingToString() {
        String expectedWebServiceEndpoint = "mcol";
        Long expectedId = 1L;
        String expectedServiceTypeToString = "MOCK_SERVICE_TYPE";
        String expectedTargetApplicationToString = "MOCK_TARGET_APP";
        String actualToString = serviceRouting.toString();
        assertEquals(expectedWebServiceEndpoint, serviceRouting.getWebServiceEndpoint(),
                     "ServiceRouting WebServiceEndpoint is not equal");
        assertEquals(expectedId, serviceRouting.getId(),
                     "ServiceRouting Id is not equal");
        assertEquals(mockServiceType, serviceRouting.getServiceType(),
                     "ServiceRouting ServiceType is not equal");
        assertEquals(mockTargetApplication, serviceRouting.getTargetApplication(),
                     "ServiceRouting TargetApplication is not equal");
        assertTrue(actualToString.contains(expectedWebServiceEndpoint),"Should contain webServiceEndpoint");
        assertTrue(actualToString.contains(Long.toString(expectedId)),"Should contain Id");
        assertTrue(actualToString.contains(expectedServiceTypeToString),"Should contain serviceType");
        assertTrue(actualToString.contains(expectedTargetApplicationToString),"Should contain targetApplication");
    }
}
