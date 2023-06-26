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

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import uk.gov.moj.sdt.interceptors.AbstractSdtInterceptor;
import uk.gov.moj.sdt.utils.SdtContext;

/**
 * Interceptor class which handles bulk submission message received by SDT.
 * <p>
 * This interceptor is necessary in order to process the raw XML sent to SDT before CXF has turned it into JAXB objects,
 * after which any non generic portions of the XML (those portions which are case management system specific) will no
 * longer be visible. This is because the XSD defined for SDT treats the case management specific portions of the XML as
 * part of an <any> tag. This non generic XML must be stored in the database as a blob and the details of its content
 * should be hidden from SDT. SDT stores the entire XML in ThreadLocal storage so that it can be retrieved later and
 * various portions of it used to populate the domain objects with the raw XML before storing them in the database via
 * Hibernate.
 *
 * @author Robin Compston
 */
public class XmlInboundInterceptor extends AbstractSdtInterceptor {
    /**
     * Test interceptor to prove concept.
     */
    public XmlInboundInterceptor() {
        super(Phase.RECEIVE);
    }

    @Override
    public void handleMessage(final SoapMessage message) throws Fault {
        // Read contents of message, i.e. XML received from client.
        String rawXml = this.readInputMessage(message);

        // Remove linefeeds as they stop the regular expression working.
        rawXml = rawXml.replace('\n', ' ');
        rawXml = rawXml.replace('\r', ' ');

        // Place entire XML in ThreadLocal from where other processing can extract it.
        SdtContext.getContext().setRawInXml(rawXml);
    }
}
