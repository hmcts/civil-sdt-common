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

import uk.gov.moj.sdt.domain.api.IServiceType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * Each defined target application will have a set of associated Request Types.
 * E.g. Claim,, Judgment, Warrant, JudgmentWarrant and ClaimUpate.
 *
 * @author d130680
 */
@Table(name = "SERVICE_TYPES")
@Entity
public class ServiceType extends AbstractDomainObject implements IServiceType {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ser_typ_seq")
    @SequenceGenerator(name = "ser_typ_seq", sequenceName = "ser_typ_seq", allocationSize = 1)
    @Column(name = "SERVICE_TYPE_ID")
    private long id;

    @Version
    @Column(name = "VERSION_NUMBER")
    private int version;

    /**
     * Request type name.
     */
    @Column(name = "SERVICE_TYPE_NAME")
    private String name;

    /**
     * Request type status.¬
     */
    @Column(name = "SERVICE_TYPE_STATUS")
    private String status;

    /**
     * Request type description.
     */
    @Column(name = "SERVICE_TYPE_DESCRIPTION")
    private String description;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public void setStatus(final String status) {
        this.status = status;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(final String description) {
        this.description = description;
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
        sb.append(", name=").append(this.getName());
        sb.append(", status=").append(this.getStatus());
        sb.append(", webServiceEndpoint=").append(this.getDescription());
        sb.append("]");
        return sb.toString();
    }
}
