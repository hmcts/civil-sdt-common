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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.moj.sdt.utils.visitor.api.ITree;
import uk.gov.moj.sdt.utils.visitor.api.IVisitor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Implement visit method, common to all {@link IDomainObjectVisitor}s.
 *
 * @author Robin Compston
 */
public abstract class AbstractVisitor implements IVisitor {

    private static final String CANNOT_FIND_MSG = "Cannot find visit method for target bean";

    /**
     * Static logging object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractVisitor.class);

    /**
     * Map containing all registers {@link IVisitor}s which can be retrieved as needed by the
     * {@link VisitableTreeWalker}.
     */
    private static Map<String, IVisitor> allVisitors = new HashMap<>();

    /**
     * Default constructor for {@link AbstractVisitor}.
     */
    protected AbstractVisitor() {
        final String key = this.getClass().getSimpleName();

        // Is visitor already registered?
        if (!AbstractVisitor.allVisitors.containsKey(key)) {
            // Store an instance of this class in a static map so it can be used to retrieve this Visitor when walking a
            // tree of IVisitable objects and applied as appropriate.
            AbstractVisitor.allVisitors.put(key, this);
            LOGGER.debug("Visitor [{}] registered in allVisitors map.", key);
        }
    }

    /**
     * Getter visitor matching given key from allVisitors map.
     *
     * @param key key to visitor to be retrieved from map.
     * @return map containing all registerd {@link IVisitor}s.
     */
    public static IVisitor getVisitor(final String key) {
        return allVisitors.get(key);
    }

    /**
     * Get the interface which corresponds to the given class name with the prefix 'I'.
     *
     * @param clazz the class whose corresponding interface is to be found.
     * @return Class<?> class of the interface corresponding to the given object with "I" prefix.
     */
    private Class<?> getInterfaceClass(final Class<?> clazz) {
        // Get all interfaces on the class.
        final Class<?>[] interfaces = clazz.getInterfaces();

        // Find the simple name of the given class.
        final String simpleName = clazz.getSimpleName();

        // By convention for an interface name to look for based on given class name with "I" prefix.
        final String interfaceName = "I" + simpleName;

        // Look thru all interfaces for one that matches.
        for (Class<?> interfaceInstance : interfaces) {
            if (interfaceInstance.getSimpleName().equals(interfaceName)) {
                return interfaceInstance;
            }
        }

        throw new UnsupportedOperationException(
                "No matching interface found for class - interface should be called [" + interfaceName + "]");
    }

    @Override
    public final void visit(final Object visitable, final ITree tree) {
        // Class of target bean.
        Class<?> clazz = null;

        // Interface of target bean.
        Class<?> interfaceClass = null;

        // Now we try to invoke the method visit.
        try {
            // Get class of visitable in order to find method signature.
            clazz = visitable.getClass();

            // Find the corresponding interface since the visit method expects an interface not a class. By convention
            // this is the same as the class name with "I" prefix.
            interfaceClass = getInterfaceClass(clazz);

            // Get the method appropriate for the visit method to call which takes a parameter of the target bean type.
            final Method method = getClass().getMethod("visit", new Class[]{interfaceClass, ITree.class});

            try {
                // Invoke the appropriate specific visit method in this IVisitor.
                method.invoke(this, visitable, tree);
            } catch (final InvocationTargetException e) {
                // Check if this is a validation exception thrown by one of the validators and if so extract the
                // validation exception from the InvocationTargetException added by reflection logic.
                final Throwable throwable = e.getCause();
                if (RuntimeException.class.isAssignableFrom(throwable.getClass())) {
                    throw RuntimeException.class.cast(throwable);
                }

                LOGGER.error(CANNOT_FIND_MSG + " [" + interfaceClass.getName() + "].", e);
                throw new UnsupportedOperationException(e);
            } catch (final IllegalAccessException e) {
                LOGGER.error(CANNOT_FIND_MSG + " [" + interfaceClass.getName() + "].", e);
                throw new UnsupportedOperationException(e);
            }
        } catch (final NoSuchMethodException e) {
            LOGGER.error(CANNOT_FIND_MSG + " [" + interfaceClass.getName() + "].", e);
            throw new UnsupportedOperationException(e);
        }
    }
}
