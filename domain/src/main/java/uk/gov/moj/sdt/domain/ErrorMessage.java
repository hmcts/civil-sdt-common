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

import uk.gov.moj.sdt.domain.api.IErrorMessage;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * Error message.
 *
 * @author d130680
 */
@Table(name = "ERROR_MESSAGES")
@Entity
public class ErrorMessage extends AbstractDomainObject implements IErrorMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "err_mesg_seq")
    @SequenceGenerator(name = "err_mesg_seq", sequenceName = "err_mesg_seq", allocationSize = 1)
    @Column(name = "ERROR_MESSAGE_ID")
    private long id;

    @Version
    @Column(name = "VERSION_NUMBER")
    private int version;

    /**
     * The error code.
     */
    @Column(name = "ERROR_CODE")
    private String errorCode;

    /**
     * The error text.
     */
    @Column(name = "ERROR_TEXT")
    private String errorText;

    /**
     * The error description.
     */
    @Column(name = "ERROR_DESCRIPTION")
    private String errorDescription;

    @Override
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public void setErrorCode(final String errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String getErrorText() {
        return errorText;
    }

    @Override
    public void setErrorText(final String errorText) {
        this.errorText = errorText;
    }

    @Override
    public String getErrorDescription() {
        return errorDescription;
    }

    @Override
    public void setErrorDescription(final String errorDescription) {
        this.errorDescription = errorDescription;
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
        sb.append(", errorCode=").append(this.getErrorCode());
        sb.append(", errorText=").append(this.getErrorText());
        sb.append(", errorDescription=").append(this.getErrorDescription());
        sb.append("]");
        return sb.toString();
    }
}
