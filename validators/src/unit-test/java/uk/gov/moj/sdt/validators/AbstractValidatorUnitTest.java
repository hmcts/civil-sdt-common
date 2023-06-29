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

import java.util.HashSet;
import java.util.Set;

import uk.gov.moj.sdt.domain.BulkCustomer;
import uk.gov.moj.sdt.domain.BulkCustomerApplication;
import uk.gov.moj.sdt.domain.TargetApplication;
import uk.gov.moj.sdt.domain.api.IBulkCustomer;
import uk.gov.moj.sdt.domain.api.IBulkCustomerApplication;
import uk.gov.moj.sdt.domain.api.ITargetApplication;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;

/**
 * Base class for unit testing validators.
 *
 * @author d276205
 */

public abstract class AbstractValidatorUnitTest extends AbstractSdtUnitTestBase {
    /**
     * create a bulk customer.
     *
     * @param applications the set of IBulkCustomerApplication objects
     * @return a bulk customer
     */
    protected IBulkCustomer createCustomer(final Set<IBulkCustomerApplication> applications) {
        final IBulkCustomer bulkCustomer = new BulkCustomer();
        bulkCustomer.setSdtCustomerId(12345L);
        bulkCustomer.setBulkCustomerApplications(applications);
        return bulkCustomer;
    }

    /**
     * create an application with a given name.
     *
     * @param targetApplicationCode the code for the application, MCOL etc
     * @return ITargetApplication
     */
    protected ITargetApplication createTargetApp(final String targetApplicationCode) {
        final ITargetApplication application = new TargetApplication();
        application.setTargetApplicationCode(targetApplicationCode);
        return application;
    }

    /**
     * the list of applications for a customer.
     *
     * @param applicationName the application name
     * @return the set of bulk customer applications for this customer
     */
    protected Set<IBulkCustomerApplication> createBulkCustomerApplications(final String applicationName) {
        final Set<IBulkCustomerApplication> bulkCustomerApplications = new HashSet<>();

        final IBulkCustomerApplication bulkCustomerApp = new BulkCustomerApplication();
        bulkCustomerApp.setCustomerApplicationId("appId");
        bulkCustomerApp.setTargetApplication(createTargetApp(applicationName));
        bulkCustomerApplications.add(bulkCustomerApp);
        return bulkCustomerApplications;
    }

}
