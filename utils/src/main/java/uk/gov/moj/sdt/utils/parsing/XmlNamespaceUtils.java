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

package uk.gov.moj.sdt.utils.parsing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is a utility class for dealing with namespaces for xml fragments.
 *
 * @author D303894
 */
public final class XmlNamespaceUtils {
    /**
     * key to default namespace.
     */
    public static final String DEFAULT_NAMESPACE = "default";

    /**
     * Logger object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(XmlNamespaceUtils.class);

    /**
     * No-args constructor.
     */
    private XmlNamespaceUtils() {

    }

    /**
     * Combine a number of namespace maps into a single namespace map without duplicates.
     *
     * @param namespaceMap       an individual namespace map which should be combined with any other individual
     *                           namespace maps.
     * @param combinedNamespaces combined namespace map
     * @return map of individual namespace maps combined so far.
     */
    public static Map<String, String> combineNamespaces(final Map<String, String> namespaceMap,
                                                        final Map<String, String> combinedNamespaces) {
        final Set<String> keys = namespaceMap.keySet();
        final Iterator<String> iter = keys.iterator();
        while (iter.hasNext()) {
            final String key = iter.next();

            // Check if we have already combined this one.
            if (!combinedNamespaces.containsKey(key)) {
                // Add to combined set of namespaces.
                combinedNamespaces.put(key, namespaceMap.get(key));
            }
        }

        return combinedNamespaces;
    }

    /**
     * Extract namespaces out of a piece of raw XML. Note that we find these both in the XML header, and as embedded
     * namespace definition attributes. Note that we deliberately do not find any embedded (attribute style) default
     * namespaces, since these do not have corresponding tags, and the map generated is specifically used to handle
     * tags with namespace prefixes.
     *
     * @param rawXml                raw xml.
     * @param replacementNamespaces map containing replacement namespaces.
     * @return map containing extracted namespaces
     */
    public static Map<String, String> extractAllNamespaces(final String rawXml,
                                                           final Map<String, String> replacementNamespaces) {
        final Map<String, String> extractedNamespaces = new HashMap<>();

        // Get all namespaces with prefixes.
        extractAllPrefixNamespaces(rawXml, extractedNamespaces, replacementNamespaces);

        // Get the toplevel default namespace if it exists.
        extractDefaultNamespace(rawXml, extractedNamespaces, replacementNamespaces);

        return extractedNamespaces;
    }

