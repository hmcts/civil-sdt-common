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
package uk.gov.moj.sdt.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.moj.sdt.consumers.config.ConsumersTestConfig;
import uk.gov.moj.sdt.dao.config.DaoTestConfig;
import uk.gov.moj.sdt.domain.BulkCustomer;
import uk.gov.moj.sdt.domain.ServiceRouting;
import uk.gov.moj.sdt.domain.ServiceType;
import uk.gov.moj.sdt.domain.SubmitQueryRequest;
import uk.gov.moj.sdt.domain.TargetApplication;
import uk.gov.moj.sdt.domain.api.IBulkCustomer;
import uk.gov.moj.sdt.domain.api.IServiceRouting;
import uk.gov.moj.sdt.domain.api.IServiceType;
import uk.gov.moj.sdt.domain.api.ISubmitQueryRequest;
import uk.gov.moj.sdt.domain.api.ITargetApplication;
import uk.gov.moj.sdt.services.config.ServicesTestConfig;
import uk.gov.moj.sdt.test.utils.AbstractIntegrationTest;
import uk.gov.moj.sdt.test.utils.TestConfig;
import uk.gov.moj.sdt.utils.SdtContext;
import uk.gov.moj.sdt.utils.Utilities;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Implementation of the integration test for BulkSubmissionService.
 *
 * @author Manoj kulkarni
 */
@ActiveProfiles("integ")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {TestConfig.class, ServicesTestConfig.class, ConsumersTestConfig.class, DaoTestConfig.class})
@Sql(scripts = {"classpath:uk/gov/moj/sdt/services/sql/RefData.sql", "classpath:uk/gov/moj/sdt/services/sql/SubmitQueryServiceIntTest.sql"})
public class SubmitQueryServiceIntTest extends AbstractIntegrationTest {

    /**
     * Logger object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SubmitQueryServiceIntTest.class);

    /**
     * Test subject.
     */
    private SubmitQueryService submitQueryService;

    /**
     * Setup the test.
     */
    @BeforeEach
    public void setUp() {
        submitQueryService = (SubmitQueryService) this.applicationContext.getBean("SubmitQueryService");
    }

    /**
     * This method tests for persistence of a single submission.
     *
     * @throws IOException if there is any error reading from the test file.
     */
    @Test
    public void updateRequestSoapError() throws IOException {
        final String rawXml = Utilities.getRawXml("src/integ-test/resources/", "testSampleErrorRequest.xml");
        SdtContext.getContext().setRawInXml(rawXml);

        final ISubmitQueryRequest submitQuery = this.createSubmitQuery();

        // Set the service request id so it can be retrieved in the
        // saveBulkSubmission code
        // SdtContext.getContext ().setServiceRequestId (new Long (10800));

        final Method accessibleSubmitQueryService =
                this.makeMethodAccesible(SubmitQueryService.class, "updateRequestSoapError", ISubmitQueryRequest.class);

        // Call the bulk submission service
        try {
            accessibleSubmitQueryService.invoke(submitQueryService, submitQuery);
        } catch (final IllegalAccessException e) {
            LOGGER.debug(e.getMessage());
            assertTrue(false, "IllegalAccessException please debug test");
        } catch (final IllegalArgumentException e) {
            LOGGER.debug(e.getMessage());
            assertTrue(false, "IllegalArgumentException please debug test");
        } catch (final InvocationTargetException e) {
            LOGGER.debug(e.getMessage());
            assertTrue(false, "InvocationTargetException please debug test");
        }
        // submitQueryService.updateRequestSoapError(submitQuery);

        assertEquals(submitQuery.getErrorLog().getErrorText(),
                "A system error has occurred. Please contact tbc for assistance.");
    }

    /**
     * @return SubmitQueryRequest object for the testing.
     */
    private ISubmitQueryRequest createSubmitQuery() {
        final ISubmitQueryRequest submitQuery = new SubmitQueryRequest();
        final IBulkCustomer bulkCustomer = new BulkCustomer();
        final ITargetApplication targetApp = new TargetApplication();

        targetApp.setId(1L);
        targetApp.setTargetApplicationCode("MCOL");
        targetApp.setTargetApplicationName("MCOL");
        final Set<IServiceRouting> serviceRoutings = new HashSet<IServiceRouting>();

        final IServiceRouting serviceRouting = new ServiceRouting();
        serviceRouting.setId(1L);
        serviceRouting.setWebServiceEndpoint("MCOL_END_POINT");

        final IServiceType serviceType = new ServiceType();
        serviceType.setId(1L);
        serviceType.setName("RequestTest1");
        serviceType.setDescription("RequestTestDesc1");
        serviceType.setStatus("RequestTestStatus");

        serviceRouting.setServiceType(serviceType);

        serviceRoutings.add(serviceRouting);

        targetApp.setServiceRoutings(serviceRoutings);

        submitQuery.setTargetApplication(targetApp);

        bulkCustomer.setSdtCustomerId(2L);

        submitQuery.setBulkCustomer(bulkCustomer);

        return submitQuery;
    }
}
