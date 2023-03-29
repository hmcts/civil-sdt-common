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

import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.moj.sdt.utils.logging.PerformanceLogger;
import uk.gov.moj.sdt.utils.visitor.api.IVisitable;
import uk.gov.moj.sdt.utils.visitor.api.IVisitor;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of VisitableTreeWalker.
 *
 * @author Robin Compston
 */
public final class VisitableTreeWalker extends AbstractTreeWalker {

    private static final String EXCEPTION_FAILURE_TO_GET_BEAN_MSG = "Failure to get bean info from ";
    private static final String LOG_FAILURE_TO_GET_BEAN_MSG = "Failure to get bean info from {}";
    /**
     * Static logging object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(VisitableTreeWalker.class);

    /**
     * Default private constructor for utility class.
     */
    private VisitableTreeWalker() {
    }

    /**
     * Walk a tree containing objects applying the corresponding {@link IVisitor} to each of them.
     *
     * @param target        the top level object of the object tree to walk.
     * @param visitorSuffix the suffix which by convention is appended to the target class name in order to form the
     *                      class name of the visitor class.
     */
    protected void walkTree(final Object target, final String visitorSuffix) {
        // At the top level create a Tree object representing other Visitable objects which may need to be accessed by
        // the active visitor.
        if (this.getTree() == null) {
            this.setTree(new Tree());
            this.getTree().setRoot(target);
        }

        // Is the current target an instance of IVisitable - if not, do not apply visitor to it.
        if (IVisitable.class.isAssignableFrom(target.getClass())) {
            // Get class name of target.
            String targetClassName = null;
            if (HibernateProxy.class.isInstance(target)) {
                final HibernateProxy proxy = HibernateProxy.class.cast(target);
                final LazyInitializer lazyInitializer = proxy.getHibernateLazyInitializer();
                final String nameOfClass = lazyInitializer.getEntityName();
                try {
                    targetClassName = Class.forName(nameOfClass).getSimpleName();
                } catch (final ClassNotFoundException e) {
                    throw new UnsupportedOperationException(
                            "Could not find class for entity name of Hibernate proxy [" + nameOfClass + "]");
                }
            } else {
                targetClassName = target.getClass().getSimpleName();
            }

            // Add suffix to form visitor class name.
            final String visitorClassName = targetClassName + visitorSuffix;

            // Get IVisitor registered when visitor object instantiated.
            final IVisitor visitor = AbstractVisitor.getVisitor(visitorClassName);

            if (visitor == null) {
                throw new UnsupportedOperationException("Could not find visitor class of derived name ["
                        + visitorClassName + "]");
            }

            // Do safe cast to IVisitable to allow call to accept ().
            final IVisitable visitable = IVisitable.class.cast(target);

            // Call the target with this visitor to execute the pattern.
            visitable.accept(visitor, this.getTree());
        }

        // Recursively call all nested beans.
        Method method = null;
        try {
            final BeanInfo beanInfo = Introspector.getBeanInfo(target.getClass());
            final PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                method = propertyDescriptor.getReadMethod();

                // If no read method, ignore.
                if (method == null) {
                    continue;
                }

                final Object nestedObject = method.invoke(target, (Object[]) null);

                // Check if this object has already been seen - if so ignore it.
                if (getAlreadyProcessed().contains(nestedObject)) {
                    continue;
                }

                // Record this object as now processed.
                getAlreadyProcessed().add(nestedObject);

                // Deal with all types of collection and with nested objects.
                if (nestedObject instanceof List<?>) {
                    // Save the current parent so that it can be reinstated after walking this branch.
                    final Object parent = getTree().getParent();

                    // Set this object as new parent.
                    getTree().setParent(target);

                    this.walkList((List<?>) nestedObject, visitorSuffix);

                    // Restore the previous parent.
                    getTree().setParent(parent);
                } else if (nestedObject instanceof Map<?, ?>) {
                    // Save the current parent so that it can be reinstated after walking this branch.
                    final Object parent = getTree().getParent();

                    // Set this object as new parent.
                    getTree().setParent(target);

                    this.walkMap((Map<?, ?>) nestedObject, visitorSuffix);

                    // Restore the previous parent.
                    getTree().setParent(parent);
                } else if (nestedObject instanceof Set<?>) {
                    // Save the current parent so that it can be reinstated after walking this branch.
                    final Object parent = getTree().getParent();

                    // Set this object as new parent.
                    getTree().setParent(target);

                    this.walkSet((Set<?>) nestedObject, visitorSuffix);

                    // Restore the previous parent.
                    getTree().setParent(parent);
                    // Don't go any further down the tree if the object is not of type IVisitable.
                } else if (nestedObject != null && IVisitable.class.isAssignableFrom(nestedObject.getClass())) {
                    // Save the current parent so that it can be reinstated after walking this branch.
                    final Object parent = getTree().getParent();

                    // Set this object as new parent.
                    getTree().setParent(target);

                    // Go deeper down tree.
                    this.walkTree(nestedObject, visitorSuffix);

                    // Restore the previous parent.
                    getTree().setParent(parent);
                }
            }
        } catch (final IntrospectionException e) {
            LOGGER.error(LOG_FAILURE_TO_GET_BEAN_MSG, target.getClass().getName(), e);
            throw new UnsupportedOperationException(EXCEPTION_FAILURE_TO_GET_BEAN_MSG + target.getClass().getName(),
                    e);
        } catch (final InvocationTargetException e) {
            LOGGER.error(LOG_FAILURE_TO_GET_BEAN_MSG, method.getName(), e);
            throw new UnsupportedOperationException(EXCEPTION_FAILURE_TO_GET_BEAN_MSG + target.getClass().getName(),
                    e);
        } catch (final IllegalAccessException e) {
            LOGGER.error(LOG_FAILURE_TO_GET_BEAN_MSG, target.getClass().getName(), e);
            throw new UnsupportedOperationException(EXCEPTION_FAILURE_TO_GET_BEAN_MSG + target.getClass().getName(),
                    e);
        }
    }

    /**
     * Walk a tree of objects which implement the {@link IVisitable} interface applying the corresponding
     * {@link IVisitor} to each of them.
     *
     * @param target        the top level object of the object tree to walk.
     * @param visitorSuffix the suffix which by convention is appended to the target class name in order to form the
     *                      class name of the visitor class.
     */
    public static void walk(final IVisitable target, final String visitorSuffix) {
        if (PerformanceLogger.isPerformanceEnabled(PerformanceLogger.LOGGING_POINT_12)) {
            final String detail = "\n\n\ttarget=" + target.toString() + "\n";

            // Write message to 'performance.log' for this logging point.
            PerformanceLogger.log(VisitableTreeWalker.class, PerformanceLogger.LOGGING_POINT_12,
                    "Start tree walker for " + visitorSuffix, detail);
        }

        // Create instance for this thread and call it.
        final VisitableTreeWalker visitableTreeWalker = new VisitableTreeWalker();
        visitableTreeWalker.walkTree(target, visitorSuffix);

        if (PerformanceLogger.isPerformanceEnabled(PerformanceLogger.LOGGING_POINT_13)) {
            final String detail = "\n\n\ttarget=" + target.toString() + "\n";

            // Write message to 'performance.log' for this logging point.
            PerformanceLogger.log(VisitableTreeWalker.class, PerformanceLogger.LOGGING_POINT_13,
                    "End tree walker for " + visitorSuffix, detail);
        }
    }
}
