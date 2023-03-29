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

package uk.gov.moj.sdt.domain;

import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.proxy.HibernateProxy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import uk.gov.moj.sdt.domain.api.IBulkCustomer;
import uk.gov.moj.sdt.domain.api.IBulkCustomerApplication;
import uk.gov.moj.sdt.domain.api.ITargetApplication;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link BulkCustomer}.
 *
 * @author d276205
 */
class BulkCustomerTest extends AbstractSdtUnitTestBase {
    /**
     * Test subject.
     */
    private IBulkCustomer bulkCustomer;

    /**
     * Set up test data.
     */
    @Override
    @BeforeEach
    public void setUp() {
        bulkCustomer = new BulkCustomer();
        final Set<IBulkCustomerApplication> bulkCustomerApplications = new HashSet<>();

        final IBulkCustomerApplication bulkCustomerApplication = new BulkCustomerApplication();
        final ITargetApplication targetApplication = new TargetApplication();
        targetApplication.setTargetApplicationCode("YES");
        bulkCustomerApplication.setTargetApplication(targetApplication);
        bulkCustomerApplications.add(bulkCustomerApplication);

        bulkCustomer.setBulkCustomerApplications(bulkCustomerApplications);
        bulkCustomer.setId(1L);
    }

    /**
     * Tests that hasAccess() method works correctly.
     */
    @Test
    @DisplayName("Test Has Access")
    void testHasAccess() {
        assertTrue(bulkCustomer.hasAccess("YES"),"Customer should have access");
        assertFalse(bulkCustomer.hasAccess("NO"),"Customer should not have access");
    }

    @Test
    @DisplayName("Test Bulk Customer")
    void testIBulkCustomer(){
        assertNotNull(bulkCustomer,"BulkCustomer Object should be populated");
        assertEquals(1L, bulkCustomer.getId(), "Id should be populated");
        final String hashcode = Integer.toHexString(bulkCustomer.hashCode());
        assertTrue(bulkCustomer.toString().contains(hashcode),"Object toString should be populated");
    }

    @Test
    @DisplayName("Test Get Bulk Customer Applications")
    void testBulkCustomerApplications(){
        assertNotNull(bulkCustomer.getBulkCustomerApplications(),"Bulk Customer Applications should be populated");
        assertEquals(1, bulkCustomer.getBulkCustomerApplications().size(),
                     "Bulk Customer Applications should be populated");
    }

    @Test
    @DisplayName("Test Abstract Domain Object for Persistent Collection type")
    void testGetHashIdForPersistentCollection() {
        PersistentCollection mockPersistentCollection = Mockito.mock(PersistentCollection.class);
        assertEquals("PersistentCollection", new BulkCustomer().getHashId(mockPersistentCollection));
    }

    @Test
    @DisplayName("Test Abstract Domain Object for Hibernate Proxy type")
    void testGetHashIdForHibernateProxy() {
        HibernateProxy mockHibernateProxy = Mockito.mock(HibernateProxy.class);
        assertEquals("HibernateProxy", new BulkCustomer().getHashId(mockHibernateProxy));
    }

    @Test
    @DisplayName("Test Abstract Domain Object for other object type")
    void testGetHashIdForOtherObject() {
        Object obj = new Object();
        String expected = obj.getClass().getSimpleName() + "@" + Integer.toHexString(obj.hashCode());
        assertEquals(expected, new BulkCustomer().getHashId(obj));
    }

    @Test
    @DisplayName("Test Abstract Domain Object for a null object")
    void testGetHashIdForNull() {
        assertNull(new BulkCustomer().getHashId(null));
    }

    @Test
    @DisplayName("Test Get Bulk Customer Application")
    void testBulkCustomerApplication(){
        String expected ="YES";
        IBulkCustomerApplication actual = bulkCustomer.getBulkCustomerApplication(expected);
        assertNotNull(actual);
        assertNull(bulkCustomer.getBulkCustomerApplication("NO"));
    }

}
