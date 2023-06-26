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
import org.apache.cxf.interceptor.AttachmentOutInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.StaxOutInterceptor;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.phase.Phase;
import uk.gov.moj.sdt.interceptors.AbstractSdtInterceptor;
import uk.gov.moj.sdt.utils.SdtContext;

/**
 * This interceptor stores the original output stream created by CXF (with application server implementation specifics
 * within it) and replaces it with a caching output stream. This has 2 purposes: it allows data being written to the
 * stream to be retrieved so that it can be enriched with additional raw XML as required by SDT for its handling of
 * non-generic content; and it avoids writing any data to the remote endpoint until we are ready to write the enriched
 * content. This interceptor must be run after the original endpoint has been created and before the first write to the
 * output stream has been performed by StaxOutInterceptor. The cache output stream is setup in anticipation of the
 * processing done by XmlOutboudInterceptor and ServiceRequestInterceptor.
 *
 * @author Robin Compston
 */
public class CacheSetupOutboundInterceptor extends AbstractSdtInterceptor {

    /**
     * Constructor to position this interceptor before anything has been written to the output stream.
     */
    public CacheSetupOutboundInterceptor() {
        super(Phase.PRE_STREAM);
        addAfter(AttachmentOutInterceptor.class.getName());
        addBefore(StaxOutInterceptor.class.getName());
    }

    @Override
    public void handleMessage(final SoapMessage message) throws Fault {
        // Save the original output stream for when we want to write down the wire.
        SdtContext.getContext().setOriginalOutputStream(message.getContent(OutputStream.class));

        // Create a caching stream to use as the output stream for the message. This will gather up the entire
        // content as the chain is run. Do not use CacheAndWriteOutputStream (created by LoggingOutInterceptor - this
        // therefore cannot be turned on) since it caches the message but also writes it down the wire before it has
        // been enriched.
        final CachedOutputStream cacheOutputStream = new CachedOutputStream();
        message.setContent(OutputStream.class, cacheOutputStream);
    }
}
