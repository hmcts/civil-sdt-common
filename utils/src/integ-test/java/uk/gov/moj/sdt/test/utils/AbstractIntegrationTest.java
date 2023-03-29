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
package uk.gov.moj.sdt.test.utils;

import org.junit.Rule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import java.lang.reflect.Method;

import static org.junit.Assert.assertTrue;

/**
 * Implementation of the integration test for BulkSubmissionService.
 *
 * @author Manoj kulkarni
 */
public abstract class AbstractIntegrationTest extends AbstractTransactionalJUnit4SpringContextTests {

    /**
     * Logger object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractIntegrationTest.class);

    /**
     * Watcher to detect current test name.
     */
    // CHECKSTYLE:OFF
    @Rule
    public TestWatcher watcher = new TestWatcher() {
        // CHECKSTYLE:ON

        /**
         * Method called whenever JUnit starts a test.
         *
         * @param description Information about the test.
         */
        protected void starting(final Description description) {
            LOGGER.info("Start Test: " + description.getClassName() + "." + description.getMethodName() + ".");
        }
    };

    /**
     * Retrieve a field in its accessible state.
     *
     * <p>
     * Scenario: field exists without a getter and is protected or private. This allows you to inspect that field's
     * state e.g. <code>LocalDateTime requestDateTimeField = (LocalDateTime) getAccesibleField(
     * ServiceRequest.class, "requestDateTime", LocalDateTime.class,
     * serviceRequest)</code>
     * </p>
     *
     * @param clazzUnderTest This is the class that owns the method
     * @param methodName     this is the method name
     * @param paramTypes     the arguments
     * @return the method in its accesible form.
     */
    public Method makeMethodAccesible(final Class<?> clazzUnderTest, final String methodName,
                                      final Class<?>... paramTypes) {
        Method method = null;
        try {
            method = clazzUnderTest.getDeclaredMethod(methodName, paramTypes);
            method.setAccessible(true);
        } catch (final SecurityException e) {
            LOGGER.debug(e.getMessage());
            assertTrue("SecurityException please debug test", false);
        } catch (final NoSuchMethodException e) {
            LOGGER.debug(e.getMessage());
            assertTrue("NoSuchMethodException please debug test", false);
        }
        return method;
    }
}
