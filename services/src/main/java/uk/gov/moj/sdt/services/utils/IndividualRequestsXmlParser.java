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
package uk.gov.moj.sdt.services.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import uk.gov.moj.sdt.domain.api.IIndividualRequest;
import uk.gov.moj.sdt.utils.SdtContext;
import uk.gov.moj.sdt.utils.parsing.XmlNamespaceUtils;

import static uk.gov.moj.sdt.domain.api.IIndividualRequest.IndividualRequestStatus.REJECTED;

/**
 * This class reads a bulk submission xml and parses it into individual raw xml requests.
 *
 * @author D303894
 */
@Component("IndividualRequestsXmlParser")
public class IndividualRequestsXmlParser {
    /**
     * Logger object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(IndividualRequestsXmlParser.class);

    /**
     * Contains mapping of namespaces to be replaced.
     */
    private Map<String, String> replacementNamespaces = new HashMap<>();

    /**
     * Populate individual requests with the raw xml request.
     *
     * @param individualRequests the individual requests to be populated with data from the raw XML.
     */
    public void populateRawRequest(final List<IIndividualRequest> individualRequests) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Started parsing raw xml to extract payload for {} requests",
                    individualRequests.size());
        }

        // Get iterator so we can traverse the list of requests and the payload (raw XML) to each one.
        final Iterator<IIndividualRequest> iter = individualRequests.iterator();

        // Match it against the result of all previous match replacements.
        String rawXml = SdtContext.getContext().getRawInXml();

        // Get rid of comments to simplify subsequent processing.
        rawXml = XmlNamespaceUtils.removeComments(rawXml);

        // Retrieve all namespaces
        final Map<String, String> allNamespaces =
                XmlNamespaceUtils.extractAllNamespaces(rawXml, replacementNamespaces);

        final Map<String, String> allRawIndividualRequests = new HashMap<>();

        // Build a search pattern with this request id. Allow for any order of requestId
        // attributes.
        //
        // Search for:
        // optional namespace prefix and request start tag
        // any attributes, single or double quotes
        // requestId attribute and its value, single or double quotes
        // any attributes, single or double quotes
        // optional namespace prefix and request end tag
        final Pattern pattern =
                Pattern.compile("<[\\S:&&[^!>/]]*?request[ \\w\"\'=]*"
                        + "requestId=[\"\']([\\w-]+)[\"\'][ \\w\"\'=]*>(.*?)" + "</[\\S:&&[^!>/]]*?request>");

        final Matcher matcher = pattern.matcher(rawXml);

        // Find and store all the matches.
        while (matcher.find()) {
            // Capture the request id associated with this request.
            final String requestId = matcher.group(1).trim();

            // Capture the raw XML associated with this request.
            String individualRequestRawXml = matcher.group(2).trim();

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Found match: requestId [{}], individualRequestRawXml [{}]",
                        requestId, individualRequestRawXml);
            }

            // Find namespaces applicable for fragment
            final Map<String, String> matchingNamespaces =
                    XmlNamespaceUtils.findMatchingNamespaces(individualRequestRawXml, allNamespaces);

            // In preparation for adding namespace definitions, remove existing namespace definitions (which may be
            // different) from XML fragment.
            individualRequestRawXml = XmlNamespaceUtils.removeEmbeddedNamespaces(individualRequestRawXml);

            // Translate any embedded default namespaces to their target application equivalent.
            individualRequestRawXml =
                    XmlNamespaceUtils.translateDefaultNamespaces(individualRequestRawXml, replacementNamespaces);

            // Embed namespaces within fragment
            individualRequestRawXml = XmlNamespaceUtils.addNamespaces(individualRequestRawXml, matchingNamespaces);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Raw XML enhanced with namespaces [{}]", individualRequestRawXml);
            }

            allRawIndividualRequests.put(requestId, individualRequestRawXml);
        }

        // Apply the stored matches to the associated individual requests.
        while (iter.hasNext()) {
            // Get the next request.
            final IIndividualRequest individualRequest = iter.next();

            if (individualRequest.getRequestStatus().equals(
                    REJECTED.getStatus())) {
                // Do not get the payload for this one as it may have a duplicate customer reference and does not need
                // to be sent to the case management system.
                continue;
            }

            if (null != allRawIndividualRequests.get(
                    individualRequest.getCustomerRequestReference())) {
                individualRequest.setRequestPayload(allRawIndividualRequests.get(
                        individualRequest.getCustomerRequestReference()).getBytes());
            }
        }
    }

    /**
     * Setter for replacementNamespaces property.
     *
     * @param replacementNamespaces namespaces to be replaced.
     */
    public void setReplacementNamespaces(final Map<String, String> replacementNamespaces) {
        this.replacementNamespaces = replacementNamespaces;
    }
}
