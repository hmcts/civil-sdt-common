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

package uk.gov.moj.sdt.utils.parsing.api;

import java.util.Map;

/**
 * Interface for XmlNamespaceParser.
 *
 * @author Robin Compston
 */
public interface IXmlNamespaceUtils {

    /**
     * Extract namesspaces out of a piece of raw XML.
     *
     * @param rawXml the raw XML out of which names spaces are to be extracted.
     */
    void extractNamespaces(final String rawXml);

    /**
     * Get the extracted namesspaces map.
     *
     * @return the extracted namesspaces map.
     */
    Map<String, String> getNamespaces();

    /**
     * Find map of applicable namespaces for given xml fragment based on namespaces in entire xml.
     *
     * @param xmlFragment fragment of xml to which namespaces should apply.
     * @return map of applicable namespaces.
     */
    Map<String, String> findMatchingNamespaces(final String xmlFragment);

    /**
     * Combine a number of namespace maps into a single namespace map without duplicates.
     *
     * @param namespaceMap an individual namespace map which should be combined with any other individual namespace
     *                     maps.
     * @return map of individual namespace maps combined so far.
     */
    Map<String, String> combineNamespaces(final Map<String, String> namespaceMap);
}
