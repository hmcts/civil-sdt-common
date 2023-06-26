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
package uk.gov.moj.sdt.interceptors.enricher;

import org.apache.commons.lang3.StringUtils;
import uk.gov.moj.sdt.interceptors.enricher.api.ISdtEnricher;

/**
 * Abstract class for xml enricher holding common code for SDT enrichers.
 *
 * @author d130680
 */
public abstract class AbstractSdtEnricher implements ISdtEnricher {
    /**
     * Parent tag to search for set by child class.
     */
    private String parentTag;

    /**
     * The response tag to inject into.
     */
    private String insertionTag;

    /**
     * Try to find whether the parent tag exists in the message or not.
     *
     * @param message original xml message
     * @return true or false
     */
    protected boolean findParentTag(final String message) {
        return message.contains(this.getParentTag());
    }

    /**
     * Works out the start tag for a single line tag e.g.
     * <ns1:results/> becomes <ns1:results>
     *
     * @param tag single line tag
     * @return start tag
     */
    protected String changeToStartTag(final String tag) {
        return StringUtils.remove(tag, "/");
    }

    /**
     * Works out the end tag for a single line tag.
     *
     * @param tag single line tag
     * @return end tag
     */
    protected String changeToEndTag(final String tag) {
        // First remove the '/'
        final String s = StringUtils.remove(tag, "/");

        // Then close the tag
        return s.replace("<", "</");
    }

    /**
     * Get parent tag.
     *
     * @return parent tag
     */
    protected String getParentTag() {
        return parentTag;
    }

    /**
     * Set parent tag.
     *
     * @param parentTag parent tag
     */
    public void setParentTag(final String parentTag) {
        this.parentTag = parentTag;
    }

    /**
     * Get insertion tag.
     *
     * @return insertion tag.
     */
    protected String getInsertionTag() {
        return insertionTag;
    }

    /**
     * Set insertion tag.
     *
     * @param insertionTag insertion tag
     */
    public void setInsertionTag(final String insertionTag) {
        this.insertionTag = insertionTag;
    }

    /**
     * Prefix all instances of target character not already escaped in text with an escape character.
     *
     * @param targetCharacter character all occurrences of which are to be escaped if not already escaped.
     * @param text            string in which all instances of target character are to be escaped.
     * @return the fully escaped string.
     */
    protected String escapeUnescapedCharacters(final char targetCharacter, final String text) {
        final StringBuilder result = new StringBuilder();

        int cursor = 0;
        boolean escaped = false;

        // Parse through all input characters.
        while (cursor < text.length()) {
            final char next = text.charAt(cursor);

            // Is it the escape character?
            if (next == '\\') {
                // Toggle to handle escaped escape.
                escaped = !escaped;
            }
            // Is it a dollar?
            else if (next == targetCharacter) {
                // No action if already escaped.
                if (!escaped) {
                    result.append('\\');
                }

                // No further opportunity.
                escaped = false;
            } else {
                // Switch off escaped as it does not affect the target character now which is all we care about.
                escaped = false;
            }

            // Copy into results.
            result.append(next);

            cursor++;
        }

        return result.toString();
    }
}
