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

import org.hibernate.annotations.Type;
import uk.gov.moj.sdt.domain.api.IServiceRequest;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;
import java.time.LocalDateTime;

/**
 * Audit log for incoming and outgoing request.
 *
 * @author d195274
 */
@Table(name = "SERVICE_REQUESTS")
@Entity
public class ServiceRequest extends AbstractDomainObject implements IServiceRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "srv_req_seq")
    @SequenceGenerator(name = "srv_req_seq", sequenceName = "srv_req_seq", allocationSize = 1)
    @Column(name = "SERVICE_REQUEST_ID")
    private long id;

    @Version
    @Column(name = "VERSION_NUMBER")
    private int version;

    /**
     * The time the request is received.
     */
    @Column(name = "REQUEST_TIMESTAMP")
    private LocalDateTime requestDateTime;

    /**
     * The time the response is sent.
     */
    @Column(name = "RESPONSE_TIMESTAMP")
    private LocalDateTime responseDateTime;

    /**
     * The id of the bulk customer. Initially generated by SDT but included in each request by the client.
     */
    @Column(name = "SDT_CUSTOMER_ID")
    private String bulkCustomerId;

    /**
     * The request type. Must be accepted by SDT.
     */
    @Column(name = "REQUEST_TYPE")
    private String requestType;

    /**
     * The incoming message.
     */
    @Column(name = "REQUEST_PAYLOAD")
    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    private byte[] requestPayload;

    /**
     * The outgoing message.
     */
    @Column(name = "RESPONSE_PAYLOAD")
    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    private byte[] responsePayload;

    /**
     * The SDT generated bulk reference.
     */
    @Column(name = "SDT_BULK_REFERENCE")
    private String bulkReference;

    /**
     * The server host name coming from the SDT gateway.
     */
    @Column(name = "SERVER_HOST_NAME")
    private String serverHostName;

    /**
     * set request  date time.
     *
     * @param requestDateTime the time the request is received.
     * @see uk.gov.moj.sdt.domain.api.IServiceRequest#setRequestDateTime(java.time.LocalDateTime)
     */
    @Override
    public void setRequestDateTime(final LocalDateTime requestDateTime) {
        this.requestDateTime = requestDateTime;
    }

    /**
     * set response date time.
     *
     * @param responseDateTime the time the response is sent.
     * @see uk.gov.moj.sdt.domain.api.IServiceRequest#setResponseDateTime(java.time.LocalDateTime)
     */
    @Override
    public void setResponseDateTime(final LocalDateTime responseDateTime) {
        this.responseDateTime = responseDateTime;
    }

    /**
     * set bulk customer.
     *
     * @param bulkCustomerId The id of the bulk customer.
     */
    @Override
    public void setBulkCustomerId(final String bulkCustomerId) {
        this.bulkCustomerId = bulkCustomerId;
    }

    /**
     * set request type.
     *
     * @param requestType The request type. Must be accepted by SDT.
     * @see uk.gov.moj.sdt.domain.api.IServiceRequest#setRequestType(String)
     */
    @Override
    public void setRequestType(final String requestType) {
        this.requestType = requestType;
    }

    /**
     * set request payload.
     *
     * @param requestPayload the incoming message.
     * @see uk.gov.moj.sdt.domain.api.IServiceRequest#setRequestPayload(byte[])
     */
    @Override
    public void setRequestPayload(final byte[] requestPayload) {
        this.requestPayload = requestPayload;
    }

    /**
     * set response payload.
     *
     * @param responsePayload the outgoing message.
     * @see uk.gov.moj.sdt.domain.api.IServiceRequest#setResponsePayload(byte[])
     */
    @Override
    public void setResponsePayload(final byte[] responsePayload) {
        this.responsePayload = responsePayload;
    }

    /**
     * set bulk reference.
     *
     * @param bulkReference the SDT generated reference.
     * @see uk.gov.moj.sdt.domain.api.IServiceRequest#setBulkReference(String)
     */
    @Override
    public void setBulkReference(final String bulkReference) {
        this.bulkReference = bulkReference;
    }

    /**
     * get request date time.
     *
     * @return date time
     */
    @Override
    public LocalDateTime getRequestDateTime() {
        return this.requestDateTime;
    }

    /**
     * get response date time.
     *
     * @return date time
     */
    @Override
    public LocalDateTime getResponseDateTime() {
        return this.responseDateTime;
    }

    /**
     * get bulk customer id.
     *
     * @return id id
     */
    @Override
    public String getBulkCustomerId() {
        return this.bulkCustomerId;
    }

    /**
     * get request type.
     *
     * @return requestType string
     */
    @Override
    public String getRequestType() {
        return this.requestType;
    }

    /**
     * get request payload.
     *
     * @return request payload
     */
    @Override
    public byte[] getRequestPayload() {
        return requestPayload;
    }

    /**
     * get response payload.
     *
     * @return response payload
     */
    @Override
    public byte[] getResponsePayload() {
        return responsePayload;
    }

    /**
     * get bulk reference.
     *
     * @return bulkReference string
     */
    @Override
    public String getBulkReference() {
        return this.bulkReference;
    }

    /**
     * set server host name.
     *
     * @param hostName the server host name
     */
    @Override
    public void setServerHostName(final String hostName) {
        this.serverHostName = hostName;
    }

    /**
     * get server host name.
     *
     * @return host name
     */
    @Override
    public String getServerHostName() {
        return this.serverHostName;
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

    /**
     * to String.
     *
     * @return String
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(getHashId(this) + "[");
        sb.append(super.toString());
        sb.append(", requestDateTime=").append(this.getRequestDateTime());
        sb.append(", responseDateTime=").append(this.getResponseDateTime());
        sb.append(", bulkCustomerId=").append(this.getBulkCustomerId());
        sb.append(", requestType=").append(this.getRequestType());
        sb.append(", requestPayload=").append(getHashId(this.getRequestPayload()));
        sb.append(", responsePayload=").append(getHashId(this.getResponsePayload()));
        sb.append(", bulkReference=").append(this.getBulkReference());
        sb.append(", serverHostName=").append(this.getServerHostName());
        sb.append("]");
        return sb.toString();
    }
}
