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
package uk.gov.moj.sdt.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.moj.sdt.dao.api.IBulkCustomerDao;
import uk.gov.moj.sdt.dao.config.DaoTestConfig;
import uk.gov.moj.sdt.domain.BulkCustomer;
import uk.gov.moj.sdt.domain.api.IBulkCustomer;
import uk.gov.moj.sdt.test.utils.AbstractIntegrationTest;
import uk.gov.moj.sdt.test.utils.TestConfig;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;


/**
 * Test {@link BulkCustomerDao} query methods.
 *
 * @author Robin Compston
 */
@ActiveProfiles("integ")
@SpringBootTest(classes = { TestConfig.class, DaoTestConfig.class})
@Sql(scripts = {"classpath:uk/gov/moj/sdt/dao/sql/BulkCustomerDaoTest.sql"})
@Transactional
class BulkCustomerDaoTest extends AbstractIntegrationTest {
    /**
     * Logger object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BulkCustomerDaoTest.class);
    CriteriaBuilder criteriaBuilder;
    CriteriaQuery<BulkCustomer> criteriaQuery;
    Root<BulkCustomer> root;
    @BeforeEach
    public void setup() {
        final IBulkCustomerDao bulkCustomersDao = this.applicationContext.getBean(IBulkCustomerDao.class);
        criteriaBuilder = bulkCustomersDao.getEntityManager().getCriteriaBuilder();
        criteriaQuery = criteriaBuilder.createQuery(BulkCustomer.class);
        root = criteriaQuery.from(BulkCustomer.class);
    }

    /**
     * Tests {@link uk.gov.moj.sdt.dao.GenericDao} fetch.
     */
    @Test
    void testGetBulkCustomerBySdtId() {
        final IBulkCustomerDao bulkCustomersDao = this.applicationContext.getBean(IBulkCustomerDao.class);

        final IBulkCustomer bulkCustomer = bulkCustomersDao.getBulkCustomerBySdtId(2);
        if (bulkCustomer != null) {
            LOGGER.debug("Retrieved bulk customer id [{}]", bulkCustomer.getId());
        } else {
            fail("Could not find bulk customer [" + 2 + "]");
        }
    }

    @Test
    void testFetchBulkCustomerId() {
        final IBulkCustomerDao bulkCustomersDao = this.applicationContext.getBean(IBulkCustomerDao.class);

        final IBulkCustomer bulkCustomer = bulkCustomersDao.fetch(IBulkCustomer.class, 10711);
        if (bulkCustomer != null) {
            LOGGER.debug("Retrieved bulk customer id [{}]", bulkCustomer.getId());
        } else {
            fail("Could not find bulk customer [" + 10711 + "]");
        }
    }

    /**
     * Tests the bulk insert.
     */
    @Test
    void testBulkInsert() {
        final IBulkCustomerDao bulkCustomersDao = this.applicationContext.getBean(IBulkCustomerDao.class);

        final List<BulkCustomer> bulkObjectList = new ArrayList<>();
        final BulkCustomer bulkCustomer = new BulkCustomer();
        bulkCustomer.setSdtCustomerId(456);

        final BulkCustomer bulkCustomer2 = new BulkCustomer();
        bulkCustomer2.setSdtCustomerId(457);

        bulkObjectList.add(bulkCustomer);
        bulkObjectList.add(bulkCustomer2);

        bulkCustomersDao.persistBulk(bulkObjectList);

        final List<BulkCustomer> savedBulkObjectList =
            bulkCustomersDao.queryAsList(BulkCustomer.class, () -> criteriaQuery.select(root).where(createCriteria(456L, 457L)));

        assertNotNull(savedBulkObjectList);
        assertEquals(2, savedBulkObjectList.size());
        for (IBulkCustomer savedBulkCustomer : savedBulkObjectList) {
            assertNotNull(savedBulkCustomer);
        }

    }

    /**
     * Tests the bulk update.
     */
    @Test
    void testBulkUpdate() {
        final IBulkCustomerDao bulkCustomersDao = this.applicationContext.getBean(IBulkCustomerDao.class);

        final List<IBulkCustomer> bulkObjectList = new ArrayList<>();
        final IBulkCustomer bulkCustomer = new BulkCustomer();
        bulkCustomer.setId(10711L);
        bulkCustomer.setSdtCustomerId(456);

        bulkObjectList.add(bulkCustomer);

        bulkCustomersDao.persistBulk(bulkObjectList);

        final List<BulkCustomer> savedBulkObjectList =
            bulkCustomersDao.queryAsList(BulkCustomer.class, () -> criteriaQuery.select(root).where(createCriteria(456L)));

        assertNotNull(savedBulkObjectList);
        assertEquals(1, savedBulkObjectList.size());
        for (IBulkCustomer savedBulkCustomer : savedBulkObjectList) {
            assertNotNull(savedBulkCustomer);
            assertEquals(456, savedBulkCustomer.getSdtCustomerId());
        }
    }

    private Predicate createCriteria(long... values) {
        List<Predicate> predicates = new ArrayList<>();
        for(long value : values) {
            predicates.add(criteriaBuilder.equal(root.get("sdtCustomerId"), value));
        }
        return criteriaBuilder.or(predicates.toArray(new Predicate[]{}));
    }
}
