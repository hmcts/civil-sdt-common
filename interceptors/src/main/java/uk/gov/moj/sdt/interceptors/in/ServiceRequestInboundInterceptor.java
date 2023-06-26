/* Copyrights and Licenses
 *
 * Copyright (c) 2012-2014 by the Ministry of Justice. All rights reserved.
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
package uk.gov.moj.sdt.interceptors.in;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.moj.sdt.interceptors.AbstractServiceRequest;
import uk.gov.moj.sdt.interceptors.service.RequestDaoService;

/**
 * Class to intercept incoming messages and log them to the database.
 *
 * <p>
 * Generates <code>ServiceDomain</code> domain objects and by means of a Thread Local <SdtContext> instance keeps track
 * of the persisted entity.
 * <p>
 * Outbound message interceptors in the same thread can then access the persisted entity to update as required.
 * </p>
 *
 * @author d195274
 */
public class ServiceRequestInboundInterceptor extends AbstractServiceRequest {

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRequestInboundInterceptor.class);

    @Autowired
    public ServiceRequestInboundInterceptor(RequestDaoService requestDaoService) {
        super(Phase.RECEIVE);
        setRequestDaoService(requestDaoService);
        addAfter(XmlInboundInterceptor.class.getName());
    }

    @Override
    public void handleMessage(final SoapMessage soapMessage) throws Fault {
        LOGGER.debug("ServiceRequestInboundInterceptor handle incoming message being processed");
        getRequestDaoService().persistRequest();
    }
}
