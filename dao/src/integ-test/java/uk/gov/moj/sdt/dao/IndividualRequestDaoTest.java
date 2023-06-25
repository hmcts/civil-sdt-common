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
import uk.gov.moj.sdt.dao.api.IBulkCustomerDao;
import uk.gov.moj.sdt.dao.api.IBulkSubmissionDao;
import uk.gov.moj.sdt.dao.api.IIndividualRequestDao;
import uk.gov.moj.sdt.dao.config.DaoTestConfig;
import uk.gov.moj.sdt.domain.BulkCustomer;
import uk.gov.moj.sdt.domain.BulkSubmission;
import uk.gov.moj.sdt.domain.IndividualRequest;
import uk.gov.moj.sdt.domain.api.IBulkCustomer;
import uk.gov.moj.sdt.domain.api.IBulkSubmission;
import uk.gov.moj.sdt.domain.api.IIndividualRequest;
import uk.gov.moj.sdt.test.utils.AbstractIntegrationTest;
import uk.gov.moj.sdt.test.utils.TestConfig;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for the Individual Request Dao.
 *
 * @author Son Loi
 */
@ActiveProfiles("integ")
@SpringBootTest(classes = { TestConfig.class, DaoTestConfig.class})
@Sql(scripts = {"classpath:uk/gov/moj/sdt/dao/sql/IndividualRequestDaoTest.sql"})
class IndividualRequestDaoTest extends AbstractIntegrationTest {
    /**
     * Logger object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(IndividualRequestDaoTest.class);

    /**
     * *Individual Request DAO.
     */
    private IIndividualRequestDao individualRequestDao;

    /**
     * Bulk Customer to use for the test.
     */
    private IBulkCustomer bulkCustomer;

    /**
     * Bulk Submission to use for the test.
     */
    private IBulkSubmission bulkSubmission;

    /**
     * Data retention period.
     */
    private int dataRetentionPeriod;

    /**
     * Setup the test.
     */
    @BeforeEach
    public void setUp() {
        individualRequestDao = (IIndividualRequestDao) this.applicationContext.getBean("IndividualRequestDao");
        IBulkSubmissionDao iBulkSubmissionDao = this.applicationContext.getBean(IBulkSubmissionDao.class);
        IBulkCustomerDao iBulkCustomerDao = this.applicationContext.getBean(IBulkCustomerDao.class);

        bulkSubmission = iBulkSubmissionDao.fetch(BulkSubmission.class, 10710);
        bulkCustomer = iBulkCustomerDao.fetch(BulkCustomer.class, 10711);
        dataRetentionPeriod = 90;
    }

    /**
     * Test the retrieval of an Individual Request by SDT Request Reference.
     */
    @Test
    void testGetRequestBySdtReference() {

        final String sdtRequestReference = "SDT_REQ_TEST_1";
        // There should already be a record in the DB loaded as part of the test
        final IIndividualRequest individualRequest = individualRequestDao.getRequestBySdtReference(sdtRequestReference);

        assertNotNull(individualRequest);
        assertEquals(sdtRequestReference, individualRequest.getSdtRequestReference());
    }

    /**
     * Test the upper limit of the data retention period, i.e. today
     */
    @Test
    void testGetIndividualRequestUpper() {
        final String customerRequestReference = "customer request reference 1";

        createIndividualRequest(customerRequestReference, LocalDateTime.now());

        final IIndividualRequest individualRequest =
                individualRequestDao.getIndividualRequest(bulkCustomer, customerRequestReference, dataRetentionPeriod);
        assertNotNull(individualRequest);
        assertEquals(individualRequest.getCustomerRequestReference(), customerRequestReference);
    }

    /**
     * Test the upper limit of the data retention period, i.e. 90 days ago.
     */
    @Test
    void testGetBulkSubmissionLower() {
        final String customerRequestReference = "customer request reference 2";
        // Set the created date to be 90 days ago

        createIndividualRequest(customerRequestReference, LocalDateTime.now().plusDays(dataRetentionPeriod * -1L));
        final IIndividualRequest individualRequest =
                individualRequestDao.getIndividualRequest(bulkCustomer, customerRequestReference, dataRetentionPeriod);

        assertNotNull(individualRequest);
        assertEquals(individualRequest.getCustomerRequestReference(), customerRequestReference);
    }

    /**
     * Test with an individual request past 90 days ago and use an old customer reference.
     */
    @Test
    void testGetIndividualRequestPastRetention() {
        final String customerRequestReference = "customer request reference 1";

        createIndividualRequest(customerRequestReference, LocalDateTime.now().plusDays((dataRetentionPeriod + 1) * -1L));
        IIndividualRequest individualRequest = individualRequestDao.getIndividualRequest(bulkCustomer, customerRequestReference, dataRetentionPeriod);
        assertNull(individualRequest);
    }

    /**
     * Test stale individual request updated 1 day ago.
     */
    @Test
    void testStaleIndividualRequestWithDeadLetter() {
        // Get requests that have been updated more than 12 hrs ago.
        final List<IIndividualRequest> individualRequests = individualRequestDao.getStaleIndividualRequests(720);

        assertNotNull(individualRequests);
        assertEquals(2, individualRequests.size(), "The individual requests size is not correct");
    }

    /**
     * Test get individual request not found.
     */
    @Test
    void testGetBulkSubmissionNotFound() {
        final IIndividualRequest individualRequest =
                individualRequestDao.getIndividualRequest(bulkCustomer, "NOT_FOUND", dataRetentionPeriod);

        assertNull(individualRequest);
    }

    /**
     * Test where pending individual requests are found.
     */
    @Test
    void getPendingIndividualRequestsFound() {
        final List<IIndividualRequest> individualRequests = individualRequestDao.getPendingIndividualRequests(3);

        assertNotNull(individualRequests);
        assertFalse(individualRequests.isEmpty(), "Pending individual requests not found");
        assertEquals(1, individualRequests.size(), "The pending requests size is not correct");
    }

    /**
     * Test where pending individual requests are not found.
     */
    @Test
    void getPendingIndividualRequestsNotFound() {
        final List<IIndividualRequest> individualRequests = individualRequestDao.getPendingIndividualRequests(4);

        assertNotNull(individualRequests);
        assertTrue(individualRequests.isEmpty(), "Pending individual requests found");
    }

    /**
     * Create a individual request.
     *
     * @param customerReference customer reference
     * @param date              created date
     */
    private void createIndividualRequest(final String customerReference, final LocalDateTime date) {
        final IIndividualRequest individualRequest = new IndividualRequest();
        individualRequest.setBulkSubmission(bulkSubmission);
        individualRequest.setCreatedDate(date);
        individualRequest.setCustomerRequestReference(customerReference);
        individualRequest.setLineNumber(1);
        individualRequest.setRequestStatus(IIndividualRequest.IndividualRequestStatus.ACCEPTED.getStatus());
        individualRequest.setSdtBulkReference("sdtBulkReference");
        individualRequest.setSdtRequestReference("sdtRequestReference");
        individualRequest.setForwardingAttempts(1);
        individualRequest.setTargetApplicationResponse("targetApplicationResponse".getBytes());
        individualRequest.setRequestPayload("requestPayload".getBytes());
        individualRequest.setRequestType("mcolClaim");

        individualRequestDao.persist(individualRequest);

        LOGGER.debug("Individual Request persisted successfully");
    }

}