    /**
     * Extract namespaces out of a piece of raw XML which have a prefix. Note that we find these both in the XML header,
     * and as embedded namespace definition attributes. Note that we deliberately do not find any embedded (attribute
     * style) default namespaces, since this is handled by a separate method.
     *
     * @param rawXml                raw xml.
     * @param extractedNamespaces   map containing extracted namespaces.
     * @param replacementNamespaces map containing replacement namespaces.
     */
    private static void extractAllPrefixNamespaces(final String rawXml, final Map<String, String> extractedNamespaces,
                                                   final Map<String, String> replacementNamespaces) {
        // Build a search pattern to find all prefixed namespaces.
        //
        // Search for:
        // non default namespace definition prefix
        // namespace name without '=' character
        // '=' character
        // url surrounded by single or double quotes
        //
        // Capture:
        // namespace prefix
        final Pattern pattern = Pattern.compile("xmlns:([\\S&&[^=]]*?)=[\"\']\\S*?[\"\']");

        final Matcher matcher = pattern.matcher(rawXml);

        while (matcher.find()) {
            // Capture the raw XML associated with this request.
            final String namespaceKey = matcher.group(1);
            String namespaceValue = matcher.group(0);

            if (replacementNamespaces != null) {
                // Replace all namespaces matching entries in replacementNamespaces. This 'translation' is necessary
                // because the namespaces required for target application messages are not present in the incoming SDT
                // messages, but since an equivalent of them is present, it is convenient to do this translation.
                final Set<String> keys = replacementNamespaces.keySet();
                for (String key : keys) {
                    if (namespaceValue.contains(key)) {
                        namespaceValue = "xmlns:" + namespaceKey + "=\"" + replacementNamespaces.get(key) + "\"";
                    }
                }
            }

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Extracting namespace with key [{}], value [{}]", namespaceKey, namespaceValue);
            }

            extractedNamespaces.put(namespaceKey, namespaceValue);
        }
    }

    /**
     * Extract the single default namespace in the soap envelope since this is needed for tags which have no prefix when
     * building XML to be sent to the target application. When the request is sent to the target application SOAP does
     * not add a default namespace if one is not present in the WSDL, and therefore all tags lacking a prefix namespace
     * which reside in the non-generic target application section need to have the default namespace added explicitly.
     * This is also necessary because the replacement may need to translate this default namespace and only works if it
     * is explicitly present on the tag. Default namespaces are those with no namespace name in their definition,
     * e.g. xmlns="< url >".
     *
     * @param rawXml                raw xml.
     * @param extractedNamespaces   map containing extracted namespaces.
     * @param replacementNamespaces map containing replacement namespaces.
     */
    private static void extractDefaultNamespace(final String rawXml, final Map<String, String> extractedNamespaces,
                                                final Map<String, String> replacementNamespaces) {
        // Find a tag with a default namespace definition attribute, and all nested tags within it which are in scope.
        //
        // Search for:
        // optional name space and some tag name
        // any attributes before the default namespace
        // an embedded default namespace
        //
        // Capture:
        // prefix and tag name
        // default namespace definition
        final Pattern pattern = Pattern.compile("<([\\S&&[^>/]]*?[\\w-]+)[^>]*?(xmlns=[\"\'][\\S&&[^>]]*?[\"\'])");

        final Matcher matcher = pattern.matcher(rawXml);

        if (matcher.find()) {
            // Capture the raw XML associated with this request.
            final String tagName = matcher.group(1);
            String namespaceValue = matcher.group(2);

            if (replacementNamespaces != null) {
                // Replace namespace value if mapping found in replacement namespaces map.
                final Set<String> keys = replacementNamespaces.keySet();
                for (String key : keys) {
                    if (namespaceValue.contains(key)) {
                        namespaceValue = "xmlns=\"" + replacementNamespaces.get(key) + "\"";
                    }
                }
            }

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Extracting default namespace from tag name [" + tagName + "], value [" + namespaceValue
                        + "]");
            }

            extractedNamespaces.put(DEFAULT_NAMESPACE, namespaceValue);
        }
    }

    /**
     * Translate default namespaces if they match the values in the replacementNamespaces. These must be
     * left in place since if removed we would lose information and they could not be re-applied.
     *
     * @param xmlFragment           xml to act upon.
     * @param replacementNamespaces map containing replacement namespaces.
     * @return the translated XML with replaced default namespace values.
     */
    public static String translateDefaultNamespaces(final String xmlFragment,
                                                    final Map<String, String> replacementNamespaces) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Translating default namespaces for fragment [{}]", xmlFragment);
        }

        // Buffer for accumulating results.
        final StringBuilder result = new StringBuilder();

        // Point up to which text has been copied so far.
        int copyPosition = 0;

        // Find a tag with a default namespace definition attribute, and all nested tags within it which are in scope.
        //
        // Search for:
        // an embedded default namespace
        // namespace URL surrounded by single or double quotes
        //
        // Capture:
        // entire matching string
        final Pattern pattern = Pattern.compile("xmlns=[\"\'][\\S&&[^>]]*?[\"\']");
        final Matcher matcher = pattern.matcher(xmlFragment);

        while (matcher.find()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Found matching group[{}]", matcher.group());
            }

            // Find if there is any uncopied text before the start of the match.
            final int start = matcher.start();
            if (start > copyPosition) {
                // Some uncopied text precedes the start of the match point - copy it to results.
                result.append(xmlFragment.substring(copyPosition, start));
            }

            String namespaceValue = matcher.group(0);
            if (replacementNamespaces != null) {
                // Replace namespace value if mapping found in replacement namespaces map.
                final Set<String> keys = replacementNamespaces.keySet();
                for (String key : keys) {
                    if (namespaceValue.contains(key)) {
                        namespaceValue = "xmlns=\"" + replacementNamespaces.get(key) + "\"";
                    }
                }
            }

            // Copy the match.
            result.append(namespaceValue);

            // Update position to take account of copy of matched substring.
            copyPosition = matcher.end();
        }

        // Copy any remaining text not yet copied from original XML.
        if (copyPosition < xmlFragment.length()) {
            // Some uncopied text follows the end of the last match point - copy it to results.
            result.append(xmlFragment.substring(copyPosition));
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Enhanced fragment [{}]", result);
        }

        return result.toString();
    }

    /**
     * Find map of applicable namespaces for given xml fragment based on what namespaces have been used a tag prefixes.
     * Look at the entire xml.
     *
     * @param xmlFragment   fragment of xml to which namespaces should apply.
     * @param allNamespaces namespaces for entire xml
     * @return map of applicable namespaces.
     */
    public static Map<String, String> findMatchingNamespaces(final String xmlFragment,
                                                             final Map<String, String> allNamespaces) {
        // Clear out previous namespaces.
        final Map<String, String> matchingNamespaces = new HashMap<>();

        // Build a search pattern to find all start tag namespaces used in this xml fragment.
        //
        // Search for:
        // Opening brace
        // Compulsory namespace prefix, excluding comments, end tag and other tags
        //
        // Capture:
        // namespace prefix
        final Pattern pattern = Pattern.compile("<([\\S&&[^>/]]+?):");

        final Matcher matcher = pattern.matcher(xmlFragment);

        while (matcher.find()) {
            // Capture the raw XML associated with this request.
            final String namespaceKey = matcher.group(1);

            // Form the replacement string from the matched groups and the extra XML.
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Matching namespace for fragment [{}]", namespaceKey);
            }

            if (!matchingNamespaces.containsKey(namespaceKey)) {
                // Make sure this namespace has been defined.
                if (!allNamespaces.containsKey(namespaceKey)) {
                    LOGGER.error("Namespace[{}] missing from incoming raw xml[{}]", namespaceKey, xmlFragment);

                    throw new RuntimeException("Namespace [" + namespaceKey + "] missing from incoming raw xml["
                            + xmlFragment + "]");
                }

                // Copy namespace to the set of matching namespaces for this fragment.
                matchingNamespaces.put(namespaceKey, allNamespaces.get(namespaceKey));
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Found matching namespaces for fragment [{}];{}", xmlFragment, matchingNamespaces);
        }

        // Always treat default namespace as a match
        if (allNamespaces.containsKey(DEFAULT_NAMESPACE)) {
            // Copy namespace to the set of matching namespaces for this fragment.
            matchingNamespaces.put(DEFAULT_NAMESPACE, allNamespaces.get(DEFAULT_NAMESPACE));
        }

        return matchingNamespaces;
    }

    /**
     * Adds namespaces to xml fragment. Assume that any embedded namespace definitions have been removed before this
     * method is called. Assume XML comments already removed.
     *
     * @param xmlFragment        xml to be decorated.
     * @param matchingNamespaces namespaces to be added.
     * @return xml with namespace
     */
    public static String addNamespaces(final String xmlFragment, final Map<String, String> matchingNamespaces) {
        // Add embedded namespace to all tags with namespace prefix.
        String generatedXml = addPrefixNamespaces(xmlFragment, matchingNamespaces);

        // Add default namespace to all tags with no namespace prefix and no default namespace.
        generatedXml = addDefaultNamespaces(generatedXml, matchingNamespaces);

        return generatedXml;
    }

    /**
     * Adds prefix namespaces to xml fragment. Assume that any embedded namespace definitions have been removed before
     * this method is called. Corresponding embedded namespaces are added to each tag with a namespace prefix.
     *
     * @param xmlFragment        xml to be decorated.
     * @param matchingNamespaces namespaces to be added.
     * @return xml with namespace
     */
    private static String addPrefixNamespaces(final String xmlFragment, final Map<String, String> matchingNamespaces) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Adding prefix namespaces to fragment [{}]", xmlFragment);
        }

        // Buffer for accumulating results.
        final StringBuilder result = new StringBuilder();

        // Point up to which text has been copied so far.
        int copyPosition = 0;

        // Look for all tags with any namespace prefix (excluding any attributes).
        //
        // Search for:
        // compulsory namespace prefix, excluding comments, end tag and other tags
        // tag name
        //
        // Capture:
        // namespace prefix
        final Pattern pattern = Pattern.compile("<([\\S&&[^>/]]+?):[\\w-]+");
        final Matcher matcher = pattern.matcher(xmlFragment);
        while (matcher.find()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Found matching group[{}]", matcher.group());
            }

            // Find if there is any uncopied text before the start of the match.
            final int start = matcher.start();
            if (start > copyPosition) {
                // Some uncopied text precedes the start of the match point - copy it to results.
                result.append(xmlFragment.substring(copyPosition, start));
            }

            // Copy the match.
            result.append(matcher.group(0));

            // Get prefix out of match (1st group).
            final String prefix = matcher.group(1);

            // Append the corresponding namespace.
            result.append(" " + matchingNamespaces.get(prefix));

            // Update position to take account of copy of matched substring.
            copyPosition = matcher.end();
        }

        // Copy any remaining text not yet copied from original XML.
        if (copyPosition < xmlFragment.length()) {
            // Some uncopied text follows the end of the last match point - copy it to results.
            result.append(xmlFragment.substring(copyPosition));
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Enhanced fragment [{}]", result);
        }

        return result.toString();
    }

    /**
     * Adds default namespace to xml fragment. An embedded default namespace is added only to the first tag encountered
     * which is assumed to include all other tags in its scope. By adding the default namespace to this tag, it
     * applies to all other tags that fall within its scope. Note that the target tag may already have a prefixed
     * namespace, and the default namespace being added is for the benefit of other tags within its scope. Note that
     * this may be added needlessly since we do not search any further than the first tag and do not know if there are
     * any tags which lack prefixes and need the default namespace definition.
     *
     * @param xmlFragment        xml to be decorated.
     * @param matchingNamespaces namespaces to be added.
     * @return xml with embedded default namespace
     */
    private static String addDefaultNamespaces(final String xmlFragment, final Map<String, String> matchingNamespaces) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Adding default namespaces to fragment [{}]", xmlFragment);
        }

        if (!matchingNamespaces.containsKey(DEFAULT_NAMESPACE)) {
            // Do not go any further as there is no default namespace to add.
            return xmlFragment;
        }

        // Buffer for accumulating results.
        final StringBuilder result = new StringBuilder();

        // Point up to which text has been copied so far.
        int copyPosition = 0;

        boolean finished = false;

        // Look for top level without default namespace definition. Because we cannot NOT match on a string, we must do
        // the match and then ignore it if the default namespace string is found.
        //
        // Search for:
        // start of string
        // possible leading whitespace
        // tag name with possible namespace prefix
        // possible white space
        // optional tag attributes which can include embedded prefixed namespaces
        // closing tag character
        //
        // Capture:
        // tag prefix + name
        // tag attributes
        final Pattern pattern = Pattern.compile("(^[\\s]*?<[\\S&&[^>]]+)([\\s]*?[^>]*?>)");
        final Matcher matcher = pattern.matcher(xmlFragment);
        while (matcher.find() && !finished) {
            // Ignore match if there is a default namespace definition in the tag already (but not if there is a prefix
            // namespace definition).
            if (matcher.group(2).contains("xmlns=")) {
                continue;
            }

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Found matching group[{}]", matcher.group());
            }

            // Find if there is any uncopied text before the start of the match.
            final int start = matcher.start();
            if (start > copyPosition) {
                // Some uncopied text precedes the start of the match point - copy it to results.
                result.append(xmlFragment.substring(copyPosition, start));
            }

            // Copy the match.
            result.append(matcher.group(1));

            // Append the default namespace.
            result.append(" " + matchingNamespaces.get(DEFAULT_NAMESPACE));

            // Copy any attributes.
            result.append(matcher.group(2));

            // Update position to take account of copy of matched substring.
            copyPosition = matcher.end();

            // Only look for first match - lower level tags will be covered by this one or else have their own
            // overriding namespace which we must not interfere with.
            finished = true;
        }

        // Copy any remaining text not yet copied from original XML.
        if (copyPosition < xmlFragment.length()) {
            // Some uncopied text follows the end of the last match point - copy it to results.
            result.append(xmlFragment.substring(copyPosition));
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Enhanced fragment [{}]", result);
        }

        return result.toString();
    }

    /**
     * Removes all embedded namespaces so that they can be re-added. Unless they are removed, it is difficult to
     * distinguish them from other element attributes when adding embedded namespaces again (which is required before
     * sending to target application to keep XSD checking happy). Note: do not remove default namespaces (which have no
     * associated name) because there will be no corresponding prefix on the tag and these cannot be re-added as is
     * required.
     *
     * @param xmlFragment xml to be cleaned.
     * @return xml without embedded namespaces.
     */
    public static String removeEmbeddedNamespaces(final String xmlFragment) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Removing namespaces from fragment [{}]", xmlFragment);
        }

        // Buffer for accumulating results.
        final StringBuilder result = new StringBuilder();

        // Build pattern to find all embedded namespace attributes in XML fragment. Note; default namespaces have no
        // colon.
        //
        // Search for:
        // optional preceding white space
        // non default namespace definition
        // namespace name
        // equals sign
        // namespace URL surrounded by single or double quotes
        //
        // Capture:
        // entire matching string
        final Pattern pattern = Pattern.compile("[\\s]*xmlns:([\\S&&[^=]]+?)=[\"\'].*?[\"\']");
        // "[\\s]*xmlns:[\\S][\\S]+[\"\']");
        final Matcher matcher = pattern.matcher(xmlFragment);

        // Position of XML copied into results so far.
        int copyPosition = 0;

        while (matcher.find()) {
            final int start = matcher.start();
            if (start > copyPosition) {
                // Copy uncopied text preceding start of the match point to results.
                result.append(xmlFragment.substring(copyPosition, start));
            }

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Found matching namespace[{}]", matcher.group());
            }

            // Update position to take account of copy of matched substring.
            copyPosition = matcher.end();
        }

        // Copy any remaining text not yet copied from original XML.
        if (copyPosition < xmlFragment.length()) {
            // Some uncopied text follows the end of the last match point - copy it to results.
            result.append(xmlFragment.substring(copyPosition));
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Cleaned fragment [{}]", result);
        }

        return result.toString();
    }

    /**
     * Removes all XML comments - this simplifies subsequent searches by removing the need to cope with comments in
     * regular expressions.
     *
     * @param xmlFragment xml to be cleaned.
     * @return xml without embedded namespaces.
     */
    public static String removeComments(final String xmlFragment) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Removing comments from fragment [{}]", xmlFragment);
        }

        // Buffer for accumulating results.
        final StringBuilder result = new StringBuilder();

        // Build pattern to find all embedded namespace attributes in XML fragment. Note; default namespaces have no
        // colon.
        //
        // Search for:
        // non default namespace definition
        final Pattern pattern = Pattern.compile("<!--.*?-->");
        final Matcher matcher = pattern.matcher(xmlFragment);

        // Position of XML copied into results so far.
        int copyPosition = 0;

        while (matcher.find()) {
            final int start = matcher.start();
            if (start > copyPosition) {
                // Copy uncopied text preceding start of the match point to results.
                result.append(xmlFragment.substring(copyPosition, start));
            }

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Found matching comment [{}]", matcher.group());
            }

            // Update position to take account of copy of matched substring.
            copyPosition = matcher.end();
        }

        // Copy any remaining text not yet copied from original XML.
        if (copyPosition < xmlFragment.length()) {
            // Some uncopied text follows the end of the last match point - copy it to results.
            result.append(xmlFragment.substring(copyPosition));
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Cleaned fragment [{}]", result);
        }

        return result.toString();
    }
}
