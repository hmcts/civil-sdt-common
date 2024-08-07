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

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * Bulk Customer Information manually set up and maintained
 * for the SDT application for each registered Target Application.
 *
 * @author d130680
 */
@Table(name = "BULK_CUSTOMERS")
@Entity
public class BulkCustomer extends AbstractDomainObject implements IBulkCustomer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bulk_cust_seq")
    @SequenceGenerator(name = "bulk_cust_seq", sequenceName = "bulk_cust_seq", allocationSize = 1)
    @Column(name = "BULK_CUSTOMER_ID")
    private long id;

    @Version
    @Column(name = "VERSION_NUMBER")
    private int version;

    @Column(name = "READY_FOR_ALTERNATE_SERVICE")
    private Boolean readyForAlternateService;

    /**
     * The bulk customer applications that this customer can work with.e.g. 'MCOL'.
     */
    @OneToMany(mappedBy = "bulkCustomer", orphanRemoval = true, targetEntity = BulkCustomerApplication.class, cascade = {CascadeType.ALL})
    private Set<IBulkCustomerApplication> bulkCustomerApplications = new HashSet<>();

    /**
     * This is a manually allocated and maintained value.
     */
    @Column(name = "SDT_CUSTOMER_ID")
    private long sdtCustomerId;

    @Override
    public long getSdtCustomerId() {
        return sdtCustomerId;
    }

    @Override
    public void setSdtCustomerId(final long sdtCustomerId) {
        this.sdtCustomerId = sdtCustomerId;
    }

    @Override
    public boolean hasAccess(final String targetApplicationCode) {
        for (IBulkCustomerApplication bulkCustomerApplication : bulkCustomerApplications) {
            if (bulkCustomerApplication.getTargetApplication().getTargetApplicationCode().equalsIgnoreCase(
                    targetApplicationCode)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setBulkCustomerApplications(final Set<IBulkCustomerApplication> bulkCustomerApplications) {
        this.bulkCustomerApplications = bulkCustomerApplications;
    }

    @Override
    public Set<IBulkCustomerApplication> getBulkCustomerApplications() {
        return bulkCustomerApplications;
    }

    @Override
    public IBulkCustomerApplication getBulkCustomerApplication(final String targetApplicationCode) {
        for (IBulkCustomerApplication bulkCustomerApplication : bulkCustomerApplications) {
            if (bulkCustomerApplication.getTargetApplication().getTargetApplicationCode().equalsIgnoreCase(
                    targetApplicationCode)) {
                return bulkCustomerApplication;
            }
        }
        return null;
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

    public Boolean getReadyForAlternateService() {
        return readyForAlternateService;
    }

    public void setReadyForAlternateService(Boolean readyForAlternateService) {
        this.readyForAlternateService = readyForAlternateService;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(getHashId(this) + "[");
        sb.append(super.toString());
        sb.append(", sdtCustomerId=").append(this.getSdtCustomerId());
        sb.append("]");
        return sb.toString();
    }
}
