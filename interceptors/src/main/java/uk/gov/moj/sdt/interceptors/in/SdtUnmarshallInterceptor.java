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
package uk.gov.moj.sdt.interceptors.in;

import java.lang.reflect.Field;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.cxf.wsdl.interceptors.DocLiteralInInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.moj.sdt.interceptors.AbstractSdtInterceptor;
import uk.gov.moj.sdt.utils.mbeans.SdtMetricsMBean;

/**
 * Interceptor class which handles bulk submission message received by SDT.
 * <p>
 * This interceptor is used to catch any faults detected in CXF and to record the count of these in the metrics.
 *
 * @author Robin Compston
 */
public class SdtUnmarshallInterceptor extends AbstractSdtInterceptor {
    /**
     * Logger object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SdtUnmarshallInterceptor.class);

    /**
     * Test interceptor to prove concept.
     */
    public SdtUnmarshallInterceptor() {
        super(Phase.UNMARSHAL);
        this.addBefore(DocLiteralInInterceptor.class.getName());
    }

    @Override
    public void handleMessage(final SoapMessage message) throws Fault {
        // Call the following interceptors in the chain.
        final PhaseInterceptorChain interceptorChain = (PhaseInterceptorChain) message.getInterceptorChain();
        interceptorChain.doIntercept(message);

        try {
            // Tweak the private field so we can read it.
            final Field privateStringField = PhaseInterceptorChain.class.getDeclaredField("faultOccurred");
            privateStringField.setAccessible(true);

            // Was there a fault?
            final boolean faultOccurred = (Boolean) privateStringField.get(interceptorChain);
            if (faultOccurred) {
                SdtMetricsMBean.getMetrics().upXmlValidationFailureCount();
            }
        }
        catch (final Exception e)
        {
            LOGGER.error("Error in unmarshalling interceptor", e);
        }
    }
}
