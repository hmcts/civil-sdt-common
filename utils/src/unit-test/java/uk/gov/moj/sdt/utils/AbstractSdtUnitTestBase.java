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

package uk.gov.moj.sdt.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.util.ReflectionUtils;

/**
 * Class represents base class for all unit test cases.
 *
 * @author Robin Compston
 */
public abstract class AbstractSdtUnitTestBase {

    /**
     * Standard JUnit setUp method. Gets run before each test method.
     *
     * @throws Exception May throw runtime exception.
     */
    @BeforeEach
    public void setUp() throws Exception {
        // Local set up if we are running outside the appserver.
        this.setUpLocalTests();
    }

    /**
     * Method to be overridden in derived class to do test specific setup.
     *
     * @throws Exception general problem.
     */
    protected void setUpLocalTests() throws Exception {
    }

    /**
     * Convert windows String to UNIX by removing all Hex 0d.
     *
     * @param in String from Windows platform.
     * @return String for UNIX platrofm.
     */
    protected String dos2Unix(final String in) {
        // Array to hold input.
        final char[] win = in.toCharArray();
        final int len = in.length();

        // Array to hold output.
        final char[] unix = new char[len];

        int pos2 = 0;
        for (int pos1 = 0; pos1 < len; pos1++) {
            // Copy all but CARRIAGE RETURN.
            if (win[pos1] != '\r') {
                unix[pos2++] = win[pos1];
            }
        }

        return new String(unix);
    }

    /**
     * Retrieve a field in its accessible state.
     *
     * <p>
     * Scenario: field exists without a getter and is protected or private. This allows you to inspect that field's
     * state e.g. <code>LocalDateTime requestDateTimeField = (LocalDateTime) getAccessibleField(
     * ServiceRequest.class, "requestDateTime", LocalDateTime.class,
     * serviceRequest)</code>
     * </p>
     *
     * @param clazzUnderTest This is the class that owns the field
     * @param fieldName      this is the field name
     * @param clazzOfField   this is the field type
     * @param target         this is the object which has the variable
     * @return the field from the target object as an object.
     */
    public Object getAccessibleField(final Class<?> clazzUnderTest, final String fieldName,
                                    final Class<?> clazzOfField, final Object target) {
        Object object = null;

        Field field = ReflectionUtils.findField(clazzUnderTest, fieldName);
        try {
            if (null == field) {
                field = ReflectionUtils.findField(clazzUnderTest, fieldName, clazzOfField);
            }
            ReflectionUtils.makeAccessible(field);
            object = ReflectionUtils.getField(field, target);
            if (null == object) {
                throw new RuntimeException("Field " + fieldName + " does not exist");
            }
        } catch (Exception e) {
            throw new RuntimeException("Field " + fieldName + " does not exist or is not accessible");
        }
        return object;
    }

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
     */
    public void makeMethodAccessible(final Class<?> clazzUnderTest, final String methodName) {
        final Method method = ReflectionUtils.findMethod(clazzUnderTest, methodName);
        ReflectionUtils.makeAccessible(method);
    }

}
