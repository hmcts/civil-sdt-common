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
 * $Id$
 * $LastChangedRevision$
 * $LastChangedDate$
 * $LastChangedBy$ */

package uk.gov.moj.sdt.interceptors.enricher;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import uk.gov.moj.sdt.utils.SdtContext;

/**
 * Generic xml enricher, used to enrich outbound messages.
 *
 * @author d276205
 */
@Component("GenericEnricher")
public class GenericEnricher extends AbstractSdtEnricher {
    /**
     * Logger object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GenericEnricher.class);

    @Override
    public String enrichXml(final String message) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Message before enrichment [{}]", message);
        }

        // Assume no change to input.
        String newXml = message;

        // Check to ensure the parent tag can be found in the message.
        if (super.findParentTag(message)) {
            // Remove linefeeds as they stop the regular expression working.
            newXml = newXml.replace('\n', ' ');
            newXml = newXml.replace('\r', ' ');

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Parent tag [" + this.getParentTag() + "] found...performing enrichment.");
            }

            // Get the system specific response from thread local to inject into the outbound message
            String replacementXml = SdtContext.getContext().getRawOutXml();

            // Build search pattern for insertion point.
            //
            // Search for:
            // optional tag prefix (excluding comments, end tag, and other tags)
            // insertion tag (without attributes)
            final Pattern pattern = Pattern.compile("<[\\S:&&[^!>/]]*?" + getInsertionTag() + "/>");

            final Matcher matcher = pattern.matcher(newXml);

            if (matcher.find()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Found matching group[{}]", matcher.group());
                }

                // Get the string matching the regular expression.
                final String singleLineTag = matcher.group();

                // Add the start and end tag to the replacement xml.
                replacementXml = changeToStartTag(singleLineTag) + replacementXml + changeToEndTag(singleLineTag);

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Replacement string[{}]", replacementXml);
                }

                // Escape any $ in the string, otherwise matcher may crash depending on what comes after $. N.B. Do not
                // use replaceAll or anything making use of java.util.regex.Matcher because this will produce the
                // problem we are trying to avoid.
                replacementXml = this.escapeUnescapedCharacters('$', replacementXml);

                // Inject the system specific response into the current envelope.
                newXml = matcher.replaceFirst(replacementXml);
            }

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Message after enrichment [{}]", newXml);
            }

        } else {
            if (LOGGER.isDebugEnabled()) {
                // Failure to find matching request in outgoing XML.
                LOGGER.debug("Parent tag [" + this.getParentTag() + "] not found...skipping enrichment.");
            }
        }

        return newXml;
    }
}
