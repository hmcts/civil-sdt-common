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

package uk.gov.moj.sdt.domain;

import uk.gov.moj.sdt.domain.api.IBulkCustomer;
import uk.gov.moj.sdt.domain.api.IBulkCustomerApplication;
import uk.gov.moj.sdt.domain.api.ITargetApplication;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * Bulk customer application.
 *
 * @author d130680
 */
@Table(name = "BULK_CUSTOMER_APPLICATIONS")
@Entity
public class BulkCustomerApplication extends AbstractDomainObject implements IBulkCustomerApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bulk_cust_app_seq")
    @SequenceGenerator(name = "bulk_cust_app_seq", sequenceName = "bulk_cust_app_seq", allocationSize = 1)
    @Column(name = "BULK_CUSTOMER_APPLICATIONS_ID")
    private long id;

    @Version
    @Column(name = "VERSION_NUMBER")
    private int version;

    /**
     * Bulk customer.
     */
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = BulkCustomer.class)
    @JoinColumn(name = "BULK_CUSTOMER_ID")
    private IBulkCustomer bulkCustomer;

    /**
     * Target Application that the Bulk Customer has been set up to submit messages to.
     */
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = TargetApplication.class)
    @JoinColumn(name = "TARGET_APPLICATION_ID")
    private ITargetApplication targetApplication;

    /**
     * Customer application id.
     */
    @Column(name = "CUSTOMER_APPLICATION_ID")
    private String customerApplicationId;

    @Override
    public IBulkCustomer getBulkCustomer() {
        return bulkCustomer;
    }

    @Override
    public void setBulkCustomer(final IBulkCustomer bulkCustomer) {
        this.bulkCustomer = bulkCustomer;
    }

    @Override
    public ITargetApplication getTargetApplication() {
        return targetApplication;
    }

    @Override
    public void setTargetApplication(final ITargetApplication targetApplication) {
        this.targetApplication = targetApplication;
    }

    @Override
    public String getCustomerApplicationId() {
        return customerApplicationId;
    }

    @Override
    public void setCustomerApplicationId(final String customerApplicationId) {
        this.customerApplicationId = customerApplicationId;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(getHashId(this) + "[");
        sb.append(super.toString());
        sb.append(", bulkCustomer=").append(this.getBulkCustomer());
        sb.append(", targetApplication=").append(this.getTargetApplication());
        sb.append(", customerApplicationId=").append(this.getCustomerApplicationId());
        sb.append("]");
        return sb.toString();
    }
}
