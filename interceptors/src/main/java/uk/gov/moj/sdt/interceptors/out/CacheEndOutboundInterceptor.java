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

import java.io.OutputStream;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.phase.Phase;
import uk.gov.moj.sdt.interceptors.AbstractSdtInterceptor;
import uk.gov.moj.sdt.utils.SdtContext;

/**
 * This interceptor restores the original output stream created by CXF. This should be done after the cached output
 * stream has been finished with. While the original output stream is replaced by the caching output stream nothing will
 * be written down the wire. When we are ready to send enriched content the original stream is restored and the contents
 * of the cache output stream written to it.
 * <p>
 * This interceptor works as a pair with CacheSetupOutboundInterceptor. Both should be configured as CXF interceptors.
 *
 * @author Robin Compston
 */
public class CacheEndOutboundInterceptor extends AbstractSdtInterceptor {

    /**
     * Constructor to position this interceptor before anything has been written to the output stream.
     */
    public CacheEndOutboundInterceptor() {
        super(Phase.PREPARE_SEND_ENDING);
        addAfter(ServiceRequestOutboundInterceptor.class.getName());
    }

    @Override
    public void handleMessage(final SoapMessage message) throws Fault {
        // Do safety check.
        final OutputStream os = message.getContent(OutputStream.class);
        if (!CachedOutputStream.class.isAssignableFrom(os.getClass())) {
            throw new RuntimeException(this.getClass().getCanonicalName() +
                    " may only be configure if preceded by CacheSetupOutboundInterceptor.");
        }

        // Get the cached output stream payload.
        final String payload = this.readOutputMessage(message);

        // Restore the original output stream created by CXF.
        message.setContent(OutputStream.class, SdtContext.getContext().getOriginalOutputStream());

        // Write the payload to the original output stream.
        this.writeOutputMessage(message, payload);
    }
}
