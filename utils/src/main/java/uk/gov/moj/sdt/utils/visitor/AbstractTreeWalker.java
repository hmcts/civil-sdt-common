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
 * $Id: ClaimXsdTest.java 16414 2013-05-29 10:56:45Z agarwals $
 * $LastChangedRevision: 16414 $
 * $LastChangedDate: 2013-05-29 11:56:45 +0100 (Wed, 29 May 2013) $
 * $LastChangedBy: holmessm $ */

package uk.gov.moj.sdt.utils.visitor;

import uk.gov.moj.sdt.utils.visitor.api.ITree;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Abstract Class containing methods common to all tree walkers.
 *
 * @author Robin Compston.
 */
public abstract class AbstractTreeWalker {

    /**
     * Collection of all objects in tree which have already been processed.
     */
    private Set<Object> alreadyProcessed = new HashSet<>();

    /**
     * Representation of the tree being walked which can be used by Visitors to find other related nodes.
     */
    private ITree tree;

    /**
     * Constructor for {@link AbstractTreeWalker}.
     */
    protected AbstractTreeWalker() {
        super();
    }

    /**
     * Getter for alreadyProcessed.
     *
     * @return alreadyProcessed.
     */
    protected Set<Object> getAlreadyProcessed() {
        return alreadyProcessed;
    }

    /**
     * Getter for tree.
     *
     * @return tree.
     */
    public ITree getTree() {
        return tree;
    }

    /**
     * Setter for alreadyProcessed.
     *
     * @param alreadyProcessed new value of alreadyProcessed.
     */
    public void setAlreadyProcessed(final Set<Object> alreadyProcessed) {
        this.alreadyProcessed = alreadyProcessed;
    }

    /**
     * Setter for tree.
     *
     * @param tree new value of tree.
     */
    public void setTree(final ITree tree) {
        this.tree = tree;
    }

    /**
     * Walk a list containing objects applying the corresponding {@link IVisitor} to each of them.
     *
     * @param target        the list to walk.
     * @param visitorSuffix the suffix which by convention is appended to the target class name in order to form the
     *                      class name of the visitor class.
     */
    protected void walkList(final List<?> target, final String visitorSuffix) {
        // Iterate over list and walk its elements.
        for (int i = 0; i < target.size(); i++) {
            this.walkTree(target.get(i), visitorSuffix);
        }
    }

    /**
     * Walk a map containing objects applying the corresponding {@link IVisitor} to each of them.
     *
     * @param target        the map to walk.
     * @param visitorSuffix the suffix which by convention is appended to the target class name in order to form the
     *                      class name of the visitor class.
     */
    protected void walkMap(final Map<?, ?> target, final String visitorSuffix) {
        // Iterate over map and walk its elements.
        final Set<?> keys = target.keySet();
        for (final Iterator<?> iter = keys.iterator(); iter.hasNext(); ) {
            final Object key = iter.next();
            this.walkTree(target.get(key), visitorSuffix);
        }
    }

    /**
     * Walk a set containing objects applying the corresponding {@link IVisitor} to each of them.
     *
     * @param target        the set to walk.
     * @param visitorSuffix the suffix which by convention is appended to the target class name in order to form the
     *                      class name of the visitor class.
     */
    protected void walkSet(final Set<?> target, final String visitorSuffix) {
        // Iterate over map and walk its elements.
        for (final Iterator<?> iter = target.iterator(); iter.hasNext(); ) {
            final Object object = iter.next();
            this.walkTree(object, visitorSuffix);
        }
    }

    /**
     * Abstract method to be implemented by all tree walkers.
     *
     * @param target        the target to apply visitor to.
     * @param visitorSuffix the suffix to be used by convention to for visitor class name.
     */
    protected abstract void walkTree(final Object target, final String visitorSuffix);

}
