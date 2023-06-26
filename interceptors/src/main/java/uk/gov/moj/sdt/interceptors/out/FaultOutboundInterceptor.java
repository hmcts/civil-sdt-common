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
package uk.gov.moj.sdt.interceptors.out;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.Soap11FaultOutInterceptor;
import org.apache.cxf.binding.soap.interceptor.Soap12FaultOutInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.moj.sdt.interceptors.AbstractServiceRequest;
import uk.gov.moj.sdt.interceptors.service.RequestDaoService;

/**
 * Interceptor class which handles faults.
 * <p>
 * Stores original error message.
 *
 * @author Robin Compston
 */
public class FaultOutboundInterceptor extends AbstractServiceRequest {
    /**
     * Logger object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(FaultOutboundInterceptor.class);

    /**
     * Test interceptor to prove concept.
     */
    public FaultOutboundInterceptor(RequestDaoService requestDaoService) {
        super(Phase.MARSHAL);
        setRequestDaoService(requestDaoService);
        // Assume that the interceptor will run after the default SOAP interceptor.
        getAfter().add(Soap11FaultOutInterceptor.class.getName());
        getAfter().add(Soap12FaultOutInterceptor.class.getName());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleMessage(final SoapMessage message) throws Fault {
        // Get the fault detected during SOAP validation.
        final Fault fault = (Fault) message.getContent(Exception.class);

        // Show the error of the original exception instead of the unhelpful CXF fault message.
        final Throwable t = getOriginalCause(fault.getCause());
        String msg = "Error cause unknown";
        if (null != t) {
            msg = t.getMessage();
        }

        final String errorMsg = "Error encountered: " + fault.getFaultCode() + ": " + fault.getMessage() +
                ", sending this message in SOAP fault: " + msg;
        LOGGER.error(errorMsg);
        this.getRequestDaoService().persistEnvelope(errorMsg);

        fault.setMessage(msg);
    }

    /**
     * Method to retrieve the original error message. This method is recursive.
     *
     * @param t the fault
     * @return the original wrapped error
     */
    private Throwable getOriginalCause(final Throwable t) {
        if (null == t || null == t.getCause() || t.getCause().equals(t)) {
            return t;
        }
        return getOriginalCause(t.getCause());
    }
}
