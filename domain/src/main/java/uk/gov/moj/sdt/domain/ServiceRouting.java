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

import uk.gov.moj.sdt.domain.api.IServiceRouting;
import uk.gov.moj.sdt.domain.api.IServiceType;
import uk.gov.moj.sdt.domain.api.ITargetApplication;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * SDT will maintain routing information for target applications based on the Request Type Tag.
 *
 * @author d130680
 */
@Table(name = "SERVICE_ROUTINGS")
@Entity
public class ServiceRouting extends AbstractDomainObject implements IServiceRouting {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "srv_req_seq")
    @Column(name = "SERVICE_ROUTINGS_ID")
    private long id;

    @Column(name = "VERSION_NUMBER")
    private int version;

    /**
     * Target application.
     */
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = TargetApplication.class)
    @JoinColumn(name="TARGET_APPLICATION_ID")
    private ITargetApplication targetApplication;

    /**
     * Service type.
     */
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = ServiceType.class)
    @JoinColumn(name="SERVICE_TYPE_ID")
    private IServiceType serviceType;

    /**
     * Web service endpoint.
     */
    @Column(name = "WEB_SERVICE_ENDPOINT")
    private String webServiceEndpoint;

    @Override
    public ITargetApplication getTargetApplication() {
        return targetApplication;
    }

    @Override
    public void setTargetApplication(final ITargetApplication targetApplication) {
        this.targetApplication = targetApplication;
    }

    @Override
    public IServiceType getServiceType() {
        return serviceType;
    }

    @Override
    public void setServiceType(final IServiceType serviceType) {
        this.serviceType = serviceType;
    }

    @Override
    public String getWebServiceEndpoint() {
        return webServiceEndpoint;
    }

    @Override
    public void setWebServiceEndpoint(final String webServiceEndpoint) {
        this.webServiceEndpoint = webServiceEndpoint;
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
        sb.append(", targetApplication=").append(this.getTargetApplication());
        sb.append(", serviceType=").append(this.getServiceType());
        sb.append(", webServiceEndpoint=").append(this.getWebServiceEndpoint());
        sb.append("]");
        return sb.toString();
    }
}
