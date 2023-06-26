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

import java.util.ArrayList;
import java.util.List;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import uk.gov.moj.sdt.interceptors.AbstractSdtInterceptor;
import uk.gov.moj.sdt.interceptors.enricher.AbstractSdtEnricher;
import uk.gov.moj.sdt.interceptors.enricher.BulkFeedbackEnricher;
import uk.gov.moj.sdt.interceptors.enricher.GenericEnricher;
import uk.gov.moj.sdt.interceptors.enricher.SubmitQueryEnricher;

/**
 * Interceptor class which handles bulk submission message sent by SDT.
 * <p>
 * This interceptor is necessary in order to process the raw XML sent from SDT after CXF has produced it from JAXB
 * objects. Non generic XML content (which should be hidden from SDT) must NOT be represented by JAXB classes known to
 * SDT. Instead, this non generic XML is inserted as raw XML into the XML already produced by CXF and at the relevant
 * insertion point. This is because the XML sent by SDT should have non generic content (this is true both of the System
 * Gateway and the specific Case Managements Systems). This non generic XML is be stored in the database as a blob and
 * read via Hibernate and loaded into ThreadLocal memory from which this interceptor takes it in order to populate the
 * outgoing XML.
 *
 * @author Robin Compston
 */
public class XmlOutboundInterceptor extends AbstractSdtInterceptor {


    /**
     * Test interceptor to prove concept.
     */
    public XmlOutboundInterceptor() {
        super(Phase.PREPARE_SEND_ENDING);
        addBefore(ServiceRequestOutboundInterceptor.class.getName());
        List<AbstractSdtEnricher> enricherList = new ArrayList<>();
        enricherList.add(new SubmitQueryEnricher());
        enricherList.add(new BulkFeedbackEnricher());
        enricherList.add(submitQueryRequestEnricher());
        enricherList.add(individualRequestEnricher());
        setEnricherList(enricherList);
    }

    private GenericEnricher submitQueryRequestEnricher() {
        GenericEnricher genericEnricher = new GenericEnricher();
        genericEnricher.setParentTag("submitQueryRequest");
        genericEnricher.setInsertionTag("targetAppDetail");
        return genericEnricher;
    }

    private GenericEnricher individualRequestEnricher() {
        GenericEnricher genericEnricher = new GenericEnricher();
        genericEnricher.setParentTag("individualRequest");
        genericEnricher.setInsertionTag("targetAppDetail");
        return genericEnricher;
    }

    @Override
    public void handleMessage(final SoapMessage message) throws Fault {
        // Get the cached output stream payload.
        final String payload = this.readOutputMessage(message);

        // Modify the payload with enrichers.
        String modifiedPayload = changeOutboundMessage(payload);
        if (modifiedPayload == null) {
            // No enrichers matched - restore original payload.
            modifiedPayload = payload;
        }

        this.replaceOutputMessage(message, modifiedPayload);
    }
}
