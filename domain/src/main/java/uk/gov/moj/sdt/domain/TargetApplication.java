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
import uk.gov.moj.sdt.domain.api.IServiceType.ServiceTypeName;
import uk.gov.moj.sdt.domain.api.ITargetApplication;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Target Application that the Bulk Customer has been set up to submit messages to.
 *
 * @author d130680
 */
@Table(name = "TARGET_APPLICATIONS")
@Entity
public class TargetApplication extends AbstractDomainObject implements ITargetApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tar_app_seq")
    @Column(name ="TARGET_APPLICATION_ID")
    private long id;

    @Column(name ="VERSION_NUMBER")
    private int version;

    /**
     * A collection of all service types that can be used with this target application.
     */
    @OneToMany(mappedBy = "targetApplication", orphanRemoval = true, cascade = {CascadeType.ALL}, targetEntity = ServiceRouting.class)
    private Set<IServiceRouting> serviceRoutings = new HashSet<>();

    /**
     * Target application code.
     */
    @Column(name ="TARGET_APPLICATION_CODE")
    private String targetApplicationCode;

    /**
     * Target application name.
     */
    @Column(name ="TARGET_APPLICATION_NAME")
    private String targetApplicationName;

    @Override
    public String getTargetApplicationCode() {
        return targetApplicationCode;
    }

    @Override
    public void setTargetApplicationCode(final String targetApplicationCode) {
        this.targetApplicationCode = targetApplicationCode;
    }

    @Override
    public String getTargetApplicationName() {
        return targetApplicationName;
    }

    @Override
    public void setTargetApplicationName(final String targetApplicationName) {
        this.targetApplicationName = targetApplicationName;
    }

    @Override
    public Set<IServiceRouting> getServiceRoutings() {
        return serviceRoutings;
    }

    @Override
    public void setServiceRoutings(final Set<IServiceRouting> serviceRoutings) {
        this.serviceRoutings = serviceRoutings;
    }

    @Override
    public IServiceRouting getServiceRouting(final ServiceTypeName serviceTypeName) {
        final Iterator<IServiceRouting> iterator = serviceRoutings.iterator();
        while (iterator.hasNext()) {
            final IServiceRouting serviceRouting = iterator.next();
            if (serviceRouting.getServiceType().getName().toUpperCase().equals(serviceTypeName.name())) {
                return serviceRouting;
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(getHashId(this) + "[");
        sb.append(super.toString());
        sb.append(", serviceRoutings=").append(getHashId(this.getServiceRoutings()));
        sb.append(", targetApplicationCode=").append(this.getTargetApplicationCode());
        sb.append(", targetApplicationName=").append(this.getTargetApplicationName());
        sb.append("]");
        return sb.toString();
    }
}
