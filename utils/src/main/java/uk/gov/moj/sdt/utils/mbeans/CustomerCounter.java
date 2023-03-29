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

package uk.gov.moj.sdt.utils.mbeans;

import org.springframework.stereotype.Component;
import uk.gov.moj.sdt.utils.mbeans.api.ICustomerCounter;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to count unique users.
 *
 * @author Robin Compston
 */
@Component("CustomerCounter")
public final class CustomerCounter implements ICustomerCounter {
    /**
     * Map of unique bulk customers seen by system.
     */
    private Map<String, String> uniqueBulkCustomers = new HashMap<>();

    /**
     * Local store of unique bulk customer count.
     */
    private long uniqueBulkCustomerCount;

    @Override
    public synchronized void updateBulkCustomerCount(final String customer) {
        if (customer == null) {
            return;
        }

        // Have the SdtMetrics been reset?
        if (SdtMetricsMBean.getMetrics().getActiveBulkCustomers() < uniqueBulkCustomerCount) {
            // Clear map.
            uniqueBulkCustomers = new HashMap<>();
        }

        if (!uniqueBulkCustomers.containsKey(customer)) {
            // Add this customer to the map, since we have not seen them before.
            uniqueBulkCustomers.put(customer, customer);

            // Update metrics.
            SdtMetricsMBean.getMetrics().upActiveBulkCustomers();
        }

        uniqueBulkCustomerCount = SdtMetricsMBean.getMetrics().getActiveBulkCustomers();
    }
}
